package com.expert.analyze.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.revwalk.RevCommit;

import com.expert.analyze.model.ConfigCredential;
import com.expert.analyze.model.ConfigProperties;
import com.expert.analyze.model.Developer;
import com.expert.analyze.model.git.MeasuarePerDOK;
import com.expert.analyze.model.git.MeasurePerCommit;
import com.expert.analyze.model.git.MeasurePerFile;
import com.expert.analyze.model.git.MeasurePerLine;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.DateUtil;
import com.expert.analyze.util.Validador;

public class RunController {

	private ConfigProperties configProperties;
	private RepositoryGitController repositorio;

	public RunController() {
	}

	public RunController(ConfigProperties configProperties) {
		setConfigProperties(configProperties);
		initRun();
	}

	public void initRun() {
		System.out.println("Iniciando o processo de extrações de contribuições.....");
		repositorioInit(configProperties.getConfigCredential());
		if (configProperties.getTimes()) {
			queryCommitPerDate(configProperties.getDateInitial(), configProperties.getDateFinal());
		}
		if (configProperties.getMeasureCommit() != null && configProperties.getMeasureCommit()) {
			measurePerCommitAllProject();
			measurePerFile();
		}
		if(configProperties.getMeasureLoc()!= null && configProperties.getMeasureLoc()){
			measurePerLineChange();
		}
		
		if(configProperties.getMeasureDOK() != null && configProperties.getMeasureDOK()) {
			measurePerDOK();
		}
	}

	private void repositorioInit(ConfigCredential credential) {
		repositorio = new RepositoryGitController();
		if (!Validador.isStringEmpty(credential.getNameProject())
				&& !Validador.isStringEmpty(credential.getLinkProject())) {
			repositorio.getRepositoryGit().setLinkProjectLocal(Constants.PATH_DEFAULT + credential.getNameProject());
			//repositorio.getRepositoryGit().setLinkProjectRemote(credential.getLinkProject());
		}
		try {
			if (Validador.isDirectoryExist(new File(Constants.PATH_DEFAULT + credential.getNameProject()))) {
				System.out.println("Clonando o repositório, Aguarde pode demorar um pouco....");
				if (credential.getAuthentication()) {
					if (!Validador.isStringEmpty(credential.getUserName())
							&& !Validador.isStringEmpty(credential.getUserPassword())) {
						repositorio.clonneRepositoryWithAuthentication(credential.getLinkProject(),
								credential.getNameProject(), credential.getBranch(), credential.getUserName(),
								credential.getUserPassword());
					}
				} else {
					repositorio.cloneRepositoryWithOutAuthentication(credential.getLinkProject(),
							credential.getNameProject(), credential.getBranch());
				}
			}

			repositorio.loadCommitsLocal();
			repositorio.loadFilesProject();
			repositorio.loadTeamDeveloper();
			//TODO:retirar
			System.out.println("Normalizando os contribuidores do projeto");
			// Normalize team developer
			DeveloperController dc = new DeveloperController(repositorio.getRepositoryGit().getTeamDeveloper());
			dc.contributorsNormalizere();
			
			System.out.println("Exportando contribuidores....");
			dc.exportContributorsNormalizeInTXT(Constants.PATH_DEFAULT_REPORT+configProperties.getConfigCredential().getNameProject());
			repositorio.getRepositoryGit().setTeamDeveloper(dc.getTeamDeveloper());
			
		} catch (TransportException e) {
			System.out.println("Error in Initar Clone repository:" + e.getMessage());
		} catch (InvalidRemoteException e) {
			System.out.println("Error in Initar Clone repository:" + e.getMessage());
		}
	}

