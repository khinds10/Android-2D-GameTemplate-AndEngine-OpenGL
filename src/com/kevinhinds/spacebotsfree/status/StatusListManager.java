package com.kevinhinds.spacebotsfree.status;

import java.util.Arrays;

/**
 * Android SharedPreferences are limited to basic types, 
 * 	if we have a list of items saved for a particular key (CSV format) utilities exist here to create/update lists quickly
 * 
 * @author khinds
 */
public class StatusListManager {

	/**
	 * create an empty CSV list with all default values
	 * 
	 * @param count
	 * @param defaultValue
	 * @return
	 */
	public static String createDefaultCSVList(int count, String defaultValue) {
		String defaultList = "";
		int x = 1;
		while (x <= count) {
			defaultList = defaultList + defaultValue + ",";
			x++;
		}
		return defaultList.replaceAll(",$", "");
	}

	/**
	 * for given CSV list, update the referred item to a new status value
	 * 
	 * @param CSVList
	 * @param index,
	 *            starting counting from number 1
	 * @param newStatus
	 * @return
	 */
	public static String updateIndexByValue(String CSVList, int index, String newStatus) {
		if (StatusListManager.isEmpty(CSVList)) {
			return "";
		}
		String[] listItems = CSVList.split(",");
		String listUpdated = "";
		int x = 1;
		for (String listItem : listItems) {
			if (x == index) {
				listUpdated = listUpdated + newStatus + ",";
			} else {
				listUpdated = listUpdated + listItem + ",";
			}
			x++;
		}
		return listUpdated.replaceAll(",$", "");
	}

	/**
	 * check if the current CSVList contains a value you've specified
	 * 
	 * @param CSVList
	 * @param checkValue
	 * @return
	 */
	public static boolean containsValue(String CSVList, String checkValue) {
		String[] listItems = CSVList.split(",");
		return Arrays.asList(listItems).contains(checkValue);
	}

	/**
	 * get CSV individual value by index
	 * 
	 * @param CSVList
	 * @param index,
	 *            starting counting from number 1
	 * @return
	 */
	public static String getValueByIndex(String CSVList, int index) {
		if (StatusListManager.isEmpty(CSVList)) {
			return "";
		}
		String[] listItems = CSVList.split(",");
		return listItems[index - 1];
	}

	/**
	 * check if a string isEmpty
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		if (string != null && string.length() != 0) {
			return false;
		}
		return true;
	}
}