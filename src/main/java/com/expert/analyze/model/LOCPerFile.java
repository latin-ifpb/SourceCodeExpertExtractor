package com.expert.analyze.model;

public class LOCPerFile {
	private String fileName;
	private Integer quantityCommit;
	private Integer quantityLOCAdd;
	private Integer quantityLOCDel;
	private Developer developer;
	
	public LOCPerFile() {
	}
	
	public LOCPerFile(Developer developer,String file, Integer qtCommit, Integer qtLOCAdd, Integer qtLOCDel) {
		setDeveloper(developer);
		setFileName(file);
		setQuantityCommit(qtCommit);
		setQuantityLOCAdd(qtLOCAdd);
		setQuantityLOCDel(qtLOCDel);
	}

	/**
	 * @return the quantityCommit
	 */
	public Integer getQuantityCommit() {
		return quantityCommit;
	}
	/**
	 * @param quantityCommit the quantityCommit to set
	 */
	public void setQuantityCommit(Integer quantityCommit) {
		this.quantityCommit = quantityCommit;
	}
	/**
	 * @return the quantityLOCAdd
	 */
	public Integer getQuantityLOCAdd() {
		return quantityLOCAdd;
	}
	/**
	 * @param quantityLOCAdd the quantityLOCAdd to set
	 */
	public void setQuantityLOCAdd(Integer quantityLOCAdd) {
		this.quantityLOCAdd = quantityLOCAdd;
	}
	/**
	 * @return the quantityLOCDel
	 */
	public Integer getQuantityLOCDel() {
		return quantityLOCDel;
	}
	/**
	 * @param quantityLOCDel the quantityLOCDel to set
	 */
	public void setQuantityLOCDel(Integer quantityLOCDel) {
		this.quantityLOCDel = quantityLOCDel;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LOCPerFile [fileName=" + fileName + ", quantityCommit=" + quantityCommit + ", quantityLOCAdd="
				+ quantityLOCAdd + ", quantityLOCDel=" + quantityLOCDel + ", developer=" + developer + "]";
	}
	
}
