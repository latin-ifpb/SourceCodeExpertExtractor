package com.expert.analyze.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.expert.analyze.util.Constants;

/**
 * This class responsible per create and export file with data 
 * building in measure the repository 
 * 
 * Export file (CVS and TXT)
 * 
 * @author wemerson
 *
 */
public class BuildReport {
	
	//Used for create break line in write file
	private String newLine = System.getProperty("line.separator");
	//Print data in file
	private PrintWriter recordFile;
	//File to export
	private File file;

	public BuildReport(){
		creatDirectoryDefault();
	}
	
	/**
	 * Creat the directory for export the report
	 */
	public void creatDirectoryDefault() {
		new File(Constants.PATH_DEFAULT_REPORT).mkdir();
	}

	/**
	 * Method responsabible for construit the file to export in TXT
	 * @param pathDirectory - Directory to  export the file
	 * @param data  - List<String> data to export in file
	 */
	public void buildReportTXT(String pathDirectory, List<String> data) {
		System.out.println("Building report .....");
		try {
			//Create file to export
			file = new File(pathDirectory + Constants.TYPE_FILE_TXT);
			//Create  PrinWrite with file to export
			recordFile = new PrintWriter(file);
			//walks in list the data 
			data.forEach(d -> {
				//add in recordFile one line the list at a time
				recordFile.printf(d + newLine);
			});
			//Close file
			cloneConnect();

		} catch (IOException e) {
			System.err.println("Erro ao tentar Relatorio em TXT: "+e.getMessage());
		}
		System.out.println("Report building sucess.....");
	}

	/**
	 * Method responsabible for construit the file to export in CSV
	 * @param pathDirectory - Directory to  export the file
	 * @param data  - List<String> data to export in file
	 */
	public void buildReportCSV(String pathDirectory, List<String> data) {
		System.out.println("Building report .....");
		//File writes the data 
		FileWriter writer;
		try {
			//Create fileerites for export, recieve the path and type export
			writer = new FileWriter(pathDirectory + Constants.TYPE_FILE_CSV);
			//walks in list the data 
			for (String line : data) {
				//add in filewrite a line
				writer.append(line);
				//new break line
				writer.append(newLine);
			}

			//free and close file writer
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println("Erro ao tentar Relatorio em CSV:"+e.getMessage());			
		}
		System.out.println("Report building sucess.....");
	}

	/**
	 * Close object recordFile for don't corrupt the export
	 */
	private void cloneConnect() {
		recordFile.close();
	}

}
