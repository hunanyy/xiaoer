package com.example.wifi_info.tools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ApSet {

	private static String[] str = { "TP-LINK_7CD9D2", "TP-LINK_792BC0",
			"TP-LINK_7CDF82", "TP-LINK_7CD82C", "TP-LINK_7CD9D0",
			"TP-LINK_7CF91E", "TP-LINK_zy" };

	private static Set<String> ap = new HashSet<String>(Arrays.asList(str));

	public static Set<String> getAps() {
		return ap;

	}

	/*
	 * public static List<String> myAPName() {
	 * 
	 * List<String> al = new ArrayList<String>(); al.add("TP-LINK_7CD9D2");
	 * al.add("TP-LINK_792BC0"); al.add("TP-LINK_7CDF82");
	 * al.add("TP-LINK_7CD82C"); al.add("TP-LINK_7CD9D0");
	 * al.add("TP-LINK_7CF91E"); al.add("TP-LINK_zy");
	 * 
	 * // al.add("GF");
	 * 
	 * // al.add("NJUPT_M"); // al.add("CMCC-EDU");
	 * 
	 * return al;
	 * 
	 * }
	 */
}
