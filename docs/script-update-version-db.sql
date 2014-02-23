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

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAR2014 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('MAR2014', true);

-- Informações pessoais do usuário
create table pessoal(
	id bigint not null auto_increment,
	genero char(1) not null default 'M',
	etnia varchar(50) null,
	tipoSanguineo varchar(5) null,
	dataNascimento date null,
	nacionalidade varchar(50) null,
	naturalidade varchar(50) null,
	escolaridade varchar(50) null,
	filiacaoPai varchar(100) null,
	filiacaoMae varchar(100) null,
	estadoCivil varchar(50) null,
	idUsuario bigint not null,
	primary key(id)
) engine=InnoDB; 

alter table pessoal add constraint fk_pessoal_usuario foreign key(idUsuario) references usuario(id);

-- Endereços do usuário
create table endereco(
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	tipoLogradouro varchar(30) not null,
	logradouro varchar(150) not null,
	numero varchar(10) null,
	complemento varchar(50) null,
	bairro varchar(50) not null,
	cidade varchar(100) not null,
	estado varchar(2) not null,
	cep varchar(8) not null,
	idUsuario bigint not null,
	primary key(id)
) engine=InnoDB;

alter table endereco add constraint fk_endereco_usuario foreign key(idUsuario) references usuario(id);

-- Telefones do usuário
create table telefone(
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	ddd varchar(5) null,
	numero varchar(15) not null,
	ramal varchar(5) null,
	idUsuario bigint not null,
	primary key(id)
) engine=InnoDB;

alter table telefone add constraint fk_telefone_usuario foreign key(idUsuario) references usuario(id);

-- Documentos
ALTER TABLE `orcamento`.`documento` DROP FOREIGN KEY `FK383D52B47083BD82` ;
ALTER TABLE `orcamento`.`documento` DROP COLUMN `idUsuario`, DROP INDEX `FK383D52B47083BD82`;

-- Controle de versão das entidades
alter table versao add column versionEntity datetime not null default '2014-01-01 00:00:00';
update versao set versionEntity = '2014-01-01 00:00:00';

alter table usuario add column versionEntity datetime not null default '2014-01-01 00:00:00';
update usuario set versionEntity = '2014-01-01 00:00:00';

alter table unidademedida add column versionEntity datetime not null default '2014-01-01 00:00:00';
update unidademedida set versionEntity = '2014-01-01 00:00:00';

alter table telefone add column versionEntity datetime not null default '2014-01-01 00:00:00';
update telefone set versionEntity = '2014-01-01 00:00:00';

alter table previsaolancamentoconta add column versionEntity datetime not null default '2014-01-01 00:00:00';
update previsaolancamentoconta set versionEntity = '2014-01-01 00:00:00';

alter table pessoal add column versionEntity datetime not null default '2014-01-01 00:00:00';
update pessoal set versionEntity = '2014-01-01 00:00:00';

alter table panoramalancamentocartao add column versionEntity datetime not null default '2014-01-01 00:00:00';
update panoramalancamentocartao set versionEntity = '2014-01-01 00:00:00';

alter table opcaosistema add column versionEntity datetime not null default '2014-01-01 00:00:00';
update opcaosistema set versionEntity = '2014-01-01 00:00:00';

alter table movimentoitemdespensa add column versionEntity datetime not null default '2014-01-01 00:00:00';
update movimentoitemdespensa set versionEntity = '2014-01-01 00:00:00';

alter table moeda add column versionEntity datetime not null default '2014-01-01 00:00:00';
update moeda set versionEntity = '2014-01-01 00:00:00';

alter table meiopagamento add column versionEntity datetime not null default '2014-01-01 00:00:00';
update meiopagamento set versionEntity = '2014-01-01 00:00:00';

alter table lancamentoimportado add column versionEntity datetime not null default '2014-01-01 00:00:00';
update lancamentoimportado set versionEntity = '2014-01-01 00:00:00';

alter table lancamentoconta add column versionEntity datetime not null default '2014-01-01 00:00:00';
update lancamentoconta set versionEntity = '2014-01-01 00:00:00';

