package com.expert.analyze.model.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Util;

public class MeasurePerFile extends Measure {

	private Map<Developer, Map<String, Integer>> developerPerFiles;
	private List<FileDeveloper> matrixFileDevelopersPerCommits;
	private Map<Developer, Map<String, Integer>> developersPerFiles;
	private Map<String, Map<Developer, Integer>> filePerDeveloper;
	private Map<String, Map<Developer, Integer>> filesPerDeveloper;
	private Integer COUNT = 0;

	public MeasurePerFile(Repository repository) {
		setRepository(repository);
	}

	public Integer evaluateQuantityCommitPerFilePerDeveloper(List<RevCommit> commitsLocal, String fileFind,
			Developer developer) {
		Integer count = 0;
		try {
			for (RevCommit commit : commitsLocal) {
				TreeWalk treeWalk = new TreeWalk(getRepository());
				treeWalk.addTree(commit.getTree());
				treeWalk.setRecursive(true);
				while (treeWalk.next()) {
					if (treeWalk.getPathString().equalsIgnoreCase(fileFind)
							&& commit.getAuthorIdent().getName().equalsIgnoreCase(developer.getName())
							&& commit.getAuthorIdent().getEmailAddress().equalsIgnoreCase(developer.getEmail())) {
						count++;
					}
				}
				treeWalk.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	public void evaluateQuantityCommitPerFilesPerDeveloper(List<RevCommit> commitsLocal, Set<String> files,
			Developer developer) {
		developerPerFiles = new HashMap<>();
		Map<String, Integer> fileQuantityCommit = new HashMap<>();
		for (String file : files) {
			fileQuantityCommit.put(file, evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, file, developer));
		}
		developerPerFiles.put(developer, fileQuantityCommit);

	}

	public void evaluateQuantityCommitPerFilesPerDeveloperMatrix(List<RevCommit> commitsLocal, Set<String> files,
			Developer developer) {

		matrixFileDevelopersPerCommits = new ArrayList<>();
		for (String file : files) {
			Set<Developer> developers = new HashSet<>();
			List<Integer> commitsQuantity = new ArrayList<>();
			commitsQuantity.add(evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, file, developer));
			developers.add(developer);
			matrixFileDevelopersPerCommits.add(new FileDeveloper(file, developers, commitsQuantity));

		}

	}

	public void evaluateQuantityCommitPerFilesPerDevelopersMatrix(List<RevCommit> commitsLocal, Set<String> files,
			Set<Developer> developers) {

		matrixFileDevelopersPerCommits = new ArrayList<>();

		for (String file : files) {
			List<Integer> commitsQuantity = new ArrayList<>();
			for (Developer developer : developers) {
				commitsQuantity.add(evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, file, developer));
			}
			matrixFileDevelopersPerCommits.add(new FileDeveloper(file, developers, commitsQuantity));
		}
	}

