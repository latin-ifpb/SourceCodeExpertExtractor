package com.expert.analyze.main;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;

import com.expert.analyze.controller.BuildReport;
import com.expert.analyze.controller.DeveloperController;
import com.expert.analyze.controller.RepositoryGitController;
import com.expert.analyze.model.Developer;
import com.expert.analyze.model.git.MeasurePerCommit;
import com.expert.analyze.model.git.MeasurePerFile;
import com.expert.analyze.model.git.MeasurePerLine;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.DateUtil;
import com.expert.analyze.util.Validador;

public class Main {
	// https://github.com/WemersonThayne/projeto_poo
	// private static String linkTeste = "https://github.com/JoseRenan/POP-Judge";
	// private static String projectTeste = "teste2";

	private static String linkTeste = "https://github.com/JoseRenan/POP-Judge";
	private static String projectTeste = "teste3";

	private static RepositoryGitController repositorio;
	private static MeasurePerCommit measurePerCommit;
	private static MeasurePerFile mpf;
	private static String projeto = null;
	private static String linkRepositorio = null;

	public static void main(String[] args) {
		
		 for(int i = 0; i < args.length; i++) {
	            System.out.println("Args"+args[i]);
	        }
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
				buildCSVFilesPerCommits();
				break;
			case 7:
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
		System.out.println(" 6 - Exportar CSV Métrica Por File x Commit x  Dev");
		System.out.println(" 7 - Métrica Por Linhas Modificadas");
		System.out.println(" 0 - Sair");
		System.out.println("");
		try {
			int op = Integer.parseInt(input("Digite a opção desejada:"));
			while (op != 0) {
				if (op >= 1 && op <= 7) {
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

//		 linkRepositorio = input(Constants.INPUT_REPOSITORY);
//		 projeto = input(Constants.NAME_PROJETO);
//
		linkRepositorio = linkTeste;
		projeto = projectTeste;
		repositorio = new RepositoryGitController();
		if (!Validador.isStringEmpty(projeto) && !Validador.isStringEmpty(linkRepositorio)) {
			repositorio.getRepositoryGit().setLinkProjectLocal(Constants.PATH_DEFAULT + projeto);
			repositorio.getRepositoryGit().setLinkProjectRemote(linkRepositorio);
		}

		try {
			if (Validador.isDirectoryExist(new File(Constants.PATH_DEFAULT + projeto))) {
				System.out.println("Clonando o repositório, Aguarde pode demorar um pouco....");
				if (repositorio.cloneRepositoryWithOutAuthentication(linkRepositorio, projeto,null)) {
					System.out.println("Repositorio Clonado com Sucesso...");
				}
			}

			repositorio.loadCommitsLocal();
			repositorio.loadFilesProject();
			repositorio.loadTeamDeveloper();

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

	public static void measurePerCommit() {
		System.out.println("");
		System.out.println("--------- MESOURES PER COMMIT ----------");
		measurePerCommit = new MeasurePerCommit();

		queryCommitPerDate();

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

	public static void buildCSVFilesPerCommits() {
		System.out.println("--------- BUILD REPORT CSV MEASURE PER ----------");
		BuildReport b = new BuildReport();
		b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + projeto, mpf.printFileDeveloper(repositorio.getRepositoryGit().getNamesFiles()));

	}

	public static void measurePerFile() {

		System.out.println("\n\n");
		System.out.println("--------- MESOURES PER FILES ----------");

		try {
			mpf = new MeasurePerFile(repositorio.getRepositoryGit().getLocal().getRepository());
			mpf.evaluateQuantityCommitPerFilesPerDevelopersMatrix(repositorio.getRepositoryGit().getCommitsLocal(),
					repositorio.getRepositoryGit().getFilesProject(),
					repositorio.getRepositoryGit().getTeamDeveloper());
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void measurePerLineChange() {
		System.out.println("");
		System.out.println("--------- MESOURES PER LINES CHANGE ----------");

		MeasurePerLine mpl;
		try {
			mpl = new MeasurePerLine(repositorio.getRepositoryGit().getLocal().getRepository());
			String fileName = "teste";
			if (!Validador.isStringEmpty(fileName)) {
				mpl.linesChangeInFile(repositorio.getRepositoryGit().getLocal(),
						repositorio.getRepositoryGit().getCommitsLocal(), fileName,
						Constants.PATH_DEFAULT + projeto + "\\");
				mpl.showChangeFilePerAllDevelopers(repositorio.getRepositoryGit().getLocal(),
						repositorio.getRepositoryGit().getCommitsLocal(), fileName,
						Constants.PATH_DEFAULT + projeto + "\\", repositorio.getRepositoryGit().getTeamDeveloper());
			}

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private static LocalDate inputDate(String messege) {
		String dateString = input(messege);
		if (dateString != null) {
			return DateUtil.createLocalDate(dateString);
		}
		return LocalDate.now();
	}

	private static void queryCommitPerDate() {
		String op = input("Deseja saber métrica por algum período do projeto (S/N)?");
		System.out.println("");

		if (op.equalsIgnoreCase("S")) {
			LocalDate localDateInicial = inputDate("Data Inical do Período:");
			LocalDate localDateFinal = inputDate("Data Final do Período:");
			if (localDateInicial != null && localDateFinal != null) {
				List<RevCommit> commits = repositorio.findCommitsPerDate(localDateInicial, localDateFinal);
				if (commits.isEmpty()) {
					System.err.println("Não há commits nesse período.");
				}
				repositorio.getRepositoryGit().setCommitsLocal(commits);
			}
		}
	}
}
