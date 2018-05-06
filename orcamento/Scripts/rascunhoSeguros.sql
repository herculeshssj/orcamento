show tables;

select * from seguro;

desc seguro;

alter table seguro add column tipoSeguro varchar(15) not null;
alter table seguro add column periodicidadeRenovacao varchar(15) not null;
alter table seguro add column periodicidadePagamento varchar(15) not null;
alter table seguro add column premioSeguro varchar(15) not null;
alter table seguro add column dataRenovacao date not null;
alter table seguro change column `valorCobertura` `valorCobertura` decimal(18,2) null;
alter table seguro add column ativo boolean;
alter table seguro add column idArquivo bigint null;
alter table seguro add column idFavorecido bigint null;
alter table seguro add column idLancamentoPeriodico bigint not null;
alter table seguro add column idMoeda bigint not null;

alter table seguro add constraint fk_favorecido_seguro foreign key (idFavorecido) references favorecido (id);
alter table seguro add constraint fk_arquivo_seguro foreign key (idArquivo) references arquivo (id);
alter table seguro add constraint fk_moeda_seguro foreign key (idMoeda) references moeda (id);
alter table seguro add constraint fk_lancamento_periodico_seguro foreign key (idLancamentoPeriodico) references lancamentoperiodico (id);