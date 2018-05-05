package com.expert.analyze.main;

import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.expert.analyze.controller.BuildReport;
import com.expert.analyze.controller.RepositoryGitController;
import com.expert.analyze.model.Developer;
import com.expert.analyze.model.git.MeasurePerCommit;
import com.expert.analyze.model.git.MeasurePerFile;
import com.expert.analyze.model.git.MeasurePerLine;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;

public class Main {
	// https://github.com/WemersonThayne/projeto_poo
	// private static String linkTeste = "https://github.com/JoseRenan/POP-Judge";
	// private static String projectTeste = "teste2";

	private static RepositoryGitController repositorio;
	private static MeasurePerCommit measurePerCommit;
	private static String projeto = null;
	private static String linkRepositorio = null;

	public static void main(String[] args) {
		int op = -1;
		while (op != 0) {

			op = menuInicialGit();

			switch (op) {
			case 1:
				repositorioInit();
				break;
			case 2:
				measurePerCommit();
				break;
			case 3:
				buildTXTPerCommit();
				break;
			case 4:
				buildCSVPerCommit();
				break;
			case 5:
				measurePerFile();
				break;
			case 6:
				measurePerLineChange();
				break;
			default:
				System.out.println("Saindo da aplicação...");
				break;
			}
		}
	}

	private static String input(String message) throws IllegalArgumentException {
		Scanner sc = new Scanner(System.in);
		System.out.println(message);
		String input = sc.nextLine();
		System.out.println("\n");
		return input;
	}

	private static int menuInicialGit() {
		System.out.println("");
		System.out.println("--- Menu de Opções para repositório Git ---");
		System.out.println(" 1 - Clone Repositório");
		System.out.println(" 2 - Métrica Por Commit");
		System.out.println(" 3 - Exportar TXT Métrica Por Commit");
		System.out.println(" 4 - Exportar CSV Métrica Por Commit");
		System.out.println(" 5 - Métrica Por File");
		System.out.println(" 6 - Métrica Por Linhas Modificadas");
		System.out.println(" 0 - Sair");
		System.out.println("");
		try {
			int op = Integer.parseInt(input("Digite a opção desejada:"));
			while (op != 0) {
				if (op >= 1 && op <= 6) {
					return op;
				} else {
					System.err.println("Opção Invalida.....");
					op = Integer.parseInt(input("Digite a opção desejada:"));
				}
			}

		} catch (Exception e) {
			return 0;
		}

		return 0;
	}

	private static void repositorioInit() {

		linkRepositorio = input(Constants.INPUT_REPOSITORY);
		projeto = input(Constants.NAME_PROJETO);
		repositorio = new RepositoryGitController();
		if (!Validador.isStringEmpty(projeto) && !Validador.isStringEmpty(linkRepositorio)) {
			repositorio.getRepositoryGit().setLinkProjectLocal(Constants.PATH_DEFAULT + projeto);
			repositorio.getRepositoryGit().setLinkProjectRemote(linkRepositorio);
		}

		try {
			System.out.println("Clonando o repositório, Aguarde pode demorar um pouco....");
			if (repositorio.cloneRepository(linkRepositorio, projeto)) {
				System.out.println("Repositorio Clonado com Sucesso...");

				repositorio.loadCommitsLocal();
				repositorio.loadFilesProject();
				repositorio.loadTeamDeveloper();
			}
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		}
	}

	public static void measurePerCommit() {
		System.out.println("");
		System.out.println("--------- MESOURES PER COMMIT ----------");
		measurePerCommit = new MeasurePerCommit();
		measurePerCommit.developerPerCommits(repositorio.getRepositoryGit().getCommitsLocal(),
				repositorio.getRepositoryGit().getTeamDeveloper());

		measurePerCommit.showEvaluateDeveloperPerCommit(repositorio.getRepositoryGit());
		measurePerCommit.showInformations();
		System.out.println("Developer Max Commit: " + measurePerCommit.developerMaxContributionInProject());
		System.out.println("Max Number Commit: " + measurePerCommit.commitMaxContributionPerDeveloper());
		System.out.println("Developer Min Commit: " + measurePerCommit.developerMinContributionInProject());
		System.out.println("Min Number Commit: " + measurePerCommit.commitMinContributionPerDeveloper());
	}

