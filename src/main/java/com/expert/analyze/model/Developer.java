package com.expert.analyze.model;

import java.util.List;

public class Developer {

	private List<String> nicksNames;
	private String name;
	private String email;

	public Developer() {
	}
	
	public Developer(String name, String email) {
		setEmail(email);
		setName(name);
	}
	public Developer(String name, String email,List<String> nicks) {
		setEmail(email);
		setName(name);
		setNicksNames(nicks);
	}

	/**
	 * @return the nicksNames
	 */
	public List<String> getNicksNames() {
		return nicksNames;
	}

	/**
	 * @param nicksNames the nicksNames to set
	 */
	public void setNicksNames(List<String> nicksNames) {
		this.nicksNames = nicksNames;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param nome the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Developer [nicksNames=" + nicksNames + ", name=" + name + ", email=" + email + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Developer other = (Developer) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
