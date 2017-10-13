select * from lancamentoconta order by id desc;

select * from taxaconversao order by id desc;
select * from taxaconversao where taxaConversao = 0;

select * from lancamentoconta where id = 12948;
select * from lancamentoconta where id = 12842;


select 
* 
from 
lancamentoconta l
-- inner join taxaconversao t on t.id = l.idTaxaConversao
inner join taxaconversao t on t.id = l.idTaxaConversao
-- WHERE
-- t.taxaConversao = 0
-- l.dataPagamento >= '2017-09-01' and l.dataPagamento <= '2017-10-31'
-- and l.idConta = 2
order by l.id desc;

select * from lancamentoconta l where l.idTaxaConversao is null and l.dataPagamento >= '2017-09-01' and l.dataPagamento <= '2017-10-31' and l.idConta = 2 order by id desc;
select * from conta;


select *, valorMoedaOrigem * -1 from taxaconversao where valorMoedaOrigem < 0 or valorMoedaDestino < 0;



-- Correção de taxa de conversão negativa
update taxaconversao set valorMoedaOrigem = valorMoedaOrigem * -1 where valorMoedaOrigem < 0;
update taxaconversao set valorMoedaDestino = valorMoedaDestino * -1 where valorMoedaDestino < 0;