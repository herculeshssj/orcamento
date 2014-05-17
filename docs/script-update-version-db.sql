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

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUL2014 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('JUL2014', true);

alter table conta drop column arquivado;

-- Recuperando as categorias, favorecidos e meios de pagamento dos lançamentos arquivados
update lancamentoconta l set idMeioPagamento = (select id from meiopagamento where descricao = l.descricaoMeioPagamento) 
	where l.descricaoMeioPagamento is not null and l.idMeioPagamento is null;
update lancamentoconta l set idFavorecido = (select id from favorecido where nome = l.descricaoFavorecido) 
	where l.descricaoFavorecido is not null and l.idFavorecido is null;
update lancamentoconta l set idCategoria = (select id from categoria where descricao = l.descricaoCategoria) 
	where l.descricaoCategoria is not null and l.idCategoria is null;
	
alter table lancamentoconta drop column descricaoMeioPagamento;
alter table lancamentoconta drop column descricaoFavorecido;
alter table lancamentoconta drop column descricaoCategoria;

-- Inclusão do campo moeda
alter table conta add column idMoeda bigint not null;
update conta c set idMoeda = (select id from moeda where idUsuario = c.idUsuario and padrao = true);
alter table conta add constraint fk_conta_moeda foreign key (idMoeda) references moeda(id);

-- Remoção dos campos sensíveis do cadastro de cartão de crédito
-- Tarefa #1003
alter table cartaocredito drop column codigoSeguranca;
alter table cartaocredito drop column nomeCliente;
alter table cartaocredito drop column numeroCartao;

-- Remoção da coluna dataLancamento na tabela lancamentoconta
update lancamentoconta set dataPagamento = dataLancamento where idConta in (select id from conta where tipoConta = 'CARTAO');
alter table lancamentoconta drop column dataLancamento;

-- Remoção da coluna lancadoEm na tabela buscasalva
update buscasalva set dataInicio = lancadoEm, dataFim = lancadoEm where idConta in (select id from conta where tipoConta = 'CARTAO');
alter table buscasalva drop column lancadoEm;