	public static void buildTXTPerCommit() {
		System.out.println("--------- BUILD REPORT TXT MEASURE PER COMMIT ----------");
		BuildReport b = new BuildReport();
		b.buildReportTXT(Constants.PATH_DEFAULT_REPORT + projeto, measurePerCommit.getData());

	}

	public static void buildCSVPerCommit() {
		System.out.println("--------- BUILD REPORT CSV MEASURE PER COMMIT ----------");
		BuildReport b = new BuildReport();
		b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + projeto, measurePerCommit.getDataCSV());
	}

	public static void measurePerFile() {

		System.out.println("\n\n");
		System.out.println("--------- MESOURES PER FILES ----------");

		MeasurePerFile mpf;
		try {
			mpf = new MeasurePerFile(repositorio.getRepositoryGit().getLocal().getRepository());
			System.out.println("--------- MESOURES DEVELOPER SPECIFIC PER FILES ----------");
			Developer d = queryDeveloperPerName();
			if (d != null) {
				mpf.evaluateQuantityCommitPerFilesPerDeveloper(repositorio.getRepositoryGit().getCommitsLocal(),
						repositorio.getRepositoryGit().getFilesProject(), d);
				mpf.showDevelopersPerFiles(mpf.getDeveloperPerFiles());
			} else {
				System.out.println("Desenvolvedor não encontrado...");
			}

			System.out.println("\n --------- MESOURES ALL DEVELOPERES PER ALL FILES ----------");
			mpf.evaluateQuantityCommitPerFilesPerDevelopers(repositorio.getRepositoryGit().getCommitsLocal(),
					repositorio.getRepositoryGit().getFilesProject(),
					repositorio.getRepositoryGit().getTeamDeveloper());
			mpf.showDevelopersPerFiles(mpf.getDevelopersPerFiles());

			System.out.println("\n --------- MESOURES FILES PER DEVELOPERES ----------");
			mpf.evaluateQuantityCommitInFilesPerDevelopers(repositorio.getRepositoryGit().getCommitsLocal(),
					repositorio.getRepositoryGit().getFilesProject(),
					repositorio.getRepositoryGit().getTeamDeveloper());
			mpf.showFilesPerDevelopers();
			
			System.out.println("\n--------- MESOURES FILE SPECIFIC PER DEVELOPERES ----------");
			String fileName = queryFilePerName();
			if (!Validador.isStringEmpty(fileName)) {
				mpf.evaluateQuantityCommitInFilePerDevelopers(repositorio.getRepositoryGit().getCommitsLocal(),
						fileName, repositorio.getRepositoryGit().getTeamDeveloper());
				System.out.println("\n--------- MESOURES FILE SPECIFIC MAX COMMIT  ----------");
				mpf.developerMaxQuantityCommitPerFile(fileName);
				System.out.println("\n--------- MESOURES FILE SPECIFIC MIN COMMIT  ----------");
				mpf.developerMimQuantityCommitPerFile(fileName);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static void measurePerLineChange(){
		System.out.println("");
		System.out.println("--------- MESOURES PER LINES CHANGE ----------");
		
		MeasurePerLine mpl;
		try {
			mpl = new MeasurePerLine(repositorio.getRepositoryGit().getLocal().getRepository());
			String fileName = queryFilePerName();
			if (!Validador.isStringEmpty(fileName)) {			
				mpl.linesChangeInFile(repositorio.getRepositoryGit().getLocal(),repositorio.getRepositoryGit().getCommitsLocal(),fileName,Constants.PATH_DEFAULT+projeto+"\\");
				mpl.showChangeFilePerAllDevelopers(repositorio.getRepositoryGit().getLocal(),repositorio.getRepositoryGit().getCommitsLocal(),fileName,Constants.PATH_DEFAULT+projeto+"\\",repositorio.getRepositoryGit().getTeamDeveloper());
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Developer queryDeveloperPerName() {
		String name = input("Digite o nome do Desenvolver para pesquisar");
		Developer d = repositorio.getRepositoryGit().getTeamDeveloper().stream().filter(dev -> {
			if (dev.getName().equalsIgnoreCase(name)) {
				return true;
			} else {
				return false;
			}
		}).findAny().orElse(null);
		return d;
	}

	private static String queryFilePerName() {
		String name = input("Digite o nome do arquivo para pesquisar");
		String fileName = repositorio.getRepositoryGit().getFilesProject().stream().filter(file -> {
			if (file.contains(name)) {
				return true;
			} else {
				return false;
			}
		}).findAny().orElse(null);
		return fileName;
	}
}
