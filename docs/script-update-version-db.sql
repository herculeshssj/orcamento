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

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO DEZ2014 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('DEZ2014', true);

-- Tabelas orcamento e detalheorcamento - Tarefa #969
create table orcamento(
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	tipoOrcamento varchar(15) not null,
	tipoConta varchar(10) null,
	inicio date not null,
	fim date not null,
	periodoLancamento varchar(15) not null,
	automatico boolean,
	arquivar boolean,
	ativo boolean,
	abrangenciaOrcamento varchar(15) not null,
	idConta bigint null,
	idUsuario bigint not null,
	versionEntity datetime not null default '2014-11-01 00:00:00',
	primary key(id)
) engine=InnoDB; 

alter table orcamento add constraint fk_conta_orcamento foreign key (idConta) references conta(id);
alter table orcamento add constraint fk_usuario_orcamento foreign key (idUsuario) references usuario(id);

create table detalheorcamento (
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	idEntity bigint not null,
	tipoCategoria varchar(10) null,
	previsao decimal (18,2) default 0.0,
	previsaoCredito decimal (18,2) default 0.0,
	previsaoDebito decimal (18,2) default 0.0,
	realizado decimal (18,2) default 0.0,
	realizadoCredito decimal (18,2) default 0.0,
	realizadoDebito decimal (18,2) default 0.0,
	versionEntity datetime not null default '2014-11-01 00:00:00', 
	primary key(id)
);

create table orcamento_detalheorcamento (
	orcamento_id bigint not null,
	detalhes_id bigint not null,
	unique(detalhes_id)
) engine=InnoDB;

alter table orcamento_detalheorcamento add constraint fk_orcamento foreign key (orcamento_id) references orcamento(id);
alter table orcamento_detalheorcamento add constraint fk_detalheorcamento foreign key (detalhes_id) references detalheorcamento(id);

-- Inclusão da coluna de statusLancamento - Tarefa #1023
alter table lancamentoconta add column statusLancamentoConta varchar(15) null;
update lancamentoconta set statusLancamentoConta = 'AGENDADO' where agendado = true;
update lancamentoconta set statusLancamentoConta = 'QUITADO' where quitado = true;
update lancamentoconta set statusLancamentoConta = 'REGISTRADO' where statusLancamentoConta is null;
alter table lancamentoconta change column `statusLancamentoConta` `statusLancamentoConta` varchar(15) not null;