package com.expert.analyze.model;

public class DOI {

	private Double firstAuthority = 0D; 
	private Double deLiveries = 0D;
	private Double acCeptances = 0D;

	public DOI() {
	}
	
	public DOI(Double fa, Double dL, Double ac){
		setAcCeptances(ac);
		setDeLiveries(dL);
		setFirstAuthority(fa);
	}

	/**
	 * @return the firstAuthority
	 */
	public Double getFirstAuthority() {
		return firstAuthority;
	}

	/**
	 * @param firstAuthority the firstAuthority to set
	 */
	public void setFirstAuthority(Double firstAuthority) {
		this.firstAuthority = firstAuthority;
	}

	/**
	 * @return the deLiveries
	 */
	public Double getDeLiveries() {
		return deLiveries;
	}

	/**
	 * @param deLiveries the deLiveries to set
	 */
	public void setDeLiveries(Double deLiveries) {
		this.deLiveries = deLiveries;
	}

	/**
	 * @return the acCeptances
	 */
	public Double getAcCeptances() {
		return acCeptances;
	}

	/**
	 * @param acCeptances the acCeptances to set
	 */
	public void setAcCeptances(Double acCeptances) {
		this.acCeptances = acCeptances;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DOI [firstAuthority=" + firstAuthority + ", deLiveries=" + deLiveries + ", acCeptances=" + acCeptances
				+ "]";
	}
	
}
