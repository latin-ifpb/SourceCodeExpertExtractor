package com.expert.analyze.model;

import java.util.List;
import java.util.Set;

public class FileDeveloper {

	private String file;//file name
	private Set<Developer> developers;//team developers
	private List<Integer> commits;//quantity the commit in file per team developers
	
	public FileDeveloper() {	
	}
	
	public FileDeveloper(String file, Set<Developer> developers,List<Integer> commits) {
		setDevelopers(developers);
		setFile(file);
		setCommits(commits);
	}
	
	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * @return the developers
	 */
	public Set<Developer> getDevelopers() {
		return developers;
	}

	/**
	 * @param developers the developers to set
	 */
	public void setDevelopers(Set<Developer> developers) {
		this.developers = developers;
	}

	/**
	 * @return the commits
	 */
	public List<Integer> getCommits() {
		return commits;
	}

	/**
	 * @param commits the commits to set
	 */
	public void setCommits(List<Integer> commits) {
		this.commits = commits;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FileDeveloper [file=" + file + ", developers=" + developers + ", commits=" + commits + "]";
	}

}
