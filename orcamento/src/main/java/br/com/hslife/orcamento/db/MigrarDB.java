/***

	Copyright (c) 2012 - 2021 Hércules S. S. José

	Este arquivo é parte do programa Orçamento Doméstico.


	Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

	modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

	publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

	Licença.


	Este programa é distribuído na esperança que possa ser útil, mas SEM

	NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

	MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

	GNU em português para maiores detalhes.


	Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

	o nome de "LICENSE" junto com este programa, se não, acesse o site do

	projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.db;

import org.flywaydb.core.Flyway;

/**
 * Ambiente de migração da base de produção
 * 
 * @author herculeshssj
 *
 */
public class MigrarDB {
	
    public static void main(String[] args) {
    	
    	// Verifica se o args recebeu algum parâmetro para identificar o ambiente
    	// Caso contrário, o ambiente padrão é o de testes
    	if (args != null && args.length > 0) {
    		
    		if (args[0].equals("producao")) {
    			
    			// Cria uma nova instância do Flyway
    	    	Flyway ambienteProducao = new Flyway();
    	    	
    	    	// Seta o datasource
    	    	ambienteProducao.setDataSource("jdbc:mysql://localhost:3306/orcamento", "orcamento", "d1nh31r0");
    	    	
    	    	// A linha abaixo foi usada no primeiro build usando o Flyway. Nos builds seguintes a linha foi comentada
    	    	// Ela serve para definir o estado atual da base como ponto de partida para novas migrações
    	    	ambienteProducao.baseline();
    	    	
    	    	// Verifica se nenhum dos migrations foi alterado acidentalmente
    	    	//ambienteProducao.validate();
    	    	
    	    	// Executa a migração
    	    	ambienteProducao.migrate();
    	    	
    	    	// Imprime o relatório da migração
    	    	ambienteProducao.info();
    			
    		} 
    		
    	} else {
			
			// Cria uma nova instância do Flyway
	    	Flyway ambienteTeste = new Flyway();
	    	
	    	// Seta o datasource
	    	ambienteTeste.setDataSource("jdbc:mysql://localhost:3306/orcamentotest", "orcamento", "d1nh31r0");
	    	
	    	// A linha abaixo foi usada no primeiro build usando o Flyway. Nos builds seguintes a linha foi comentada
	    	// Ela serve para definir o estado atual da base como ponto de partida para novas migrações
	    	ambienteTeste.baseline();
	    	
	    	// Verifica se nenhum dos migrations foi alterado acidentalmente
	    	//ambienteTeste.validate();
	    	
	    	// Executa a migração
	    	ambienteTeste.migrate();
	    	
	    	// Imprime o relatório da migração
	    	ambienteTeste.info();
			 
		}	
    }
}
