package com.expert.analyze.util;

import java.io.File;

/**
 * Class for validade actions 
 * @author wemerson
 *
 */
public class Validador {

	/**
	 * Verify this directory exist in path 
	 * @param file - Directory to create
	 * @return Boolean
	 */
	public static Boolean isDirectoryExist(File file) {
		if (!file.exists()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Verify this String is empty or null
	 * @param s String
	 * @return Boolean
	 */
	public static Boolean isStringEmpty(String s) {
		if (s != null && s.trim().isEmpty()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
}