	public void measurePerCommitAllProject() {
		System.out.println("");
		System.out.println("--------- MESOURES PER COMMIT ----------");
		MeasurePerCommit measurePerCommit = new MeasurePerCommit();

		measurePerCommit.developerPerCommits(repositorio.getRepositoryGit().getCommitsLocal(),repositorio.getRepositoryGit().getTeamDeveloper());
		measurePerCommit.showEvaluateDeveloperPerCommit(repositorio.getRepositoryGit());
		//TODO:retirar
		BuildReport b = new BuildReport();
		b.buildReportCSV(Constants.PATH_DEFAULT_REPORT +configProperties.getConfigCredential().getNameProject()+"CommitAllProject-"
				+LocalDate.now().toString()+"-"+LocalTime.now().toSecondOfDay(), measurePerCommit.getDataCSV());

	}
	
	public void measurePerFile() {
		System.out.println("\nGerando Matriz File X Commit.....");
		try {
			MeasurePerFile mpf = new MeasurePerFile(repositorio.getRepositoryGit().getLocal().getRepository());
			mpf.evaluateQuantityCommitPerFilesPerDevelopersMatrix(repositorio.getRepositoryGit().getCommitsLocal(),
					repositorio.getRepositoryGit().getFilesProject(),
					repositorio.getRepositoryGit().getTeamDeveloper());
			
			BuildReport b = new BuildReport();
			b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + configProperties.getConfigCredential().getNameProject()+
					"COMIT_REPORT"+LocalDate.now().toString()+"-"+LocalTime.now().toSecondOfDay(), mpf.printFileDeveloper());
			System.out.println("Matriz gerada com sucesso.....");
		} catch (IOException e) {
			System.err.println("Error ao tentar gerar metrica por arquivo:"+e.getMessage());
		}

	}

	public  void measurePerLineChange() {
		System.out.println("");
		System.out.println("--------- MESOURES PER LINES CHANGE ----------");

		MeasurePerLine mpl;
		try {
			mpl = new MeasurePerLine(repositorio.getRepositoryGit().getLocal().getRepository(), repositorio.getRepositoryGit().getLocal());
			for (Developer dev : repositorio.getRepositoryGit().getTeamDeveloper()) {
				for(String fileName :repositorio.getRepositoryGit().getFilesProject()){				
					mpl.resolveCommitsPerDeveloper(repositorio.getRepositoryGit().getCommitsLocal(),fileName, dev);
				}
			}
			
			BuildReport b = new BuildReport();
			b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + configProperties.getConfigCredential().getNameProject()+
					"LOC_REPORT"+LocalDate.now().toString()+"-"+LocalTime.now().toSecondOfDay(), mpl.printLineChangePerFile(repositorio.getRepositoryGit().getTeamDeveloper()));

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	
	public  void measurePerDOK() {
		System.out.println("");
		System.out.println("--------- MESOURES PER DEGREE KNOLEGDE ----------");

		try {
			MeasuarePerDOK mpd = new MeasuarePerDOK(repositorio.getRepositoryGit().getLocal().getRepository(),repositorio.getRepositoryGit().getLocal());
			mpd.buildMatrizDegree(repositorio.getRepositoryGit().getCommitsLocal(), repositorio.getRepositoryGit().getTeamDeveloper());
			
			BuildReport b = new BuildReport();
			b.buildReportCSV(Constants.PATH_DEFAULT_REPORT + configProperties.getConfigCredential().getNameProject()+
					"DOK_REPORT"+LocalDate.now().toString()+"-"+LocalTime.now().toSecondOfDay(),mpd.getDataExport());

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	
	private void queryCommitPerDate(String dtInital, String dtFinal) {

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

	/**
	 * @return the configProperties
	 */
	public ConfigProperties getConfigProperties() {
		return configProperties;
	}

	/**
	 * @param configProperties
	 *            the configProperties to set
	 */
	public void setConfigProperties(ConfigProperties configProperties) {
		this.configProperties = configProperties;
	}

	/**
	 * @return the repositorio
	 */
	public RepositoryGitController getRepositorio() {
		return repositorio;
	}

	/**
	 * @param repositorio
	 *            the repositorio to set
	 */
	public void setRepositorio(RepositoryGitController repositorio) {
		this.repositorio = repositorio;
	}

}