	public void evaluateQuantityCommitPerFilesPerDevelopers(List<RevCommit> commitsLocal, Set<String> files,
			Set<Developer> developeres) {
		developersPerFiles = new HashMap<>();
		for (Developer developer : developeres) {
			Map<String, Integer> fileQuantityCommit = new HashMap<>();
			for (String file : files) {
				fileQuantityCommit.put(file, evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, file, developer));
			}
			developersPerFiles.put(developer, fileQuantityCommit);
		}

	}

	public void evaluateQuantityCommitInFilesPerDevelopers(List<RevCommit> commitsLocal, Set<String> files,
			Set<Developer> developeres) {
		filesPerDeveloper = new HashMap<>();
		for (String fileName : files) {
			Map<Developer, Integer> developerQuantityCommit = new HashMap<>();
			for (Developer developer : developeres) {
				developerQuantityCommit.put(developer,
						evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, fileName, developer));
			}
			filesPerDeveloper.put(fileName, developerQuantityCommit);
		}
	}

	public void evaluateQuantityCommitInFilePerDevelopers(List<RevCommit> commitsLocal, String fileName,
			Set<Developer> developeres) {
		filePerDeveloper = new HashMap<>();
		Map<Developer, Integer> developerQuantityCommit = new HashMap<>();
		for (Developer developer : developeres) {
			developerQuantityCommit.put(developer,
					evaluateQuantityCommitPerFilePerDeveloper(commitsLocal, fileName, developer));
		}
		filePerDeveloper.put(fileName, developerQuantityCommit);

		for (Map.Entry<String, Map<Developer, Integer>> cd : filePerDeveloper.entrySet()) {
			Map<Developer, Integer> values = cd.getValue();
			for (Map.Entry<Developer, Integer> v : values.entrySet()) {
				System.out.println(
						"File: " + cd.getKey() + " Developer:" + v.getKey().getName() + " Commit: " + v.getValue());
			}
		}
	}

	public Developer developerMaxQuantityCommitPerFile(String fileName) {
		Developer d = new Developer();
		Map<Developer, Integer> mapDeveloper = filePerDeveloper.get(fileName);
		d = Collections.max(mapDeveloper.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue())
				.getKey();
		System.out.println("Developer:" + d.getName() + " in File " + fileName + "has max number commit");
		return d;
	}

	public Developer developerMimQuantityCommitPerFile(String fileName) {
		Developer d = new Developer();
		Map<Developer, Integer> mapDeveloper = filePerDeveloper.get(fileName);
		d = Collections.min(mapDeveloper.entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue())
				.getKey();
		System.out.println("Developer:" + d.getName() + " in File " + fileName + "has min number commit");
		return d;
	}

	/**
	 * @return the developerPerFiles
	 */
	public Map<Developer, Map<String, Integer>> getDeveloperPerFiles() {
		return developerPerFiles;
	}

	/**
	 * @param developerPerFiles
	 *            the developerPerFiles to set
	 */
	public void setDeveloperPerFiles(Map<Developer, Map<String, Integer>> developerPerFiles) {
		this.developerPerFiles = developerPerFiles;
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
	 * @return the filePerDeveloper
	 */
	public Map<String, Map<Developer, Integer>> getFilePerDeveloper() {
		return filePerDeveloper;
	}

	/**
	 * @param filePerDeveloper
	 *            the filePerDeveloper to set
	 */
	public void setFilePerDeveloper(Map<String, Map<Developer, Integer>> filePerDeveloper) {
		this.filePerDeveloper = filePerDeveloper;
	}

	public void showDevelopersPerFiles(Map<Developer, Map<String, Integer>> developerPerFiles) {
		for (Map.Entry<Developer, Map<String, Integer>> cd : developerPerFiles.entrySet()) {
			Map<String, Integer> values = cd.getValue();
			for (Map.Entry<String, Integer> v : values.entrySet()) {
				System.out.println(
						"Developer: " + cd.getKey().getName() + " File:" + v.getKey() + " Commit: " + v.getValue());
			}
		}
	}

	public void showFilesPerDevelopers() {
		for (Map.Entry<String, Map<Developer, Integer>> cd : filesPerDeveloper.entrySet()) {
			Map<Developer, Integer> values = cd.getValue();
			for (Map.Entry<Developer, Integer> v : values.entrySet()) {
				System.out.println(
						"File: " + cd.getKey() + " Developer: " + v.getKey().getName() + " Commit: " + v.getValue());
			}
		}
	}

	public List<String> printFileDeveloper(Set<String> namesFiles) {

		List<String> lines = new ArrayList<>();
		matrixFileDevelopersPerCommits.forEach(m -> {
			StringBuilder sb = new StringBuilder();

			if (COUNT == 0) {
				sb.append(";");
				for (Developer dev : m.getDevelopers()) {
					sb.append(dev.getName());
					sb.append(";");
				}
				lines.add(sb.toString());
				COUNT++;
			} else {
				sb.append(Util.findFileNamePerPath(namesFiles, m.getFile()));
				sb.append(";");
				m.getCommits().forEach(c -> {
					sb.append(c);
					sb.append(";");
				});

				lines.add(sb.toString());
			}

		});

		return lines;
	}

}
