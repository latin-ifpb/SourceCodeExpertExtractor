package com.expert.analyze.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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

import com.expert.analyze.util.Constants;
import com.expert.analyze.util.Validador;

public class MeasurePerLine {

	private Repository repository;

	public MeasurePerLine(Repository repository) {
		setRepository(repository);
	}

	public void linesChangeInFile(Git git, List<RevCommit> commits, String fileName, String pathRepository) {
		int currentLines = 0;
		try {
			List<RevCommit> commitsComparer = new ArrayList<>();
			// commitsComparer.addAll(commits.stream().filter(c ->
			// c.getAuthorIdent().getName().equalsIgnoreCase("Jos�
			// Renan")).collect(Collectors.toList()));

			List<String> linesChange = new ArrayList<>();
			for (int i = 0; i < commits.size() - 1; i++) {

				ObjectId commitIDOld = commits.get(i).getId();
				if (Validador.isFileExistInCommit(commits.get(i), repository, fileName)) {
					if (i != commits.size() - 1 && !commitsComparer.contains(commits.get(i))) {
						ObjectId commitIDNew = commits.get(i + 1);
						commitsComparer.add(commits.get(i));
						linesChange.add(diff(git, commitIDOld.getName(), commitIDNew.getName(), fileName));

					}

					try (final FileInputStream input = new FileInputStream(pathRepository + "\\" + fileName)) {
						currentLines = IOUtils.readLines(input, "UTF-8").size();
					}
					// System.out.println("Lines inn Commit:" + lines +"Line now:"+currentLines);
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

	public void linesChangeInFilePerDeveloper(Git git, List<RevCommit> commits, String fileName, String pathRepository,
			Developer developer) {
		List<RevCommit> commitsDeveloper = new ArrayList<>();
		commitsDeveloper.addAll(commits.stream()
				.filter(c -> c.getAuthorIdent().getName().equalsIgnoreCase(developer.getName())
						&& c.getAuthorIdent().getEmailAddress().equalsIgnoreCase(developer.getEmail()))
				.collect(Collectors.toList()));
		linesChangeInFile(git, commitsDeveloper, fileName, pathRepository);
	}

	private String diff(Git git, String commitIDOld, String commitIDNew, String fileName) {
		int linesAdded = 0;
		int linesDeleted = 0;
		DiffFormatter df = null;
		try {
			AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, commitIDOld);
			AbstractTreeIterator newTreeParser = prepareTreeParser(repository, commitIDNew);

			List<DiffEntry> diffs = git.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser)
					.setPathFilter(PathFilter.create(fileName)).call();

			df = new DiffFormatter(DisabledOutputStream.INSTANCE);
			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);

			for (DiffEntry entry : diffs) {
				// System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to:
				// " + entry.getNewId());
				// try (DiffFormatter formatter = new DiffFormatter(System.out)) {
				// formatter.setContext(0);
				// formatter.setRepository(repository);
				// formatter.format(entry);
				// }
				for (Edit edit : df.toFileHeader(entry).toEditList()) {
					linesDeleted += edit.getEndA() - edit.getBeginA();
					linesAdded += edit.getEndB() - edit.getBeginB();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("linesAdded: " + linesAdded + " ld: " + linesDeleted);

		return linesAdded + ";" + linesDeleted;

	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
		// from the commit we can build the tree which allows us to construct the
		// TreeParser noinspection Duplicates
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

	private static int countLinesOfFileInCommit(Repository repository, ObjectId commitID, String name)
			throws IOException {
		try (RevWalk revWalk = new RevWalk(repository)) {
			RevCommit commit = revWalk.parseCommit(commitID);
			RevTree tree = commit.getTree();
			// System.out.println("Having tree: " + tree);

			// now try to find a specific file
			try (TreeWalk treeWalk = new TreeWalk(repository)) {
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create(name));
				if (!treeWalk.next()) {
					throw new IllegalStateException("Did not find expected file 'README.md'");
				}

				ObjectId objectId = treeWalk.getObjectId(0);
				ObjectLoader loader = repository.open(objectId);

				// load the content of the file into a stream
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				loader.copyTo(stream);

				revWalk.dispose();

				return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray()), "UTF-8").size();
			}
		}
	}

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return repository;
	}

	/**
	 * @param repository
	 *            the repository to set
	 */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

}
