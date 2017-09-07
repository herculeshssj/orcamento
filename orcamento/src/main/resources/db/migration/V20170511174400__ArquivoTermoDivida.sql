/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da 

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

-- Inclusão de arquivo para o termo de dívida - Github Issue #3
alter table dividaterceiro add column idArquivoTermoDivida bigint null;
alter table dividaterceiro add constraint fk_dividaterceiro_arquivo foreign key (idArquivoTermoDivida) references arquivo (id);

-- Inclusão da coluna 'attribute' para setar o nome do atributo
-- da classe que o arquivo está vinculado - Github Issue #12
alter table arquivo add column attribute varchar(50) not null default 'arquivo';

-- Inclusão de tempo de guarda dos anexos de dívida de terceiros - Github Issue #12
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso, idUsuario)
	select
	'ARQUIVO_TEMPO_GUARDA_DIVIDATERCEIRO',
	'1',
	'USER',
	true,
	true,
	false,
	'INTEGER',
	'Arquivo',
	id
	from
	usuario;
    
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso, idUsuario)
	select
	'ARQUIVO_TEMPO_GUARDA_PAGAMENTODIVIDATERCEIRO',
	'1',
	'USER',
	true,
	true,
	false,
	'INTEGER',
	'Arquivo',
	id
	from
	usuario;