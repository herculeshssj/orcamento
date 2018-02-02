select * from logrequisicao;

desc logrequisicao;

desc auditoria;

select * from auditoria order by id desc limit 100;
select * from logrequisicao order by dataHora desc limit 100;

SELECT * FROM usuario;


/***** SQL definitivo da atualização das bibliotecas *****/

-- Inclusão de browser e IP nos registros de auditoria
alter table auditoria add column browser varchar(255) not null;
alter table auditoria add column ip varchar(255) not null;