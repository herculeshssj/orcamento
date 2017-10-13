select * from meta;
select * from itemmeta;

show create table itemmeta;

CREATE TABLE `itemmeta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(200) NOT NULL,
  `tipoLancamento` varchar(10) NOT NULL,
  `valor` decimal(18,2) NOT NULL DEFAULT '0.00',
  `idMeta` bigint(20) NOT NULL,
  `data` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_grupolancamento_itemgrupolancamento` (`idMeta`),
  CONSTRAINT `fk_grupolancamento_itemgrupolancamento` FOREIGN KEY (`idMeta`) REFERENCES `meta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=latin1


/* SQL definitivo */
-- Renomear a tabela
rename table grupolancamento to meta; -- OK
rename table itemgrupolancamento to itemmeta; -- OK

-- Renomeia a columa "meta"
alter table meta change column `meta` `objetivo` varchar(50) default null;
alter table meta change column `detalhemeta` `detalheobjetivo` text;

-- Exclui o vínculo com lançamento da conta
alter table itemmeta drop FOREIGN KEY fk_lancamentoconta_itemgrupolancamento;
alter table itemmeta drop column idLancamentoConta;

-- Renomeia as FKs
alter table itemmeta change column `idGrupoLancamento` `idMeta` bigint not null;


show create table itemmeta;
CREATE TABLE `itemmeta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(200) NOT NULL,
  `tipoLancamento` varchar(10) NOT NULL,
  `valor` decimal(18,2) NOT NULL DEFAULT '0.00',
  `idGrupoLancamento` bigint(20) NOT NULL,
  `data` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_grupolancamento_itemgrupolancamento` (`idGrupoLancamento`),
  CONSTRAINT `fk_grupolancamento_itemgrupolancamento` FOREIGN KEY (`idGrupoLancamento`) REFERENCES `meta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=latin1

/* Meta */

-- Renomear a tabela
rename table grupolancamento to meta;
rename table itemgrupolancamento to itemmeta;

-- Renomeia a columa meta
alter table meta change column `meta` `objetivo` varchar(50) default null;
alter table meta change column `detalhemeta` `detalheobjetivo` text;

-- Exclui o vínculo com lançamento da conta
alter table itemmeta drop FOREIGN KEY fk_lancamentoconta_itemgrupolancamento;
alter table itemmeta drop column idLancamentoConta;

-- Renomeia as FKs
alter table itemmeta drop FOREIGN key fk_grupolancamento_itemgrupolancamento;
alter table itemmeta drop index fk_grupolancamento_itemgrupolancamento;
alter table itemmeta change column `idGrupoLancamento` `idMeta` bigint not null;
alter table itemmeta add constraint fk_meta_itemmeta FOREIGN key (idMeta) REFERENCES meta (id);