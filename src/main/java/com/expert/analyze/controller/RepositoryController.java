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

public class RepositoryController {

	private Git remote;
	private Git local;
	private String linkProjectLocal;
	private String linkProjectRemote;
	private List<RevCommit> commitsLocal;
	private Set<Developer> teamDeveloper;

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
				local = Git.open(new File(linkProjectLocal));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			refs = local.branchList().call();
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

	private String getRemoteUrl() throws IOException {
		return remote.getRepository().getDirectory().getCanonicalPath();
	}

	public void getNameFilesProject() {
		this.commitsLocal.stream().forEach(c -> {
			try {
				TreeWalk treeWalk = new TreeWalk(local.getRepository());
				treeWalk.addTree(c.getTree());
				treeWalk.setRecursive(true);
				while (treeWalk.next()) {
					System.out.println("found: " + treeWalk.getPathString());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
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
			local = Git.open(new File(linkProjectLocal));

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RepositoryController [remote=" + remote + ", local=" + local + ", linkProjectLocal=" + linkProjectLocal
				+ ", linkProjectRemote=" + linkProjectRemote + ", commitsLocal=" + commitsLocal + ", teamDeveloper="
				+ teamDeveloper + "]";
	}

}
