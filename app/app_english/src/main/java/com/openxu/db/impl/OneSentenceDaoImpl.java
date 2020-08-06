package com.openxu.db.impl;

import java.util.List;

import com.openxu.db.SentenceDao;
import com.openxu.db.base.SentenceDaoSupport;
import com.openxu.db.bean.OneSentence;

public class OneSentenceDaoImpl extends SentenceDaoSupport<OneSentence> implements SentenceDao {

	private String TAG = "OneSentenceDaoImpl";

	public OneSentenceDaoImpl() {
		super();
	}

	@Override
	public List<OneSentence> findSort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTotalCount(int remenber) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<OneSentence> findShunxu(int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OneSentence> findRandom(int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OneSentence> findFxRandom(int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OneSentence> findTestRandom(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OneSentence> searchWords(String en, int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OneSentence searchWord(String en) {
		return null;
	}

	@Override
	public List<OneSentence> getPartWords(int start, int num) {
		// TODO Auto-generated method stub
		return null;
	}


}
