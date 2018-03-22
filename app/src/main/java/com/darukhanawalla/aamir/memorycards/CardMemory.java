package com.darukhanawalla.aamir.memorycards;

import java.util.*;

public class CardMemory extends LinkedHashMap<Integer, Integer> {
	private static final int MAX_ENTRIES = 4;

	protected boolean removeEldestEntry(Entry eldest) {
		return size() > MAX_ENTRIES;
	}
}
