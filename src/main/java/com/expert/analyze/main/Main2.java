package com.expert.analyze.main;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.expert.analyze.controller.BuildReport;
import com.expert.analyze.controller.RepositoryGitController;
import com.expert.analyze.model.git.MeasurePerFile;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;

public class Main2 {

	private static RepositoryGitController repositorio;
	private static String linkTeste = "https://github.com/WemersonThayne/projeto_poo";
	private static String projectTeste = "teste2";

	public static void main(String[] args) {
		repositorioInit();
		repositorio.findCommitsPerDate(LocalDate.of(2017, 04, 01), LocalDate.of(2017, 05, 01));
		teste();

		
	}

	private static void repositorioInit() {

		repositorio = new RepositoryGitController();
		if (!Validador.isStringEmpty(projectTeste) && !Validador.isStringEmpty(linkTeste)) {
			repositorio.getRepositoryGit().setLinkProjectLocal(Constants.PATH_DEFAULT + projectTeste);
			repositorio.getRepositoryGit().setLinkProjectRemote(linkTeste);
		}

		try {
			if (Validador.isDirectoryExist(new File(Constants.PATH_DEFAULT + projectTeste))) {
				System.out.println("Clonando o repositório, Aguarde pode demorar um pouco....");
				if (repositorio.cloneRepository(linkTeste, projectTeste)) {
					System.out.println("Repositorio Clonado com Sucesso...");
				}
			}
			repositorio.loadCommitsLocal();
			repositorio.loadFilesProject();
			repositorio.loadTeamDeveloper();
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
			b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + projectTeste, mpf.printFileDeveloper(repositorio.getRepositoryGit().getNamesFiles()));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
