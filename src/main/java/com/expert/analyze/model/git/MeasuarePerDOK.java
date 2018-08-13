package com.expert.analyze.model.git;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;

import com.expert.analyze.model.DOI;
import com.expert.analyze.model.Developer;
import com.expert.analyze.model.DeveloperDOA;
import com.expert.analyze.util.GitUtil;
import com.expert.analyze.util.Util;
import com.expert.analyze.util.Validador;

public class MeasuarePerDOK  extends Measure{

	private Map<String,List<DeveloperDOA>> matrixFileDeveloperDOI = new HashMap<String,List<DeveloperDOA>>();
	private List<String> dataExport = new ArrayList<>();
	private Git git;
	
	private double fa = 1.0D;
	private double dl = 0.5D;
	private double ac = 0.1D;
	
	public MeasuarePerDOK(Repository repository,Git git){
		setRepository(repository);
		setGit(git);
	}
	
	public void buildMatrizDegree(List<RevCommit> commits, Set<Developer> developers){
		Collections.reverse(commits);
		List<RevCommit> teste = new ArrayList<>();
		teste.addAll(commits.subList(0, 15));
		commits.forEach(c->{
			ObjectId commitIdOld = c.getId();
			RevCommit nextCommit = Util.getNextCommit(commits, c);
			ObjectId commitIdNew = nextCommit != null ? nextCommit.getId() : c.getId();
			fillDOKPerTypeChange(commitIdOld.name(),commitIdNew.name(),Util.findDeveloperPerEmail(c.getAuthorIdent().getEmailAddress(), developers));
		});
		
		/**Calcular o Degree apartir da matrix.**/	
		calcularDegreeKnowledge(developers,matrixFileDeveloperDOI);
	}
	
	
	private void calcularDegreeKnowledge(Set<Developer> developers, Map<String, List<DeveloperDOA>> matrixFileDeveloperDOI) {

		Map<String,Map<Developer,Double>> degreeResult = new HashMap<>();
		
 		Set<String> chaves = matrixFileDeveloperDOI.keySet();
		for (String chave : chaves){
			if(chave != null) {				
				List<DeveloperDOA> doasItem =  matrixFileDeveloperDOI.get(chave);
				Map<Developer,Double> developerDok = new HashMap<>();
				
				if(doasItem.size() > 0) {
					for (DeveloperDOA developerDOA : doasItem) {
						Double dok = new Double(0F);
						dok = calculateDOK(developerDOA,doasItem);
						developerDok.put(developerDOA.getDeveloper(), dok);
						System.out.println("Degree:"+ dok + " Developer:"+developerDOA.getDeveloper()+ " File:"+chave);
					}
				}
				degreeResult.put(chave,developerDok);
			}
		}
		
		
		printDOK(degreeResult,developers);
	}

	private Double calculateDOK(DeveloperDOA developerDOA, List<DeveloperDOA> doasItem) {
		float dlTotalEntidade = 0F;
		float acTotalEntidade = 0F;
		Double dok = new Double(0F);

		List<DeveloperDOA> result = doasItem.stream()
				.filter(line -> !developerDOA.getDeveloper().getEmail().equals(line.getDeveloper().getEmail()))
				.collect(Collectors.toList());
		if (result != null && result.size() > 0) {
			for (DeveloperDOA dResult : result) {
				dlTotalEntidade += dResult.getDoi().getDeLiveries();
				acTotalEntidade += dResult.getDoi().getAcCeptances();
			}

			dok = developerDOA.getDoi().getFirstAuthority() * fa
					+ (dlTotalEntidade  - developerDOA.getDoi().getDeLiveries()) * dl
					+ (acTotalEntidade  - developerDOA.getDoi().getAcCeptances() ) * ac;
			
		} else {
			dok = 1D;
		}
		return dok;
	}
		
	private void fillDOKPerTypeChange(String commitIDOld, String commitIDNew,Developer developer){
		try {
			
			AbstractTreeIterator oldTreeParser = GitUtil.prepareTreeParser(getRepository(), commitIDOld);
			AbstractTreeIterator newTreeParser = GitUtil.prepareTreeParser(getRepository(), commitIDNew);
			List<DiffEntry> diffs = getGit().diff()
										.setOldTree(oldTreeParser)
										.setNewTree(newTreeParser)
										.setContextLines(0)
										.setCached(false)
										.call();
			for (DiffEntry entry : diffs) {				
				if(Validador.isFileValid(entry.getOldPath())){		
					/**
					 * Adicionar o valor para DL
					 * Pesquisar se já existe na matriz e atualizar
					 * Caso o developer seja outro adicionar
					 * Caso a entidade for removida remorver na matriz
					 * depois de preencher a matriz calcular o DOK para cada entidade baseado
					 * na matriz e salvar na dkf.
					 */
					matrixFileDeveloperDOI.put(entry.getOldPath(), montarDeveloperDOA(entry.getChangeType(), developer,entry.getOldPath()));
				}
			}
		} catch (IOException | GitAPIException e) {
			System.err.println("Error diff commits:" + e.getMessage());
		}
	}
	
