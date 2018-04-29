select * from dividaterceiro;
select * from pagamentodividaterceiro;

-- alterações na base - 1º etapa

alter table dividaterceiro add column tipoDivida varchar(10) null;

update dividaterceiro set tipoDivida = 'TERCEIROS';

alter table dividaterceiro change column `tipoDivida` `tipoDivida` varchar(10) not null;

alter table dividaterceiro add column emprestimo boolean default false;
alter table dividaterceiro add column quantParcelas integer default 0;
alter table dividaterceiro add column taxaJuros decimal(18,2) default 0.00;
alter table dividaterceiro add column valorParcela decimal(18,2) default 0.00;