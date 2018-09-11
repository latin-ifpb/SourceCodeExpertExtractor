package com.expert.analyze.util;

public class Constants {

	public static String   PATH_DEFAULT = "c:\\repository";
	public static String   PATH_DEFAULT_REPORT = "c:\\report\\";
	public static String   TYPE_FILE_TXT = ".txt";
	public static String   TYPE_FILE_CSV = ".csv";
	public static String   INPUT_REPOSITORY = "Por favor, informe o link para o repositório:";
	public static String   VALID_INFORMATION = "Por favor, informe um valor válido";
	public static String[] FILES_IGNORE = {".settings/",".project",".class",".jar",".png",".gitignore",".classpath",".war",".MF"};
	public static Double   PERCENTE_TOTAL = 100D;
	public static String   PROTOCOL = ";";
	public static String   ERROR_GET_COMMIT_FIRST = "Error, Need get Commit First";
	public static Integer  CONSTANT_ZERO = 0;
	public static Integer  INDEX_DIFF_DESCARTE = 2;
	
	public static String  IGNORE_DIFF_INITIAL_CHANGE_INDEX = "index";
	public static String  IGNORE_DIFF_INITIAL_CHANGE_AT = "@@";
	public static String  IGNORE_DIFF_INITIAL_CHANGE_MINUS = "---";
	public static String  IGNORE_DIFF_INITIAL_CHANGE_PLUS = "+++";
	
	
	public static String PROPERTIES = "-prop";
	public static String MEASUERECOMMIT = "-mc";
	public static String MEASUERELOC = "-ml";
	public static String MEASUEREDOK = "-mk";
	public static String TIME = "--t";
	public static String TIMEINITIAL = "-ti";
	public static String TIMEFINAL = "-tf";
	public static int CONST_INDEX = 1;
	public static String PROP_PROTOCOL = "=";
	public static String SEPARETOR = ";";
	
	
	public static String NAME_PROJECT ="  ______                                _                                  _                             \r\n" + 
			" |  ____|                              | |         /\\                     | |                            \r\n" + 
			" | |__    __  __  _ __     ___   _ __  | |_       /  \\     _ __     __ _  | |  _   _   ____   ___   _ __ \r\n" + 
			" |  __|   \\ \\/ / | '_ \\   / _ \\ | '__| | __|     / /\\ \\   | '_ \\   / _` | | | | | | | |_  /  / _ \\ | '__|\r\n" + 
			" | |____   >  <  | |_) | |  __/ | |    | |_     / ____ \\  | | | | | (_| | | | | |_| |  / /  |  __/ | |   \r\n" + 
			" |______| /_/\\_\\ | .__/   \\___| |_|     \\__|   /_/    \\_\\ |_| |_|  \\__,_| |_|  \\__, | /___|  \\___| |_|   \r\n" + 
			"                 | |                                                            __/ |                    \r\n" + 
			"                 |_|                                                           |___/                     ";
}
