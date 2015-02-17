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

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAR2015 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('MAR2015', true);

-- Remoção da tabela de junção detalhefatura - Github issue #25
alter table lancamentoconta add column idFaturaCartao bigint null;

update lancamentoconta set idFaturaCartao = (select idFaturaCartao from detalhefatura where idLancamento = id) where id in (select idLancamento from detalhefatura);

drop table detalhefatura;

-- Alteração na nomenclatura de colunas - Github issue #23
alter table agenda change column `idEntity` `entityID` bigint null;
alter table detalheorcamento change column `idEntity` `entityID` bigint not null;

-- Opção para notificação de agendamento por e-mail


-- Inclusão de opção de sistema para o usuário admin
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_METODO_ENVIO', 'JAVAMAIL', 'GLOBAL_ADMIN', true, true, true, 'STRING', 'email');

-- Informações para a implementação do GED - Github issue #30
alter table arquivo add column dataCriacao datetime not null default '2015-01-01 00:00:00.000';
alter table arquivo add column container varchar(30) null;
alter table arquivo add column idUsuario bigint null;

update arquivo set container = 'DOCUMENTOS' where id in (select idArquivo from documento);
update arquivo set container = 'LANCAMENTOCONTA' where id in (select idArquivo from lancamentoconta);
update arquivo set container = 'LANCAMENTOPERIODICO' where id in (select idArquivo from lancamentoperiodico);
update arquivo set container = 'FATURACARTAO' where id in (select idArquivo from faturacartao);
update arquivo set container = 'ARQUIVO' where container is null;

update arquivo a set a.idUsuario = (select cd.idUsuario from categoriadocumento cd inner join documento d on d.idCategoriaDocumento = cd.id where d.idArquivo = a.id) where a.container = 'DOCUMENTOS';
update arquivo a set a.idUsuario = (select c.idUsuario from lancamentoconta l inner join conta c on c.id = l.idConta where l.idArquivo = a.id) where a.container = 'LANCAMENTOCONTA';
update arquivo a set a.idUsuario = (select l.idUsuario from lancamentoperiodico l where l.idArquivo = a.id) where container = 'LANCAMENTOPERIODICO';
update arquivo a set a.idUsuario = (select c.idUsuario from faturacartao f inner join conta c on c.id = f.idConta where f.idArquivo = a.id) where container = 'FATURACARTAO';
update arquivo set idUsuario = (select id from usuario where login = 'admin') where container = 'ARQUIVO';

update arquivo a set a.dataCriacao = (select d.versionEntity from documento d where d.idArquivo = a.id) where a.container = 'DOCUMENTOS';
update arquivo a set a.dataCriacao = (select l.dataPagamento from lancamentoconta l where l.idArquivo = a.id) where a.container = 'LANCAMENTOCONTA';
update arquivo a set a.dataCriacao = (select l.dataAquisicao from lancamentoperiodico l where l.idArquivo = a.id) where container = 'LANCAMENTOPERIODICO';
update arquivo a set a.dataCriacao = (select f.dataVencimento from faturacartao f where f.idArquivo = a.id) where container = 'FATURACARTAO';
update arquivo a set a.dataCriacao = (select u.dataCriacao from usuario u where u.login = 'admin') where container = 'ARQUIVO';

alter table arquivo change column `container` `container` varchar(30) not null;
alter table arquivo change column `idUsuario` `idUsuario` bigint not null;

-- Opção do sistema para definir o tempo de guarda dos anexo - Github #30
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso, idUsuario, versionEntity)
	select
	'ARQUIVO_TEMPO_GUARDA_GERAL',
	'1',
	'USER',
	true,
	true,
	false,
	'INTEGER',
	'Arquivo',
	id,
	'2015-02-01 00:00:00.000'
	from
	usuario;
	
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso, idUsuario, versionEntity)
	select
	'ARQUIVO_TEMPO_GUARDA_LANCAMENTOCONTA',
	'1',
	'USER',
	true,
	true,
	false,
	'INTEGER',
	'Arquivo',
	id,
	'2015-02-01 00:00:00.000'
	from
	usuario;
	
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso, idUsuario, versionEntity)
	select
	'ARQUIVO_TEMPO_GUARDA_FATURACARTAO',
	'1',
	'USER',
	true,
	true,
	false,
	'INTEGER',
	'Arquivo',
	id,
	'2015-02-01 00:00:00.000'
	from
	usuario;
	
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso, idUsuario, versionEntity)
	select
	'ARQUIVO_TEMPO_GUARDA_LANCAMENTOPERIODICO',
	'1',
	'USER',
	true,
	true,
	false,
	'INTEGER',
	'Arquivo',
	id,
	'2015-02-01 00:00:00.000'
	from
	usuario;
	
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso, idUsuario, versionEntity)
	select
	'ARQUIVO_TEMPO_GUARDA_DOCUMENTOS',
	'1',
	'USER',
	true,
	true,
	false,
	'INTEGER',
	'Arquivo',
	id,
	'2015-02-01 00:00:00.000'
	from
	usuario;
	
-- Inclusão de marca favorita no item de despensa
alter table itemdespensa add column marca varchar(50) null;

-- Regras de importação
create table regraimportacao (
	id bigint not null auto_increment,
	texto varchar(100) not null,
	idCategoria bigint null,
	idFavorecido bigint null,
	idMeioPagamento bigint null,
	idConta bigint not null,
	versionEntity datetime not null default '2015-04-01 00:00:00',
	primary key (id)
) Engine=InnoDB;

alter table regraimportacao add constraint fk_regraimportacao_conta foreign key(idConta) references conta(id);