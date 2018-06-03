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

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.expert.analyze.model.Developer;
import com.expert.analyze.model.git.RepositoryGit;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;
/**
 * Class responsible controller the information the repository
 * @author wemerson
 *
 */
public class RepositoryGitController {

	//Repository cloning for analyzer
	private RepositoryGit repositoryGit;

	public RepositoryGitController() {
		repositoryGit = new RepositoryGit();
	}

	private UsernamePasswordCredentialsProvider configAuthentication(String user, String password) {
		return new UsernamePasswordCredentialsProvider(user, password ); 
	}
	
	public void clonneRepositoryWithAuthentication(String link, String directory,String branch,String user, String password){
		 System.out.println("Cloning repository private from bitcketebuk");
		try {
			if(Validador.isStringEmpty(branch)){
				Git.cloneRepository()//function responsible to clone repository
				.setURI(link)// set link to repository git
				.setDirectory(new File(Constants.PATH_DEFAULT + directory))//Defined the path local the cloning
				.setCredentialsProvider(configAuthentication(user, password))
				.setBranch(branch)
				.setCloneAllBranches(true)//Defined clone all branch exists on repository
				.call();//execute call the clone repository git
			}else {
				Git.cloneRepository()//function responsible to clone repository
				   .setURI(link)// set link to repository git
				   .setDirectory(new File(Constants.PATH_DEFAULT + directory))//Defined the path local the cloning
				   .setCredentialsProvider(configAuthentication(user, password))
				   .setCloneAllBranches(true)//Defined clone all branch exists on repository
				   .call();//execute call the clone repository git
			}
			System.out.println("Cloning sucess.....");
		} catch (GitAPIException e) {
			System.err.println("Error Cloning repository " + link + " : "+ e.getMessage());
		}
		
	}
	/**
	 * Method cloning repository remote
	 * 
	 * @param nomePrjeto
	 *            - nome do director
	 * @param link - link the repository remote
	 * @param branch - name branch for clonning
	 * @return
	 */
	public Boolean cloneRepositoryWithOutAuthentication(String link, String directory,String branch) throws TransportException, InvalidRemoteException {
		try {
			if(!Validador.isStringEmpty(branch)) {				
				//Set repository remote for clonning on local analyzer
				repositoryGit.setRemote(Git.cloneRepository()//function repsponsible to clone reposotory
						.setURI(link)// set link to repository git
						.setDirectory(new File(Constants.PATH_DEFAULT + directory))//definid the path local the clonnig
						.setBranch(branch)
						.setCloneAllBranches(true)//definid clone all brach exists on repository
						.call());//execute call the clone repository git
				repositoryGit.getRemote().close();// close connect and finaly the process the clone repository
			}else {
				//Set repository remote for clonning on local analyzer
				repositoryGit.setRemote(Git.cloneRepository()//function repsponsible to clone reposotory
						.setURI(link)// set link to repository git
						.setDirectory(new File(Constants.PATH_DEFAULT + directory))//definid the path local the clonnig
						.setCloneAllBranches(true)//definid clone all brach exists on repository
						.call());//execute call the clone repository git
				repositoryGit.getRemote().close();// close connect and finaly the process the clone repository
			}

			System.out.println("Cloning sucess.....");
			return Boolean.TRUE;
		} catch (GitAPIException e) {
			System.err.println("Error Cloning repository " + link + " : "+ e.getMessage());
			return Boolean.FALSE;
		}
	}

	/**
	 * Checkout in branch specific remote to local
	 * @param branch
	 */
	private void chekoutBranch(String branch){
		Git git = repositoryGit.getRemote();
		CheckoutCommand checkoutCommand = git.checkout()
			      .setCreateBranch(false)
			      .setName(branch);

			  List<Ref> refList;
			try {
				refList = git.branchList().call();
				 if (!anyRefMatches(refList,branch)) {
					    checkoutCommand = checkoutCommand
					        .setCreateBranch(true)
					        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
					        .setStartPoint("origin/" + branch);
					  }
				 
				 checkoutCommand.call();
			} catch (GitAPIException e1) {
				System.err.println("Error checkout branch "+ branch +": "+e1.getMessage());
			}
	}
	/**
	 * Verify this branch exist in repository remote
	 * @param refList
	 * @param branch
	 * @return
	 */
	private boolean anyRefMatches(List<Ref> refList, String branch) {
	    for (Ref ref : refList) {
	      if (ref.getName().replace("refs/heads/", "").equals(branch)) {
	        return true;
	      }
	    }

	    return false;
	  }
	/**
	 * Get all branch remote projetc
	 * 
	 * @return List<String> name the branch exists in repository
	 */
	public List<String> getBranchesRemote() {
		Collection<Ref> refs; // this collection contains object Ref his all information about branch the repository
		//List all names the branchs exists on repository remote
		List<String> branches = new ArrayList<String>();
		try {
			refs = Git.lsRemoteRepository() //config the call remote repository
					  .setHeads(true) //defined the initial project
					  .setRemote(repositoryGit.getLinkProjectRemote())//get link to repository
					  .call();//call the brachs
			
			//walk the return list refs and get only name the branchs
			for (Ref ref : refs) {
				branches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()));
			}
			
