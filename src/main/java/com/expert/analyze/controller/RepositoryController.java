package com.expert.analyze.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.expert.analyze.model.Developer;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;

public class RepositoryController {

	private Git remote;
	private Git local;
	private String linkProjectLocal;
	private String linkProjectRemote;
	private List<RevCommit> commitsLocal;
	private Set<Developer> teamDeveloper;
	private Set<String> filesProject;

	/**
	 * Method cloning repository remote
	 * 
	 * @param nomePrjeto
	 *            - nome do director
	 * @param link
	 * @return
	 */
	public Boolean cloneRepository(String link, String directory) {
		try {
			remote = Git.cloneRepository().setURI(link).setDirectory(new File(Constants.PATH_DEFAULT + directory))
					.setCloneAllBranches(true).call();
			remote.close();
			System.out.println("Cloning sucess.....");
			return Boolean.TRUE;
		} catch (InvalidRemoteException e) {
			System.err.println(e.getMessage());
			return Boolean.FALSE;
		} catch (TransportException e) {
			System.err.println(e.getMessage());
			return Boolean.FALSE;
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
			return Boolean.FALSE;
		}
	}

	public List<String> getBranchesRemote() {
		Collection<Ref> refs;
		List<String> branches = new ArrayList<String>();
		try {
			refs = Git.lsRemoteRepository().setHeads(true).setRemote(linkProjectRemote).call();
			for (Ref ref : refs) {
				branches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
			}
			Collections.sort(branches);
		} catch (InvalidRemoteException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} catch (TransportException e) {
			System.err.println(e.getMessage());
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
		}
		return branches;
	}

	public List<String> getBranchesLocal() {
		Collection<Ref> refs;
		List<String> branches = new ArrayList<String>();
		try {
			try {
				openRepository();
			} catch (IOException e) {
				e.printStackTrace();
			}
			refs = local.branchList().call();
			for (Ref ref : refs) {
				branches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
			}
			Collections.sort(branches);

		} catch (InvalidRemoteException e) {
			System.err.println(e.getMessage());
		} catch (TransportException e) {
			System.err.println(e.getMessage());
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
		}
		return branches;
	}

	private String getRemoteUrl() throws IOException {
		return remote.getRepository().getDirectory().getCanonicalPath();
	}

	public Integer getQuantityCommitLocal() {
		return this.commitsLocal.size();
	}

	/**
	 * @return the remote
	 */
	public Git getRemote() {
		return remote;
	}

	/**
	 * @param remote
	 *            the remote to set
	 */
	public void setRemote(Git remote) {
		this.remote = remote;
	}

	/**
	 * @return the local
	 */
	public Git getLocal() {
		return local;
	}

	/**
	 * @param local
	 *            the local to set
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
	 * @param linkProjectLocal
	 *            the linkProjectLocal to set
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
	 * @param linkProjectRemote
	 *            the linkProjectRemote to set
	 */
	public void setLinkProjectRemote(String linkProjectRemote) {
		this.linkProjectRemote = linkProjectRemote;
	}

	/**
	 * @return the teamDeveloper
	 */
	public Set<Developer> getTeamDeveloper() {
		return teamDeveloper;
	}

	/**
	 * @param teamDeveloper
	 *            the teamDeveloper to set
	 */
	public void setTeamDeveloper() {
		teamDeveloper = new HashSet<Developer>();
		if (commitsLocal.isEmpty()) {
			setCommitsLocal();
		} else {
			for (RevCommit commit : this.commitsLocal) {
				teamDeveloper.add(
						new Developer(commit.getAuthorIdent().getName(), commit.getAuthorIdent().getEmailAddress()));
			}
		}
	}

	/**
	 * @param commitsLocal
	 *            the commitsLocal to set
	 */
	public void setCommitsLocal() {
		commitsLocal = new ArrayList<RevCommit>();
		try {
			openRepository();
			Iterable<RevCommit> log = local.log().call();
			for (RevCommit commit : log) {
				commitsLocal.add(commit);
				// System.out.println(commit.getId() + ": "+ commit.getAuthorIdent() + " : "+
				// commit.getCommitTime());
			}
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "RepositoryController [remote=" + remote + ", local=" + local + ", linkProjectLocal=" + linkProjectLocal
				+ ", linkProjectRemote=" + linkProjectRemote + ", commitsLocal=" + commitsLocal + ", teamDeveloper="
				+ teamDeveloper + "]";
	}

	private void openRepository() throws IOException {
		local = Git.open(new File(linkProjectLocal));
	}

	/**
	 * @return the commitsLocal
	 */
	public List<RevCommit> getCommitsLocal() {
		return commitsLocal;
	}

	/**
	 * @return the filesProject
	 */
	public Set<String> getFilesProject() {
		return filesProject;
	}

	/**
	 * @param filesProject
	 *            the filesProject to set
	 */
	public void setFilesProject() {
		this.filesProject = new HashSet<>();

		if (this.commitsLocal.isEmpty()) {
			setCommitsLocal();
		}

		this.commitsLocal.stream().forEach(c -> {
			try {
				TreeWalk treeWalk = new TreeWalk(local.getRepository());
				treeWalk.addTree(c.getTree());
				treeWalk.setRecursive(true);
				while (treeWalk.next()) {
					if (Validador.isFileValid(treeWalk)) {
						filesProject.add(treeWalk.getPathString());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @param teamDeveloper
	 *            the teamDeveloper to set
	 */
	public void setTeamDeveloper(Set<Developer> teamDeveloper) {
		this.teamDeveloper = teamDeveloper;
	}

}
