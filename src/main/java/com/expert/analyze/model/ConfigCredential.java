package com.expert.analyze.model;

public class ConfigCredential {

	private Boolean cloneRepository;
	private String linkProject;
	private String nameProject;
	private String branch;
	private Boolean authentication;
	private String userName;
	private String userPassword;
	
	public ConfigCredential() {
	}

	/**
	 * @return the cloneRepository
	 */
	public Boolean getCloneRepository() {
		return cloneRepository;
	}

	/**
	 * @param cloneRepository the cloneRepository to set
	 */
	public void setCloneRepository(Boolean cloneRepository) {
		this.cloneRepository = cloneRepository;
	}

	/**
	 * @return the linkProject
	 */
	public String getLinkProject() {
		return linkProject;
	}

	/**
	 * @param linkProject the linkProject to set
	 */
	public void setLinkProject(String linkProject) {
		this.linkProject = linkProject;
	}

	/**
	 * @return the nameProject
	 */
	public String getNameProject() {
		return nameProject;
	}

	/**
	 * @param nameProject the nameProject to set
	 */
	public void setNameProject(String nameProject) {
		this.nameProject = nameProject;
	}

	/**
	 * @return the branch
	 */
	public String getBranch() {
		return branch;
	}

	/**
	 * @param branch the branch to set
	 */
	public void setBranch(String branch) {
		this.branch = branch;
	}

	/**
	 * @return the authentication
	 */
	public Boolean getAuthentication() {
		return authentication;
	}

	/**
	 * @param authentication the authentication to set
	 */
	public void setAuthentication(Boolean authentication) {
		this.authentication = authentication;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * @param userPassword the userPassword to set
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConfigCredential [cloneRepository=" + cloneRepository + ", linkProject=" + linkProject
				+ ", nameProject=" + nameProject + ", branch=" + branch + ", authentication=" + authentication
				+ ", userName=" + userName + ", userPassword=" + userPassword + "]";
	}
	
}