alter table itemdespensa add column versionEntity datetime not null default '2014-01-01 00:00:00';
update itemdespensa set versionEntity = '2014-01-01 00:00:00';

alter table identidade add column versionEntity datetime not null default '2014-01-01 00:00:00';
update identidade set versionEntity = '2014-01-01 00:00:00';

alter table fechamentoperiodo add column versionEntity datetime not null default '2014-01-01 00:00:00';
update fechamentoperiodo set versionEntity = '2014-01-01 00:00:00';

alter table favorecido add column versionEntity datetime not null default '2014-01-01 00:00:00';
update favorecido set versionEntity = '2014-01-01 00:00:00';

alter table faturacartao add column versionEntity datetime not null default '2014-01-01 00:00:00';
update faturacartao set versionEntity = '2014-01-01 00:00:00';

alter table endereco add column versionEntity datetime not null default '2014-01-01 00:00:00';
update endereco set versionEntity = '2014-01-01 00:00:00';

alter table documento add column versionEntity datetime not null default '2014-01-01 00:00:00';
update documento set versionEntity = '2014-01-01 00:00:00';

alter table despensa add column versionEntity datetime not null default '2014-01-01 00:00:00';
update despensa set versionEntity = '2014-01-01 00:00:00';

alter table conversaomoeda add column versionEntity datetime not null default '2014-01-01 00:00:00';
update conversaomoeda set versionEntity = '2014-01-01 00:00:00';

alter table conta add column versionEntity datetime not null default '2014-01-01 00:00:00';
update conta set versionEntity = '2014-01-01 00:00:00';

alter table categoriadocumento add column versionEntity datetime not null default '2014-01-01 00:00:00';
update categoriadocumento set versionEntity = '2014-01-01 00:00:00';

alter table categoria add column versionEntity datetime not null default '2014-01-01 00:00:00';
update categoria set versionEntity = '2014-01-01 00:00:00';

alter table cartaocredito add column versionEntity datetime not null default '2014-01-01 00:00:00';
update cartaocredito set versionEntity = '2014-01-01 00:00:00';

alter table buscasalva add column versionEntity datetime not null default '2014-01-01 00:00:00';
update buscasalva set versionEntity = '2014-01-01 00:00:00';

alter table banco add column versionEntity datetime not null default '2014-01-01 00:00:00';
update banco set versionEntity = '2014-01-01 00:00:00';

alter table arquivo add column versionEntity datetime not null default '2014-01-01 00:00:00';
update arquivo set versionEntity = '2014-01-01 00:00:00';

alter table aberturafechamentoconta add column versionEntity datetime not null default '2014-01-01 00:00:00';
update aberturafechamentoconta set versionEntity = '2014-01-01 00:00:00';

alter table auditoria change column `versionEntity` `versionAuditedEntity` datetime not null default '2014-01-01 00:00:00';

-- Previsão dos lançamentos da conta

delete from previsaolancamentoconta where agrupamento = 'FAVORECIDO';
delete from previsaolancamentoconta where agrupamento = 'MEIOPAGAMENTO';
alter table previsaolancamentoconta drop column agrupamento;

-- Agenda
create table agenda(
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	localAgendamento varchar(200) null,
	inicio datetime null,
	fim datetime null,
	tipoAgendamento varchar(15) not null,
	prioridadeTarefa varchar(10) null,
	diaInteiro boolean,
	concluido boolean,
	emitirAlerta boolean,
	notas text,
	idUsuario bigint not null,
	versionEntity datetime not null default '2014-01-01 00:00:00',
	primary key(id)
) engine=InnoDB;

alter table agenda add constraint fk_agenda_usuario foreign key(idUsuario) references usuario(id);

-- Panorama dos lançamentos da conta
ALTER TABLE previsaolancamentoconta RENAME TO panoramalancamentoconta;
ALTER TABLE panoramalancamentoconta CHANGE COLUMN `descricaoPrevisao` `descricao` VARCHAR(255) NOT NULL;
