package com.expert.analyze.model;

public class LOCPerFile implements Comparable<LOCPerFile> {
	
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((developer == null) ? 0 : developer.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((quantityCommit == null) ? 0 : quantityCommit.hashCode());
		result = prime * result + ((quantityLOCAdd == null) ? 0 : quantityLOCAdd.hashCode());
		result = prime * result + ((quantityLOCDel == null) ? 0 : quantityLOCDel.hashCode());
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
		LOCPerFile other = (LOCPerFile) obj;
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
		if (quantityCommit == null) {
			if (other.quantityCommit != null)
				return false;
		} else if (!quantityCommit.equals(other.quantityCommit))
			return false;
		if (quantityLOCAdd == null) {
			if (other.quantityLOCAdd != null)
				return false;
		} else if (!quantityLOCAdd.equals(other.quantityLOCAdd))
			return false;
		if (quantityLOCDel == null) {
			if (other.quantityLOCDel != null)
				return false;
		} else if (!quantityLOCDel.equals(other.quantityLOCDel))
			return false;
		return true;
	}

	@Override
	public int compareTo(LOCPerFile o) {
		 return this.getFileName().compareTo(o.getFileName());
	}
	
}
