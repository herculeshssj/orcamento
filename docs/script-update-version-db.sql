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

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO SET2015 ***/

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