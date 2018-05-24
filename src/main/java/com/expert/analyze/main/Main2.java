package com.expert.analyze.main;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.expert.analyze.controller.BuildReport;
import com.expert.analyze.controller.DeveloperController;
import com.expert.analyze.controller.RepositoryGitController;
import com.expert.analyze.model.git.MeasurePerFile;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;

public class Main2 {

	private static RepositoryGitController repositorio;
	private static String linkTeste = "https://github.com/JoseRenan/POP-Judge";
	private static String projectTeste = "teste3";
	private static String repositPrivate = "https://wemersonthayne22@bitbucket.org/wemersonthayne22/expertanalyzer.git";

//	public static void main(String[] args) {
//		repositorioInit();
//		//repositorio.findCommitsPerDate(LocalDate.of(2017, 04, 01), LocalDate.of(2017, 05, 01));
//		//teste();
//
//	}

	private static void repositorioInit() {

		repositorio = new RepositoryGitController();
		if (!Validador.isStringEmpty(projectTeste) && !Validador.isStringEmpty(linkTeste)) {
			repositorio.getRepositoryGit().setLinkProjectLocal(Constants.PATH_DEFAULT + projectTeste);
			repositorio.getRepositoryGit().setLinkProjectRemote(linkTeste);
		}

		try {
			String branch = repositorio.getBranchesRemote().get(Constants.CONSTANT_ZERO);
			if (Validador.isDirectoryExist(new File(Constants.PATH_DEFAULT + projectTeste))) {
				System.out.println("Clonando o	 repositório, Aguarde pode demorar um pouco....");
				if (repositorio.cloneRepositoryWithOutAuthentication(linkTeste, projectTeste,branch)) {
					System.out.println("Repositorio Clonado com Sucesso...");
				}
			}
			repositorio.loadCommitsLocal();
			repositorio.loadFilesProject();
			repositorio.loadTeamDeveloper();
			System.out.println("Branch Local"+repositorio.getBranchesLocal());
			
			repositorio.clonneRepositoryWithAuthentication(repositPrivate,"privateRepositoy",null, "wemersonthayne22","w3m3450n@");
			
			//Normalize team developer 
			DeveloperController dc = new DeveloperController(repositorio.getRepositoryGit().getTeamDeveloper());
			dc.contributorsNormalizere();
			repositorio.getRepositoryGit().setTeamDeveloper(dc.getTeamDeveloper());
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		}
	}

	private static void teste() {

		System.out.println("\n\n");
		System.out.println("--------- MESOURES PER FILES ----------");

		MeasurePerFile mpf;

		try {
			mpf = new MeasurePerFile(repositorio.getRepositoryGit().getLocal().getRepository());
			mpf.evaluateQuantityCommitPerFilesPerDeveloperMatrix(repositorio.getRepositoryGit().getCommitsLocal(),
					repositorio.getRepositoryGit().getFilesProject(),
					repositorio.getRepositoryGit().getTeamDeveloper().iterator().next());
			System.out.println("\n\n");
			System.out.println("\n\n");
			mpf.evaluateQuantityCommitPerFilesPerDevelopersMatrix(repositorio.getRepositoryGit().getCommitsLocal(),
					repositorio.getRepositoryGit().getFilesProject(),
					repositorio.getRepositoryGit().getTeamDeveloper());
					
			
			BuildReport b = new BuildReport();
			b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + projectTeste, mpf.printFileDeveloper());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
