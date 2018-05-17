package com.expert.analyze.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
	/**
	 * Create a LocalDate with String in format (dd/MM/yyyy)
	 * @param dateString - String represention on data
	 * @return LocalDate
	 */
	public static LocalDate createLocalDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
		return LocalDate.parse(dateString, formatter);
	}
}
