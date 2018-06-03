package com.expert.analyze.model.git;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.expert.analyze.model.DegreeKnowledgeFile;
import com.expert.analyze.model.Developer;
import com.expert.analyze.util.Constants;
import com.expert.analyze.util.GitUtil;
import com.expert.analyze.util.Util;
import com.expert.analyze.util.Validador;

public class MeasuarePerDOK  extends Measure{

	private Set<DegreeKnowledgeFile> degreeKnows = new HashSet<>();
	private Git git;
	
	public MeasuarePerDOK(Repository repository,Git git){
		setRepository(repository);
		setGit(git);
	}
	
	public void addDegree(List<RevCommit> commits, Set<Developer> developers){
		//Util.sortCommits(commits);
		Collections.reverse(commits);
		commits.forEach(c->{
			ObjectId commitIdOld = c.getId();
			RevCommit nextCommit = Util.getNextCommit(commits, c);
			ObjectId commitIdNew = nextCommit != null ? nextCommit.getId() : c.getId();
			System.out.println("Author:"+c.getAuthorIdent().getEmailAddress());
			//System.out.println("Author next:"+nextCommit.getAuthorIdent().getEmailAddress());
			fillDOKPerTypeChange(commitIdOld.name(),commitIdNew.name(),getDeveloper(c.getAuthorIdent().getEmailAddress(), developers));
		});
		
		System.out.println("Degree:"+degreeKnows.size());
		degreeKnows.forEach(dk->{
			System.out.println(dk);
		});
	}
	
	private void fillDOKPerTypeChange(String commitIDOld, String commitIDNew,Developer developer){
		try {
			DegreeKnowledgeFile dkf = new DegreeKnowledgeFile();
			dkf.setDeveloper(developer);
			
			AbstractTreeIterator oldTreeParser = GitUtil.prepareTreeParser(getRepository(), commitIDOld);
			AbstractTreeIterator newTreeParser = GitUtil.prepareTreeParser(getRepository(), commitIDNew);
			System.out.println(commitIDOld);
			List<DiffEntry> diffs = getGit().diff()
										.setOldTree(oldTreeParser)
										.setNewTree(newTreeParser)
										.setContextLines(0)
										.setCached(false)
										.call();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DiffFormatter df = new DiffFormatter(out);
			df.setContext(0);
			df.setRepository(getRepository());
			df.setDiffComparator(RawTextComparator.DEFAULT);

			for (DiffEntry entry : diffs) {
//				if(entry.getChangeType().equals(DiffEntry.ChangeType.DELETE)) {
//					System.out.println(entry.getOldPath());
//				}
//				System.out.println(entry.getChangeType());
//				System.out.println(entry.getNewPath());
				switch (entry.getChangeType()) {
					case DELETE:
						// Remover DKF da lista
						break;
					case ADD:
						dkf.setFileName(entry.getNewPath());
						dkf.setFirstAuthority(1F);
						dkf.setAcCeptances(0F);
						dkf.setDeLiveries(0F);
						degreeKnows.add(dkf);
						break;
					case MODIFY:
						verifyIsDKFExist(developer, dkf, entry.getNewPath());
						degreeKnows.add(dkf);
						break;
					case COPY:
						break;
					case RENAME:
						break;
					default:
						break;
				}
			}

		
		} catch (IOException | GitAPIException e) {
			System.err.println("Error diff commits:" + e.getMessage());
		}

	}
	
	private void verifyIsDKFExist(Developer developer, DegreeKnowledgeFile dkf, String newPath) {
		 //verifica-se os desenvolvedores um a um
		DegreeKnowledgeFile dkfNew = degreeKnows.stream().filter(dev -> {
			if (dev.getFileName() != null && dev.getFileName().equalsIgnoreCase(newPath)) {
				return true;
			} else {
				return false;
			}
		}).findAny().orElse(null);
		
		if(dkfNew != null){			
			if(dkf.compareTo(dkfNew) == Constants.CONSTANT_ZERO) {
				//se for o autor adicionar 0.5
				dkf.setDeLiveries(dkf.getDeLiveries() + 0.5F);
			}else {
				//se não for o autor e já tiver modificado a entidade antes, remover 0.1        
				dkf.setAcCeptances(dkf.getAcCeptances()- 0.1F);
			}
		}else {
			dkf.setDeveloper(developer);
			dkf.setFileName(newPath);
			dkf.setDeLiveries(0F);
			dkf.setAcCeptances(0F);
			dkf.setFirstAuthority(1F);
		}
	
	}

	private Developer getDeveloper(String email, Set<Developer> developers) {
		Developer d = developers.stream().filter(dev -> {
			if (dev.getEmail().equalsIgnoreCase(email)) {
				return true;
			} else {
				return false;
			}
		}).findAny().orElse(null);
		return d;
	}
	
	/**
	 * @return the degreeKnows
	 */
	public Set<DegreeKnowledgeFile> getDegreeKnows() {
		return degreeKnows;
	}

	/**
	 * @param degreeKnows the degreeKnows to set
	 */
	public void setDegreeKnows(Set<DegreeKnowledgeFile> degreeKnows) {
		this.degreeKnows = degreeKnows;
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
	
}