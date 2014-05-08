package com.gmnav.model.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
	
	public interface Predicate<T> {
		boolean check(T item, int index);
	}
	
	public static <T> List<T> removeAll(List<T> list, Predicate<T> pred) {
		List<T> newList = new ArrayList<T>();
		for (int i = 0; i < list.size(); i++) {			
			if (pred.check(list.get(i), i)) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}
}
