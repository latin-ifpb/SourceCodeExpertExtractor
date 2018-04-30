package com.expert.analyze.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.expert.analyze.util.Constants;

public class BuildReport {
	
	private String newLine = System.getProperty("line.separator");
	private PrintWriter recordFile;
	private File file;

	public void creatDirectoryDefault() {
		new File(Constants.PATH_DEFAULT_REPORT).mkdir();
	}

	public void buildReportTXT(String pathDirectory, List<String> data) {
		System.out.println("Building report .....");
		try {
			file = new File(pathDirectory + Constants.TYPE_FILE_TXT);
			recordFile = new PrintWriter(file);
			data.forEach(d -> {
				recordFile.printf(d + newLine);
			});
			cloneConnect();

		} catch (IOException e) {
			System.err.println("Erro ao tentar Relatorio em TXT");
			e.printStackTrace();
		}
		System.out.println("Report building sucess.....");
	}

	public void buildReportCSV(String pathDirectory, List<String> data) {
		System.out.println("Building report .....");
		FileWriter writer;
		try {
			writer = new FileWriter(pathDirectory + Constants.TYPE_FILE_CSV);
			for (String line : data) {
				writer.append(line);
				writer.append(newLine);
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.err.println("Erro ao tentar Relatorio em CSV");
			e.printStackTrace();
		}
		System.out.println("Report building sucess.....");
	}

	private void cloneConnect() {
		recordFile.close();
	}

}
