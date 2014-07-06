/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 2.1 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, 

    mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a 
    
    qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública 
    
    Geral Menor GNU em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob o 

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

/*** Script de atualização da base de dados ***/

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUL2014.1 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('JUL2014.1', true);

-- Inclusão da coluna codigoMonetario na tabela moeda
-- Tarefa #1067
alter table moeda add column codigoMonetario varchar(5) null;

-- Inclusão da coluna numeroCartao na tabela cartaocredito
-- Tarefa #1068
alter table cartaocredito add column numeroCartao varchar(40) null;

-- Limite de lançamentos de conta/cartão exibidos na busca
-- Tarefa #1069
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso, idUsuario, versionEntity)
	select
	'RESUMO_LIMITE_QUANTIDADE_FECHAMENTOS',
	'12',
	'USER',
	true,
	true,
	false,
	'INTEGER',
	'',
	id,
	'2014-06-01 00:00:00.000'
	from
	usuario;

-- Inclusão do campo de moeda para os lançamento importados
alter table lancamentoimportado add column moeda varchar(5) null;

-- Seta todos os lançamentos sem moeda com a moeda da conta
update lancamentoconta l set l.idMoeda = (select moeda.id from moeda inner join conta on conta.idMoeda = moeda.id where conta.id = l.idConta) where l.idMoeda is null;

-- Renomeando o parâmetro de conexão SSL de envio de e-mail
-- Tarefa #1071
update opcaosistema set chave = 'EMAIL_USAR_SSL' where chave = 'EMAIL_SSL_TLS';