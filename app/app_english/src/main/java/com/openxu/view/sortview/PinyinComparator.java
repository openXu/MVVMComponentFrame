package com.openxu.view.sortview;

import java.util.Comparator;

import com.openxu.db.bean.OpenWord;

public class PinyinComparator implements Comparator<OpenWord> {

	/**
	 * >0, o1排后面
	 */
	public int compare(OpenWord o1, OpenWord o2) {
		//
		if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			int sort = o1.getSortLetters().compareTo(o2.getSortLetters());
			if (sort == 0) // 排序字母相同，需要排getEnglish
				return o1.getEnglish().compareTo(o2.getEnglish());
			return sort;
		}
	}

}
