package com.expert.analyze.main;

import java.util.ArrayList;
import java.util.List;

import com.expert.analyze.controller.BuildReport;
import com.expert.analyze.controller.RepositoryController;
import com.expert.analyze.model.MeasurePerCommit;
import com.expert.analyze.model.MeasurePerFile;
import com.expert.analyze.model.MeasurePerLine;
import com.expert.analyze.util.Constants;

public class Main {
	// https://github.com/JoseRenan/POP-Judge
	private static String linkTeste = "https://github.com/JoseRenan/POP-Judge";
	private static String projectTeste = "teste2";

	public static void main(String[] args) {

		// String projeto = null;
		// String linkRepositorio = null;
		// Scanner sc = new Scanner(System.in);
		// int valido = 0;

		System.out.println("-------- BEM VINDO AO EXPERT ANALYZER ------------");
		System.out.println("\n\n");

		// projeto = input(Constants.INPUT_REPOSITORY);
		// linkRepositorio = input(Constants.NAME_PROJETO);
		RepositoryController repositorio = new RepositoryController();
		repositorio.setLinkProjectLocal(Constants.PATH_DEFAULT + projectTeste);
		repositorio.setLinkProjectRemote(linkTeste);
		// RepositoryController repositorio = new RepositoryController(projeto,linkRepo
		// sitorio);

		/*
		 * if(repositorio.cloneRepository()){
		 * System.out.println("Repositorio clonado com suceso"); }
		 */

		// repositorio.cloneRepository(linkTeste,projectTeste);
		// System.out.println(repositorio.getBranchesRemote());
		repositorio.setCommitsLocal();
		repositorio.setTeamDeveloper();

		System.out.println("Branch Project:" + repositorio.getBranchesLocal());
		System.out.println("Quantity Commit on Project: " + repositorio.getQuantityCommitLocal());
		System.out.println("Team Developer:" + repositorio.getTeamDeveloper());
		repositorio.setFilesProject();
		repositorio.getFilesProject().stream().forEach(f -> {
			System.out.println("File:" + f);
		});

		System.out.println("\n\n");
		System.out.println("--------- MESOURES PER COMMIT ----------");
		List<String> data = new ArrayList<>();
		List<String> dataCSV = new ArrayList<>();
		MeasurePerCommit m = new MeasurePerCommit();
		repositorio.getTeamDeveloper().iterator().forEachRemaining(dev -> {
			int total = 0;
			total = m.countCommitPerDeveloper(repositorio.getCommitsLocal(), dev);
			System.out.println("Developer: " + dev.getName() + " has Commit: " + total + " Percente:"
					+ m.evaluatePercentageCommitsAllProjectPerDeveloper(repositorio.getQuantityCommitLocal(), total));
			data.add("Developer: " + dev.getName() + " has Commit: " + total + " Percente:"
					+ m.evaluatePercentageCommitsAllProjectPerDeveloper(repositorio.getQuantityCommitLocal(), total));
			dataCSV.add(dev.getName() + ";" + total + ";"
					+ m.evaluatePercentageCommitsAllProjectPerDeveloper(repositorio.getQuantityCommitLocal(), total));

		});

		m.developerPerCommits(repositorio.getCommitsLocal(), repositorio.getTeamDeveloper());
		System.out.println("Developer Max Commit: " + m.developerMaxContributionInProject());
		System.out.println("Max Number Commit: " + m.commitMaxContributionPerDeveloper());
		System.out.println("Developer Min Commit: " + m.developerMinContributionInProject());
		System.out.println("Min Number Commit: " + m.commitMinContributionPerDeveloper());

		System.out.println("\n\n");
		System.out.println("--------- BUILD REPORT TXT MEASURE PER COMMIT ----------");
		BuildReport b = new BuildReport();
		b.buildReportTXT(Constants.PATH_DEFAULT_REPORT + projectTeste, data);

		System.out.println("\n\n");
		System.out.println("--------- BUILD REPORT CSV MEASURE PER COMMIT ----------");
		b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + projectTeste, dataCSV);

		System.out.println("\n\n");
		System.out.println("--------- MESOURES PER FILES ----------");

		MeasurePerFile mpf = new MeasurePerFile(repositorio.getLocal().getRepository());
		repositorio.getTeamDeveloper().iterator().forEachRemaining(dev -> {
			Integer count;
			count = mpf.evaluateQuantityCommitPerFilePerDeveloper(repositorio.getCommitsLocal(),"WebContent/webapp/user/prob.xhtml", dev);
			System.out.println("File: WebContent/webapp/user/prob.xhtml "+" Developer: "+dev.getName() +" has Quantity Commit in: " + count);
		});
		System.out.println("\n\n");
		System.out.println("--------- MESOURES DEVELOPER PER FILES ----------");
		mpf.evaluateQuantityCommitPerFilesPerDeveloper(repositorio.getCommitsLocal(), repositorio.getFilesProject(), repositorio.getTeamDeveloper().iterator().next());
		mpf.showDevelopersPerFiles(mpf.getDeveloperPerFiles());
		
		System.out.println("\n\n");
		System.out.println("--------- MESOURES DEVELOPERES PER FILES ----------");
		//mpf.evaluateQuantityCommitPerFilesPerDevelopers(repositorio.getCommitsLocal(), repositorio.getFilesProject(), repositorio.getTeamDeveloper());
		//mpf.showDevelopersPerFiles(mpf.getDevelopersPerFiles());
		System.out.println("\n\n");
		System.out.println("--------- MESOURES FILES PER DEVELOPERES ----------");
		//mpf.evaluateQuantityCommitInFilesPerDevelopers(repositorio.getCommitsLocal(), repositorio.getFilesProject(), repositorio.getTeamDeveloper());
		
		System.out.println("\n\n");
		System.out.println("--------- MESOURES FILE PER DEVELOPERES ----------");
		mpf.evaluateQuantityCommitInFilePerDevelopers(repositorio.getCommitsLocal(), "WebContent/webapp/user/prob.xhtml", repositorio.getTeamDeveloper());
		
		System.out.println("\n\n");
		System.out.println("-------------------------------");
		mpf.developerMaxQuantityCommitPerFile("WebContent/webapp/user/prob.xhtml");
		mpf.developerMimQuantityCommitPerFile("WebContent/webapp/user/prob.xhtml");
		
		System.out.println("\n\n");
		System.out.println("--------- MESOURES PER LINES CHANGE ----------");
		
		MeasurePerLine mpl = new MeasurePerLine(repositorio.getLocal().getRepository());
		//mpl.linesChange();
		mpl.linesChangeInFile(repositorio.getLocal(),repositorio.getCommitsLocal(),"src/br/edu/popjudge/bean/UserBean.java",Constants.PATH_DEFAULT+projectTeste+"\\");
		repositorio.getTeamDeveloper().iterator().forEachRemaining(dev -> {
			System.out.println("\n");
			System.out.println(" Developer: "+dev.getName());
			System.out.println(" -----------------");
			mpl.linesChangeInFilePerDeveloper(repositorio.getLocal(),repositorio.getCommitsLocal(),"src/br/edu/popjudge/bean/UserBean.java",Constants.PATH_DEFAULT+projectTeste+"\\",dev);
			System.out.println(" -----------------");
		});
			
	}

	// private static String input(String message) {
	// Scanner sc = new Scanner(System.in);
	// System.out.println(message);
	// String input = sc.nextLine();
	// System.out.println("\n");
	// return input;
	// }

}
