package com.expert.analyze.model.git;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.expert.analyze.model.Developer;
import com.expert.analyze.model.LOCPerFile;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.GitUtil;
import com.expert.analyze.util.Util;
import com.expert.analyze.util.Validador;

public class MeasurePerLine extends Measure {

	private List<LOCPerFile> locPerFiles = new ArrayList<>();
	private Git git;

	public MeasurePerLine(Repository repository,Git git) {
		setGit(git);
		setRepository(repository);
	}

	private String diff(String commitIDOld, String commitIDNew, String fileName) {
		String diffText = "";
		DiffFormatter df = null;
		try {
			AbstractTreeIterator oldTreeParser = GitUtil.prepareTreeParser(getRepository(), commitIDOld);
			AbstractTreeIterator newTreeParser = GitUtil.prepareTreeParser(getRepository(), commitIDNew);

			List<DiffEntry> diffs = git.diff()
										.setOldTree(oldTreeParser)
										.setNewTree(newTreeParser)
										.setContextLines(0)
										.setCached(false)
										.setPathFilter(PathFilter.create(fileName))
										.call();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			df = new DiffFormatter(out);
			df.setContext(0);
			df.setRepository(getRepository());
			df.setDiffComparator(RawTextComparator.DEFAULT);

			for (DiffEntry entry : diffs) {
				// System.out.println(entry.getChangeType());
				df.format(entry);
				entry.getOldId();
				diffText += out.toString("UTF-8");
				
			}

		} catch (IOException | GitAPIException e) {
			System.err.println("Error diff commits:" + e.getMessage());
		}

		return diffText;
	}


	public void resolveCommitsPerDeveloper(Git git, List<RevCommit> commits, String fileName, Developer developer) {
		List<RevCommit> commitDeveloper = commits.stream()
				.filter(commit -> (commit.getAuthorIdent().getEmailAddress().equalsIgnoreCase(developer.getEmail())
						&& Validador.isFileExistInCommit(commit, getRepository(), fileName)))
				.collect(Collectors.toList());
		Util.sortCommits(commitDeveloper);
		RevCommit prRevCommit = null;
		if (commitDeveloper.size() > 0) {
			prRevCommit = Util.getPreviousCommit(commits, commitDeveloper.get(Constants.CONSTANT_ZERO));
		}
		if (prRevCommit != null) {
			commitDeveloper.add(0, prRevCommit);
		}

		linesChangePerFile(git, commitDeveloper, fileName, developer);
	}

	public void linesChangePerFile(Git git, List<RevCommit> commitDeveloper, String fileName, Developer developer) {
		int currentLines = 0;
		try {
			// //Developer dev = new Developer("Daniel","rerissondaniel@gmail.com");

			List<String> linesChange = new ArrayList<>();
			for (RevCommit revCommit : commitDeveloper) {
				RevCommit nextCommit = Util.getNextCommit(commitDeveloper, revCommit);
				ObjectId commitIdOld = revCommit.getId();
				ObjectId commitIdNew = nextCommit != null ? nextCommit.getId() : revCommit.getId();
				String diff = diff(commitIdOld.name(), commitIdNew.name(), fileName);
				if (!diff.equals("")) {
					linesChange.add(linesAdd(diff));
				}
			}

			setLOCChanges(linesChange, developer, commitDeveloper.size() - 1, fileName);
			Collections.sort(locPerFiles);
		} catch (RevisionSyntaxException e) {
			e.printStackTrace();
		}
	}

	public void setLOCChanges(List<String> diffs, Developer dev, Integer qtCommits, String fileName) {
		Integer sumLinesAdd = 0;
		Integer sumLinesDel = 0;
		for (String lineChange : diffs) {
			String[] lChange = lineChange.split(Constants.PROTOCOL);
			sumLinesAdd += Integer.parseInt(lChange[0]);
			sumLinesDel += Integer.parseInt(lChange[1]);
		}
		locPerFiles.add(new LOCPerFile(dev, fileName, qtCommits, sumLinesAdd, sumLinesDel));
	}

	/**
	 * @return the locPerFiles
	 */
	public List<LOCPerFile> getLocPerFiles() {
		return locPerFiles;
	}

	/**
	 * @param locPerFiles
	 *            the locPerFiles to set
	 */
	public void setLocPerFiles(List<LOCPerFile> locPerFiles) {
		this.locPerFiles = locPerFiles;
	}

	private String linesAdd(String diff) {
		Integer linesAdd = 0;
		Integer linesDel = 0;
		String[] lines = diff.split("\n");
		for (int i = Constants.INDEX_DIFF_DESCARTE; i < lines.length; i++) {
			String line = lines[i];
			if (!line.startsWith(Constants.IGNORE_DIFF_INITIAL_CHANGE_PLUS)
					&& !line.startsWith(Constants.IGNORE_DIFF_INITIAL_CHANGE_MINUS)
					&& !line.startsWith(Constants.IGNORE_DIFF_INITIAL_CHANGE_AT)
					&& !line.startsWith(Constants.IGNORE_DIFF_INITIAL_CHANGE_INDEX)) {

				if (line.startsWith("+")) {
					linesAdd++;
				}

				if (line.startsWith("-")) {
					linesDel++;
				}
			}

		}
		return linesAdd + ";" + linesDel;
	}

	/**
	 * @return the git
	 */
	public Git getGit() {
		return git;
	}

	/**
	 * @param git the git to set
	 */
	public void setGit(Git git) {
		this.git = git;
	}
	
	public List<String> printLineChangePerFile(Set<Developer> developers){
		Set<String> data = new TreeSet<>();

		for (LOCPerFile loc : locPerFiles) {
			StringBuilder item = new StringBuilder();			
			List<LOCPerFile> findLOCS = locPerFiles.stream()                // convert list to stream
	                .filter(line -> line.getFileName().equalsIgnoreCase(loc.getFileName()))//when developer equal email his add in list
	                .collect(Collectors.toList());
		
			item.append(loc.getFileName());
			
			findLOCS.forEach(f ->{
				item.append(";");
				item.append((f.getQuantityLOCAdd() +f.getQuantityLOCDel()));
			});
			//System.out.println(item);
//			if(data.contains(item.toString())) {				
				data.add(item.toString());
//			}
		}
		
		StringBuilder sb = new StringBuilder(";");
		developers.forEach(d ->{			
			sb.append(d.getName());
			sb.append(";");
		});
		
		List<String> dataExport = new ArrayList<>();
		dataExport.add(0, sb.toString());
		
		data.forEach(d -> {
			System.out.println(d);
			dataExport.add(d);
		});
		return dataExport;
	}
	
	
}


