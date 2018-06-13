package com.expert.analyze.main;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;

import com.expert.analyze.controller.BuildReport;
import com.expert.analyze.controller.DeveloperController;
import com.expert.analyze.controller.RepositoryGitController;
import com.expert.analyze.model.Developer;
import com.expert.analyze.model.LOCPerFile;
import com.expert.analyze.model.git.MeasuarePerDOK;
import com.expert.analyze.model.git.MeasurePerCommit;
import com.expert.analyze.model.git.MeasurePerFile;
import com.expert.analyze.model.git.MeasurePerLine;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.DateUtil;
import com.expert.analyze.util.Validador;

public class Main2 {

	private static RepositoryGitController repositorio;
	//private static String linkTeste = "https://gitlab.dpf.gov.br/mobilizacao/mobilizacao-rest.git";
	//private static String linkTeste = "https://github.com/JoseRenan/POP-Judge";
	private static String linkTeste = "https://github.com/usebens/amazon.git";
	//private static String projectTeste = "teste_project";
	private static String projectTeste = "mobilizacao-rest";
	private static String repositPrivate = "https://gitlab.dpf.gov.br/mobilizacao/mobilizacao-rest.git";

	public static void main(String[] args) {
		repositorioInit();
		// repositorio.findCommitsPerDate(LocalDate.of(2017, 04, 01), LocalDate.of(2017,
		// 05, 01));
		// teste();

		//teste();
		//testeLOC();
		
		//testeDOK();
	}

	private static void repositorioInit() {

		repositorio = new RepositoryGitController();
		if (!Validador.isStringEmpty(projectTeste) && !Validador.isStringEmpty(linkTeste)) {
			repositorio.getRepositoryGit().setLinkProjectLocal(Constants.PATH_DEFAULT + projectTeste);
			repositorio.getRepositoryGit().setLinkProjectRemote(linkTeste);
		}

		//String branch = repositorio.getBranchesRemote().get(1);
		String branch = "12sprint";

		if (Validador.isDirectoryExist(new File(Constants.PATH_DEFAULT + projectTeste))) {
			System.out.println("Clonando o	 repositório, Aguarde pode demorar um pouco....");
				try {
					if (repositorio.cloneRepositoryWithOutAuthentication(linkTeste, projectTeste, branch)) {
						System.out.println("Repositorio Clonado com Sucesso...");
					}
				} catch (TransportException | InvalidRemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			repositorio.clonneRepositoryWithAuthentication(repositPrivate,projectTeste,branch,
//					"wemerson.wtvp","W3m3450n@1");
		}
		repositorio.loadCommitsLocal();
		repositorio.loadFilesProject();
		System.out.println("Commits Inital:" + repositorio.getRepositoryGit().getCommitsLocal().size());
		System.out.println("Files Inital:" + repositorio.getRepositoryGit().getFilesProject().size());
		repositorio.loadTeamDeveloper();
		DeveloperController dc = new DeveloperController(repositorio.getRepositoryGit().getTeamDeveloper());
		dc.contributorsNormalizere();
		repositorio.getRepositoryGit().setTeamDeveloper(dc.getTeamDeveloper());
		System.out.println("Developer:"+repositorio.getRepositoryGit().getTeamDeveloper().size());
		
		queryCommitPerDate("01/01/2018","02/02/2018");
		repositorio.loadFilesProject();
		repositorio.loadTeamDeveloper();
		//System.out.println("Branch Local" + repositorio.getBranchesLocal());
		System.out.println("Commits:" + repositorio.getRepositoryGit().getCommitsLocal().size());
		System.out.println("Files:" + repositorio.getRepositoryGit().getFilesProject().size());
		
		// Normalize team developer
		dc = new DeveloperController(repositorio.getRepositoryGit().getTeamDeveloper());
		dc.contributorsNormalizere();
		repositorio.getRepositoryGit().setTeamDeveloper(dc.getTeamDeveloper());
		System.out.println("Developer:"+repositorio.getRepositoryGit().getTeamDeveloper().size());
		
//		repositorio.getRepositoryGit().getTeamDeveloper().forEach(d ->{
//			System.out.println(d);
//		});
	}

	private static void teste() {
		MeasurePerCommit measurePerCommit = new MeasurePerCommit();

		measurePerCommit.developerPerCommits(repositorio.getRepositoryGit().getCommitsLocal(),repositorio.getRepositoryGit().getTeamDeveloper());
		measurePerCommit.showEvaluateDeveloperPerCommit(repositorio.getRepositoryGit());
		System.out.println("\n\n");
		System.out.println("--------- MESOURES PER FILES ----------");
//
		MeasurePerFile mpf;

		try {
			mpf = new MeasurePerFile(repositorio.getRepositoryGit().getLocal().getRepository());
					
			mpf.evaluateQuantityCommitPerFilesPerDevelopersMatrix(repositorio.getRepositoryGit().getCommitsLocal(),
					repositorio.getRepositoryGit().getFilesProject(),
					repositorio.getRepositoryGit().getTeamDeveloper());

			BuildReport b = new BuildReport();
			b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + projectTeste+"-MC-"+LocalDate.now().toString()+"-"+LocalTime.now().toSecondOfDay(), mpf.printFileDeveloper());

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
				
				BuildReport b = new BuildReport();
				b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + projectTeste+"LOC_REPORT"+LocalDate.now().toString()+"-"+LocalTime.now().toSecondOfDay(), mpl.printLineChangePerFile(repositorio.getRepositoryGit().getTeamDeveloper()));
			//mpl = new MeasurePerLine(repositorio.getRepositoryGit().getLocal().getRepository());
//			for (Developer dev : repositorio.getRepositoryGit().getTeamDeveloper()) {
//				System.out.println("Dev:"+dev.getName());
//				mpl.resolveCommitsPerDeveloper(repositorio.getRepositoryGit().getLocal(), repositorio.getRepositoryGit().getCommitsLocal(),
//						"src/br/edu/popjudge/bean/UserBean.java", dev);
//			}

//			mpl.teste(repositorio.getRepositoryGit().getLocal(), repositorio.getRepositoryGit().getCommitsLocal(),
//					"src/br/edu/popjudge/bean/UsuarioBean.java", null);
//			for (LOCPerFile loc : mpl.getLocPerFiles()) {
//				System.out.println(loc);
//			}
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
	

	private static void queryCommitPerDate(String dtInital, String dtFinal) {

		LocalDate localDateInicial = DateUtil.createLocalDate(dtInital);
		LocalDate localDateFinal = DateUtil.createLocalDate(dtFinal);

		if (localDateInicial != null && localDateFinal != null) {
			List<RevCommit> commits = repositorio.findCommitsPerDate(localDateInicial, localDateFinal);
			if (commits.isEmpty()) {
				repositorio.loadCommitsLocal();
				System.err.println("Não há commits nesse período.");
			} else {
				repositorio.getRepositoryGit().setCommitsLocal(commits);
			}
		}
	}
	
	
	private static Map<Developer,List<RevCommit>> filtrarCommitsDeveloper(Set<Developer> developers, List<RevCommit> commits) {
		Map<Developer,List<RevCommit>> mapa = new HashMap<>();
		List<RevCommit> commit = new ArrayList<>();
		developers.forEach(d ->{
			commits.forEach(c ->{
				if(c.getAuthorIdent().getEmailAddress().equalsIgnoreCase(d.getEmail())){
					commit.add(c);
				}
			});
			mapa.put(d, commit);
		});
		System.out.println(mapa);
		return null;
	}
}
