package com.openxu.db.base;

import java.io.Serializable;
import java.util.List;

/**
 * 数据操作的接口的公共部分
 * 
 * @author Administrator
 * 
 * @param <M>
 */
public interface DAO<M> {
	/**
	 * 添加
	 * @param m
	 * @return
	 */
	long insert(M m);
	/**
	 * 添加
	 * @param m
	 * @return
	 */
	void insertAll(List<M> list);

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	int delete(Serializable id);// int long String:JPA id Serializable
	void deleteAll();
	/**
	 * 更新
	 * @param m
	 * @return
	 */
	int updata(M m);
	int updataAll(List<M> ms);

	/**
	 * 查询
	 * @return
	 */
	List<M> findAll();

	/**
	 * 按照条件查询
	 * @param selection
	 * @param selectionArgs
	 * @param orderBy
	 * @return
	 */
	List<M> findByCondition(String selection, String[] selectionArgs,
                            String orderBy);
	List<M> getPartWords(int start, int num);
	List<M> findSort();
	
	int getTotalCount();
	int getTotalCount(int remenber);
	
	List<M> findShunxu(int num);
	List<M> findRandom(int num);
	List<M> findFxRandom(int num);
	List<M> findTestRandom(int id);
	
	List<M> searchWords(String en, int num);
	
	M searchWord(String en);
}
