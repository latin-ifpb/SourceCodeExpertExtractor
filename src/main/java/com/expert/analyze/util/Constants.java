package com.expert.analyze.util;

public class Constants {

	public static String PATH_DEFAULT = "c:\\repository";
	public static String PATH_DEFAULT_REPORT = "c:\\report\\";
	public static String TYPE_FILE_TXT = ".txt";
	public static String TYPE_FILE_CSV = ".csv";
	public static String INPUT_REPOSITORY = "Por favor, informe o link para o repositório:";
	public static String NAME_PROJETO = "Por favor, informe o nome do projeto:";
	public static String VALID_INFORMATION = "Por favor, informe um valor válido";
	public static String[] FILES_IGNORE = {".settings/",".project",".class",".jar",".png",".gitignore",".classpath"};
	public static Double PERCENTE_TOTAL = 100D;
	public static String PROTOCOL = ";";
	public static String ERROR_GET_COMMIT_FIRST = "Error, Need get Commit First";
	public static Integer CONSTANT_ZERO = 0;	
}
//modelar as duas métricas
//gerar matriz  -> commit x arquivo/ desevolvedor
//pesquisa por periodo para gerar as métricas
