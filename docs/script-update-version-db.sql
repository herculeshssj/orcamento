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

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAI2014 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('MAI2014', true);

-- Lançamentos periódicos
create table lancamentoperiodico(
	id bigint not null auto_increment,
	dataAquisicao date not null,
	descricao varchar(50) not null,
	observacao text null,
	valorParcela decimal(18,2) default 0.0,
	valorCompra decimal(18,2) null,
	tipoLancamento varchar(10) not null,
	statusLancamento varchar(15) not null,
	idConta bigint not null,
	idCategoria bigint null,
	idFavorecido bigint null,
	idMeioPagamento bigint null,
	idArquivo bigint null,
	idMoeda bigint not null,
	idUsuario bigint not null,
	totalParcela integer null,
	diaVencimento integer not null,
	tipoLancamentoPeriodico varchar(10) not null,
	periodoLancamento varchar(20) null,
	versionEntity datetime not null default '2014-05-01 00:00:00', 
	primary key(id)
);

alter table lancamentoperiodico add constraint fk_lancamentoperiodico_conta foreign key (idConta) references conta(id);
alter table lancamentoperiodico add constraint fk_lancamentoperiodico_categoria foreign key (idCategoria) references categoria(id);
alter table lancamentoperiodico add constraint fk_lancamentoperiodico_favorecido foreign key (idFavorecido) references favorecido(id);
alter table lancamentoperiodico add constraint fk_lancamentoperiodico_meiopagamento foreign key (idMeioPagamento) references meiopagamento(id);
alter table lancamentoperiodico add constraint fk_lancamentoperiodico_arquivo foreign key (idArquivo) references arquivo(id);
alter table lancamentoperiodico add constraint fk_lancamentoperiodico_moeda foreign key (idMoeda) references moeda(id);
alter table lancamentoperiodico add constraint fk_lancamentoperiodico_usuario foreign key (idUsuario) references usuario(id);

create table pagamentoperiodo(
	id bigint not null auto_increment,
	idLancamentoConta bigint null,
	idLancamentoPeriodico bigint not null,
	periodo integer,
	ano integer,
	parcela integer,
	dataPagamento date,
	dataVencimento date not null,
	valorPago decimal(18,2) default 0.0,
	pago boolean,
	versionEntity datetime not null default '2014-05-01 00:00:00', 
	primary key(id)
);

alter table pagamentoperiodo add constraint fk_pagamentoperiodo_lancamentoconta foreign key(idLancamentoConta) references lancamentoconta(id);
alter table pagamentoperiodo add constraint fk_pagamentoperiodo_lancamentoperiodico foreign key(idLancamentoPeriodico) references lancamentoperiodico(id);

-- Remoção da tabela de histórico de abertura e fechamento de contas
-- Tarefa #977
drop table aberturafechamentoconta;

-- Inclusão do browser do usuário no registro de auditoria
-- Tarefa #940
alter table auditoria add column browser varchar(255) not null;

-- Remoção das tabelas de registro de auditoria no modo antigo
-- Tarefa #983
drop table auditoria_auditoriadados;
drop table auditoriadados;

-- Correção de nomenclatura de opção do sistema
-- Tarefa #988
update opcaosistema set chave = 'CONTA_EXIBIR_MEIO_PAGAMENTO' where chave = 'EXIBIR_MEIO_PAGAMENTO';