	private List<DeveloperDOA> montarDeveloperDOA(ChangeType changeType, Developer developer,String fileName) {
		List<DeveloperDOA> dDoas = new ArrayList<>();
		if(changeType.equals(ChangeType.ADD)) {
				dDoas.add(calculateFI(developer));
		}else if(changeType.equals(ChangeType.MODIFY)){
			dDoas = isExisteKey(fileName);
			if(dDoas.isEmpty()){
				dDoas.add(calculateFI(developer));
			}else {
				
			  DeveloperDOA doa = dDoas.stream().filter(d -> {
					if (d.getDeveloper().getEmail().equals(developer.getEmail())) {
						  Double dl = d.getDoi().getDeLiveries();
						  dl = dl + 0.5F;
						  d.getDoi().setDeLiveries(dl);
						  return true;
					} else {
						return false;
					}
				}).findAny().orElse(null);
			  
			  if(doa == null) {
				  	dDoas.add(calculateAC(developer));  
			  }
			}
			
		}else if(changeType.equals(ChangeType.DELETE)){
			matrixFileDeveloperDOI.entrySet().removeIf(m-> isExisteKey(fileName).size()> 0 );
		}
		System.out.println("File:"+fileName+" DOA:"+dDoas.toString());
		return dDoas;
	}

	private List<DeveloperDOA> isExisteKey(String fileName){
		List<DeveloperDOA> dDoa = new ArrayList<>();
		Set<String> chaves = matrixFileDeveloperDOI.keySet();
		for (String chave : chaves){
			if(chave != null && chave.equalsIgnoreCase(fileName)) {				
				dDoa = matrixFileDeveloperDOI.get(chave);
				break;
			}
		}
		return dDoa;
	}
	
	private DeveloperDOA calculateFI(Developer developer) {
		DeveloperDOA developerDOA = new DeveloperDOA();
		DOI doi = new DOI(1D,0D,0D);
		developerDOA.setDeveloper(developer);
		developerDOA.setIsFistAuthor(true);
		developerDOA.setDoi(doi);
		return developerDOA;
	}
	
	private DeveloperDOA calculateAC(Developer developer) {
		DeveloperDOA developerDOA = new DeveloperDOA();
		DOI doi = new DOI(0D,0.1D,0D);
		developerDOA.setDeveloper(developer);
		developerDOA.setDoi(doi);
		developerDOA.setIsFistAuthor(false);
		return developerDOA;
	}
	
	private void printDOK(Map<String, Map<Developer, Double>> degreeResult, Set<Developer> developers) {
		
			int COUNT = 0;
			List<String> lines = new ArrayList<>();
			List<Developer> developerHEAD = new ArrayList<>();
			DecimalFormat df = new DecimalFormat("00.000");
			
			StringBuilder sb = new StringBuilder();
			sb.append(";");
			
			for (Developer d : developers) {
				developerHEAD.add(d);
				sb.append(d.getName());
				COUNT++;
				if(developers.size() != COUNT){				
					sb.append(";");
				}
			}
			
			
			lines.add(sb.toString());
			System.out.println(lines);
			
			Set<String> keys = degreeResult.keySet();
			for (String key : keys) {
				int tamanhoString = 1 + developerHEAD.size();
				String[] sbArray = new String[tamanhoString];
				sbArray[0] = key+";";

				if(key != null){
					Map<Developer, Double> dd = degreeResult.get(key);
					Set<Developer> keyDev = dd.keySet();
					
					for (Developer developer : keyDev) {
						if(keyDev.size() == developerHEAD.size()) {
							int position = getPositionDeveloperList(developer,developerHEAD);
							for (int i = 0; i <= position ; i++) {
								if(i == position){
									sbArray[position + 1] = df.format(dd.get(developer)) + ";"; 
								}
							}
						}else {
							
							  List<Developer> developerNotDegreeFile =  findOtherDeveloper(developerHEAD,keyDev);
							  int positionDevWithDegree = getPositionDeveloperList(developer,developerHEAD);
							  sbArray[positionDevWithDegree + 1] = df.format(dd.get(developer)) + ";"; 
							  
							  for (Developer dNotDegree : developerNotDegreeFile) {
								  int position = getPositionDeveloperList(dNotDegree,developerHEAD);
								  for (int i = 0; i <= position ; i++) {
										if(i == position){
											sbArray[position + 1] = "0.0;"; 
										}
								  } 
							}
						}
					}
				}

				if(degreeResult.get(key).size() > 0){
					lines.add(convertStringArrayToString(sbArray));
				}else {
					for (int i = 0; i < developerHEAD.size(); i++) {
						sbArray[i + 1] = "0.0;"; 
					}
					lines.add(convertStringArrayToString(sbArray));
				}
				
			}
			
			
			lines.forEach(l ->{
				System.out.println(l);
			});
			
			setDataExport(lines);
			
	}
	
	private List<Developer> findOtherDeveloper(List<Developer> devs, Set<Developer> devKey){
		List<Developer> result = new ArrayList<>();

		devs.forEach(d->{
			if(!devKey.contains(d)) {
				result.add(d);
			}
		});

		return result;
	}

	
	private String convertStringArrayToString(String[] sbArray) {
		List<String> wordList = Arrays.asList(sbArray);
		StringBuilder sb = new StringBuilder();
		wordList.forEach(s ->{
			sb.append(s);
		});
		
		return sb.toString();
	}

	private int getPositionDeveloperList(Developer developer, List<Developer> developerHEAD) {
		return developerHEAD.indexOf(developer);
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

	/**
	 * @return the matrixFileDeveloperDOI
	 */
	public Map<String, List<DeveloperDOA>> getMatrixFileDeveloperDOI() {
		return matrixFileDeveloperDOI;
	}

	/**
	 * @param matrixFileDeveloperDOI the matrixFileDeveloperDOI to set
	 */
	public void setMatrixFileDeveloperDOI(Map<String, List<DeveloperDOA>> matrixFileDeveloperDOI) {
		this.matrixFileDeveloperDOI = matrixFileDeveloperDOI;
	}

	/**
	 * @return the dataExport
	 */
	public List<String> getDataExport() {
		return dataExport;
	}

	/**
	 * @param dataExport the dataExport to set
	 */
	public void setDataExport(List<String> dataExport) {
		this.dataExport = dataExport;
	}
	
}