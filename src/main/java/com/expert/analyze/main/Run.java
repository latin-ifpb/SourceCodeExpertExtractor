package com.expert.analyze.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.expert.analyze.model.ConfigCredential;
import com.expert.analyze.model.ConfigProperties;
import com.expert.analyze.util.Constants;

public class Run {
	
	static ConfigProperties cp = new ConfigProperties();
	
	public static void main(String[] args) {
		List<String> argsList = Arrays.asList(args);
		for (int i = 0; i < args.length; i++) {
			if (argsList.get(i).equalsIgnoreCase("-prop")) {
				int aux = i + Constants.CONST_INDEX;
				cp.setConfigCredential(configCloneRepository(readFileProperties(argsList.get(aux))));
				break;
			}
		}

		verifyParameters(argsList);
		if (cp.getTimes()) {
			verifyTimes(argsList);
		}
		System.out.println("CP:"+cp);

	}


	/**
	 * verify is specifi time for analyzer 
	 * @param argsList
	 */
	private static void verifyTimes(List<String> argsList) {
		String dateInitial = null;
		String dateFinal = null;
		for (int i = 0; i < argsList.size(); i++) {
			int aux = i < argsList.size() - 1 ? i + Constants.CONST_INDEX : argsList.size() - 1;
			if (argsList.get(i).equalsIgnoreCase(Constants.TIMEINITIAL)) {
				dateInitial = argsList.get(aux);
			} else if (argsList.get(i).equalsIgnoreCase(Constants.TIMEFINAL)) {
				dateFinal = argsList.get(aux);
			}
		}
		cp.setDateInitial(dateInitial);
		cp.setDateFinal(dateFinal);
	}

	/**
	 * Read file properties and create a list with parameters on file
	 * @param path
	 * @return
	 */
	private static List<String> readFileProperties(String path) {
		List<String> properties = new ArrayList<>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String linha;
			while ((linha = br.readLine()) != null) {
				properties.add(linha.trim());
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error to read file propertie: " + e.getMessage());
		}
		return properties;
	}

	/**
	 * method verify parameters receive on terminal, and build object {@link ConfigCredential}
	 * @param parameters
	 */
	private static void verifyParameters(List<String> parameters) {

		for (String p : parameters) {
			if (p.equalsIgnoreCase(Constants.PROPERTIES)) {
				cp.setProp(Boolean.TRUE);
			} else if (p.equalsIgnoreCase(Constants.MEASUERECOMMIT)) {
				cp.setMeasureCommit(Boolean.TRUE);
			} else if (p.equalsIgnoreCase(Constants.MEASUERELOC)) {
				cp.setMeasureLoc(Boolean.TRUE);
			} else if (p.equalsIgnoreCase(Constants.MEASUEREDOK)) {
				cp.setMeasureDOK(Boolean.TRUE);
			} else if (p.equalsIgnoreCase(Constants.TIME)) {
				cp.setTimes(Boolean.TRUE);
			}
		}
	}

	/**
	 * method responsible to create a config object to the clone in repository
	 */
	private static ConfigCredential configCloneRepository(List<String> properties) {
		ConfigCredential cfc = new ConfigCredential();
		// walk in list of prop for get a parameters in file properties
		for (String prop : properties) {
			/**
			 * because a file properties have a patterer for example c=true, so need get a
			 * information after "=" this away
			 * prop.substring(prop.lastIndexOf(PROP_PROTOCOL) + CONST_INDEX) get a String
			 * index after a identify '='
			 */
			if (prop.substring(0, prop.lastIndexOf(Constants.PROP_PROTOCOL)).equalsIgnoreCase("c")) {
				cfc.setCloneRepository(Boolean.valueOf(getIteFileProperties(prop)));
			} else if (prop.substring(0, prop.lastIndexOf(Constants.PROP_PROTOCOL)).equalsIgnoreCase("l")) {
				cfc.setLinkProject(getIteFileProperties(prop));
			} else if (prop.substring(0, prop.lastIndexOf(Constants.PROP_PROTOCOL)).equalsIgnoreCase("n")) {
				cfc.setNameProject(getIteFileProperties(prop));
			} else if (prop.substring(0, prop.lastIndexOf(Constants.PROP_PROTOCOL)).equalsIgnoreCase("b")) {
				cfc.setBranch(getIteFileProperties(prop));
			} else if (prop.substring(0, prop.lastIndexOf(Constants.PROP_PROTOCOL)).equalsIgnoreCase("a")) {
				cfc.setAuthentication(Boolean.valueOf(getIteFileProperties(prop)));
			} else if (prop.substring(0, prop.lastIndexOf(Constants.PROP_PROTOCOL)).equalsIgnoreCase("u")) {
				cfc.setUserName(getIteFileProperties(prop));
			} else if (prop.substring(0, prop.lastIndexOf(Constants.PROP_PROTOCOL)).equalsIgnoreCase("p")) {
				cfc.setUserPassword(getIteFileProperties(prop));
			}
		}
		
		return cfc;
	}
	
	/**
	 * return a string after '='
	 * @param prop
	 * @return
	 */
	private static String getIteFileProperties(String prop){
		return prop.substring(prop.lastIndexOf(Constants.PROP_PROTOCOL) + Constants.CONST_INDEX);
	}
}
