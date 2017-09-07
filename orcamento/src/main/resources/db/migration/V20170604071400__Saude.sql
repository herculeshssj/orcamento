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

create table saude (
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	sintomas text not null,
	medico varchar(100) null,
	especialidade varchar(50) null,
	contatoMedico text null,
	ativo boolean,
	idUsuario bigint not null,
	idCategoriaDocumento bigint null,
	primary key(id)
) Engine=InnoDB;

alter table saude add constraint fk_saude_usuario foreign key (idUsuario) references usuario(id);
alter table saude add constraint fk_saude_categoriadocumento foreign key (idCategoriaDocumento) references categoriadocumento(id);

create table historicosaude (
	id bigint not null auto_increment,
	dataConsulta date not null,
	quadroClinico text not null,
	diagnostico text not null,
	tratamento text not null,
	idSaude bigint not null,
	primary key(id)
) Engine=InnoDB;

alter table historicosaude add constraint fk_historicosaude_saude foreign key(idSaude) references saude(id);

create table tratamentosaude (
	id bigint not null auto_increment,
	descricao varchar(100) not null,
	tipoTratamento varchar(15) not null,
	periodicidade varchar(50) null,
	observacao text null,
	idSaude bigint not null,
	primary key(id)
) Engine=InnoDB;

alter table tratamentosaude add constraint fk_tratamento_saude_saude foreign key (idSaude) references saude(id);