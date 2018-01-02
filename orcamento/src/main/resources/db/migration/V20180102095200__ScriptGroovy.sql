/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

-- Criação das tabelas para armazenar os scripts e seus resultados - Github Issue #315
create table script (
	id bigint not null auto_increment,
	nome varchar(50) not null,
	descricao varchar(255) not null,
	script text not null,
	notificar boolean,
	ativo boolean,
	idUsuario bigint null,
	primary key(id)
) Engine=InnoDB;

alter table script add constraint fk_script_usuario FOREIGN key (idUsuario) references usuario(id);

create table resultadoscript (
	id bigint not null auto_increment,
	idScript bigint not null,
	inicioExecucao DATETIME not null,
	terminoExecucao datetime not null,
	resultado LONGTEXT null,
	primary key (id)
) Engine=InnoDB;

alter table resultadoscript add constraint fk_resultadoscript_script FOREIGN KEY (idScript) REFERENCES script(id);