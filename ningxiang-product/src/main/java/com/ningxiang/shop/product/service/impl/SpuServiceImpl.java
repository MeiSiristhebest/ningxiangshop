package com.ningxiang.shop.product.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.ningxiang.shop.api.multishop.feign.IndexImgFeignClient;
import com.ningxiang.shop.api.multishop.feign.ShopDetailFeignClient;
import com.ningxiang.shop.api.multishop.vo.ShopDetailVO;
import com.ningxiang.shop.api.product.bo.EsAttrBO;
import com.ningxiang.shop.api.product.bo.EsProductBO;
import com.ningxiang.shop.api.product.vo.CategoryVO;
import com.ningxiang.shop.api.product.vo.SpuAttrValueVO;
import com.ningxiang.shop.common.cache.constant.CacheNames;
import com.ningxiang.shop.common.cache.util.RedisUtil;
import com.ningxiang.shop.common.constant.Constant;
import com.ningxiang.shop.common.constant.StatusEnum;
import com.ningxiang.shop.common.database.dto.PageDTO;
import com.ningxiang.shop.common.database.util.PageUtil;
import com.ningxiang.shop.common.database.vo.PageVO;
import com.ningxiang.shop.common.exception.NingxiangException;
import com.ningxiang.shop.common.response.ServerResponseEntity;
import com.ningxiang.shop.common.security.AuthUserContext;
import com.ningxiang.shop.product.dto.SpuDTO;
import com.ningxiang.shop.product.dto.SpuPageSearchDTO;
import com.ningxiang.shop.product.mapper.SpuMapper;
import com.ningxiang.shop.product.service.*;
import com.ningxiang.shop.api.product.vo.SpuVO;
import com.ningxiang.shop.product.model.Spu;
import com.ningxiang.shop.product.model.SpuAttrValue;
import com.ningxiang.shop.product.model.SpuDetail;
import com.ningxiang.shop.product.model.SpuExtension;
import io.seata.spring.annotation.GlobalTransactional;
import com.ningxiang.shop.common.util.BeanUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * spu信息
 *
 * @author FrozenWatermelon
 * @date 2020-10-28 15:27:24
 */
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailService spuDetailService;

    @Autowired
    private SpuExtensionService spuExtensionService;

    @Autowired
    private SpuAttrValueService spuAttrValueService;



    @Autowired
    private SkuService skuService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ShopDetailFeignClient shopDetailFeignClient;

    @Autowired
    private IndexImgFeignClient indexImgFeignClient;

    @Override
    public PageVO<SpuVO> page(PageDTO pageDTO, SpuPageSearchDTO spuDTO) {
        PageVO<SpuVO> spuPage = PageUtil.doPage(pageDTO, () -> spuMapper.list(spuDTO));
        return spuPage;
    }

    @Override
    @Cacheable(cacheNames = CacheNames.SPU_KEY, key = "#spuId",sync = true)
    public SpuVO getBySpuId(Long spuId) {
        return spuMapper.getBySpuId(spuId);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.SPU_EXTENSION_KEY, key = "#spuId",sync = true)
    public SpuExtension getSpuExtension(Long spuId) {
        SpuExtension spuExtension = spuExtensionService.getBySpuId(spuId);
        return spuExtension;
    }
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.SPU_KEY, key = "#spuId"),
            @CacheEvict(cacheNames = CacheNames.SPU_EXTENSION_KEY, key = "#spuId")
    })
    public void removeSpuCacheBySpuId(Long spuId) {
    }

    @Override
    public void batchRemoveSpuCacheBySpuId(List<Long> spuIds) {
        RedisUtil.deleteBatch(CacheNames.SPU_KEY, spuIds);
        RedisUtil.deleteBatch(CacheNames.SPU_EXTENSION_KEY, spuIds);
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void changeSpuStatus(Long spuId, Integer status) {
        spuMapper.changeSpuStatus(spuId, status);
        if (!Objects.equals(status, StatusEnum.ENABLE)) {
            SpuVO spuVO = spuMapper.getBySpuId(spuId);
            ServerResponseEntity<Void> imgRes = indexImgFeignClient.deleteBySpuId(spuVO.getSpuId(), spuVO.getShopId());
            if (!imgRes.isSuccess()) {
                throw new NingxiangException("服务异常");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SpuDTO spuDTO) {
        Spu spu = BeanUtil.map(spuDTO, Spu.class);
        spu.setShopId(AuthUserContext.get().getTenantId());
        spu.setStatus(StatusEnum.ENABLE.value());
        spu.setShopId(AuthUserContext.get().getTenantId());
        // 1.保存商品信息
        spuMapper.save(spu);
        // 2.保存商品其他信息，规格、详细、扩展信息
        List<SpuAttrValue> spuAttrValues = BeanUtil.mapAsList(spuDTO.getSpuAttrValues(), SpuAttrValue.class);
        for (SpuAttrValue spuAttrValue : spuAttrValues) {
            if (Objects.isNull(spuAttrValue.getAttrValueId())) {
                spuAttrValue.setAttrValueId(0L);
            }
        }
        spuAttrValueService.saveBatch(spu.getSpuId(),spuAttrValues);
        SpuDetail spuDetail = new SpuDetail();
        spuDetail.setSpuId(spu.getSpuId());
        spuDetail.setDetail(spuDTO.getDetail());
        spuDetailService.save(spuDetail);

        SpuExtension spuExtension = new SpuExtension();
        spuExtension.setSpuId(spu.getSpuId());
        spuExtension.setActualStock(spuDTO.getTotalStock());
        spuExtension.setStock(spuDTO.getTotalStock());
        spuExtensionService.save(spuExtension);

        // 3.保存sku信息
        skuService.save(spu.getSpuId(),spuDTO.getSkuList());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SpuDTO spuDTO) {
        Spu spu = BeanUtil.map(spuDTO, Spu.class);
        SpuServiceImpl spuService = (SpuServiceImpl) AopContext.currentProxy();
        SpuVO dbSpu = spuService.getBySpuId(spu.getSpuId());
        // 1.修改商品信息
        spuMapper.update(spu);
        List<SpuAttrValue> spuAttrValues = BeanUtil.mapAsList(spuDTO.getSpuAttrValues(), SpuAttrValue.class);
        spuAttrValueService.update(spu.getSpuId(),spuAttrValues, dbSpu.getSpuAttrValues());

        // 2.修改商品详情
        SpuDetail spuDetail = new SpuDetail();
        spuDetail.setSpuId(spu.getSpuId());
        spuDetail.setDetail(spuDTO.getDetail());
        spuDetailService.update(spuDetail);

        // 3.修改商品库存
        if (Objects.nonNull(spuDTO.getChangeStock()) && spuDTO.getChangeStock() > 0) {
            SpuExtension spuExtension = new SpuExtension();
            spuExtension.setSpuId(spu.getSpuId());
            spuExtension.setStock(spuDTO.getChangeStock());
            spuExtensionService.updateStock(spu.getSpuId(), spuDTO.getTotalStock());
        }

        // 4.修改商品sku信息
        skuService.update(spu.getSpuId(),spuDTO.getSkuList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @GlobalTransactional(rollbackFor = Exception.class)
    public void deleteById(Long spuId) {
        SpuVO spuVO = getBySpuId(spuId);
        if(Objects.isNull(spuVO) || Objects.equals(spuVO.getStatus(), StatusEnum.DELETE.value())){
            throw new NingxiangException("商品不存在或者已被删除！");
        }
        // 删除商品、sku信息(逻辑删除)
        spuMapper.updateStatusBySpuId(spuId);
        skuService.deleteBySpuId(spuId);
        // 把轮播图中关联了该商品的数据删除
        ServerResponseEntity<Void> imgRes = indexImgFeignClient.deleteBySpuId(spuId, spuVO.getShopId());
        if (!imgRes.isSuccess()) {
            throw new NingxiangException("服务异常");
        }
    }

    @Override
    public void updateSpuOrSku(SpuDTO spuDTO) {
        Spu spu = new Spu();
        spu.setSpuId(spuDTO.getSpuId());
        spu.setName(spuDTO.getName());
        spu.setSeq(spuDTO.getSeq());
        if (CollUtil.isNotEmpty(spuDTO.getSkuList())) {
            skuService.updateAmountOrStock(spuDTO);
        }
        if (Objects.nonNull(spuDTO.getChangeStock()) && spuDTO.getChangeStock() > 0) {
            spuExtensionService.updateStock(spuDTO.getSpuId(), spuDTO.getChangeStock());
            return;
        }
        spu.setPriceFee(spuDTO.getPriceFee());
        spuMapper.update(spu);
    }

    @Override
    public void updateSpuUpdateTime(List<Long> spuIds, List<Long> categoryIds) {
        if (CollUtil.isEmpty(spuIds) && CollUtil.isEmpty(categoryIds)) {
            return;
        }
        spuMapper.updateSpuUpdateTime(spuIds, categoryIds);
    }

    @Override
    public EsProductBO loadEsProductBO(Long spuId) {
        // 获取商品、品牌数据
        EsProductBO esProductBO = spuMapper.loadEsProductBO(spuId);
        // 获取分类数据
        CategoryVO category = categoryService.getPathNameByCategoryId(esProductBO.getCategoryId());
        String[] categoryIdArray = category.getPath().split(Constant.CATEGORY_INTERVAL);
        esProductBO.setCategoryName(category.getName());
        for (int i = 0;i<categoryIdArray.length;i++) {
            if (i == 0) {
                esProductBO.setPrimaryCategoryId(Long.valueOf(categoryIdArray[i]));
                esProductBO.setPrimaryCategoryName(category.getPathNames().get(i));
            } else {
                esProductBO.setSecondaryCategoryId(Long.valueOf(categoryIdArray[i]));
                esProductBO.setSecondaryCategoryName(category.getPathNames().get(i));
            }
        }
        CategoryVO shopCategory = categoryService.getById(esProductBO.getShopSecondaryCategoryId());
        if (Objects.nonNull(shopCategory)) {
            esProductBO.setShopSecondaryCategoryName(shopCategory.getName());
            esProductBO.setShopPrimaryCategoryId(shopCategory.getParentId());
//            esProductBO.setShopPrimaryCategoryName(shopCategory.getPathNames().get(0));
        }
        // 获取属性
        List<SpuAttrValueVO> spuAttrsBySpuId = spuAttrValueService.getSpuAttrsBySpuId(spuId);
        List<SpuAttrValueVO> attrs = spuAttrsBySpuId.stream().filter(spuAttrValueVO -> spuAttrValueVO.getSearchType().equals(1)).collect(Collectors.toList());
        esProductBO.setAttrs(BeanUtil.mapAsList(attrs, EsAttrBO.class));
        return esProductBO;
    }

    @Override
    public List<Long> getSpuIdsBySpuUpdateDTO(List<Long> shopCategoryIds, List<Long> categoryIds, Long brandId, Long shopId) {
        if (CollUtil.isEmpty(shopCategoryIds) && CollUtil.isEmpty(categoryIds) && Objects.isNull(brandId) && Objects.isNull(shopId)) {
            return new ArrayList<>();
        }
        return spuMapper.getSpuIdsBySpuUpdateDTO(shopCategoryIds, categoryIds, brandId, shopId);
    }

    @Override
    public List<SpuVO> listBySpuIds(List<Long> spuIds, String prodName, Integer isFailure) {
        if (CollUtil.isEmpty(spuIds)) {
            return new ArrayList<>();
        }
        return spuMapper.listBySpuIds(spuIds,prodName,isFailure);
    }

    @Override
    public PageVO<SpuVO> platformPage(PageDTO pageDTO, SpuPageSearchDTO spuDTO) {
        PageVO<SpuVO> page = page(pageDTO, spuDTO);
        Set<Long> shopIdSet = page.getList().stream().map(SpuVO::getShopId).collect(Collectors.toSet());
        ServerResponseEntity<List<ShopDetailVO>> shopResponse = shopDetailFeignClient.listByShopIds(new ArrayList<>(shopIdSet));
        Map<Long, ShopDetailVO> shopMap = shopResponse.getData().stream().collect(Collectors.toMap(ShopDetailVO::getShopId, s -> s));
        for (SpuVO spuVO : page.getList()) {
            ShopDetailVO shopDetailVO = shopMap.get(spuVO.getShopId());
            if (Objects.isNull(shopDetailVO)) {
                continue;
            }
            spuVO.setShopName(shopDetailVO.getShopName());
        }
        return page;
    }

    @Override
    public PageVO<SpuVO> pageByLangAndTagId(PageDTO pageDTO, SpuDTO spuDTO, Integer isContain) {
        return PageUtil.doPage(pageDTO, () -> spuMapper.pageByLangAndTagId(pageDTO,spuDTO,isContain));
    }

    @Override
    public void batchChangeSpuStatusByCids(List<Long> cidList, Long shopId, Integer status) {
        List<Long> spuIdList;
        if (Objects.equals(shopId, Constant.PLATFORM_SHOP_ID)) {
            spuIdList = spuMapper.listIdsByCidListAndTypeAndShopId(cidList, 1, shopId);
        } else {
            spuIdList = spuMapper.listIdsByCidListAndTypeAndShopId(cidList, 2, shopId);
        }
        if (Objects.isNull(spuIdList) || spuIdList.size() == 0) {
            return;
        }
        spuMapper.batchChangeSpuStatusBySpuIdsAndStatus(spuIdList, status);
        this.batchRemoveSpuCacheBySpuId(spuIdList);
    }
}
