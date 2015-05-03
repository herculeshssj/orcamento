/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

/*** Script de atualização da base de dados ***/

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUN2015 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('SET2015', true);

-- Correção do valor de conversão das moedas
update moeda set valorConversao = 1.0000 where valorConversao = 0.0000;
alter table moeda change column `valorConversao` `valorConversao` decimal(18,4) not null default 1.0000;

-- Criação da tabela de modelos de documento
create table modelodocumento (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    conteudo text,
    ativo boolean,
    idUsuario bigint not null,
    versionEntity datetime not null default '2015-06-01 00:00:00', 
    primary key(id)
) engine=InnoDB;

alter table modelodocumento add constraint fk_usuario_modelodocumento foreign key (idUsuario) references usuario (id);

-- Dívidas de terceiros - Github Issue #84
create table dividaterceiro(
	id bigint not null auto_increment,
	valorDivida decimal(18,2) not null,
	dataNegociacao date not null,
	justificativa varchar(4000) not null,
	termoDivida text,
	termoQuitacao text,
	statusDivida varchar(15) not null,
	tipoCategoria varchar(10) not null,
	idFavorecido bigint not null,
	idUsuario bigint not null,
    idMoeda bigint not null,
	versionEntity datetime not null default '2015-06-01 00:00:00',
	primary key(id)
) Engine=InnoDB;

alter table dividaterceiro add constraint fk_favorecido_dividaterceiro foreign key(idFavorecido) references favorecido (id);
alter table dividaterceiro add constraint fk_usuario_dividaterceiro foreign key(idUsuario) references usuario (id);
alter table dividaterceiro add constraint fk_moeda_dividaterceiro foreign key(idMoeda) references moeda (id);

create table pagamentodividaterceiro(
	id bigint not null auto_increment,
	valorPago decimal(18,2) not null,
	dataPagamento date not null,
	comprovantePagamento text,
	taxaConversao decimal(18,4) not null default 1.0000,
	idDivida bigint not null,
	versionEntity datetime not null default '2015-06-01 00:00:00',
	primary key(id)
) Engine=InnoDB;

alter table pagamentodividaterceiro add constraint fk_dividaterceiro_pagamentodividaterceiro foreign key(idDivida) references dividaterceiro(id);

-- Corrige a versão
update versao set versao = 'JUN2015', dataLiberacao = '2015-06-28' where versao = 'SET2015';