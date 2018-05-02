package com.expert.analyze.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

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

import com.expert.analyze.util.Validador;

public class MeasurePerLine {

	private Repository repository;
	
	public MeasurePerLine(Repository repository) {
		setRepository(repository);
	}
	
	public void linesChange(){
		int linesAdded = 0;
		int linesDeleted = 0;
		int filesChanged = 0;
		try {
		   
		    RevWalk rw = new RevWalk(repository);
		    RevCommit commit = rw.parseCommit(repository.resolve("HEAD~~")); // Any ref will work here (HEAD, a sha1, tag, branch)
		    RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
		    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		    df.setRepository(repository);
		    df.setDiffComparator(RawTextComparator.DEFAULT);
		    df.setDetectRenames(true);
		    List<DiffEntry> diffs;
		    diffs = df.scan(parent.getTree(), commit.getTree());
		    filesChanged = diffs.size();
		    //TODO: Utilizar essa solução para recuperar a direfença de lihas no diff
		    for (DiffEntry diff : diffs) {
		        for (Edit edit : df.toFileHeader(diff).toEditList()) {
		            linesDeleted += edit.getEndA() - edit.getBeginA();
		            linesAdded += edit.getEndB() - edit.getBeginB();
		        }
		    }
		} catch (IOException e1) {
		    throw new RuntimeException(e1);
		}
		
		System.out.println("linesAdded: "+linesAdded+" ld:"+linesDeleted + " fC:"+filesChanged);
	}
	
	public void teste(List<RevCommit> commits, String fileName, String pathRepository){
			try {
				BlameCommand blamer = new BlameCommand(repository);
			
				for (RevCommit revCommit : commits) {

						ObjectId commitID = revCommit.getId();
					    blamer.setStartCommit(commitID);
				        blamer.setFilePath(fileName);
				        
				        BlameResult blame = blamer.call();

				        if(Validador.isFileExistInCommit(revCommit, repository, fileName)) {				   
					        // read the number of lines from the given revision, this excludes changes from the last two commits due to the "~~" above
					        int lines = countLinesOfFileInCommit(repository, commitID,fileName);
//					        for (int i = 0; i < lines; i++) {
//				                RevCommit commit = blame.getSourceCommit(i);
//				                System.out.println("Line: " + i + ": " + commit);
//				            }
					        
					        final int currentLines;
				            try (final FileInputStream input = new FileInputStream(pathRepository+"\\README.md")) {
				                currentLines = IOUtils.readLines(input, "UTF-8").size();
				            }


					        System.out.println("Displayed commits: "+ revCommit.getId() +"has number lines "+lines+" responsible for " + revCommit.getAuthorIdent().getName() + " new Lines: "+ currentLines);
				        }
				}
				
			} catch (RevisionSyntaxException | IOException | GitAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	}

	 private static int countLinesOfFileInCommit(Repository repository, ObjectId commitID, String name) throws IOException {
	        try (RevWalk revWalk = new RevWalk(repository)) {
	            RevCommit commit = revWalk.parseCommit(commitID);
	            RevTree tree = commit.getTree();
	            System.out.println("Having tree: " + tree);

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
	 
	 public  void diff(Git git, String commitIDOld,String commitIDNew,  String fileName){
		 
		try {
			AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, commitIDOld);
			AbstractTreeIterator newTreeParser = prepareTreeParser(repository, commitIDNew);
			 List<DiffEntry> diff = git.diff().
	                 setOldTree(oldTreeParser).
	                 setNewTree(newTreeParser).
	                 setPathFilter(PathFilter.create(fileName)).
	                 // to filter on Suffix use the following instead
	                 //setPathFilter(PathSuffixFilter.create(".java")).
	                 call();
	         for (DiffEntry entry : diff) {
	             System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
	             try (DiffFormatter formatter = new DiffFormatter(System.out)) {
	                 formatter.setRepository(repository);
	                 formatter.format(entry);
	             }
	         }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	 }
	 
	 private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
	        // from the commit we can build the tree which allows us to construct the TreeParser
	        //noinspection Duplicates
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
