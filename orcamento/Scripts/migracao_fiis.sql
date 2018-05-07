-- Administradores de FIIs
select * from administrador_fii;

-- FIIs
select * from fii;
select * from investimento;
select * from categoriainvestimento;

-- Movimetações FIIs
select * from movimentacao_fii;
select * from movimentacaoinvestimento;
select * from investimento_movimentacaoinvestimento;

-- Rendimentos FIIs
select * from rendimento_fii limit 300;
select * from dividendo limit 300;

-- Informações sobre FIIs
-- select * from info_fii;

/****** Rascunhos gerais *******/

desc administradorinvestimento;

select * from administradorinvestimento;
select * from usuario where login = "herculeshssj";

/*** SQL definitivo - Executado unicamente na base de produção ***/

-- Exclusão da tabela info_fii
drop table info_fii;

-- Transforma a tabela administrador_fii em administrador_investimento
RENAME TABLE administrador_fii TO administradorinvestimento;

-- Seta os registros de administradorinvestimento para o usuário 'herculeshssj'
update administradorinvestimento set id_usuario = (select id from usuario where login = 'herculeshssj')

-- Realiza as mudanças na estrutura da tabela
alter table administradorinvestimento add PRIMARY key (id);
alter table administradorinvestimento change column `id_usuario` `idUsuario` bigint not null;
alter table administradorinvestimento add CONSTRAINT fk_administradorinvestimento_usuario FOREIGN KEY (idUsuario) references usuario(id);

