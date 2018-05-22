package com.expert.analyze.model;

public class ConfigProperties {

	private ConfigCredential configCredential;
	private Boolean prop;
	private Boolean measureCommit;
	private Boolean measureLoc;
	private Boolean measureDOK;
	private Boolean times;
	private String dateInitial;
	private String dateFinal;

	public ConfigProperties() {
	}

	/**
	 * @return the configCredential
	 */
	public ConfigCredential getConfigCredential() {
		return configCredential;
	}

	/**
	 * @param configCredential
	 *            the configCredential to set
	 */
	public void setConfigCredential(ConfigCredential configCredential) {
		this.configCredential = configCredential;
	}

	/**
	 * @return the prop
	 */
	public Boolean getProp() {
		return prop;
	}

	/**
	 * @param prop the prop to set
	 */
	public void setProp(Boolean prop) {
		this.prop = prop;
	}

	/**
	 * @return the measureCommit
	 */
	public Boolean getMeasureCommit() {
		return measureCommit;
	}

	/**
	 * @param measureCommit the measureCommit to set
	 */
	public void setMeasureCommit(Boolean measureCommit) {
		this.measureCommit = measureCommit;
	}

	/**
	 * @return the measureLoc
	 */
	public Boolean getMeasureLoc() {
		return measureLoc;
	}

	/**
	 * @param measureLoc the measureLoc to set
	 */
	public void setMeasureLoc(Boolean measureLoc) {
		this.measureLoc = measureLoc;
	}

	/**
	 * @return the measureDOK
	 */
	public Boolean getMeasureDOK() {
		return measureDOK;
	}

	/**
	 * @param measureDOK the measureDOK to set
	 */
	public void setMeasureDOK(Boolean measureDOK) {
		this.measureDOK = measureDOK;
	}

	/**
	 * @return the times
	 */
	public Boolean getTimes() {
		return times;
	}

	/**
	 * @param times the times to set
	 */
	public void setTimes(Boolean times) {
		this.times = times;
	}

	/**
	 * @return the dateInitial
	 */
	public String getDateInitial() {
		return dateInitial;
	}

	/**
	 * @param dateInitial the dateInitial to set
	 */
	public void setDateInitial(String dateInitial) {
		this.dateInitial = dateInitial;
	}

	/**
	 * @return the dateFinal
	 */
	public String getDateFinal() {
		return dateFinal;
	}

	/**
	 * @param dateFinal the dateFinal to set
	 */
	public void setDateFinal(String dateFinal) {
		this.dateFinal = dateFinal;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfigProperties [configCredential=" + configCredential + ", prop=" + prop + ", measureCommit="
				+ measureCommit + ", measureLoc=" + measureLoc + ", measureDOK=" + measureDOK + ", times=" + times
				+ ", dateInitial=" + dateInitial + ", dateFinal=" + dateFinal + "]";
	}

}
