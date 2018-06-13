package com.expert.analyze.model.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.expert.analyze.model.Developer;
import com.expert.analyze.model.FileDeveloper;
import com.expert.analyze.util.Util;
import com.expert.analyze.util.Validador;

/**
 * Class responsable to measure expert per file the reposotor
 * @author wemerson
 *
 */
public class MeasurePerFile extends Measure {

	//List the object matrix, relationship file x developer and quantity commit in file
	private List<FileDeveloper> matrixFileDevelopersPerCommits;
	//Map contins the developer and file per commit
	private Map<Developer, Map<String, Integer>> developersPerFiles;
	//Map the File per developer and commit
	private Map<String, Map<Developer, Integer>> filesPerDeveloper;
	private Integer COUNT = 0;

	public MeasurePerFile(Repository repository) {
		setRepository(repository);
	}

	/**
	 * Evaluate a quantity commit in file per developer
	 * @param commitsLocal
	 * @param fileFind
	 * @param developer
	 * @return Integer - quantity of commit
	 */
	public Integer evaluateQuantityCommitPerFilePerDeveloper(List<RevCommit> commitsLocal, String fileFind,
			Developer developer) {
		Integer count = 0;
		try {
			//walk in list the commit 
			for (RevCommit commit : commitsLocal) {
				TreeWalk treeWalk = new TreeWalk(getRepository());//defined tree walk in commits
				treeWalk.addTree(commit.getTree());
				treeWalk.setRecursive(true);//defined for recursive walks
				while (treeWalk.next()) {
					//Vefiry is fileFind equal a pathFile in treewalk and is same author on commit
					if (treeWalk.getPathString().equalsIgnoreCase(fileFind) && Validador.isAuthorCommit(commit.getAuthorIdent(),developer)) {
						count++;
					}
				}
				treeWalk.close();
			}
		} catch (IOException e) {
			System.err.println("Error in quantity of commit in file per developer:"+e.getMessage());
		}
		return count;
	}

