package com.ningxiang.shop.multishop.service.impl;

import com.ningxiang.shop.common.cache.constant.CacheNames;
import com.ningxiang.shop.common.database.dto.PageDTO;
import com.ningxiang.shop.common.database.util.PageUtil;
import com.ningxiang.shop.common.database.vo.PageVO;
import com.ningxiang.shop.multishop.dto.HotSearchDTO;
import com.ningxiang.shop.multishop.model.HotSearch;
import com.ningxiang.shop.multishop.mapper.HotSearchMapper;
import com.ningxiang.shop.multishop.service.HotSearchService;
import com.ningxiang.shop.multishop.vo.HotSearchVO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 热搜
 *
 * @author YXF
 * @date 2021-01-27 09:10:00
 */
@Service
public class HotSearchServiceImpl implements HotSearchService {

    @Autowired
    private HotSearchMapper hotSearchMapper;

    @Override
    public PageVO<HotSearchVO> page(PageDTO pageDTO, HotSearchDTO hotSearchDTO) {
        return PageUtil.doPage(pageDTO, () -> hotSearchMapper.list(hotSearchDTO));
    }

    @Override
    public HotSearchVO getByHotSearchId(Long hotSearchId) {
        return hotSearchMapper.getByHotSearchId(hotSearchId);
    }

    @Override
    public void save(HotSearch hotSearch) {
        hotSearchMapper.save(hotSearch);
    }

    @Override
    public void update(HotSearch hotSearch) {
        hotSearchMapper.update(hotSearch);
    }

    @Override
    public void deleteById(Long hotSearchId, Long shopId) {
        hotSearchMapper.deleteById(hotSearchId, shopId);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.HOT_SEARCH_LIST_KEY, key = "#shopId")
    public List<HotSearchVO> listByShopId(Long shopId) {
        return hotSearchMapper.listByShopId(shopId);
    }

    @Override
    @CacheEvict(cacheNames = CacheNames.HOT_SEARCH_LIST_KEY, key = "#shopId")
    public void removeHotSearchListCache(Long shopId) {
    }
}
