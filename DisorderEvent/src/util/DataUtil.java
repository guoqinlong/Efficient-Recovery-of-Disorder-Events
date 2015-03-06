package util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

public class DataUtil {

	/**
	 * 
	 * 
	 * 
	 * @param eventPositions
	 * @return
	 */
	public static HashMap<String, LinkedList<Integer>> cloneEventPositions(
			HashMap<String, LinkedList<Integer>> eventPositions) 
	{
		HashMap<String, LinkedList<Integer>> ret = new HashMap<String, LinkedList<Integer>> ();
		for (Entry<String, LinkedList<Integer> > entry	:	eventPositions.entrySet())
		{
			String key = entry.getKey();
			LinkedList<Integer> value = entry.getValue();
			LinkedList<Integer> newValue = new LinkedList<Integer>(value);
			ret.put(key, newValue);
		}
		return ret;
	}

}
