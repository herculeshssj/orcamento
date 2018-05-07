show tables;

-- Administradores de FIIs
-- select * from administrador_fii;

-- FIIs
/*
select * from fii;
select * from investimento;
select * from categoriainvestimento; -- FIIs é o ID 9
select * from conta; -- Socopa é o ID 33

insert into investimento (descricao, cnpj, inicioInvestimento, terminoInvestimento, observacao, `idCategoriaInvestimento`, `idConta`, ticker, idAdministradorInvestimento)
    select
    nome_reduzido,
    cnpj,
    inicio_investimento,
    termino_investimento,
    observacao,
    9,
    33,
    ticker,
    id_administrador_fii
    from
    fii;
*/

-- Movimetações FIIs
/*
select * from movimentacao_fii;
select * from movimentacaoinvestimento;
select * from investimento_movimentacaoinvestimento;
*/

-- Rendimentos FIIs
select * from rendimento_fii;
select * from dividendo;

-- Informações sobre FIIs
-- select * from info_fii;

/****** Rascunhos gerais *******/

desc administradorinvestimento;

select * from administradorinvestimento;
select * from usuario where login = "herculeshssj";

select * from movimentacaoinvestimento order by id desc;

select last_insert_id ();

desc movimentacaoinvestimento;
-- insert into movimentacaoinvestimento (`tipoLancamento`, `data`, historico, valor, `impostoRenda`, iof,  cotas, `valorCota`)

insert into investimento_movimentacaoinvestimento (investimento_id, `movimentacoesInvestimento_id`)
    select
    (select i.id from investimento i inner join fii fi on fi.ticker = i.ticker where fi.id = mfi.id_fii) as id_fii,
    mfi.id + 144 as id_movimentacao
    from
    movimentacao_fii mfi;

select
(select if(mfi.tipo_lancamento='CREDITO','RECEITA','DESPESA') ) as tipo_lancamento,
mfi.data_operacao,
(select if(mfi.tipo_lancamento='CREDITO','COMPRA','VENDA') ) as historico,
mfi.valor,
mfi.imposto_renda,
mfi.taxas,
mfi.cotas,
mfi.valor_cota
from
movimentacao_fii mfi;

desc investimento_movimentacaoinvestimento;

/******** SQL definitivo - Executado unicamente na base de produção ********/

-- Exclusão da tabela info_fii
drop table info_fii;

-- Transforma a tabela administrador_fii em administrador_investimento
RENAME TABLE administrador_fii TO administradorinvestimento;

-- Seta os registros de administradorinvestimento para o usuário 'herculeshssj'
update administradorinvestimento set id_usuario = (select id from usuario where login = 'herculeshssj');

-- Realiza as mudanças na estrutura da tabela
alter table administradorinvestimento add PRIMARY key (id);
alter table administradorinvestimento change column `id_usuario` `idUsuario` bigint not null;
alter table administradorinvestimento add CONSTRAINT fk_administradorinvestimento_usuario FOREIGN KEY (idUsuario) references usuario(id);

-- Insere a FK para administradorinvestimento em investimento
alter table investimento add column idAdministradorInvestimento bigint null;
alter table investimento add constraint fk_administradorinvestimento_investimento foreign key (idAdministradorInvestimento) references administradorinvestimento(id);

-- Insere os FIIs na tabela investimento
insert into investimento (descricao, cnpj, inicioInvestimento, terminoInvestimento, observacao, `idCategoriaInvestimento`, `idConta`, ticker, idAdministradorInvestimento)
    select
    nome_reduzido,
    cnpj,
    inicio_investimento,
    termino_investimento,
    observacao,
    9,
    33,
    ticker,
    id_administrador_fii
    from
    fii;

-- Insere as movimentações
insert into movimentacaoinvestimento (`tipoLancamento`, `data`, historico, valor, `impostoRenda`, iof,  cotas, `valorCota`)
    select
    (select if(mfi.tipo_lancamento='CREDITO','RECEITA','DESPESA') ) as tipo_lancamento,
    mfi.data_operacao,
    (select if(mfi.tipo_lancamento='CREDITO','COMPRA','VENDA') ) as historico,
    mfi.valor,
    mfi.imposto_renda,
    mfi.taxas,
    mfi.cotas,
    mfi.valor_cota
    from
    movimentacao_fii mfi;

-- Insere o vínculo entre os investimentos e suas movimentações
insert into investimento_movimentacaoinvestimento (investimento_id, `movimentacoesInvestimento_id`)
    select
    (select i.id from investimento i inner join fii fi on fi.ticker = i.ticker where fi.id = mfi.id_fii) as id_fii,
    mfi.id + 144 as id_movimentacao
    from
    movimentacao_fii mfi;