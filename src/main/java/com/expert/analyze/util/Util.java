package com.expert.analyze.util;

import java.util.Set;

public class Util {

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
