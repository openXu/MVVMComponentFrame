package com.openxu.view.sortview;

import java.util.Comparator;

import com.openxu.db.bean.ChatUser;

public class ContactComparator implements Comparator<ChatUser> {

	public int compare(ChatUser o1, ChatUser o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
