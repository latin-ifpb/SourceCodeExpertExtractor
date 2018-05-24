package com.expert.analyze.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.expert.analyze.model.Developer;
import com.expert.analyze.util.Constants;

public class DeveloperController {

	Set<Developer> teamDeveloper;
	List<String> teamDeveloperNormalize;
	
	
	public DeveloperController(){
	}
	
	public DeveloperController(Set<Developer> teDevelopers) {
		setTeamDeveloper(teDevelopers);
	}
	/**
	 * method responsible for normalize contributions, when same email this
	 * Considered a unique contributor and get a various 
	 */
	public void contributorsNormalizere() {
		teamDeveloperNormalize = new ArrayList<>();
		//Creat aux for walk in list the team developer find other item with same email
		Set<Developer> teamAux = teamDeveloper;
		teamDeveloper.forEach(d ->{	
			//Find other developer with same email
			 List<Developer> findDevelopers = teamAux.stream()                // convert list to stream
		                .filter(line -> line.getEmail().equalsIgnoreCase(d.getEmail()))//when developer equal email his add in list
		                .collect(Collectors.toList());//convert result for list
			 
			 //Call normalize for pattern Name<email>:nick,nickN,email
			 String normalize = mountDeveloperNormalize(findDevelopers);
			 //Verify exit item in list
			 if(!teamDeveloperNormalize.contains(normalize)){
				 teamDeveloperNormalize.add(normalize);
			 }
		});
	
		//set developer team normalize
		setTeamNormalize(teamDeveloperNormalize);
		
	}
	
	/**
	 * Set teamdeveloper normalize 
	 * @param teamDeveloperNormalizeNicks - List the String in pa
	 */
	private void setTeamNormalize(List<String> teamDeveloperNormalizeNicks){
		//Clear teamdeveloper for set again
		teamDeveloper = new HashSet<>();
		//walk in list
		teamDeveloperNormalizeNicks.forEach(s ->{
			//Get a nick name list
			List<String> nicks = getNickNamesDeveloper(s);
			//Mount developer with nicks names, name and email unique per developer in team
			Developer dev = new Developer(nicks.get(Constants.CONSTANT_ZERO),// Get a first name 
										  s.substring(s.lastIndexOf(",")+1)//get email, plus 1 skip a "," get only email 
										  ,nicks);
			teamDeveloper.add(dev);
		});
		
	}
	
	/**
	 * Get a nicks names the developer specific
	 * @param s String in pattener Name<email>:nick1,nickN,email
	 * @return List<String> nicks names.
	 */
	private List<String> getNickNamesDeveloper(String s) {
		String [] nicks = s.substring(s.lastIndexOf(":")+1, s.lastIndexOf(",")).split(",");
		return Arrays.asList(nicks);
	}

	/**
	 * Export team developer for a CVS file
	 * @param pathDirectory
	 */
	public void exportContributorsNormalizeInTXT(String pathDirectory){
		BuildReport b = new BuildReport();
		b.buildReportTXT(pathDirectory+"contributors", teamDeveloperNormalize);
	}

	/**
	 * Mount string pattern name<dev@mail>:name,nameN,email
	 * @param developer
	 * @return String 
	 */
	private String mountDeveloperNormalize(List<Developer> developers){
		StringBuilder developerN = new StringBuilder();
		for (int i = 0; i < developers.size(); i++) {
			if(i == 0){
				developerN.append(developers.get(i).getName());
				developerN.append("<");
				developerN.append(developers.get(i).getEmail());
				developerN.append(">");
				developerN.append(":");	
				developerN.append(developers.get(i).getName());
			}else {				
				developerN.append(",");
				developerN.append(developers.get(i).getName());
			}
		
			if(i == developers.size() - 1) {
				developerN.append(",");
				developerN.append(developers.get(i).getEmail());
			}
		}

		return developerN.toString();
	}
	
	/**
	 * @return the teamDeveloper
	 */
	public Set<Developer> getTeamDeveloper() {
		return teamDeveloper;
	}

	/**
	 * @param teamDeveloper the teamDeveloper to set
	 */
	public void setTeamDeveloper(Set<Developer> teamDeveloper) {
		this.teamDeveloper = teamDeveloper;
	}

	/**
	 * @return the teamDeveloperNormalize
	 */
	public List<String> getTeamDeveloperNormalize() {
		return teamDeveloperNormalize;
	}

	/**
	 * @param teamDeveloperNormalize the teamDeveloperNormalize to set
	 */
	public void setTeamDeveloperNormalize(List<String> teamDeveloperNormalize) {
		this.teamDeveloperNormalize = teamDeveloperNormalize;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeveloperController [teamDeveloper=" + teamDeveloper + ", teamDeveloperNormalize="
				+ teamDeveloperNormalize + "]";
	}
	
	
}
