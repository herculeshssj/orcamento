show tables;

select * from resumoinvestimento;

drop table investimento_resumoinvestimento;
drop table resumoinvestimento;

select * from taxaconversao;

show create table banco;

alter table banco drop FOREIGN KEY FK592C0BB7083BD82;
alter table banco drop column idUsuario;

CREATE TABLE `banco` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `numero` varchar(5) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `ativo` tinyint(1) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK592C0BB7083BD82` (`idUsuario`),
  CONSTRAINT `FK592C0BB7083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1