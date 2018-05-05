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

public class MeasurePerCommit extends Measure {

	private Map<Developer, Integer> devPerCommits;
	private List<String> data = new ArrayList<>();
	private List<String> dataCSV = new ArrayList<>();

	public Integer countCommitPerDeveloper(List<RevCommit> commits, Developer developer) {
		Integer countCommit = 0;
		for (RevCommit c : commits) {
			if (c.getAuthorIdent().getName().equalsIgnoreCase(developer.getName())
					&& c.getAuthorIdent().getEmailAddress().equalsIgnoreCase(developer.getEmail())) {
				countCommit++;
			}
		}
		return countCommit;
	}

	public Double evaluatePercentageCommitsAllProjectPerDeveloper(Integer totalCommits, Integer commitsDeveloper) {
		Double percente = totalCommits != 0 ? ((Constants.PERCENTE_TOTAL * commitsDeveloper) / totalCommits) : 0;
		return percente;
	}

	public Map<Developer, Integer> developerPerCommits(List<RevCommit> commits, Set<Developer> teamDev) {
		Map<Developer, Integer> commitsPerDeveloper = new HashMap<>();
		for (Developer developer : teamDev) {
			commitsPerDeveloper.put(developer, countCommitPerDeveloper(commits, developer));
		}

		setDevPerCommits(commitsPerDeveloper);
		return commitsPerDeveloper;
	}
	
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

	public Developer developerMaxContributionInProject() {
		Developer d = Collections
				.max(getDevPerCommits().entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
		return d;
	}

	public Integer commitMaxContributionPerDeveloper() {
		return Collections.max(getDevPerCommits().entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue())
				.getValue();
	}

	public Developer developerMinContributionInProject() {
		Developer d = Collections
				.min(getDevPerCommits().entrySet(), (entry1, entry2) -> entry1.getValue() - entry2.getValue()).getKey();
		return d;
	}

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
