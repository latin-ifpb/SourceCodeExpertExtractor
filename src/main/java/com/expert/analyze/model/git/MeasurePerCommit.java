package com.expert.analyze.model.git;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jgit.revwalk.RevCommit;

import com.expert.analyze.model.Developer;
import com.expert.analyze.util.Constants;

/**
 * Class contains away meusase identify in repository the expert only the commits in project
 * @author wemerson
 *
 */
public class MeasurePerCommit extends Measure {
	//Map the  developer and number the commit in repository
	private Map<Developer, Integer> devPerCommits;
	//list the data to export for txt
	private List<String> data = new ArrayList<>();
	//list the data to export for csv
	private List<String> dataCSV = new ArrayList<>();

	/**
	 * method count in list the commits a quantity developer contribuiton in all files
	 * @param commits - List<RevCommit> list the commit in repository
	 * @param developer - Developer espeficy
	 * @return Integer - Quantity the commit the developer
	 */
	public Integer countCommitPerDeveloper(List<RevCommit> commits, Developer developer) {
		Integer countCommit = 0;
		//walks in list the commits
		for (RevCommit c : commits) {
			//compare the commit AuthorIdent equal a developer
			if (c.getAuthorIdent().getName().equalsIgnoreCase(developer.getName())
					&& c.getAuthorIdent().getEmailAddress().equalsIgnoreCase(developer.getEmail())) {
				countCommit++;
			}
		}
		return countCommit;
	}

	/**
	 * method evaluate a percentage the commits in all project per developer
	 * @param totalCommits - total commits in reposotory
	 * @param commitsDeveloper - quantity commits the developer
	 * @return Double - percenta the commits
	 */
	public Double evaluatePercentageCommitsAllProjectPerDeveloper(Integer totalCommits, Integer commitsDeveloper) {
		Double percente = totalCommits != 0 ? ((Constants.PERCENTE_TOTAL * commitsDeveloper) / totalCommits) : 0;
		return percente;
	}

	/**
	 * create a map with developer and quantity the commit in repository.
	 * @param commits - List the commits on project
	 * @param teamDev - team the contrituion 
	 * @return HashMap<Developer, Integer> 
	 */
	public Map<Developer, Integer> developerPerCommits(List<RevCommit> commits, Set<Developer> teamDev) {
		Map<Developer, Integer> commitsPerDeveloper = new HashMap<>();
		for (Developer developer : teamDev) {
			commitsPerDeveloper.put(developer, countCommitPerDeveloper(commits, developer));
		}

		setDevPerCommits(commitsPerDeveloper);
		return commitsPerDeveloper;
	}
	
	/**
	 * Show the evaluate developer per quantity the commit and percentage
	 * @param repository
	 */
	public void showEvaluateDeveloperPerCommit(RepositoryGit repository){
		repository.getTeamDeveloper().iterator().forEachRemaining(dev -> {
			int total = 0;
			total = countCommitPerDeveloper(repository.getCommitsLocal(), dev);
			System.out.println("Developer: " + dev.getName() + " has Commit: " + total + " Percente:"+ evaluatePercentageCommitsAllProjectPerDeveloper(repository.getQuantityCommitLocal(), total));
			//Export Datas
			data.add("Developer: " + dev.getName() + " has Commit: " + total + " Percente:"+ evaluatePercentageCommitsAllProjectPerDeveloper(repository.getQuantityCommitLocal(), total));
			dataCSV.add(dev.getName() + ";" + total + ";"+ evaluatePercentageCommitsAllProjectPerDeveloper(repository.getQuantityCommitLocal(), total));

		});
	}

	/**
	 * Get a Developer with max number the commit
	 * @return Developer 
	 */
	public Developer developerMaxContributionInProject() {
		Developer d = Collections
				.max(getDevPerCommits().entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
		return d;
	}

	/**
	 * Get a number max the commit the developer
	 * @return Integer
	 */
	public Integer commitMaxContributionPerDeveloper() {
		return Collections.max(getDevPerCommits().entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue())
				.getValue();
	}

	/**
	 * Get a Developer with min number the commit
	 * @return Developer
	 */
	public Developer developerMinContributionInProject() {
		Developer d = Collections
				.min(getDevPerCommits().entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
		return d;
	}

	/**
	 * Get a number min the commit 
	 * @return Integer
	 */
	public Integer commitMinContributionPerDeveloper() {
		return Collections.min(getDevPerCommits().entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue())
				.getValue();
	}

	/**
	 * @return the devPerCommits
	 */
	public Map<Developer, Integer> getDevPerCommits() {
		return devPerCommits;
	}

	/**
	 * @param devPerCommits
	 *            the devPerCommits to set
	 */
	public void setDevPerCommits(Map<Developer, Integer> devPerCommits) {
		this.devPerCommits = devPerCommits;
	}

	/**
	 * Show information the map developer  per commit 
	 */
	public void showInformations() {
		for (Map.Entry<Developer, Integer> cd : devPerCommits.entrySet()) {
			System.out.println("Developer: " + cd.getKey().getName() + " Commit: " + cd.getValue());
		}
	}

	/**
	 * @return the data
	 */
	public List<String> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<String> data) {
		this.data = data;
	}

	/**
	 * @return the dataCSV
	 */
	public List<String> getDataCSV() {
		return dataCSV;
	}

	/**
	 * @param dataCSV the dataCSV to set
	 */
	public void setDataCSV(List<String> dataCSV) {
		this.dataCSV = dataCSV;
	}
}
