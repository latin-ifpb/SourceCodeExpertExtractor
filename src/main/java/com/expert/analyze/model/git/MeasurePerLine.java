package com.expert.analyze.model.git;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import com.expert.analyze.model.Developer;
import com.expert.analyze.model.LOCPerFile;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;

public class MeasurePerLine extends Measure {

	private List<LOCPerFile> locPerFiles = new ArrayList<>();

	public MeasurePerLine(Repository repository) {
		setRepository(repository);
	}

	public void linesChangeInFile(Git git, List<RevCommit> commits, String fileName, String pathRepository) {
		int currentLines = 0;
		try {
			List<RevCommit> commitsComparer = new ArrayList<>();
			List<String> linesChange = new ArrayList<>();

			for (int i = 0; i < commits.size() - 1; i++) {

				ObjectId commitIDOld = commits.get(i).getId();

				if (Validador.isFileExistInCommit(commits.get(i), getRepository(), fileName)) {

					if (i != commits.size() - 1 && !commitsComparer.contains(commits.get(i))) {
						ObjectId commitIDNew = commits.get(i + 1);
						commitsComparer.add(commits.get(i));
						linesChange.add(diff(git, commitIDOld.getName(), commitIDNew.getName(), fileName));
					}

					try (final FileInputStream input = new FileInputStream(pathRepository + "\\" + fileName)) {
						currentLines = IOUtils.readLines(input, "UTF-8").size();
					}
				}

			}

			Integer sumLinesAdd = 0;
			Integer sumLinesDel = 0;
			for (String lineChange : linesChange) {
				String[] lChange = lineChange.split(Constants.PROTOCOL);
				sumLinesAdd += Integer.parseInt(lChange[0]);
				sumLinesDel += Integer.parseInt(lChange[1]);
			}

			try (final FileInputStream input = new FileInputStream(pathRepository + "\\" + fileName)) {
				currentLines = IOUtils.readLines(input, "UTF-8").size();
			}

			System.out.println("Line actual in file:" + currentLines);
			System.out.println("Lines Add total:" + sumLinesAdd);
			System.out.println("Lines Del total:" + sumLinesDel);
			System.out.println("Total lines change:" + (sumLinesAdd + sumLinesDel));

		} catch (RevisionSyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String diff(Git git, String commitIDOld, String commitIDNew, String fileName) {
		int linesAdded = 0;
		int linesDeleted = 0;
		// int linesAtual = 0;
		DiffFormatter df = null;
		try {
			AbstractTreeIterator oldTreeParser = prepareTreeParser(getRepository(), commitIDOld);
			AbstractTreeIterator newTreeParser = prepareTreeParser(getRepository(), commitIDNew);

			List<DiffEntry> diffs = git.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser)
					.setContextLines(0).setCached(false).setPathFilter(PathFilter.create(fileName)).call();

			
			df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(getRepository());
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(false);
//			
//			String OUTPUT_FILE = Constants.PATH_DEFAULT_REPORT+"\\diff"+Constants.TYPE_FILE_TXT;
			for (DiffEntry entry : diffs) {
				if(!entry.getChangeType().equals(DiffEntry.ChangeType.DELETE)) {					
//					try (DiffFormatter formatter = new DiffFormatter(new FileOutputStream(OUTPUT_FILE))) {
//						formatter.setContext(0);
//						formatter.setRepository(getRepository());
//						formatter.format(entry);
//					}
//					
					for (Edit edit : df.toFileHeader(entry).toEditList()) {
						linesDeleted += edit.getEndA() - edit.getBeginA();
						linesAdded += edit.getEndB() - edit.getBeginB();
					}
				}
				
			}
				
		} catch (IOException | GitAPIException e) {
			System.err.println("Error:" + e.getMessage());
		}

		return linesAdded + ";" + linesDeleted;

	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}

			walk.dispose();

			return treeParser;
		}
	}

	public void teste(Git git, List<RevCommit> commits, String fileName, Developer developer) {
		int currentLines = 0;
		try {
			//Developer dev = new Developer("Daniel","rerissondaniel@gmail.com");
			// Filter list commit that developer
			List<RevCommit> commitDeveloper = commits.stream()
					.filter(commit -> (commit.getAuthorIdent().getEmailAddress().equalsIgnoreCase(developer.getEmail()) && Validador.isFileExistInCommit(commit, getRepository(), fileName)))
					.collect(Collectors.toList());

			Collections.sort(commitDeveloper, new Comparator<RevCommit>() {
				public int compare(RevCommit o1, RevCommit o2) {
					return o1.getAuthorIdent().getTimeZoneOffset() - o2.getAuthorIdent().getTimeZoneOffset();
				}
			});
			
			RevCommit prRevCommit = null;
			if(commitDeveloper.size() > 0) {
				 prRevCommit = getPreviousCommit(commits, commitDeveloper.get(Constants.CONSTANT_ZERO));
			}
			if(prRevCommit != null) {				
				commitDeveloper.add(0, prRevCommit);
			}

			List<String> linesChange = new ArrayList<>();	
			for (RevCommit revCommit : commitDeveloper) {
				RevCommit nextCommit = getNextCommit(commitDeveloper, revCommit);
				ObjectId commitIdOld = revCommit.getId();
				LocalDateTime commitDate = revCommit.getAuthorIdent()
						.getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				System.out.println(commitDate);
				ObjectId commitIdNew = nextCommit != null ? nextCommit.getId() : revCommit.getId();
				//System.out.println(diff(git, commitIdOld.name(), commitIdNew.name(), fileName));
				linesChange.add(diff(git, commitIdOld.name(), commitIdNew.name(), fileName));

			}
			setLOCChanges(linesChange, developer, commitDeveloper.size()-1, fileName);
		} catch (RevisionSyntaxException  e) {
			e.printStackTrace();
		}
	}

	public RevCommit getNextCommit(List<RevCommit> commits, RevCommit commitNext) {
		int idx = commits.indexOf(commitNext);
		if (idx < 0 || idx + 1 == commits.size())
			return null;
		return commits.get(idx + 1);
	}
	
	public RevCommit getPreviousCommit(List<RevCommit> commits, RevCommit commitActual){
		int idx = commits.indexOf(commitActual);
		if (idx >= 0 || idx -1 == commits.size())
			return null;
		return commits.get(idx - 1);
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

}
