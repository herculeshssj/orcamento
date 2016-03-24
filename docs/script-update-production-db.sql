/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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
set @ultima_rotina = (select routine_definition from information_schema.routines where routine_name = 'atualiza_base_producao' and routine_schema = 'orcamento');

-- Excluir a rotina caso ela já exista
drop procedure if exists orcamento.atualiza_base_producao;

-- Cria a rotina
delimiter //

create procedure orcamento.atualiza_base_producao()
begin

	declare exit handler for sqlexception
    begin
		rollback;
        signal sqlstate '45000' set message_text = 'Erro durante a atualização da base.';        
    end;

	start transaction;
    
    /*** Entre com as atualizações da base aqui ***/
    
	-- Inclusão de parâmetro para habilitar/desabilitar o controle de estoque
	insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, idUsuario, versionEntity, casoDeUso)
		select
		'CONTROLAR_ESTOQUE_DESPENSA',
		'true',
		'USER',
		true,
		true,
		false,
		'BOOLEAN',
		id,
		'2016-04-01 00:00:00.000',
		'ItemDespensa'
		from
		usuario;
	
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
drop procedure if exists orcamento.verifica_atualizacao_base;

-- Cria a rotina
delimiter //

create procedure orcamento.verifica_atualizacao_base(in ultimaRotina text)
begin
	-- Declaração de variáveis
    declare rotinaAtual text;
    
	-- Obtém o conteúdo da rotina de atualização atual
    set rotinaAtual = (select routine_definition from information_schema.routines where routine_name = 'atualiza_base_producao' and routine_schema = 'orcamento');
    
    -- Verifica se a rotina atual é igual a anterior
    if (rotinaAtual = ultimaRotina) then
		begin
			select 'DB já atualizado.';
		end;
	else
		begin
			call orcamento.atualiza_base_producao();
			select 'DB atualizado.';
		end;
    end if;
    
	
end
//

delimiter ;

/* * * Execução do script * * */
call orcamento.verifica_atualizacao_base(@ultima_rotina);