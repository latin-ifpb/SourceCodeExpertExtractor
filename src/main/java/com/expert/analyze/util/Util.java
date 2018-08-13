package com.expert.analyze.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.jgit.revwalk.RevCommit;

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
	
	public static void sortCommits(List<RevCommit> commits){
		Collections.sort(commits, new Comparator<RevCommit>() {
			public int compare(RevCommit o1, RevCommit o2) {
				return o1.getAuthorIdent().getTimeZoneOffset() - o2.getAuthorIdent().getTimeZoneOffset();
			}
		});
	}
	
	public static RevCommit getNextCommit(List<RevCommit> commits, RevCommit commitNext) {
		int idx = commits.indexOf(commitNext);
		if (idx < 0 || idx + 1 == commits.size())
			return null;
		return commits.get(idx + 1);
	}

	public static RevCommit getPreviousCommit(List<RevCommit> commits, RevCommit commitActual) {
		int idx = commits.indexOf(commitActual);
		if (idx <= 0 || idx - 1 == commits.size())
			return null;
		return commits.get(idx - 1);
	}
	
	public static Developer findDeveloperPerEmail(String email, Set<Developer> developers) {
		Developer d = developers.stream().filter(dev -> {
			if (dev.getEmail().equalsIgnoreCase(email)) {
				return true;
			} else {
				return false;
			}
		}).findAny().orElse(null);
		return d;
	}
	
}
