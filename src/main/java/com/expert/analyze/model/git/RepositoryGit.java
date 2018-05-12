package com.expert.analyze.model.git;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;

import com.expert.analyze.model.Developer;

public class RepositoryGit {

	private Git remote;
	private Git local;
	private String linkProjectLocal;
	private String linkProjectRemote;
	private List<RevCommit> commitsLocal;
	private Set<Developer> teamDeveloper;
	private Set<String> filesProject;
	private Set<String> namesFiles;
	/**
	 * @return the remote
	 */
	public Git getRemote() {
		return remote;
	}
	/**
	 * @param remote the remote to set
	 */
	public void setRemote(Git remote) {
		this.remote = remote;
	}
	/**
	 * @return the local
	 * @throws IOException 
	 */
	public Git getLocal() throws IOException {
		openRepository();
		return local;
	}
	/**
	 * @param local the local to set
	 */
	public void setLocal(Git local) {
		this.local = local;
	}
	/**
	 * @return the linkProjectLocal
	 */
	public String getLinkProjectLocal() {
		return linkProjectLocal;
	}
	/**
	 * @param linkProjectLocal the linkProjectLocal to set
	 */
	public void setLinkProjectLocal(String linkProjectLocal) {
		this.linkProjectLocal = linkProjectLocal;
	}
	/**
	 * @return the linkProjectRemote
	 */
	public String getLinkProjectRemote() {
		return linkProjectRemote;
	}
	/**
	 * @param linkProjectRemote the linkProjectRemote to set
	 */
	public void setLinkProjectRemote(String linkProjectRemote) {
		this.linkProjectRemote = linkProjectRemote;
	}
	/**
	 * @return the commitsLocal
	 */
	public List<RevCommit> getCommitsLocal() {
		return commitsLocal;
	}
	/**
	 * @param commitsLocal the commitsLocal to set
	 */
	public void setCommitsLocal(List<RevCommit> commits) {
		this.commitsLocal = commits;
	}
	/**
	 * @return the teamDeveloper
	 */
	public Set<Developer> getTeamDeveloper() {
		return teamDeveloper;
	}
	/**
	 * @param teamDeveloper the teamDeveloper to set
	 */
	public void setTeamDeveloper(Set<Developer> teamDeveloper) {
		this.teamDeveloper = teamDeveloper;
	}
	/**
	 * @return the filesProject
	 */
	public Set<String> getFilesProject() {
		return filesProject;
	}
	/**
	 * @param filesProject the filesProject to set
	 */
	public void setFilesProject(Set<String> filesProject) {
		this.filesProject = filesProject;
	}
	
	private void openRepository() throws IOException {
		local = Git.open(new File(linkProjectLocal));
	}
	
	/**
	 * Get Url the repository remote.
	 * @return link for url the respository.
	 * @throws IOException
	 */
	private String getRemoteUrl() throws IOException {
		return getRemote().getRepository().getDirectory().getCanonicalPath();
	}

	/**
	 * Return quantity the commit in branch local
	 * 
	 * @return
	 */
	public Integer getQuantityCommitLocal() {
		return getCommitsLocal().size();
	}
	
	/**
	 * @return the namesFiles
	 */
	public Set<String> getNamesFiles() {
		return namesFiles;
	}
	/**
	 * @param namesFiles the namesFiles to set
	 */
	public void setNamesFiles(Set<String> namesFiles) {
		this.namesFiles = namesFiles;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RepositoryGit [remote=" + remote + ", local=" + local + ", linkProjectLocal=" + linkProjectLocal
				+ ", linkProjectRemote=" + linkProjectRemote + ", commitsLocal=" + commitsLocal + ", teamDeveloper="
				+ teamDeveloper + ", filesProject=" + filesProject + ", namesFiles=" + namesFiles + "]";
	}
	
}
