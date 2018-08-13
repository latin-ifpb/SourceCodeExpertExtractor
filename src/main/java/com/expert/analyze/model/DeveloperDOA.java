package com.expert.analyze.model;

public class DeveloperDOA {
	
	private Developer developer;
	private Boolean isFistAuthor;
	private DOI doi;
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
	/**
	 * @return the doi
	 */
	public DOI getDoi() {
		return doi;
	}
	/**
	 * @param doi the doi to set
	 */
	public void setDoi(DOI doi) {
		this.doi = doi;
	}
	/**
	 * @return the isFistAuthor
	 */
	public Boolean getIsFistAuthor() {
		return isFistAuthor;
	}
	/**
	 * @param isFistAuthor the isFistAuthor to set
	 */
	public void setIsFistAuthor(Boolean isFistAuthor) {
		this.isFistAuthor = isFistAuthor;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeveloperDOA [developer=" + developer + ", isFistAuthor=" + isFistAuthor + ", doi=" + doi + "]";
	}
}
