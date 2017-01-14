/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

/* * * * * ATUALIZAÇÃO AUTOMÁTICA DA BASE * * * * */

/*	

	Este script realizada a atualização automática
    dos dados da base de produção no momento do 
    build do projeto.
    
    Este script está adaptado para rodar no MySQL
    Para rodar em outro SGBD será necessário realizar
    as adaptações necessárias nas routinas descritas
    neste arquivo.

*/

/* * *  Rotina de atualização da base * * */

/* 

	Este rotina contém todas as modificações 
    que devem ser efetuadas na base antes de
    realizar o deploy para o servidor.
    
*/

-- Obtém o conteúdo da rotina de atualização já existente na base
set @ultima_rotina = (select routine_definition from information_schema.routines where routine_name = 'atualiza_base_producao' and routine_schema = 'orcamentotest');

-- Excluir a rotina caso ela já exista
drop procedure if exists orcamentotest.atualiza_base_producao;

-- Cria a rotina
delimiter //

create procedure orcamentotest.atualiza_base_producao()
begin

	declare exit handler for sqlexception
    begin
		rollback;
        signal sqlstate '45000' set message_text = 'Erro durante a atualização da base.';        
    end;

	start transaction;
    
    /*** Entre com as atualizações da base aqui ***/
    
	-- Investimento - Github Issue #237
	create table investimento (
		id bigint not null auto_increment,
		idBanco bigint not null,
		tipoInvestimento varchar(25) not null,
		descricao varchar(100) not null,
		cnpj varchar(14) not null,
		inicioInvestimento date not null,
		terminoInvestimento date null,
		idUsuario bigint not null,
		primary key(id)
	) Engine=InnoDB;
	
	alter table investimento add constraint fk_banco_investimento foreign key(idBanco) references banco(id);
	
	alter table investimento add constraint fk_usuario_investimento foreign key(idUsuario) references usuario(id);
	
	create table resumoinvestimento (
		id bigint not null auto_increment,
		mes int not null,
		ano int not null,
		aplicacao decimal(18,2) not null default 0.00,
		resgate decimal(18,2) not null default 0.00,
		rendimentoBruto decimal(18,2) not null default 0.00,
		impostoRenda decimal(18,2) not null default 0.00,
		iof decimal(18,2) not null default 0.00,
		rendimentoLiquido decimal not null default 0.00,
		primary key(id)	
	) Engine=InnoDB;
	
	create table movimentacaoinvestimento (
		id bigint not null auto_increment,
		tipoLancamento varchar(10),
		data date not null,
		historico varchar(50) not null,
		documento varchar(50) null,
		valor decimal(18,2) not null default 0.00,
		impostoRenda decimal(18,2) not null default 0.00,
		iof decimal(18,2) not null default 0.00,
		compensacaoImpostoRenda decimal(18,2) not null default 0.00,
		cotas decimal(18,6) not null default 0.000000,
		valorCota decimal(18,9) not null default 0.000000000,
		saldoCotas decimal(18,9) not null default 0.000000000,
		primary key (id)
	) Engine=InnoDB;
	
	create table investimento_resumoinvestimento (
		investimento_id bigint not null,
		resumosInvestimento_id bigint not null,
		unique(resumosInvestimento_id)
	) Engine=InnoDB;
	
	alter table investimento_resumoinvestimento add constraint fk_resumoinvestimento_investimento foreign key (investimento_id) references investimento(id);
	alter table investimento_resumoinvestimento add constraint fk_resumoinvestimento foreign key(resumosInvestimento_id) references resumoinvestimento(id);
	
	create table investimento_movimentacaoinvestimento (
		investimento_id bigint not null,
		movimentacoesInvestimento_id bigint not null,
		unique(movimentacoesInvestimento_id)
	) Engine=InnoDB;
	
	alter table investimento_movimentacaoinvestimento add constraint fk_movimentacaoinvestimento_investimento foreign key (investimento_id) references investimento(id);
	alter table investimento_movimentacaoinvestimento add constraint fk_movimentacaoinvestimento foreign key(movimentacoesInvestimento_id) references movimentacaoinvestimento(id);
	
    /*** Fim do bloco de atualizações da base ***/
    
    commit;
    
end
//

delimiter ;

/* * *  Rotina de verificação de atualização da base * * */

/*

	Este rotina contém instruções para verificar se é
    necessário rodar a atualização da base ou não.

*/

-- Excluir a rotina caso ela já exista
drop procedure if exists orcamentotest.verifica_atualizacao_base;

-- Cria a rotina
delimiter //

create procedure orcamentotest.verifica_atualizacao_base(in ultimaRotina text)
begin
	-- Declaração de variáveis
    declare rotinaAtual text;
    
	-- Obtém o conteúdo da rotina de atualização atual
    set rotinaAtual = (select routine_definition from information_schema.routines where routine_name = 'atualiza_base_producao' and routine_schema = 'orcamentotest');
    
    -- Verifica se a rotina atual é igual a anterior
    if (rotinaAtual = ultimaRotina) then
		begin
			select 'DB já atualizado.';
		end;
	else
		begin
			call orcamentotest.atualiza_base_producao();
			select 'DB atualizado.';
		end;
    end if;
    
	
end
//

delimiter ;

/* * * Execução do script * * */
call orcamentotest.verifica_atualizacao_base(@ultima_rotina);