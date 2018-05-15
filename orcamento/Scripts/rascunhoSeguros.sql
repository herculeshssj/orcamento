show tables;

select * from seguro;

desc seguro;

show create table seguro;
/*
alter table seguro add column tipoSeguro varchar(15) not null;
alter table seguro add column periodicidadeRenovacao varchar(15) not null;
alter table seguro add column periodicidadePagamento varchar(15) not null;
alter table seguro add column premioSeguro varchar(15) not null;
alter table seguro add column dataRenovacao date not null;
alter table seguro change column `valorCobertura` `valorCobertura` decimal(18,2) null;
alter table seguro add column ativo boolean;
alter table seguro add column idArquivo bigint null;
alter table seguro add column idFavorecido bigint null;
alter table seguro add column idLancamentoPeriodico bigint not null;
alter table seguro add column idMoeda bigint not null;

alter table seguro add constraint fk_favorecido_seguro foreign key (idFavorecido) references favorecido (id);
alter table seguro add constraint fk_arquivo_seguro foreign key (idArquivo) references arquivo (id);
alter table seguro add constraint fk_moeda_seguro foreign key (idMoeda) references moeda (id);
alter table seguro add constraint fk_lancamento_periodico_seguro foreign key (idLancamentoPeriodico) references lancamentoperiodico (id);
*/

CREATE TABLE `seguro` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `dataAquisicao` date NOT NULL,
  `validade` date DEFAULT NULL,
  `cobertura` text DEFAULT NULL,
  `valorCobertura` decimal(18,2) DEFAULT NULL,
  `valorSeguro` decimal(18,2) NOT NULL,
  `observacao` varchar(255) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `tipoSeguro` varchar(15) NOT NULL,
  `periodicidadeRenovacao` varchar(15) NOT NULL,
  `periodicidadePagamento` varchar(15) NOT NULL,
  `premioSeguro` varchar(15) NOT NULL,
  `dataRenovacao` date NOT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `idArquivo` bigint(20) DEFAULT NULL,
  `idLancamentoPeriodico` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_seguro_usuario` (`idUsuario`),
  KEY `fk_arquivo_seguro` (`idArquivo`),
  KEY `fk_lancamento_periodico_seguro` (`idLancamentoPeriodico`),
  CONSTRAINT `fk_arquivo_seguro` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`),
  CONSTRAINT `fk_lancamento_periodico_seguro` FOREIGN KEY (`idLancamentoPeriodico`) REFERENCES `lancamentoperiodico` (`id`),
  CONSTRAINT `fk_seguro_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1

alter table seguro drop foreign key fk_seguro_usuario;
alter table seguro drop column idUsuario;