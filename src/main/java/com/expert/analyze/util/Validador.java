package com.expert.analyze.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

/**
 * Class for validade actions 
 * @author wemerson
 *
 */
public class Validador {

	/**
	 * Verify this directory exist in path 
	 * @param file - Directory to create
	 * @return Boolean
	 */
	public static Boolean isDirectoryExist(File file) {
		if (!file.exists()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * Verify this String is empty or null
	 * @param s String
	 * @return Boolean
	 */
	public static Boolean isStringEmpty(String s) {
		if (s != null && s.trim().isEmpty()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static Boolean isFileValid(TreeWalk treeWalk){
		if(!treeWalk.getPathString().contains(Constants.FILES_IGNORE[0])
				&& !treeWalk.getPathString().contains(Constants.FILES_IGNORE[1])
					&& !treeWalk.getPathString().contains(Constants.FILES_IGNORE[2])
						&& !treeWalk.getPathString().contains(Constants.FILES_IGNORE[3])
							&& !treeWalk.getPathString().contains(Constants.FILES_IGNORE[4])
								&& !treeWalk.getPathString().contains(Constants.FILES_IGNORE[5])
									&& !treeWalk.getPathString().contains(Constants.FILES_IGNORE[6])){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public static Boolean isFileExistInCommit(RevCommit commit,Repository repository,String fileFind){
		try {
			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.addTree(commit.getTree());
			treeWalk.setRecursive(true);
			while (treeWalk.next()) {
				if (treeWalk.getPathString().equalsIgnoreCase(fileFind)) {
					return Boolean.TRUE;
				}
			}
			treeWalk.close();
		} catch (MissingObjectException e) {
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			e.printStackTrace();
		} catch (CorruptObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Boolean.FALSE;
	}
}
