select * from schema_version;

delete from schema_version;

select * from investimento;

alter table investimento add column idAdministradorInvestimento bigint null;

alter table investimento add constraint fk_administradorinvestimento_investimento foreign key(idAdministradorInvestimento) references administradorinvestimento(id);