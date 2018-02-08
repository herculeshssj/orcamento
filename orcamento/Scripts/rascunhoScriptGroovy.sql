-- Criação das tabelas para armazenar os scripts e seus resultados
create table script (
	id bigint not null auto_increment,
	nome varchar(50) not null,
	descricao varchar(255) not null,
	script text not null,
	idUsuario bigint null,
	notificar boolean not null,
	ativo boolean,
	primary key(id)
) Engine=InnoDB;

alter table script add constraint fk_script_usuario FOREIGN key (idUsuario) references usuario(id);

create table resultadoscript (
	id bigint not null auto_increment,
	idScript bigint not null,
	inicioExecucao DATETIME not null,
	terminoExecucao datetime not null,
	resultado LONGTEXT not null,
	primary key (id)
) Engine=InnoDB;

alter table resultadoscript add constraint fk_resultadoscript_script FOREIGN KEY (idScript) REFERENCES script(id);

alter table script add column notificar boolean not null;
alter table script add column ativo boolean not null;

alter table resultadoscript change column `resultado` `resultado` LONGTEXT null;



select * from resultadoscript where idSCript = 2 and terminoExecucao = (select max(terminoExecucao) from resultadoscript where idscript = 2);

select * from script;


select * from resultadoscript order by terminoExecucao desc;
select count(*) from resultadoscript;