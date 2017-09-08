/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

/* Rotina de migração dos investimentos - Github Issue orcamento #304 */

-- Popula as categorias de investimento de acordo com o tipo de investimento definido na tabela
insert into categoriainvestimento (descricao, tipoInvestimento, ativo)
	SELECT distinct
		tipoInvestimento,
		'FIXO',
		true
	from 
		investimento;
		
-- Inclusão da referência de categoria de investimentos na tabela investimento
alter table investimento add column idCategoriaInvestimento bigint null;
alter table investimento add constraint fk_investimento_categoriainvestimento FOREIGN KEY (idCategoriaInvestimento) REFERENCES categoriainvestimento(id);
update investimento i set i.idCategoriaInvestimento = (select ci.id from categoriainvestimento ci where ci.descricao = i.tipoInvestimento);
alter table investimento change column `idCategoriaInvestimento` `idCategoriaInvestimento` bigint not null;

-- Altera o tamanho da coluna tipoConta
alter table conta change column `tipoConta` `tipoConta` varchar(15) not null;

-- Cadastra uma conta do tipo investimento para cada banco
insert into conta (descricao, idUsuario, tipoConta, dataAbertura, saldoInicial, saldoFinal, idMoeda, idBanco, ativo)
	select distinct 
	(select concat(b.nome,' [Corretora]')  from banco b where b.id = i.idBanco) as descricao,
	(select b.idUsuario from banco b where b.id = i.idBanco) as idUsuario,
	'INVESTIMENTO',
	NOW(),
	0.0,
	0.0,
	(select m.id from moeda m where m.padrao = true and m.idUsuario = (select b.idUsuario from banco b where b.id = i.idBanco)) as idMoeda,
	i.idBanco,
	true
	FROM
	investimento i;

-- Inclui a coluna conta
alter table investimento add column idConta bigint null;
alter table investimento add constraint fk_investimento_conta FOREIGN KEY(idConta) REFERENCES conta(id);
update investimento i set i.idConta = (select c.id from conta c where c.tipoConta = 'INVESTIMENTO' and c.idBanco = i.idBanco);
alter table investimento change column `idConta` `idConta` bigint not null;

-- Exclui as colunas desnecessárias
alter table investimento drop foreign KEY fk_banco_investimento;
alter table investimento drop foreign KEY fk_usuario_investimento;

alter table investimento drop column tipoInvestimento;
alter table investimento drop column idBanco;
alter table investimento drop column idUsuario;