show tables;

desc lancamentoconta;

select * from lancamentoconta order by id desc;
select * from documento order by id desc;
select * from lancamentoperiodico order by id desc;

select * from faturacartao order by id desc;
select * from arquivo order by id desc;

select * from dividaterceiro;
select * from pagamentodividaterceiro;

-- arquivos órfãos
select * from arquivo where id not in (
	select idArquivo from lancamentoconta where idArquivo is not null
	union
	select idArquivo from lancamentoperiodico where idArquivo is not null
	union
	select idArquivo from faturacartao where idArquivo is not null
	union
	select idArquivo from documento where idArquivo is not null
	union
	select idArquivoTermoDivida from dividaterceiro where idArquivoTermoDivida is not null
	union 
	select idArquivoTermoQuitacao from dividaterceiro where idArquivoTermoQuitacao is not NULL
	union 
	select idArquivoComprovante from pagamentodividaterceiro where idArquivoComprovante is not null
);