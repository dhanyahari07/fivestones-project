package org.me.five_stones_project.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tangl Andras
 */

public class MapFactory {
	public static<K, V> Map<K, V> createMap(K[] keys, V[] values) {
		if(keys.length != values.length)
			throw new IllegalArgumentException("The length must be the same");
		
		Map<K, V> map = new HashMap<K, V>();
		for(int i = 0; i < keys.length; ++i)
			map.put(keys[i], values[i]);
		
		return map;
	}
}