			//order names 
			Collections.sort(branches);
		} catch (GitAPIException e) {
			System.err.println("Error in call brachs remote:"+e.getMessage());
		}
		return branches;
	}

	/**
	 * Get branchs local the repository
	 * 
	 * @return
	 */
	public List<String> getBranchesLocal() {
		Collection<Ref> refs;//this collection contains object Ref his all information about branch the repository
		List<String> branches = new ArrayList<String>();//names the branchs local
		try {
			//Get the brachs local in repository
			refs = repositoryGit.getLocal().branchList().call();
			
			//walk the return list refs and get only name the branchs
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

	/**
	 * method responsible for set commit in list for use in analyzers
	 */
	public void loadCommitsLocal() {
		//List the commits local
		List<RevCommit> commitsLocal = new ArrayList<>();
		try {
			//get in repository a log information the commits
			Iterable<RevCommit> log = repositoryGit.getLocal().log().call();
			//walk in log and set object RevCommit contains information the commit in reposotory
			for (RevCommit commit : log) {
				commitsLocal.add(commit);
			}
			//Set a list the commit for use in measures
			repositoryGit.setCommitsLocal(commitsLocal);
			
		} catch (GitAPIException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method responsible to get team the developer contribuition in repository
	 */
	public void loadTeamDeveloper() {
		//set on developer team
		Set<Developer> teamDeveloper = new HashSet<>();
		//verify the list commit his doesn't empty
		if (repositoryGit.getCommitsLocal().isEmpty()) {
			System.err.println(Constants.ERROR_GET_COMMIT_FIRST);
			loadCommitsLocal();
		} else {
			/* walk in list the commit and get the AuthorIdent this object
			 * contains name and email the author the commit    
			 */
			repositoryGit.getCommitsLocal().forEach(c -> {
				teamDeveloper.add(new Developer(c.getAuthorIdent().getName(), c.getAuthorIdent().getEmailAddress()));
			});
			//set a team developer contribuition on repository
			repositoryGit.setTeamDeveloper(teamDeveloper);
		}
	}

	/**
	 * method responsible to set files in repositoryGit for
	 * use in measures.
	 */
	public void loadFilesProject() {
		//Set contains filePath on repository
		Set<String> filesProject = new HashSet<>();
		//Set contains filenames on repository
		Set<String> namesFiles = new HashSet<>();

		//verify the list commit his doesn't empty
		if (repositoryGit.getCommitsLocal().isEmpty()) {
			System.err.println(Constants.ERROR_GET_COMMIT_FIRST);
		}
		//walks in list commit get a filepath and filename 
		repositoryGit.getCommitsLocal().stream().forEach(c -> {
			try {
				//TreeWalk contains information about tree the commit the file in all commits history
				TreeWalk treeWalk = new TreeWalk(repositoryGit.getLocal().getRepository());
				//Defined the tree the file in commits
				treeWalk.addTree(c.getTree());
				treeWalk.setRecursive(true);//set walk in all tree
				while (treeWalk.next()) {
					//Verify this file is valid, remove files the configuration, proprets, file build for IDE.
					if (Validador.isFileValid(treeWalk)) {
						namesFiles.add(treeWalk.getNameString());
						filesProject.add(treeWalk.getPathString());
					}
				}
			} catch (IOException e) {
				System.err.println("Erro get files in repository local: "+e.getMessage());
			}
		});
		
		// set filepath and filename in repository anlyzer
		repositoryGit.setFilesProject(filesProject);
		repositoryGit.setNamesFiles(namesFiles);
	}
	
	/**
	 * method query per date a inteval the commit need analyzer in repository,
	 * return a list the RevCommit in period between date initial and date finaly.
	 * 
	 * @param dtInitial - Date initial the period
	 * @param dtFinal - Date Final the period
	 * @return List<RevCommit> this list the commit in perido the query.
 	 */
	public List<RevCommit> findCommitsPerDate(LocalDate dtInitial, LocalDate dtFinal){
		//list the commits
		List<RevCommit> commitsLocal = new ArrayList<>();
		if (!repositoryGit.getCommitsLocal().isEmpty()) {
				repositoryGit.getCommitsLocal().forEach(commit ->{
					// Get time the commit,  is a Date in object AuthorIdent and get a atribuit	When and set the LocalDate 
					LocalDate commitDate = commit.getAuthorIdent()
							.getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					//After get a time the commit, comparer this date intervel the dtInitial and dtFinal.
					if(commitDate.isAfter(dtInitial) && commitDate.isBefore(dtFinal) ){
						commitsLocal.add(commit);
					}
				});
				return commitsLocal;
		}else {
			System.err.println(Constants.ERROR_GET_COMMIT_FIRST);		
			return new ArrayList<>();
		}
	}
}
