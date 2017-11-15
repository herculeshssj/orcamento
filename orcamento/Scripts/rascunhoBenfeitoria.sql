select * from benfeitoria;

fk_grupolancamento_patrimonio

alter table patrimonio drop FOREIGN KEY fk_grupolancamento_patrimonio;
alter table patrimonio drop column idGrupoLancamento;


select * from patrimonio