	/**
	 * Generati a matrix with developer specific x file per commit in file
	 * @param commitsLocal - Commits
	 * @param files - filespath
	 * @param developer - developer
	 */
	public void evaluateQuantityCommitPerFilesPerDeveloperMatrix(List<RevCommit> commitsLocal, Set<String> files,
			Developer developer) {
		matrixFileDevelopersPerCommits = new ArrayList<>();
		//Walk in list the files and get quantity commit per file
		for (String file : files) {
			//Create a Set<Developer> for build a matrix
			Set<Developer> developers = new HashSet<>();
			List<Integer> commitsQuantity = new ArrayList<>();
			//Add a quantity the commit in file that developer
			commitsQuantity.add(evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, file, developer));
			developers.add(developer);
			System.out.println(commitsQuantity);
			//build matrix 
			matrixFileDevelopersPerCommits.add(new FileDeveloper(file, developers, commitsQuantity));
		}
	}

	/**
	 * Generati a matrix with all developers x file per commit in file
	 * @param commitsLocal
	 * @param files
	 * @param developers
	 */
	public void evaluateQuantityCommitPerFilesPerDevelopersMatrix(List<RevCommit> commitsLocal, Set<String> files,
			Set<Developer> developers) {

		matrixFileDevelopersPerCommits = new ArrayList<>();
		//Walk in list the files and get quantity commit per file
		for (String file : files) {
			List<Integer> commitsQuantity = new ArrayList<>();
			for (Developer developer : developers) {
				//Add a quantity the commit in file that developer
				commitsQuantity.add(evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, file, developer));
			}
			//build matrix
			FileDeveloper fd = new FileDeveloper(file, developers, commitsQuantity);
			System.out.println(fd.getFile()+":"+fd.getCommits());
			matrixFileDevelopersPerCommits.add(fd);
		}
	}

	/**
	 * Constract a map with developer per file
	 * @param commitsLocal 
	 * @param files
	 * @param developeres
	 */
	public void evaluateQuantityCommitPerFilesPerDevelopers(List<RevCommit> commitsLocal, Set<String> files,
			Set<Developer> developeres) {
		developersPerFiles = new HashMap<>();
		//walks in list the developers
		for (Developer developer : developeres) {
			//creat a map with file and quantity the commit
			Map<String, Integer> fileQuantityCommit = new HashMap<>();
			//walk in files the reposotory
			for (String file : files) {
				//evaluate quantity the commit
				fileQuantityCommit.put(file, evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, file, developer));
			}
			developersPerFiles.put(developer, fileQuantityCommit);
		}
	}

	/**
	 * Constract a map with file per developer
	 * @param commitsLocal
	 * @param files
	 * @param developeres
	 */
	public void evaluateQuantityCommitInFilesPerDevelopers(List<RevCommit> commitsLocal, Set<String> files,
			Set<Developer> developeres) {
		filesPerDeveloper = new HashMap<>();
		//walks in list the files on repository
		for (String fileName : files) {
			Map<Developer, Integer> developerQuantityCommit = new HashMap<>();
			//walks in list the developers
			for (Developer developer : developeres) {
				//evaluate quantity the commit
				developerQuantityCommit.put(developer,evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, fileName, developer));
			}
			filesPerDeveloper.put(fileName, developerQuantityCommit);
		}
	}

	/**
	 * @return the developeresPerFiles
	 */
	public Map<Developer, Map<String, Integer>> getDevelopersPerFiles() {
		return developersPerFiles;
	}

	/**
	 * @param developeresPerFiles
	 *            the developeresPerFiles to set
	 */
	public void setDevelopersPerFiles(Map<Developer, Map<String, Integer>> developeresPerFiles) {
		this.developersPerFiles = developeresPerFiles;
	}

	/**
	 * Show infortion the map developer per file
	 * @param developerPerFiles
	 */
	public void showDevelopersPerFiles(Map<Developer, Map<String, Integer>> developerPerFiles) {
		for (Map.Entry<Developer, Map<String, Integer>> cd : developerPerFiles.entrySet()) {
			Map<String, Integer> values = cd.getValue();
			for (Map.Entry<String, Integer> v : values.entrySet()) {
				System.out.println(
						"Developer: " + cd.getKey().getName() + " File:" + v.getKey() + " Commit: " + v.getValue());
			}
		}
	}

	/**
	 * show information the map files per developers
	 */
	public void showFilesPerDevelopers() {
		for (Map.Entry<String, Map<Developer, Integer>> cd : filesPerDeveloper.entrySet()) {
			Map<Developer, Integer> values = cd.getValue();
			for (Map.Entry<Developer, Integer> v : values.entrySet()) {
				System.out.println(
						"File: " + cd.getKey() + " Developer: " + v.getKey().getName() + " Commit: " + v.getValue());
			}
		}
	}

	/**
	 * Create a list<String> for export data relationship a matrix the
	 * commit in file per developer
	 * @return List<String> list the data to export csv
	 */
	public List<String> printFileDeveloper() {
		List<String> lines = new ArrayList<>();
		matrixFileDevelopersPerCommits.forEach(m -> {
			StringBuilder sb = new StringBuilder();
			//verify this firts line to export, and get names the developers
			if (COUNT == 0) {
				sb.append(";");
				for (Developer dev : m.getDevelopers()) {
					sb.append(dev.getName());
					sb.append(";");
				}
				lines.add(sb.toString());
				COUNT++;
			} else {
				//verify filepath and chance per filename
				sb.append(m.getFile());
				sb.append(";");
				//add a quantity the commit for ever developer
				m.getCommits().forEach(c -> {
					sb.append(c);
					sb.append(";");
				});
				lines.add(sb.toString());
			}
		});
	
		lines.forEach(l ->{
			System.out.println(l);
		});
		
		return lines;
	}

}
