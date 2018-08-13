package com.expert.analyze.model.git;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
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
				df.format(entry);
				entry.getOldId();
				diffText += out.toString("UTF-8");
			}

		} catch (IOException | GitAPIException e) {
			System.err.println("Error diff commits:" + e.getMessage());
		}

		return diffText;
	}


	public void resolveCommitsPerDeveloper(List<RevCommit> commits, String fileName, Developer developer) {
		//Developer dev = new Developer("Daniel","rerissondaniel@gmail.com");
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
		linesChangePerFile(commitDeveloper, fileName, developer);
	}

	public void linesChangePerFile(List<RevCommit> commitDeveloper, String fileName, Developer developer) {
		try {
			
			Collections.reverse(commitDeveloper);
			
			List<String> linesChange = new ArrayList<>();
			for (RevCommit revCommit : commitDeveloper) {
				RevCommit nextCommit = Util.getNextCommit(commitDeveloper, revCommit);
				ObjectId commitIdOld = revCommit.getId();
				ObjectId commitIdNew = nextCommit != null ? nextCommit.getId() : revCommit.getId();
				String diff = diff(commitIdOld.name(), commitIdNew.name(), fileName);
				if (!diff.equals("")) {
					linesChange.add(linesAdd(diff));
				}
				//System.out.println(diff);
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
			List<LOCPerFile> findLOCS = locPerFiles.stream()               
	                .filter(line -> line.getFileName().equalsIgnoreCase(loc.getFileName()))
	                .collect(Collectors.toList());
		
			item.append(loc.getFileName());
			
			findLOCS.forEach(f ->{
				item.append(";");
				item.append((f.getQuantityLOCAdd() + f.getQuantityLOCDel()));
			});
			
				data.add(item.toString());
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
	
	public void teste(List<RevCommit> commits, String fileName,Set<Developer> developers){
		List<RevCommit> commitFilter = filterCommitContainsFile(commits,fileName);
		int d = 0;
		for (RevCommit revCommit : commitFilter) {
			RevCommit nextCommit = Util.getNextCommit(commitFilter, revCommit);
			ObjectId commitIdOld = revCommit.getId();
			ObjectId commitIdNew = null;

			if(nextCommit != null ) {				
				commitIdNew = nextCommit.getId();
			}else {
				nextCommit = Util.getNextCommit(commits, revCommit);
				if(nextCommit != null && Validador.isFileExistInCommit(nextCommit, getRepository(), fileName)) {					
					commitIdNew = nextCommit.getId();
				}
			}
		
			String diff = diff(commitIdOld.name(), commitIdNew.name(), fileName);
			if(nextCommit != null && diff.length() > 0){
				d++;
				System.out.println("Email: "+revCommit.getAuthorIdent().getEmailAddress());
				if(revCommit.getAuthorIdent().getEmailAddress().equalsIgnoreCase(nextCommit.getAuthorIdent().getEmailAddress())){
					//System.out.println("mesmo author");
				}else {
					//System.out.println("não é mesmo author");
				}
				try {
					System.out.println("Lines: "+countLinesOfFileInCommit(commitIdOld,fileName));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 	
			//System.out.println(diff);
			System.out.println(d);
		}
//		
//		
//		if (!diff.equals("")) {
//			linesChange.add(linesAdd(diff));
		
	}
	
	private int countLinesOfFileInCommit( ObjectId commitID, String name) throws IOException {
		try (RevWalk revWalk = new RevWalk(getRepository())) {
			RevCommit commit = revWalk.parseCommit(commitID);
			RevTree tree = commit.getTree();
			System.out.println("Having tree: " + tree);

			// now try to find a specific file
			try (TreeWalk treeWalk = new TreeWalk(getRepository())) {
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create(name));
//				if (!treeWalk.next()) {
//					throw new IllegalStateException("Did not find expected file 'README.md'");
//				}

				ObjectId objectId = treeWalk.getObjectId(0);
				ObjectLoader loader = getRepository().open(objectId);

				// load the content of the file into a stream
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				loader.copyTo(stream);

				revWalk.dispose();

				return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray()), "UTF-8").size();
			}
		}
	}
	private List<RevCommit> filterCommitContainsFile(List<RevCommit> commits, String fileName){
		
		List<RevCommit> commitFilter = commits.stream()
				.filter(commit -> Validador.isFileExistInCommit(commit, getRepository(), fileName))
				.collect(Collectors.toList());
		
		RevCommit prRevCommit = null;
		if (commitFilter.size() > 0) {
			prRevCommit = Util.getPreviousCommit(commits, commitFilter.get(Constants.CONSTANT_ZERO));
		}
		if (prRevCommit != null) {
			commitFilter.add(0, prRevCommit);
		}
		
		Util.sortCommits(commitFilter);
		Collections.reverse(commitFilter);
		return commitFilter;
	}
	

//	commitFilter.forEach(c ->{
//		LocalDate commitDate = c.getAuthorIdent()
//				.getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		System.out.println(commitDate);
//	});
}


