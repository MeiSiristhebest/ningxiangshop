package com.ningxiang.shop.common.security.filter;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ningxiang.shop.api.auth.bo.UserInfoInTokenBO;
import com.ningxiang.shop.common.feign.FeignInsideAuthConfig;
import com.ningxiang.shop.common.handler.HttpHandler;
import com.ningxiang.shop.common.response.ResponseEnum;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.common.security.adapter.AuthConfigAdapter;
import com.ningxiang.shop.common.util.IpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * 授权过滤拦截器，作为业务微服务的安全卫士：
 * 直接解析网关透传并已进行 URL 编码的用户信息请求头，并存入上下文。
 *
 * @author Ningxiang
 */
@Component
public class AuthFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

	@Autowired
	private AuthConfigAdapter authConfigAdapter;

	@Autowired
	private HttpHandler httpHandler;

	@Autowired
	private FeignInsideAuthConfig feignInsideAuthConfig;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// 1. 内部 Feign 间调用校验，符合则放行
		if (!feignRequestCheck(req)) {
			httpHandler.printServerResponseToWeb(ServerResponseEntity.fail(ResponseEnum.UNAUTHORIZED));
			return;
		}

		// 2. 外部访问免鉴权的白名单路径放行
		List<String> excludePathPatterns = authConfigAdapter.excludePathPatterns();
		if (CollectionUtil.isNotEmpty(excludePathPatterns)) {
			AntPathMatcher pathMatcher = new AntPathMatcher();
			for (String excludePathPattern : excludePathPatterns) {
				if (pathMatcher.match(excludePathPattern, req.getRequestURI())) {
					chain.doFilter(req, resp);
					return;
				}
			}
		}

		// 3. 从 Header 提取网关透传的用户身份信息
		String userInfoHeader = req.getHeader("x-user-info");

		// 4. 双重防御：未携带用户信息头则直接判断为未登录
		if (StrUtil.isBlank(userInfoHeader)) {
			httpHandler.printServerResponseToWeb(ServerResponseEntity.fail(ResponseEnum.UNAUTHORIZED));
			return;
		}

		try {
			// 5. URL 解密后并还原反序列化成 BO 对象
			String decodedUserInfo = URLDecoder.decode(userInfoHeader, StandardCharsets.UTF_8.name());
			UserInfoInTokenBO userInfoInToken = objectMapper.readValue(decodedUserInfo, UserInfoInTokenBO.class);

			// 6. 保存进当前线程上下文 ThreadLocal
			AuthUserContext.set(userInfoInToken);

			chain.doFilter(req, resp);
		} catch (Exception e) {
			logger.error("解析网关透传用户信息异常", e);
			httpHandler.printServerResponseToWeb(ServerResponseEntity.fail(ResponseEnum.UNAUTHORIZED));
		} finally {
			// 7. 清理上下文，防止线程复用引发的内存遗留
			AuthUserContext.clean();
		}
	}

	private boolean feignRequestCheck(HttpServletRequest req) {
		if (!req.getRequestURI().startsWith(FeignInsideAuthConfig.FEIGN_INSIDE_URL_PREFIX)) {
			return true;
		}
		String feignInsideSecret = req.getHeader(feignInsideAuthConfig.getKey());

		if (StrUtil.isBlank(feignInsideSecret) || !Objects.equals(feignInsideSecret, feignInsideAuthConfig.getSecret())) {
			return false;
		}
		List<String> ips = feignInsideAuthConfig.getIps();
		ips.removeIf(StrUtil::isBlank);
		if (CollectionUtil.isNotEmpty(ips) && !ips.contains(IpHelper.getIpAddr())) {
			logger.error("ip not in ip White list: {}, ip, {}", ips, IpHelper.getIpAddr());
			return false;
		}
		return true;
	}
}

