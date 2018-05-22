package com.expert.analyze.util;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.expert.analyze.model.Developer;

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
	

	public static Developer queryDeveloperPerName(Set<Developer> team) {
		String name = input("Digite o nome do Desenvolver para pesquisar");
		Developer d = team.stream().filter(dev -> {
			if (dev.getName().equalsIgnoreCase(name)) {
				return true;
			} else {
				return false;
			}
		}).findAny().orElse(null);
		return d;
	}

	public static String queryFilePerName(List<String> filesName) {
		String name = input("Digite o nome do arquivo para pesquisar");
		String fileName = filesName.stream().filter(file -> {
			if (file.contains(name)) {
				return true;
			} else {
				return false;
			}
		}).findAny().orElse(null);
		return fileName;
	}

	private static String input(String message) throws IllegalArgumentException {
		Scanner sc = new Scanner(System.in);
		System.out.println(message);
		String input = sc.nextLine();
		System.out.println("\n");
		return input;
	}
}
