select * from conta where tipoConta = 'INVESTIMENTO';

select * from investimento;

desc investimento;

update conta set ativo = true where tipoConta = 'INVESTIMENTO';

select * from favorecido;
select * from movimentacaoinvestimento order by id desc;

select * from logs order by id desc;

show tables;
select * from fii;
select * from movimentacao_fii;

select * from info_fii;
select * from rendimento_fii;