package com.expert.analyze.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.expert.analyze.model.Developer;
import com.expert.analyze.model.git.RepositoryGit;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;

public class RepositoryGitController {

	private RepositoryGit repositoryGit;

	public RepositoryGitController() {
		repositoryGit = new RepositoryGit();
	}

	/**
	 * Method cloning repository remote
	 * 
	 * @param nomePrjeto
	 *            - nome do director
	 * @param link
	 * @return
	 */
	public Boolean cloneRepository(String link, String directory) throws TransportException, InvalidRemoteException {
		try {

			repositoryGit.setRemote(Git.cloneRepository().setURI(link)
					.setDirectory(new File(Constants.PATH_DEFAULT + directory)).setCloneAllBranches(true).call());
			repositoryGit.getRemote().close();

			System.out.println("Cloning sucess.....");
			return Boolean.TRUE;
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
			return Boolean.FALSE;
		}
	}

	/**
	 * Get all branch remote projetc
	 * 
	 * @return List<String> name the branch exists in repository
	 */
	public List<String> getBranchesRemote() {
		Collection<Ref> refs;
		List<String> branches = new ArrayList<String>();
		try {
			refs = Git.lsRemoteRepository().setHeads(true).setRemote(repositoryGit.getLinkProjectRemote()).call();
			for (Ref ref : refs) {
				branches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
			}
			Collections.sort(branches);
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
		}
		return branches;
	}

	/**
	 * Get branchs local the repository
	 * 
	 * @return
	 */
	public List<String> getBranchesLocal() {
		Collection<Ref> refs;
		List<String> branches = new ArrayList<String>();
		try {
			refs = repositoryGit.getLocal().branchList().call();
			for (Ref ref : refs) {
				branches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
			}
			Collections.sort(branches);

		} catch (GitAPIException | IOException e) {
			System.err.println(e.getMessage());
		}
		return branches;
	}


	/**
	 * @return the repositoryGit
	 */
	public RepositoryGit getRepositoryGit() {
		return repositoryGit;
	}

	/**
	 * @param repositoryGit
	 *            the repositoryGit to set
	 */
	public void setRepositoryGit(RepositoryGit repositoryGit) {
		this.repositoryGit = repositoryGit;
	}

	public void loadCommitsLocal() {
		List<RevCommit> commitsLocal = new ArrayList<>();
		try {
			Iterable<RevCommit> log = repositoryGit.getLocal().log().call();
			for (RevCommit commit : log) {
				commitsLocal.add(commit);
			}
			repositoryGit.setCommitsLocal(commitsLocal);
		} catch (GitAPIException | IOException e) {
			e.printStackTrace();
		}
	}

	public void loadTeamDeveloper() {
		Set<Developer> teamDeveloper = new HashSet<>();
		if (repositoryGit.getCommitsLocal().isEmpty()) {
			System.err.println(Constants.ERROR_GET_COMMIT_FIRST);
			loadCommitsLocal();
		} else {
			repositoryGit.getCommitsLocal().forEach(c -> {
				teamDeveloper.add(new Developer(c.getAuthorIdent().getName(), c.getAuthorIdent().getEmailAddress()));
			});
			repositoryGit.setTeamDeveloper(teamDeveloper);
		}
	}

	public void loadFilesProject() {
		Set<String> filesProject = new HashSet<>();
		Set<String> namesFiles = new HashSet<>();

		if (repositoryGit.getCommitsLocal().isEmpty()) {
			System.err.println(Constants.ERROR_GET_COMMIT_FIRST);
		}

		repositoryGit.getCommitsLocal().stream().forEach(c -> {
			try {
				TreeWalk treeWalk = new TreeWalk(repositoryGit.getLocal().getRepository());
				treeWalk.addTree(c.getTree());
				treeWalk.setRecursive(true);
				while (treeWalk.next()) {
					if (Validador.isFileValid(treeWalk)) {
						namesFiles.add(treeWalk.getNameString());
						filesProject.add(treeWalk.getPathString());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		repositoryGit.setFilesProject(filesProject);
		repositoryGit.setNamesFiles(namesFiles);
	}
	
	public List<RevCommit> findCommitsPerDate(LocalDate dtInitial, LocalDate dtFinal){
		List<RevCommit> commitsLocal = new ArrayList<>();
		if (!repositoryGit.getCommitsLocal().isEmpty()) {
				repositoryGit.getCommitsLocal().forEach(commit ->{					
					LocalDate commitDate = commit.getAuthorIdent().getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					if(commitDate.isAfter(dtInitial) && commitDate.isBefore(dtFinal) ){
						commitsLocal.add(commit);
					}
				});
				return commitsLocal;
		}else {
			System.err.println(Constants.ERROR_GET_COMMIT_FIRST);		
			return null;
		}
	}
}
