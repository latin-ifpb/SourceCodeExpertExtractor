package com.expert.analyze.model.git;

import org.eclipse.jgit.lib.Repository;

public abstract class Measure {
	
	private Repository repository;

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * @param repository the repository to set
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
	
	
}
