package com.expert.analyze.main;

import java.util.Scanner;

import com.expert.analyze.controller.RepositoryController;
import com.expert.analyze.util.Constants;

public class Main {
	//https://github.com/JoseRenan/POP-Judge
	private static String linkTeste = "https://github.com/JoseRenan/POP-Judge";
	private static String projectTeste = "teste2";

	public static void main(String[] args) {

//		String projeto = null;
//		String linkRepositorio = null;
//		Scanner sc = new Scanner(System.in);
//		int valido = 0;

		System.out.println("-------- BEM VINDO AO EXPERT ANALYZER ------------");
		System.out.println("\n\n");

//		projeto = input(Constants.INPUT_REPOSITORY);
//		linkRepositorio = input(Constants.NAME_PROJETO);
		RepositoryController repositorio = new RepositoryController();
		repositorio.setLinkProjectLocal(Constants.PATH_DEFAULT + projectTeste);
		repositorio.setLinkProjectRemote(linkTeste);
		 //RepositoryController repositorio = new RepositoryController(projeto,linkRepositorio);
		
		/* if(repositorio.cloneRepository()){
		 System.out.println("Repositorio clonado com suceso");
		 }*/
		
		//repositorio.cloneRepository(linkTeste,projectTeste);
		//System.out.println(repositorio.getBranchesRemote());
		//repositorio.getCommitsLocal();
		repositorio.setCommitsLocal();
		repositorio.setTeamDeveloper();
		System.out.println("Branch Project:"+repositorio.getBranchesLocal());
		System.out.println("Quantity Commit on Project: "+repositorio.getQuantityCommitLocal());
		System.out.println("Team Developer:"+repositorio.getTeamDeveloper());
		repositorio.getNameFilesProject();
		
	}

	private static String input(String message) {
		Scanner sc = new Scanner(System.in);
		System.out.println(message);
		String input = sc.nextLine();
		System.out.println("\n");
		return input;
	}

}
