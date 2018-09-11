# Expert Analyzer

A ferramenta desenvolvida para auxiliar na identificação dos especialistas de códigos fontes, no qual manipula as informações contidas nos sistemas de controles de 
versões de códigos para sugerir os especialistas, utilizando um conjuntos de métodos e métricas para aferir tal sugestão.

### Prerequisites

Instalar o maven para baixar as dependências;


### Executar a Ferramenta

 Para executar a ferramenta pelo terminal basta passar os seguintes paramêtros: 
```
java -jar ExpertAnalyzer.jar -prop [path_file.properties] -mc -ml -mk --t -ti 00/00/0000 -tf 00/00/0000.
```
## Paramêtros:
 * -prop :O arquivo de configuração para extração do VCS;
 * -[path_file.properties] : O path do arquivo de configuração;
 * -mc : Extração pela métrica de commit por arquivo;
 * -ml : Extração pela métrica de número de linhas modificadas no arquivo;
 * -mk : Extração pela métrica de grau de conhecimento de código no arquivo;
 *--t, -ti, -tf : Permite adição de períodos de extrações;

 * **Arquivo Properties**
	 c= true(Indica a clonagem);*
	 l= Link do repositório;*
	 n= Nome do projeto;*
	 b= branch(especifica uma branch para analisar);
	 a= false(indica a necessidade de autenticação);*
	 u= user;
	 p= password;
	 
	 Obs.: (*) Paramêtros Obrigatórios.

## Versioning

 Beta 1.0.0 
 
## Authors

* **Wemerson T Vital Porto** 

## License

This project is licensed under the copyright License


