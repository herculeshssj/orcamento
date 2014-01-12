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