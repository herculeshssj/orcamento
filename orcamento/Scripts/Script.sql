alter table cartaocredito add column idCategoria bigint null;
alter table cartaocredito add column idFavorecido bigint null;
alter table cartaocredito add column idMeioPagamento bigint null;

alter table cartaocredito add constraint fk_categoria_cartaocredito foreign key (idCategoria) references categoria (id);
alter table cartaocredito add constraint fk_categoria_favorecido foreign key (idFavorecido) references favorecido (id);
alter table cartaocredito add constraint fk_categoria_meiopagamento foreign key (idMeioPagamento) references meiopagamento (id);


select * from cartaocredito;


alter table lancamentoconta add column saldoFatura boolean default false;

select * from faturacartao;

