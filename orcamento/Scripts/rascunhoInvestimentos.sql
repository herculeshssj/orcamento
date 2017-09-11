select * from conta where tipoConta = 'INVESTIMENTO';

select * from investimento;

update conta set ativo = true where tipoConta = 'INVESTIMENTO';

select * from favorecido;
select * from movimentacaoinvestimento order by id desc;