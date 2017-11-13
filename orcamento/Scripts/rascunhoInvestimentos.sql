select * from conta where tipoConta = 'INVESTIMENTO';

select * from investimento;

desc investimento;

update conta set ativo = true where tipoConta = 'INVESTIMENTO';

select * from favorecido;
select * from movimentacaoinvestimento order by id desc;

select * from logs order by id desc;

