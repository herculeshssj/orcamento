select * from banco;

update banco set padrao = false where padrao is null;

insert into banco (numero, nome, ativo, padrao) select '002', 'Banco Central do Brasil', true, false from banco where numero = '002' HAVING count(*) = 0;



insert into banco (numero, nome, ativo, padrao) select '001', 'Banco do Brasil', true, false from banco where numero = '001' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '002', 'Banco Central do Brasil', true, false from banco where numero = '002' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '003', 'Banco da Amazônia', true, false from banco where numero = '003' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '004', 'Banco do Nordeste do Brasil', true, false from banco where numero = '004' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '007', 'Banco Nacional de Desenvolvimento Econômico e Social', true, false from banco where numero = '007' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '104', 'Caixa Econômica Federal', true, false from banco where numero = '104' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '046', 'Banco Regional de Desenvolvimento do Extremo Sul', true, false from banco where numero = '046' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '023', 'Banco de Desenvolvimento de Minas Gerais', true, false from banco where numero = '023' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '070', 'Banco de Brasília', true, false from banco where numero = '070' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '047', 'Banco do Estado de Sergipe', true, false from banco where numero = '047' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '021', 'Banco do Estado do Espírito Santo', true, false from banco where numero = '021' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '037', 'Banco do Estado do Pará', true, false from banco where numero = '037' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '041', 'Banco do Estado do Rio Grande do Sul', true, false from banco where numero = '041' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '075', 'Banco ABN Amro S.A.', true, false from banco where numero = '075' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '025', 'Banco Alfa', true, false from banco where numero = '025' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '719', 'Banco Banif', true, false from banco where numero = '719' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '107', 'Banco BBM', true, false from banco where numero = '107' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '318', 'Banco BMG', true, false from banco where numero = '318' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '218', 'Banco Bonsucesso', true, false from banco where numero = '218' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '208', 'Banco BTG Pactual', true, false from banco where numero = '208' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '263', 'Banco Cacique', true, false from banco where numero = '263' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '473', 'Banco Caixa Geral - Brasil', true, false from banco where numero = '473' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '745', 'Banco Citibank', true, false from banco where numero = '745' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '721', 'Banco Credibel', true, false from banco where numero = '721' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '505', 'Banco Credit Suisse', true, false from banco where numero = '505' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '707', 'Góis Monteiro & Co', true, false from banco where numero = '707' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '265', 'Banco Fator', true, false from banco where numero = '265' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '224', 'Banco Fibra', true, false from banco where numero = '224' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '121', 'Banco Gerador', true, false from banco where numero = '121' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '612', 'Banco Guanabara', true, false from banco where numero = '612' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '604', 'Banco Industrial do Brasil', true, false from banco where numero = '604' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '320', 'Banco Industrial e Comercial', true, false from banco where numero = '320' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '653', 'Banco Indusval', true, false from banco where numero = '653' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '077', 'Banco Inter', true, false from banco where numero = '077' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '184', 'Banco Itaú BBA', true, false from banco where numero = '184' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '479', 'Banco ItaúBank', true, false from banco where numero = '479' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select 'M09', 'Banco Itaucred Financiamentos', true, false from banco where numero = 'M09' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '389', 'Banco Mercantil do Brasil', true, false from banco where numero = '389' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '746', 'Banco Modal', true, false from banco where numero = '746' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '738', 'Banco Morada', true, false from banco where numero = '738' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '623', 'Banco Pan', true, false from banco where numero = '623' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '611', 'Banco Paulista', true, false from banco where numero = '611' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '643', 'Banco Pine', true, false from banco where numero = '643' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '654', 'Banco Renner', true, false from banco where numero = '654' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '741', 'Banco Ribeirão Preto', true, false from banco where numero = '741' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '422', 'Banco Safra', true, false from banco where numero = '422' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '033', 'Banco Santander', true, false from banco where numero = '033' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '637', 'Banco Sofisa', true, false from banco where numero = '637' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '082', 'Banco Topázio', true, false from banco where numero = '082' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '655', 'Banco Votorantim', true, false from banco where numero = '655' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '237', 'Bradesco', true, false from banco where numero = '237' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '341', 'Itaú Unibanco', true, false from banco where numero = '341' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '212', 'Banco Original', true, false from banco where numero = '212' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '735', 'Banco Neon', true, false from banco where numero = '735' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '260', 'Nu Pagamentos S.A', true, false from banco where numero = '260' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '102', 'XP Investimentos', true, false from banco where numero = '102' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '756', 'Banco Cooperativo do Brasil', true, false from banco where numero = '756' HAVING count(*) = 0;
insert into banco (numero, nome, ativo, padrao) select '748', 'Banco Cooperativo Sicredi', true, false from banco where numero = '748' HAVING count(*) = 0;



update banco set padrao = true where numero = '001'; -- setando o Banco do Brasil como banco padrão.