package com.expert.analyze.main;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

import com.expert.analyze.controller.BuildReport;
import com.expert.analyze.controller.DeveloperController;
import com.expert.analyze.controller.RepositoryGitController;
import com.expert.analyze.model.Developer;
import com.expert.analyze.model.LOCPerFile;
import com.expert.analyze.model.git.MeasuarePerDOK;
import com.expert.analyze.model.git.MeasurePerFile;
import com.expert.analyze.model.git.MeasurePerLine;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;

public class Main2 {

	private static RepositoryGitController repositorio;
	private static String linkTeste = "https://github.com/JoseRenan/POP-Judge";
	private static String projectTeste = "teste3";
	private static String repositPrivate = "https://wemersonthayne22@bitbucket.org/wemersonthayne22/expertanalyzer.git";

	public static void main(String[] args) {
		repositorioInit();
		// repositorio.findCommitsPerDate(LocalDate.of(2017, 04, 01), LocalDate.of(2017,
		// 05, 01));
		// teste();

		
		testeLOC();
		
		//testeDOK();
	}

	private static void repositorioInit() {

		repositorio = new RepositoryGitController();
		if (!Validador.isStringEmpty(projectTeste) && !Validador.isStringEmpty(linkTeste)) {
			repositorio.getRepositoryGit().setLinkProjectLocal(Constants.PATH_DEFAULT + projectTeste);
			repositorio.getRepositoryGit().setLinkProjectRemote(linkTeste);
		}

		try {
			String branch = repositorio.getBranchesRemote().get(1);
			
			if (Validador.isDirectoryExist(new File(Constants.PATH_DEFAULT + projectTeste))) {
				System.out.println("Clonando o	 repositório, Aguarde pode demorar um pouco....");
				if (repositorio.cloneRepositoryWithOutAuthentication(linkTeste, projectTeste, branch)) {
					System.out.println("Repositorio Clonado com Sucesso...");
				}
			}
			repositorio.loadCommitsLocal();
			repositorio.loadFilesProject();
			repositorio.loadTeamDeveloper();
			System.out.println("Branch Local" + repositorio.getBranchesLocal());
			System.out.println("Commits" + repositorio.getRepositoryGit().getCommitsLocal().size());
			System.out.println("Files" + repositorio.getRepositoryGit().getFilesProject().size());

			// repositorio.clonneRepositoryWithAuthentication(repositPrivate,"privateRepositoy",null,
			// "wemersonthayne22","w3m3450n@");

			// Normalize team developer
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
			e.printStackTrace();
		}
	}

	private static void testeLOC() {

		MeasurePerLine mpl;

		try {
			mpl = new MeasurePerLine(repositorio.getRepositoryGit().getLocal().getRepository(), repositorio.getRepositoryGit().getLocal());
				for (Developer dev : repositorio.getRepositoryGit().getTeamDeveloper()) {
					for(String fileName :repositorio.getRepositoryGit().getFilesProject()){				
						mpl.resolveCommitsPerDeveloper(repositorio.getRepositoryGit().getLocal(), repositorio.getRepositoryGit().getCommitsLocal(),fileName, dev);
					}
				}
			
			//mpl = new MeasurePerLine(repositorio.getRepositoryGit().getLocal().getRepository());
//			for (Developer dev : repositorio.getRepositoryGit().getTeamDeveloper()) {
//				System.out.println("Dev:"+dev.getName());
//				mpl.resolveCommitsPerDeveloper(repositorio.getRepositoryGit().getLocal(), repositorio.getRepositoryGit().getCommitsLocal(),
//						"src/br/edu/popjudge/bean/UserBean.java", dev);
//			}

//			mpl.teste(repositorio.getRepositoryGit().getLocal(), repositorio.getRepositoryGit().getCommitsLocal(),
//					"src/br/edu/popjudge/bean/UsuarioBean.java", null);
			for (LOCPerFile loc : mpl.getLocPerFiles()) {
				System.out.println(loc);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private static void testeDOK(){
		try {
			MeasuarePerDOK mpd = new MeasuarePerDOK(repositorio.getRepositoryGit().getLocal().getRepository(),repositorio.getRepositoryGit().getLocal());
			
			mpd.addDegree(repositorio.getRepositoryGit().getCommitsLocal(), repositorio.getRepositoryGit().getTeamDeveloper());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
