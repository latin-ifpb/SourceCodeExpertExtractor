package com.expert.analyze.util;

import java.util.Set;

public class Util {

	/**
	 * Find a filename per filepath passed per param
	 * @param namesFiles - List the namesFiles
	 * @param filePath - FilePath to find
	 * @return String FileName 
	 */
	public static String findFileNamePerPath(Set<String> namesFiles, String filePath){
		String fileName = namesFiles.stream().filter(file -> {
			if (filePath.contains(file)) {
				return true;
			} else {
				return false;
			}
		}).findAny().orElse(null);
		
		return fileName;
	}
}
