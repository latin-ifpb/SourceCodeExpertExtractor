package com.expert.analyze.model;

public class DegreeKnowledgeFile implements Comparable<DegreeKnowledgeFile> {
	
	private Float firstAuthority = 0F; 
	private Float deLiveries = 0F;
	private Float acCeptances = 0F; 
	private String  fileName;
	private Developer developer;
	/**
	 * @return the firstAuthority
	 */
	public Float getFirstAuthority() {
		return firstAuthority;
	}
	/**
	 * @param firstAuthority the firstAuthority to set
	 */
	public void setFirstAuthority(Float firstAuthority) {
		this.firstAuthority = firstAuthority;
	}
	/**
	 * @return the deLiveries
	 */
	public Float getDeLiveries() {
		return deLiveries;
	}
	/**
	 * @param deLiveries the deLiveries to set
	 */
	public void setDeLiveries(Float deLiveries) {
		this.deLiveries = deLiveries;
	}
	/**
	 * @return the acCeptances
	 */
	public Float getAcCeptances() {
		return acCeptances;
	}
	/**
	 * @param acCeptances the acCeptances to set
	 */
	public void setAcCeptances(Float acCeptances) {
		this.acCeptances = acCeptances;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the developer
	 */
	public Developer getDeveloper() {
		return developer;
	}
	/**
	 * @param developer the developer to set
	 */
	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acCeptances == null) ? 0 : acCeptances.hashCode());
		result = prime * result + ((deLiveries == null) ? 0 : deLiveries.hashCode());
		result = prime * result + ((developer == null) ? 0 : developer.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((firstAuthority == null) ? 0 : firstAuthority.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DegreeKnowledgeFile other = (DegreeKnowledgeFile) obj;
		if (acCeptances == null) {
			if (other.acCeptances != null)
				return false;
		} else if (!acCeptances.equals(other.acCeptances))
			return false;
		if (deLiveries == null) {
			if (other.deLiveries != null)
				return false;
		} else if (!deLiveries.equals(other.deLiveries))
			return false;
		if (developer == null) {
			if (other.developer != null)
				return false;
		} else if (!developer.equals(other.developer))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (firstAuthority == null) {
			if (other.firstAuthority != null)
				return false;
		} else if (!firstAuthority.equals(other.firstAuthority))
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DegreeKnowledgeFile [firstAuthority=" + firstAuthority + ", deLiveries=" + deLiveries + ", acCeptances="
				+ acCeptances + ", fileName=" + fileName + ", developer=" + developer + "]";
	}

	@Override
	public int compareTo(DegreeKnowledgeFile o) {
		if(equals(o)) {
			return 0;
		}
		return -1;
	}
	
	
}
