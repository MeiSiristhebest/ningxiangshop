package com.ningxiang.shop.leaf.segment.dao;

import com.ningxiang.shop.leaf.segment.model.LeafAlloc;

import java.util.List;

/**
 * @author leaf
 */
public interface IDAllocDao {

	/**
	 * com.ningxiang.shop.leaf.segment.dao.IDAllocMapper.getAllLeafAllocs
	 * @return List<LeafAlloc>
	 */
	List<LeafAlloc> getAllLeafAllocs();

	/**
	 * updateMaxIdAndGetLeafAlloc
	 * @param tag tag
	 * @return LeafAlloc
	 */
	LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);

	/**
	 * updateMaxIdByCustomStepAndGetLeafAlloc
	 * @param leafAlloc leafAlloc
	 * @return LeafAlloc
	 */
	LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);

	/**
	 * getAllTags
	 * @return List<String>
	 */
	List<String> getAllTags();

}
