/***
  
  	Copyright (c) 2012, 2013, 2014 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 2.1 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, 

    mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a 
    
    qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública 
    
    Geral Menor GNU em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob o 

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site HSlife
    
    no endereco www.hslife.com.br ou escreva para a Fundação do Software 
    
    Livre(FSF) Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor acesse o 

    endereço www.hslife.com.br, pelo e-mail contato@hslife.com.br ou escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
 ***/

/*** Script de atualização da base de dados ***/

/*** Script de atualização da base de dados ***/

/*** BASE DE DADOS INICIAL PARA A VERSÃO MAR2012 ***/

CREATE TABLE IF NOT EXISTS `REVINFO` (
  `REV` int(11) NOT NULL AUTO_INCREMENT,
  `REVTSTMP` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`REV`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `banco` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `numero` varchar(5) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `banco_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `nome` varchar(100) DEFAULT NULL,
  `numero` varchar(5) DEFAULT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK88AABDC0DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `categoria` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `orcamento` double DEFAULT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `categoria_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `descricao` varchar(50) DEFAULT NULL,
  `orcamento` double DEFAULT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FKF8F7E238DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `conta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agencia` varchar(10) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `contaCorrente` varchar(15) DEFAULT NULL,
  `contaPoupanca` varchar(15) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `variacao` varchar(10) DEFAULT NULL,
  `idBanco` bigint(20) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5A7376F7083BD82` (`idUsuario`),
  KEY `FK5A7376FA36EA1C` (`idBanco`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `conta_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `agencia` varchar(10) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `contaCorrente` varchar(15) DEFAULT NULL,
  `contaPoupanca` varchar(15) DEFAULT NULL,
  `descricao` varchar(50) DEFAULT NULL,
  `variacao` varchar(10) DEFAULT NULL,
  `idBanco` bigint(20) DEFAULT NULL,
  `idUsuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FKE77CBA74DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `contapagarreceber` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `arquivo` longblob,
  `dataCadastro` date NOT NULL,
  `dataEmissao` date DEFAULT NULL,
  `dataVencimento` date NOT NULL,
  `historico` varchar(255) NOT NULL,
  `numeroDocumento` varchar(255) DEFAULT NULL,
  `observacao` varchar(255) DEFAULT NULL,
  `parcela` int(11) DEFAULT NULL,
  `quitado` tinyint(1) DEFAULT NULL,
  `tipoContaPagarReceber` varchar(10) DEFAULT NULL,
  `totalParcela` int(11) DEFAULT NULL,
  `valor` double NOT NULL,
  `lancamento_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK16FF8262C3C2F39` (`lancamento_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `contapagarreceber_detalheconta` (
  `contapagarreceber_id` bigint(20) NOT NULL,
  `detalhes_id` bigint(20) NOT NULL,
  UNIQUE KEY `detalhes_id` (`detalhes_id`),
  KEY `FKBAA67611E0816315` (`detalhes_id`),
  KEY `FKBAA676118137DDDB` (`contapagarreceber_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `contapagarreceber_detalheconta_AUD` (
  `REV` int(11) NOT NULL,
  `contapagarreceber_id` bigint(20) NOT NULL,
  `detalhes_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`contapagarreceber_id`,`detalhes_id`),
  KEY `FK77973362DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `contapagarreceber_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `dataCadastro` date DEFAULT NULL,
  `dataEmissao` date DEFAULT NULL,
  `dataVencimento` date DEFAULT NULL,
  `historico` varchar(255) DEFAULT NULL,
  `numeroDocumento` varchar(255) DEFAULT NULL,
  `observacao` varchar(255) DEFAULT NULL,
  `parcela` int(11) DEFAULT NULL,
  `quitado` tinyint(1) DEFAULT NULL,
  `tipoContaPagarReceber` varchar(10) DEFAULT NULL,
  `totalParcela` int(11) DEFAULT NULL,
  `valor` double DEFAULT NULL,
  `lancamento_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FKAD0053E7DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `detalheconta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` date DEFAULT NULL,
  `descricao` varchar(255) NOT NULL,
  `valor` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `detalheconta_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `data` date DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `valor` double DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK4D0127D9DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `favorecido` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `contato` varchar(255) DEFAULT NULL,
  `nome` varchar(100) NOT NULL,
  `tipoPessoa` varchar(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `favorecido_lancamento` (
  `favorecido_id` bigint(20) NOT NULL,
  `lancamentos_id` bigint(20) NOT NULL,
  UNIQUE KEY `lancamentos_id` (`lancamentos_id`),
  KEY `FKE18E8E41D2B8EA79` (`favorecido_id`),
  KEY `FKE18E8E41C6071E1A` (`lancamentos_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `favorecido_lancamento_AUD` (
  `REV` int(11) NOT NULL,
  `favorecido_id` bigint(20) NOT NULL,
  `lancamentos_id` bigint(20) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`REV`,`favorecido_id`,`lancamentos_id`),
  KEY `FK3F57B392DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `favorecido_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `contato` varchar(255) DEFAULT NULL,
  `nome` varchar(100) DEFAULT NULL,
  `tipoPessoa` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK5033FC5DDF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `fechamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataFim` date NOT NULL,
  `dataInicio` date NOT NULL,
  `saldo` double NOT NULL,
  `idConta` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2994F574A5FD784` (`idConta`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `fechamento_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `dataFim` date DEFAULT NULL,
  `dataInicio` date DEFAULT NULL,
  `saldo` double DEFAULT NULL,
  `idConta` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FKC2A1EDF9DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `lancamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `arquivo` mediumblob,
  `dataLancamento` date NOT NULL,
  `dataPagamento` date DEFAULT NULL,
  `dataVencimento` date DEFAULT NULL,
  `historico` varchar(255) NOT NULL,
  `numeroDocumento` varchar(255) DEFAULT NULL,
  `observacao` varchar(255) DEFAULT NULL,
  `parcela` int(11) DEFAULT NULL,
  `tipoLancamento` varchar(10) DEFAULT NULL,
  `totalParcela` int(11) DEFAULT NULL,
  `valorPago` double DEFAULT NULL,
  `valorVencimento` double DEFAULT NULL,
  `idCategoria` bigint(20) NOT NULL,
  `idContaBancaria` bigint(20) NOT NULL,
  `idFavorecido` bigint(20) DEFAULT NULL,
  `idMeioPagamento` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK69A57A4D3A6AD6` (`idMeioPagamento`),
  KEY `FK69A57A8BEA680C` (`idCategoria`),
  KEY `FK69A57A90FFBACA` (`idFavorecido`),
  KEY `FK69A57ABA3900C1` (`idContaBancaria`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `lancamento_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `dataLancamento` date DEFAULT NULL,
  `dataPagamento` date DEFAULT NULL,
  `dataVencimento` date DEFAULT NULL,
  `historico` varchar(255) DEFAULT NULL,
  `numeroDocumento` varchar(255) DEFAULT NULL,
  `observacao` varchar(255) DEFAULT NULL,
  `parcela` int(11) DEFAULT NULL,
  `tipoLancamento` varchar(10) DEFAULT NULL,
  `totalParcela` int(11) DEFAULT NULL,
  `valorPago` double DEFAULT NULL,
  `valorVencimento` double DEFAULT NULL,
  `idCategoria` bigint(20) DEFAULT NULL,
  `idContaBancaria` bigint(20) DEFAULT NULL,
  `idFavorecido` bigint(20) DEFAULT NULL,
  `idMeioPagamento` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FKBFF32AFFDF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `meiopagamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `meiopagamento_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `descricao` varchar(50) DEFAULT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK80A5F2ADDF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `dataCriacao` datetime DEFAULT NULL,
  `login` varchar(50) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `senha` varchar(40) NOT NULL,
  `tipoUsuario` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 ;

CREATE TABLE IF NOT EXISTS `usuario_log` (
  `id` bigint(20) NOT NULL,
  `REV` int(11) NOT NULL,
  `REVTYPE` tinyint(4) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `dataCriacao` datetime DEFAULT NULL,
  `login` varchar(50) DEFAULT NULL,
  `nome` varchar(100) DEFAULT NULL,
  `senha` varchar(40) DEFAULT NULL,
  `tipoUsuario` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`,`REV`),
  KEY `FK31187EB3DF74E053` (`REV`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


ALTER TABLE `banco_log` ADD CONSTRAINT `FK88AABDC0DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `categoria_log` ADD CONSTRAINT `FKF8F7E238DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `conta` ADD CONSTRAINT `FK5A7376F7083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);
ALTER TABLE `conta` ADD CONSTRAINT `FK5A7376FA36EA1C` FOREIGN KEY (`idBanco`) REFERENCES `banco` (`id`);

ALTER TABLE `conta_log` ADD CONSTRAINT `FKE77CBA74DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `contapagarreceber` ADD CONSTRAINT `FK16FF8262C3C2F39` FOREIGN KEY (`lancamento_id`) REFERENCES `lancamento` (`id`);

ALTER TABLE `contapagarreceber_detalheconta` ADD CONSTRAINT `FKBAA676118137DDDB` FOREIGN KEY (`contapagarreceber_id`) REFERENCES `contapagarreceber` (`id`);
ALTER TABLE `contapagarreceber_detalheconta` ADD CONSTRAINT `FKBAA67611E0816315` FOREIGN KEY (`detalhes_id`) REFERENCES `detalheconta` (`id`);

ALTER TABLE `contapagarreceber_detalheconta_AUD` ADD CONSTRAINT `FK77973362DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `contapagarreceber_log` ADD CONSTRAINT `FKAD0053E7DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `detalheconta_log` ADD CONSTRAINT `FK4D0127D9DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `favorecido_lancamento` ADD CONSTRAINT `FKE18E8E41C6071E1A` FOREIGN KEY (`lancamentos_id`) REFERENCES `lancamento` (`id`);
ALTER TABLE `favorecido_lancamento` ADD CONSTRAINT `FKE18E8E41D2B8EA79` FOREIGN KEY (`favorecido_id`) REFERENCES `favorecido` (`id`);

ALTER TABLE `favorecido_lancamento_AUD` ADD CONSTRAINT `FK3F57B392DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `favorecido_log` ADD CONSTRAINT `FK5033FC5DDF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `fechamento` ADD CONSTRAINT `FK2994F574A5FD784` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`);

ALTER TABLE `fechamento_log` ADD CONSTRAINT `FKC2A1EDF9DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `lancamento` ADD CONSTRAINT `FK69A57A4D3A6AD6` FOREIGN KEY (`idMeioPagamento`) REFERENCES `meiopagamento` (`id`);
ALTER TABLE `lancamento` ADD CONSTRAINT `FK69A57A8BEA680C` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`id`);
ALTER TABLE `lancamento` ADD CONSTRAINT `FK69A57A90FFBACA` FOREIGN KEY (`idFavorecido`) REFERENCES `favorecido` (`id`);
ALTER TABLE `lancamento` ADD CONSTRAINT `FK69A57ABA3900C1` FOREIGN KEY (`idContaBancaria`) REFERENCES `conta` (`id`);

ALTER TABLE `lancamento_log` ADD CONSTRAINT `FKBFF32AFFDF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `meiopagamento_log` ADD CONSTRAINT `FK80A5F2ADDF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);

ALTER TABLE `usuario_log` ADD CONSTRAINT `FK31187EB3DF74E053` FOREIGN KEY (`REV`) REFERENCES `REVINFO` (`REV`);
  
/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO ABR2012 ***/
  
alter table categoria drop column orcamento;

alter table banco add column ativo tinyint(1) default null;
alter table banco_log add column ativo tinyint(1) default null;

update banco set ativo = true;

ALTER TABLE banco CHANGE ativo ativo tinyint(1) NOT NULL; 

drop table favorecido_lancamento_AUD;
drop table favorecido_lancamento;

alter table favorecido add column idUsuario bigint(20) default null;
alter table favorecido_log add column idUsuario bigint(20) default null;

update favorecido set idUsuario = (select id from usuario limit 1);

ALTER TABLE favorecido CHANGE idUsuario idUsuario bigint(20) NOT NULL;

insert into usuario(ativo,dataCriacao,login,nome,senha,tipoUsuario) values (true, "2012-01-01", "admin","Administrador do sistema" , "d033e22ae348aeb5660fc2140aec35850c4da997", "ROLE_ADMIN"); -- senha: admin

update usuario set tipoUsuario = "ROLE_ADMIN" where tipoUsuario = "ADMIN";

update usuario set tipoUsuario = "ROLE_USER" where tipoUsuario = "USER";

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAI2012 ***/

drop table fechamento_log;

drop table fechamento;

CREATE TABLE IF NOT EXISTS `transferencia` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` date DEFAULT NULL,
  `valor` double DEFAULT NULL,
  `contaDestino_id` bigint(20) DEFAULT NULL,
  `contaOrigem_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK88474A67B4EA4032` (`contaOrigem_id`),
  KEY `FK88474A671B683B1` (`contaDestino_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

ALTER TABLE `transferencia` ADD CONSTRAINT `FK88474A671B683B1` FOREIGN KEY (`contaDestino_id`) REFERENCES `conta` (`id`);
ALTER TABLE `transferencia` ADD CONSTRAINT `FK88474A67B4EA4032` FOREIGN KEY (`contaOrigem_id`) REFERENCES `conta` (`id`);
  
CREATE TABLE IF NOT EXISTS `periodolancamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataAbertura` date NOT NULL,
  `dataFechamento` date DEFAULT NULL,
  `saldoFechamento` double DEFAULT NULL,
  `idContaBancaria` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKEC523EC8BA3900C1` (`idContaBancaria`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;  
  
ALTER TABLE `periodolancamento` ADD CONSTRAINT `FKEC523EC8BA3900C1` FOREIGN KEY (`idContaBancaria`) REFERENCES `conta` (`id`);
  
update conta c set c.ativo = true where ( select count(l.id) from lancamento l where l.idContaBancaria = c.id)  > 0;

insert into periodolancamento (dataAbertura, saldoFechamento, idContaBancaria) 
	select (select min(l2.dataPagamento) from lancamento l2 
	where l2.idContaBancaria = cc.id) as dataAbertura, 0 as saldoFechamento, cc.id as idContaBancaria from conta cc 
	inner join lancamento l on l.idContaBancaria = cc.id group by cc.id;
	
alter table periodolancamento add column ordem int(11) not null;

update periodolancamento set ordem = id;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAI2012.1 ***/

drop table banco_log;
drop table categoria_log;
drop table conta_log;
drop table contapagarreceber_detalheconta_AUD;
drop table contapagarreceber_log;
drop table detalheconta_log;
drop table favorecido_log;
drop table lancamento_log;
drop table meiopagamento_log;
drop table usuario_log;
drop table REVINFO;

alter table categoria add column orcamento double not null;

alter table banco add column idUsuario bigint(20) not null;
update banco set idUsuario = (select id from usuario where login = 'admin');
ALTER TABLE `banco` ADD CONSTRAINT `FK592C0BB7083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

alter table categoria add column idUsuario bigint(20) not null;
update categoria set idUsuario = (select id from usuario where login = 'admin');
ALTER TABLE `categoria` ADD CONSTRAINT `FK5D54E1337083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

alter table meiopagamento add column idUsuario bigint(20) not null;
update meiopagamento set idUsuario = (select id from usuario where login = 'admin');
ALTER TABLE `meiopagamento` ADD CONSTRAINT `FKC31F74287083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);
  
ALTER TABLE `lancamento` CHANGE `observacao` `observacao` VARCHAR( 3000 ) NULL DEFAULT NULL;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAI2012.2 ***/

alter table lancamento add column agendado tinyint(1) default null;
update lancamento set agendado = false;
ALTER TABLE `lancamento` CHANGE `agendado` `agendado` TINYINT( 1 ) NOT NULL ;

CREATE TABLE IF NOT EXISTS `cartaocredito` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `abrangencia` varchar(15) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `bandeira` varchar(30) DEFAULT NULL,
  `codigoSeguranca` varchar(3) DEFAULT NULL,
  `diaFechamentoFatura` int(11) NOT NULL,
  `diaVencimentoFatura` int(11) NOT NULL,
  `juros` double DEFAULT NULL,
  `limiteCartao` double NOT NULL,
  `limiteSaque` double DEFAULT NULL,
  `multa` double DEFAULT NULL,
  `nomeCliente` varchar(50) DEFAULT NULL,
  `numeroCartao` varchar(30) DEFAULT NULL,
  `tipoCartao` varchar(10) DEFAULT NULL,
  `validade` date NOT NULL,
  `idBanco` bigint(20) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9BFA41887083BD82` (`idUsuario`),
  KEY `FK9BFA4188A36EA1C` (`idBanco`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

ALTER TABLE `cartaocredito` ADD CONSTRAINT `FK9BFA41887083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);
ALTER TABLE `cartaocredito` ADD CONSTRAINT `FK9BFA4188A36EA1C` FOREIGN KEY (`idBanco`) REFERENCES `banco` (`id`);
  

CREATE TABLE IF NOT EXISTS `compracartao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataCompra` date NOT NULL,
  `descricao` varchar(100) NOT NULL,
  `notaFiscal` longblob,
  `quantParcelas` int(11) NOT NULL,
  `statusCompra` varchar(10) DEFAULT NULL,
  `valorCompra` double NOT NULL,
  `valorParcela` double NOT NULL,
  `idCartaoCredito` bigint(20) DEFAULT NULL,
  `idCategoria` bigint(20) DEFAULT NULL,
  `idFavorecido` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6610DE4CEAB67D96` (`idCartaoCredito`),
  KEY `FK6610DE4C8BEA680C` (`idCategoria`),
  KEY `FK6610DE4C90FFBACA` (`idFavorecido`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

ALTER TABLE `compracartao` ADD CONSTRAINT `FK6610DE4C90FFBACA` FOREIGN KEY (`idFavorecido`) REFERENCES `favorecido` (`id`);
ALTER TABLE `compracartao` ADD CONSTRAINT `FK6610DE4C8BEA680C` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`id`);
ALTER TABLE `compracartao` ADD CONSTRAINT `FK6610DE4CEAB67D96` FOREIGN KEY (`idCartaoCredito`) REFERENCES `cartaocredito` (`id`);

CREATE TABLE IF NOT EXISTS `parcelacompra` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parcela` int(11) NOT NULL,
  `quitado` tinyint(1) DEFAULT NULL,
  `idCompra` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK165B3CF646E600C4` (`idCompra`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

ALTER TABLE `parcelacompra` ADD CONSTRAINT `FK165B3CF646E600C4` FOREIGN KEY (`idCompra`) REFERENCES `compracartao` (`id`);

CREATE TABLE IF NOT EXISTS `compracartao_parcelacompra` (
  `compracartao_id` bigint(20) NOT NULL,
  `parcelas_id` bigint(20) NOT NULL,
  UNIQUE KEY `parcelas_id` (`parcelas_id`),
  KEY `FK6500F843D75A9E59` (`compracartao_id`),
  KEY `FK6500F8439CC572D6` (`parcelas_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `compracartao_parcelacompra` ADD CONSTRAINT `FK6500F8439CC572D6` FOREIGN KEY (`parcelas_id`) REFERENCES `parcelacompra` (`id`);
ALTER TABLE `compracartao_parcelacompra` ADD CONSTRAINT `FK6500F843D75A9E59` FOREIGN KEY (`compracartao_id`) REFERENCES `compracartao` (`id`);
  
/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUN2012 ***/
  
alter table categoria drop column orcamento; 

CREATE TABLE IF NOT EXISTS `tarefa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataFim` date DEFAULT NULL,
  `dataInicio` date DEFAULT NULL,
  `detalhe` longtext,
  `horaFim` int(11) DEFAULT NULL,
  `horaInicio` int(11) DEFAULT NULL,
  `prioridade` varchar(10) DEFAULT NULL,
  `statusTarefa` varchar(10) DEFAULT NULL,
  `titulo` varchar(100) NOT NULL,
  `idUsuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKCB7E6A1B7083BD82` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

ALTER TABLE `tarefa` ADD CONSTRAINT `FKCB7E6A1B7083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

CREATE TABLE IF NOT EXISTS `detalheplanejamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `percentual` double DEFAULT NULL,
  `valorGasto` double DEFAULT NULL,
  `valorPrevisto` double DEFAULT NULL,
  `idCategoria` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB00696998BEA680C` (`idCategoria`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `planejamentoperiodo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataFim` date NOT NULL,
  `dataInicio` date NOT NULL,
  `idConta` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKCBC90AF0A5FD784` (`idConta`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `planejamentoperiodo_detalheplanejamento` (
  `planejamentoperiodo_id` bigint(20) NOT NULL,
  `planejamentos_id` bigint(20) NOT NULL,
  PRIMARY KEY (`planejamentoperiodo_id`,`planejamentos_id`),
  UNIQUE KEY `planejamentos_id` (`planejamentos_id`),
  KEY `FK7B5B56CA7660C2BB` (`planejamentoperiodo_id`),
  KEY `FK7B5B56CAD61F7D5F` (`planejamentos_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `detalheplanejamento` ADD CONSTRAINT `FKB00696998BEA680C` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`id`);

ALTER TABLE `planejamentoperiodo` ADD CONSTRAINT `FKCBC90AF0A5FD784` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`);

ALTER TABLE `planejamentoperiodo_detalheplanejamento` ADD CONSTRAINT `FK7B5B56CAD61F7D5F` FOREIGN KEY (`planejamentos_id`) REFERENCES `detalheplanejamento` (`id`);
ALTER TABLE `planejamentoperiodo_detalheplanejamento` ADD CONSTRAINT `FK7B5B56CA7660C2BB` FOREIGN KEY (`planejamentoperiodo_id`) REFERENCES `planejamentoperiodo` (`id`);

  
alter table cartaocredito add column descricao varchar(50) null;
update cartaocredito set descricao = (select nome from banco b where b.id = idBanco) where descricao is null;
ALTER TABLE `cartaocredito` CHANGE `descricao` `descricao` VARCHAR( 50 ) NOT NULL; 

-- O servidor MySQL não deixou excluir a coluna idBanco, então a mesma foi alterada para aceitar valor nulo
ALTER TABLE `cartaocredito` CHANGE `idBanco` `idBanco` BIGINT( 20 ) NULL ;

alter table conta add column idCartao bigint(20) null;
alter table conta add column tipoConta varchar(10) not null;
update conta set tipoConta = 'CORRENTE';

ALTER TABLE `conta` ADD CONSTRAINT `FK5A7376F2CE1B172` FOREIGN KEY (`idCartao`) REFERENCES `cartaocredito` (`id`);

update lancamento set parcela = 1 where parcela = 0;
update lancamento set totalParcela = 1 where totalParcela = 0;

alter table lancamento add column quitado tinyint(1) not null default 0;

update lancamento l set l.quitado = true where l.dataPagamento < (select distinct pl.dataAbertura from periodolancamento pl where pl.idContaBancaria = l.idContaBancaria and pl.dataFechamento is null);

drop table transferencia;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUL2012 ***/

drop table contapagarreceber_detalheconta;
drop table detalheconta;
drop table contapagarreceber;
drop table compracartao_parcelacompra;
drop table parcelacompra;
drop table compracartao;

alter table planejamentoperiodo add column agendado tinyint(1) default false;

alter table planejamentoperiodo add column descricao varchar(50) null;
update planejamentoperiodo set descricao = (select concat(dataInicio, ' - ', dataFim));
alter table planejamentoperiodo change `descricao` `descricao` varchar(50) not null;

alter table categoria add column tipoCategoria varchar(10) not null;
update categoria set tipoCategoria = 'DEBITO';

update categoria c set c.tipoCategoria = (select if ((select count(l.id) from lancamento l where l.idCategoria = c.id and l.tipoLancamento = 'RECEITA') > (select count(l.id) from lancamento l where l.idCategoria = c.id and l.tipoLancamento = 'DESPESA'), 'CREDITO','DEBITO'));

--
-- Estrutura da tabela `buscasalva`
--

CREATE TABLE IF NOT EXISTS `buscasalva` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agendados` tinyint(1) DEFAULT NULL,
  `dataFim` date DEFAULT NULL,
  `dataInicio` date DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO AGO2012 ***/

drop table tarefa;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO SET2012 ***/

create table arquivo(
	id bigint auto_increment,
    idlancamento bigint,
    nomeArquivo varchar(255),
    contentType varchar(255),
    tamanho bigint,
    dados mediumblob,
    primary key (id)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1;

insert into arquivo(idlancamento, dados, nomeArquivo, contentType, tamanho) select id, arquivo, 'comprovante.pdf', 'application/pdf', 307200 from lancamento;

alter table lancamento add column idArquivo bigint null;

update lancamento l set idarquivo = (select a.id from arquivo a where a.idlancamento = l.id);

alter table arquivo drop column idlancamento;

alter table lancamento drop column arquivo;

alter table lancamento add constraint FK69A57A28C1FC80 foreign key (idarquivo) references arquivo (id); -- Não está rodando atualmente, mas deve ser executado na criação da base

alter table buscasalva add column saldoAnterior tinyint(1) default false;

CREATE TABLE IF NOT EXISTS `categoriadocumento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKD0771AE17083BD82` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `documento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) DEFAULT NULL,
  `nome` varchar(50) NOT NULL,
  `idArquivo` bigint(20) NOT NULL,
  `idCategoriaDocumento` bigint(20) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK383D52B47083BD82` (`idUsuario`),
  KEY `FK383D52B46CA8AABC` (`idCategoriaDocumento`),
  KEY `FK383D52B428C1FC80` (`idArquivo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

ALTER TABLE `categoriadocumento` ADD CONSTRAINT `FKD0771AE17083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);
  
ALTER TABLE `documento` ADD CONSTRAINT `FK383D52B428C1FC80` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`);
ALTER TABLE `documento` ADD CONSTRAINT `FK383D52B46CA8AABC` FOREIGN KEY (`idCategoriaDocumento`) REFERENCES `categoriadocumento` (`id`);
ALTER TABLE `documento` ADD CONSTRAINT `FK383D52B47083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

alter table usuario add column perguntaSecreta varchar(255) null;

alter table usuario add column respostaSecreta varchar(255) null;

update usuario set perguntaSecreta = 'Seu login' where perguntaSecreta is null;
update usuario set respostaSecreta = (select sha1(login)) where respostaSecreta is null;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO NOV2012 ***/

CREATE TABLE IF NOT EXISTS `unidademedida` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `sigla` varchar(10) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2F2C0D88EC275F4E` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

ALTER TABLE `unidademedida` ADD CONSTRAINT `FK2F2C0D88EC275F4E` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);
  
CREATE TABLE IF NOT EXISTS `despensa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `arquivado` tinyint(1) DEFAULT NULL,
  `caracteristicas` varchar(255) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `localArmazenamento` varchar(100) DEFAULT NULL,
  `quantidadeAmarelo` int(11) NOT NULL,
  `quantidadeAtual` int(11) NOT NULL,
  `quantidadeVerde` int(11) NOT NULL,
  `quantidadeVermelho` int(11) NOT NULL,
  `idUnidadeMedida` bigint(20) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3D6214555F6482A2` (`idUnidadeMedida`),
  KEY `FK3D621455EC275F4E` (`idUsuario`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `historicodespensa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataOperacao` date DEFAULT NULL,
  `operacaoDespensa` varchar(10) DEFAULT NULL,
  `quantidade` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `despensa_historicodespensa` (
  `despensa_id` bigint(20) NOT NULL,
  `historico_id` bigint(20) NOT NULL,
  UNIQUE KEY `historico_id` (`historico_id`),
  KEY `FKDD4E76DBDCD678CD` (`despensa_id`),
  KEY `FKDD4E76DB10BDBA5C` (`historico_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `despensa` ADD CONSTRAINT `FK3D621455EC275F4E` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);
ALTER TABLE `despensa` ADD CONSTRAINT `FK3D6214555F6482A2` FOREIGN KEY (`idUnidadeMedida`) REFERENCES `unidademedida` (`id`);

ALTER TABLE `despensa_historicodespensa` ADD CONSTRAINT `FKDD4E76DB10BDBA5C` FOREIGN KEY (`historico_id`) REFERENCES `historicodespensa` (`id`);
ALTER TABLE `despensa_historicodespensa` ADD CONSTRAINT `FKDD4E76DBDCD678CD` FOREIGN KEY (`despensa_id`) REFERENCES `despensa` (`id`);
  
alter table buscasalva add column idConta bigint null;
  
ALTER TABLE `buscasalva` ADD CONSTRAINT `FK129C7AEB7DD97C50` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`);
  
alter table lancamento drop column dataVencimento;
alter table lancamento drop column valorVencimento;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JAN2013 ***/

alter table conta add column dataAbertura date null;
alter table conta add column dataFechamento date null;
alter table conta add column saldoInicial double null;
alter table conta add column saldoFinal double null;

update conta c set c.saldoInicial = (select l1.valorPago from lancamento l1 where l1.id = (select min(l2.id) from lancamento l2 where l2.idContaBancaria = c.id));
update conta c set c.dataAbertura = (select l1.dataPagamento from lancamento l1 where l1.id = (select min(l2.id) from lancamento l2 where l2.idContaBancaria = c.id));
update conta c set c.dataFechamento = (select l1.dataPagamento from lancamento l1 where l1.id = (select max(l2.id) from lancamento l2 where l2.idContaBancaria = c.id)) where c.ativo = false;

update conta c set c.saldoInicial = 0 where c.saldoInicial is null;
update conta c set c.saldoFinal = 0 where c.saldoFinal is null;
update conta c set c.dataAbertura = (select current_date()) where c.dataAbertura is null;
update conta c set c.dataFechamento = (select current_date()) where c.dataFechamento is null;

update conta c set c.saldoFinal = (
	(select sum(l1.valorPago) from lancamento l1 where l1.tipoLancamento = 'RECEITA' and l1.idContaBancaria = c.id and l1.dataPagamento >= c.dataAbertura and dataPagamento <= c.dataFechamento)
	-
	(select sum(l2.valorPago) from lancamento l2 where l2.tipoLancamento = 'DESPESA' and l2.idContaBancaria = c.id and l2.dataPagamento >= c.dataAbertura and dataPagamento <= c.dataFechamento)
) where c.ativo = false ;

update conta c set c.saldoFinal = 0 where c.saldoFinal is null;

alter table conta change `dataAbertura` `dataAbertura` date not null;
alter table conta change `saldoInicial` `saldoInicial` double not null;

CREATE TABLE IF NOT EXISTS `aberturafechamentoconta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `dataAlteracao` datetime NOT NULL,
  `operacao` varchar(10) NOT NULL,
  `saldo` double NOT NULL,
  `usuario` varchar(255) NOT NULL,
  `idConta` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB806F0BD7DD97C50` (`idConta`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

ALTER TABLE `aberturafechamentoconta` ADD CONSTRAINT `FKB806F0BD7DD97C50` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`);
  
alter table periodolancamento rename to fechamentoperiodo;

delete from fechamentoperiodo where dataFechamento is null;

alter table fechamentoperiodo add column operacao varchar(10) not null;
alter table fechamentoperiodo add column usuario varchar(255) not null;
alter table fechamentoperiodo add column dataAlteracao datetime not null;

alter table fechamentoperiodo change column dataFechamento data date not null;
alter table fechamentoperiodo change column saldoFechamento saldo double not null;

alter table fechamentoperiodo drop column dataAbertura;
alter table fechamentoperiodo drop column ordem;

update fechamentoperiodo set operacao = 'FECHAMENTO';
update fechamentoperiodo set dataAlteracao = data;
update fechamentoperiodo f set f.usuario = (select login from usuario u inner join conta c on c.idUsuario = u.id where c.id = f.idContaBancaria);
update fechamentoperiodo f set f.usuario = (select login from usuario u inner join conta c on c.idUsuario = u.id where c.id = f.idContaBancaria);

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JAN2013.1 ***/

drop table planejamentoperiodo_detalheplanejamento;
drop table detalheplanejamento;
drop table planejamentoperiodo;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUL2013 ***/

create table auditoria (
        id bigint not null auto_increment,
        classe varchar(255) not null,
        data date not null,
        dataHora datetime not null,
        ip varchar(255) not null,
        transacao varchar(255) not null,
        usuario varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

create table auditoria_auditoriadados (
        auditoria_id bigint not null,
        dadosAuditoria_id bigint not null,
        unique (dadosAuditoria_id)
    ) engine=InnoDB;

create table auditoriadados (
        id bigint not null auto_increment,
        nomeAtributo varchar(255) not null,
        situacaoOperacao varchar(255) not null,
        valorAtributo text not null,
        primary key (id)
    ) engine=InnoDB;

 alter table auditoria_auditoriadados 
        add index FKF2BF5F9E8841B637 (dadosAuditoria_id), 
        add constraint FKF2BF5F9E8841B637 
        foreign key (dadosAuditoria_id) 
        references auditoriadados (id);
        
alter table auditoria_auditoriadados 
        add index FKF2BF5F9E7D80A527 (auditoria_id), 
        add constraint FKF2BF5F9E7D80A527 
        foreign key (auditoria_id) 
        references auditoria (id);

alter table usuario add column email varchar(40) null;
update usuario set email = (select concat(login,'@hslife.com.br')); -- where tipoUsuario = 'ROLE_USER'; // trecho removido. Tarefa #976
alter table usuario change `email` `email` varchar(40) not null;

alter table usuario drop column perguntaSecreta;
alter table usuario drop column respostaSecreta;

update usuario set tipoUsuario = 'ROLE_USER';
update usuario set tipoUsuario = 'ROLE_ADMIN' where login = 'admin';
update usuario set email = 'contato@hslife.com.br' where login = 'admin';

create table opcaosistema (
	id bigint not null auto_increment,
	chave varchar(100) not null,
	valor text null,
	tipoOpcaoSistema varchar(15) not null,
	enabled tinyint(1),
	visible tinyint(1),
	required tinyint(1) default false,
	tipoValor varchar(20) not null,
	casoDeUso varchar(100) not null,
	idUsuario bigint null,
	primary key (id)
) engine=InnoDB;
    
alter table opcaosistema add constraint fk_opcaosistema foreign key (idUsuario) references usuario (id);

insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_SERVIDOR', 'smtp.hslife.com.br', 'GLOBAL_ADMIN', true, true, true, 'STRING', 'email');
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_PORTA', '465', 'GLOBAL_ADMIN', true, true, true, 'INTEGER', 'email');
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_USUARIO', 'nao-responde@hslife.com.br', 'GLOBAL_ADMIN', true, true, true, 'STRING', 'email');
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_SENHA', 'n0r3ply1@3', 'GLOBAL_ADMIN', true, true, true, 'STRING', 'email');
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_REMETENTE', 'HSlife Serviços de TI', 'GLOBAL_ADMIN', true, true, true, 'STRING', 'email');
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_EMAIL_REMETENTE', 'contato@hslife.com.br', 'GLOBAL_ADMIN', true, true, true, 'STRING', 'email');
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_SSL_TLS', 'true', 'GLOBAL_ADMIN', true, true, true, 'BOOLEAN', 'email');
insert into opcaosistema (chave, valor, tipoOpcaoSistema, enabled, visible, required, tipoValor, casoDeUso) values ('EMAIL_CHARSET', 'UTF-8', 'GLOBAL_ADMIN', true, true, true, 'STRING', 'email');

alter table despensa rename to itemdespensa;

create table despensa (
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	localArmazenamento varchar(255) null,
	idUsuario bigint null,
	primary key (id)
) engine=InnoDB;

alter table despensa add constraint fk_despensa foreign key (idUsuario) references usuario (id);

insert into despensa (descricao, localArmazenamento, idUsuario)
	select 'Prateleira padrão', 'Despensa padrão', id from usuario;

insert into despensa (descricao, localArmazenamento, idUsuario)
	select distinct localArmazenamento, localArmazenamento, idUsuario from itemdespensa;

create table itemdespensa_movimentoitemdespensa (
	itemDespensa_id bigint not null,
	movimentacao_id bigint not null,
	unique(movimentacao_id)
) engine=InnoDB;
	
insert into itemdespensa_movimentoitemdespensa (itemDespensa_id, movimentacao_id)
	select despensa_id, historico_id from despensa_historicodespensa;

alter table historicodespensa rename to movimentoitemdespensa;
	
alter table itemdespensa_movimentoitemdespensa add constraint fk_itemdespensa foreign key (itemDespensa_id) references itemdespensa(id);
alter table itemdespensa_movimentoitemdespensa add constraint fk_movimentoitemdespensa foreign key (movimentacao_id) references movimentoitemdespensa(id);

drop table despensa_historicodespensa;

alter table itemdespensa add column idDespensa bigint null;
alter table itemdespensa add column validade datetime null;
alter table itemdespensa add column valor double default 0;
update itemdespensa set idDespensa = (select d.id from despensa d where d.idUsuario = idUsuario limit 1);
alter table itemdespensa change `idDespensa` `idDespensa` bigint not null;

alter table itemdespensa drop column localArmazenamento;
-- alter table itemdespensa drop column idUsuario; -- ocorre erro na hora de excluir
alter table itemdespensa change `idUsuario` `idUsuario` bigint null;  -- executar caso ocorra erro na hora de excluir a coluna `idUsuario`
alter table movimentoitemdespensa add column valor double default 0;

update categoria set padrao = false;

insert into categoria (ativo, descricao, padrao, idUsuario, tipoCategoria)
	select true, 'Categoria Padrão', true, id, 'CREDITO' from usuario;
	
insert into categoria (ativo, descricao, padrao, idUsuario, tipoCategoria)
	select true, 'Categoria Padrão', true, id, 'DEBITO' from usuario;
	
alter table favorecido change column `contato` `contato` text null;
alter table favorecido add column padrao boolean null;
update favorecido set padrao = false;
alter table favorecido change column `padrao` `padrao` boolean not null;

insert into favorecido (ativo, nome, padrao, idUsuario, tipoPessoa)
	select true, 'Favorecido Padrão', true, id, 'FISICA' from usuario;
	
update meiopagamento set padrao = false;

insert into meiopagamento (ativo, descricao, padrao, idUsuario)
	select true, 'Meio de Pagamento Padrão', true, id from usuario;
	
create table lancamentoconta (
	id bigint not null auto_increment,
	dataLancamento date not null,
	dataPagamento date not null,
	descricao varchar(100) not null,
	historico varchar(255) null,
	origem varchar(20) null,
	numeroDocumento varchar(50) null,
	observacao text null,
	valorPago double default 0,
	tipoLancamento varchar(10) not null,
	agendado tinyint(1) default false,
	quitado tinyint(1) default false,
	hashImportacao varchar(32) null,
	idConta bigint not null,
	idCategoria bigint null,
	idFavorecido bigint null,
	idMeioPagamento bigint null,
	idArquivo bigint null,
	primary key (id)
) engine=InnoDB;

insert into lancamentoconta (dataLancamento, dataPagamento, descricao, numeroDocumento, observacao, valorPago, tipoLancamento, agendado, quitado, idConta, idCategoria, idFavorecido, idMeioPagamento, idArquivo)
	select dataLancamento, dataPagamento, historico, numeroDocumento, observacao, valorPago, tipoLancamento, agendado, quitado, idContaBancaria, idCategoria, idFavorecido, idMeioPagamento, idArquivo from lancamento;

alter table lancamentoconta add constraint fk_conta_lancamentoconta foreign key (idConta) references conta (id);
alter table lancamentoconta add constraint fk_categoria_lancamentoconta foreign key (idCategoria) references categoria (id);
alter table lancamentoconta add constraint fk_favorecido_lancamentoconta foreign key (idFavorecido) references favorecido (id);
alter table lancamentoconta add constraint fk_meiopagamento_lancamentoconta foreign key (idMeioPagamento) references meiopagamento (id);
alter table lancamentoconta add constraint fk_arquivo_lancamentoconta foreign key (idArquivo) references arquivo (id);

create table previsaolancamentoconta (
	id bigint not null auto_increment,
	ano integer,
	descricaoPrevisao varchar(255) not null,
	janeiro double default 0,
	fevereiro double default 0,
	marco double default 0,
	abril double default 0,
	maio double default 0,
	junho double default 0,
	julho double default 0,
	agosto double default 0,
	setembro double default 0,
	outubro double default 0,
	novembro double default 0,
	dezembro double default 0,
	idConta bigint not null,
	indice integer not null,
	primary key (id)
);

alter table previsaolancamentoconta add constraint fk_conta_previsaolancamentoconta foreign key (idConta) references conta (id);

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUL2013.1 ***/

drop table lancamento;

update conta set datafechamento = null, saldoFinal = 0 where ativo = true;

alter table lancamentoconta add column descricaoCategoria varchar(255) default null;
alter table lancamentoconta add column descricaoFavorecido varchar(255) default null;
alter table lancamentoconta add column descricaoMeioPagamento varchar(255) default null;

alter table conta add column arquivado boolean default false;

update lancamentoconta l set l.descricaoCategoria = (select descricao from categoria where id = l.idCategoria) where l.idConta in (select id from conta where ativo = false);
update lancamentoconta l set l.descricaoFavorecido = (select nome from favorecido where id = l.idFavorecido) where l.idConta in (select id from conta where ativo = false);
update lancamentoconta l set l.descricaoMeioPagamento = (select descricao from meiopagamento where id = l.idMeioPagamento) where l.idConta in (select id from conta where ativo = false);

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUL2013.2 ***/

create table lancamentoimportado(
	id bigint not null auto_increment,
	data date not null,
	historico varchar(200) null,
	documento varchar(50) null,
	valor double not null,
	hash varchar(32) not null,
	idConta bigint not null,
	primary key (id)
);

alter table lancamentoimportado add constraint fk_conta_lancamentoimportado foreign key (idConta) references conta (id);

create table versao(
	id bigint not null auto_increment,
	versao varchar(30) not null,
	ativo boolean not null,
	primary key(id)
);

insert into versao (versao, ativo) values ('JUL2013.2', true);

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO JUL2013.3 ***/

update versao set ativo = false;
insert into versao (versao, ativo) values ('JUL2013.3', true);

update conta set tipoConta = 'CORRENTE' where tipoConta = 'CARTAO';

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO SET2013 ***/

update versao set ativo = false;
insert into versao (versao, ativo) values ('SET2013', true);

alter table buscasalva add column textoBusca varchar(50) null;
-- alter table buscasalva change `idConta` `idConta` bigint(20) not null;
alter table buscasalva change `saldoAnterior` `exibirSaldoAnterior` boolean;
alter table buscasalva add column tipoAgrupamentoBusca varchar(15) not null;

update buscasalva set tipoAgrupamentoBusca = 'NENHUM';

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO SET2013.1 ***/

update versao set ativo = false;
insert into versao (versao, ativo) values ('SET2013.1', true);

alter table previsaolancamentoconta add column agrupamento varchar(15) not null;
update previsaolancamentoconta set agrupamento = 'CATEGORIA';

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO DEZ2013 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('DEZ2013', true);

-- Auditoria por JSON
alter table auditoria add column versionEntity datetime not null;
alter table auditoria add column dadosAuditados mediumtext null;

update auditoria set versionEntity = dataHora;

-- Manter Documentos de Identidade
create table identidade(
	id bigint not null auto_increment,
	tipoIdentidade varchar(30) not null,
	numero varchar(20) null,
	orgaoExpedidor varchar(50) null,
	dataExpedicao date null,
	municipio varchar(100) null,
	uf varchar(2) null,
	secao varchar(4) null,
	zona varchar(3) null,
	serie varchar(5) null,
	livro varchar(5) null,
	folha varchar(5) null,
	dataValidade date null,
	dataPrimeiraHabilitacao date null,
	categoria varchar(5),
	pais varchar(30) null,
	idUsuario bigint not null,
	primary key (id)
);

alter table identidade add constraint fk_identidade foreign key (idUsuario) references usuario (id);

-- Manter Moeda
create table moeda (
	id bigint not null auto_increment,
	nome varchar(50) not null,
	pais varchar(50) not null,
	siglaPais varchar(2) not null,
	simboloMonetario varchar(5) not null,
	padrao boolean,
	ativo boolean,
	idUsuario bigint not null,
	primary key (id)
) ENGINE=InnoDB;

alter table moeda add constraint fk_usuario_moeda foreign key (idUsuario) references usuario (id);

insert into moeda (nome, pais, siglaPais, simboloMonetario, padrao, ativo, idUsuario) 
	select 'Real', 'Brasil', 'BR', 'R$', true, true, id from usuario;

-- Manter Cartão de Crédito
update cartaocredito set validade = '2016-01-01';

alter table cartaocredito add column idCartaoSubstituto bigint null;

alter table cartaocredito add foreign key (idCartaoSubstituto) references cartaocredito (id); 

insert into conta (ativo, idBanco, idUsuario, idCartao, tipoConta, dataAbertura, saldoInicial, saldoFinal, descricao)
	select ativo, idBanco, idUsuario, id, 'CARTAO', '2013-10-01', 0, 0, descricao from cartaocredito;
	
-- Manter Lançamentos do Cartão
alter table lancamentoconta add column parcela varchar(20) null;
alter table lancamentoconta add column idMoeda bigint null;
	
alter table lancamentoconta add constraint fk_moeda_lancamentoconta foreign key(idMoeda) references moeda (id);

-- Fatura do cartão
create table faturacartao(
	id bigint not null auto_increment,
	idConta bigint not null,
	idMoeda bigint not null,
	idLancamento bigint null,
	idArquivo bigint null,
	valorFatura decimal(18,2) not null,
	statusFaturaCartao varchar(10) not null,
	valorMinimo decimal(18,2) not null,
	dataFechamento date,
	dataVencimento date,
	dataPagamento date,
	valorPago decimal(18,2) not null,
	parcelado boolean,
	saldoDevedor decimal(18,2) not null,
	primary key(id)
) ENGINE=InnoDB;

alter table faturacartao add constraint fk_conta_faturacartao foreign key(idConta) references conta(id);
alter table faturacartao add constraint fk_moeda_faturacartao foreign key(idMoeda) references moeda(id);
alter table faturacartao add constraint fk_lancamentoconta_faturacartao foreign key(idLancamento) references lancamentoconta(id);
alter table faturacartao add constraint fk_arquivo_faturacartao foreign key(idArquivo) references arquivo(id);

-- Conversão de moedas
create table conversaomoeda(
	id bigint not null auto_increment,
	valor decimal(18,2) not null,
	taxaConversao decimal(18,4) not null,
	idMoeda bigint not null,
	idFaturaCartao bigint not null,
	primary key(id)
) ENGINE=InnoDB;

alter table conversaomoeda add constraint fk_faturacartao_conversaomoeda foreign key(idFaturaCartao) references faturacartao(id);

create table detalhefatura(
	idFaturaCartao bigint not null,
	idLancamento bigint not null
) ENGINE=InnoDB;

alter table detalhefatura add constraint fk_faturacartao_detalhefatura foreign key (idFaturaCartao) references faturacartao(id);
alter table detalhefatura add constraint fk_lancamentoconta_detalhefatura foreign key (idLancamento) references lancamentoconta(id);

-- Fechamento de período
alter table fechamentoperiodo add column mes integer not null;
alter table fechamentoperiodo add column ano integer not null;

update fechamentoperiodo set mes = month(data);
update fechamentoperiodo set ano = year(data);

/*** Arredondamento de casas decimais ***/

-- Abertura e fechamento de conta
update aberturafechamentoconta set saldo = round(saldo,2);
alter table aberturafechamentoconta change column `saldo` `saldo` decimal(18,2) not null;

-- Cartão de crédito
update cartaocredito set limiteCartao = round(limiteCartao,2), limiteSaque = round(limiteSaque,2);
alter table cartaocredito change column `limiteCartao` `limiteCartao` decimal(18,2) not null;
alter table cartaocredito change column `limiteSaque` `limiteSaque` decimal(18,2) not null;

-- Conta
update conta set saldoInicial = round(saldoInicial,2), saldoFinal = round(saldoFinal,2);
alter table conta change column `saldoInicial` `saldoInicial` decimal(18,2) not null;
alter table conta change column `saldoFinal` `saldoFinal` decimal(18,2) not null;

-- Fechamento de período
update fechamentoperiodo set saldo = round(saldo,2);
alter table fechamentoperiodo change column `saldo` `saldo` decimal(18,2) not null; 

-- Item de despensa
update itemdespensa set valor = round(valor,2);
alter table itemdespensa change column `valor` `valor` decimal(18,2) not null;

-- Lançamento da conta
update lancamentoconta set valorPago = round(valorPago,2);
alter table lancamentoconta change column `valorPago` `valorPago` decimal(18,2) not null;

-- Lançamentos importados
update lancamentoimportado set valor = round(valor,2);
alter table lancamentoimportado change column `valor` `valor` decimal(18,2) not null;

-- Movimento item despensa
update movimentoitemdespensa set valor = round(valor,2);
alter table movimentoitemdespensa change column `valor` `valor` decimal(18,2) not null;

-- Previsão de lançamentos da conta
alter table previsaolancamentoconta change column `janeiro` `janeiro` decimal(18,2) not null;
alter table previsaolancamentoconta change column `fevereiro` `fevereiro` decimal(18,2) not null;
alter table previsaolancamentoconta change column `marco` `marco` decimal(18,2) not null;
alter table previsaolancamentoconta change column `abril` `abril` decimal(18,2) not null;
alter table previsaolancamentoconta change column `maio` `maio` decimal(18,2) not null;
alter table previsaolancamentoconta change column `junho` `junho` decimal(18,2) not null;
alter table previsaolancamentoconta change column `julho` `julho` decimal(18,2) not null;
alter table previsaolancamentoconta change column `agosto` `agosto` decimal(18,2) not null;
alter table previsaolancamentoconta change column `setembro` `setembro` decimal(18,2) not null;
alter table previsaolancamentoconta change column `outubro` `outubro` decimal(18,2) not null;
alter table previsaolancamentoconta change column `novembro` `novembro` decimal(18,2) not null;
alter table previsaolancamentoconta change column `dezembro` `dezembro` decimal(18,2) not null;

/*** Fim do arredondamento de casas decimais ***/

-- Buscas salvas
alter table buscasalva add column textoParcela varchar(20) null;
alter table buscasalva add column lancadoEm date null;

-- Item de despensa
alter table itemdespensa add column perecivel boolean default false;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO DEZ2013.1 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('DEZ2013.1', true);

-- Lançamento da conta
alter table lancamentoconta change column `dataPagamento` `dataPagamento` date null;
alter table lancamentoconta change column `dataLancamento` `dataLancamento` date null;

update lancamentoconta set dataPagamento = null where idConta in (select id from conta where tipoConta = 'CARTAO');
update lancamentoconta set dataLancamento = null where idConta not in (select id from conta where tipoConta = 'CARTAO');

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO DEZ2013.2 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('DEZ2013.2', true);

-- Eliminação de instâncias de arquivo sem anexo.
update documento set idArquivo = null where idArquivo in (select id from arquivo where dados is null);
update faturacartao set idArquivo = null where idArquivo in (select id from arquivo where dados is null);
update lancamentoconta set idArquivo = null where idArquivo in (select id from arquivo where dados is null);

delete from arquivo where dados is null;

-- Panorama dos Lançamentos do Cartão
CREATE TABLE IF NOT EXISTS `panoramalancamentocartao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ano` int(11) DEFAULT NULL,
  `descricao` varchar(255) NOT NULL,
  `janeiro` decimal(18,2) NOT NULL,
  `fevereiro` decimal(18,2) NOT NULL,
  `marco` decimal(18,2) NOT NULL,
  `abril` decimal(18,2) NOT NULL,
  `maio` decimal(18,2) NOT NULL,
  `junho` decimal(18,2) NOT NULL,
  `julho` decimal(18,2) NOT NULL,
  `agosto` decimal(18,2) NOT NULL,
  `setembro` decimal(18,2) NOT NULL,
  `outubro` decimal(18,2) NOT NULL,
  `novembro` decimal(18,2) NOT NULL,
  `dezembro` decimal(18,2) NOT NULL,
  `idConta` bigint(20) NOT NULL,
  `idMoeda` bigint(20) NOT NULL,
  `indice` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_conta_panoramalancamentocartao` (`idConta`),
  KEY `fk_moeda_panoramalancamentocartao` (`idMoeda`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

update lancamentoconta lc set lc.dataPagamento = (select fc.dataVencimento from faturacartao fc inner join detalhefatura df on df.idFaturaCartao = fc.id where df.idLancamento = lc.id) where dataPagamento is null;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO DEZ2013.3 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('DEZ2013.3', true);

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO DEZ2013.4 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('DEZ2013.4', true);

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAR2014 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('MAR2014', true);

-- Informações pessoais do usuário
create table pessoal(
	id bigint not null auto_increment,
	genero char(1) not null default 'M',
	etnia varchar(50) null,
	tipoSanguineo varchar(5) null,
	dataNascimento date null,
	nacionalidade varchar(50) null,
	naturalidade varchar(50) null,
	escolaridade varchar(50) null,
	filiacaoPai varchar(100) null,
	filiacaoMae varchar(100) null,
	estadoCivil varchar(50) null,
	idUsuario bigint not null,
	primary key(id)
) engine=InnoDB; 

alter table pessoal add constraint fk_pessoal_usuario foreign key(idUsuario) references usuario(id);

-- Endereços do usuário
create table endereco(
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	tipoLogradouro varchar(30) not null,
	logradouro varchar(150) not null,
	numero varchar(10) null,
	complemento varchar(50) null,
	bairro varchar(50) not null,
	cidade varchar(100) not null,
	estado varchar(2) not null,
	cep varchar(8) not null,
	idUsuario bigint not null,
	primary key(id)
) engine=InnoDB;

alter table endereco add constraint fk_endereco_usuario foreign key(idUsuario) references usuario(id);

-- Telefones do usuário
create table telefone(
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	ddd varchar(5) null,
	numero varchar(15) not null,
	ramal varchar(5) null,
	idUsuario bigint not null,
	primary key(id)
) engine=InnoDB;

alter table telefone add constraint fk_telefone_usuario foreign key(idUsuario) references usuario(id);

-- Documentos
ALTER TABLE `orcamento`.`documento` DROP FOREIGN KEY `FK383D52B47083BD82` ;
ALTER TABLE `orcamento`.`documento` DROP COLUMN `idUsuario`, DROP INDEX `FK383D52B47083BD82`;

-- Controle de versão das entidades
alter table versao add column versionEntity datetime not null default '2014-01-01 00:00:00';
update versao set versionEntity = '2014-01-01 00:00:00';

alter table usuario add column versionEntity datetime not null default '2014-01-01 00:00:00';
update usuario set versionEntity = '2014-01-01 00:00:00';

alter table unidademedida add column versionEntity datetime not null default '2014-01-01 00:00:00';
update unidademedida set versionEntity = '2014-01-01 00:00:00';

alter table telefone add column versionEntity datetime not null default '2014-01-01 00:00:00';
update telefone set versionEntity = '2014-01-01 00:00:00';

alter table previsaolancamentoconta add column versionEntity datetime not null default '2014-01-01 00:00:00';
update previsaolancamentoconta set versionEntity = '2014-01-01 00:00:00';

alter table pessoal add column versionEntity datetime not null default '2014-01-01 00:00:00';
update pessoal set versionEntity = '2014-01-01 00:00:00';

alter table panoramalancamentocartao add column versionEntity datetime not null default '2014-01-01 00:00:00';
update panoramalancamentocartao set versionEntity = '2014-01-01 00:00:00';

alter table opcaosistema add column versionEntity datetime not null default '2014-01-01 00:00:00';
update opcaosistema set versionEntity = '2014-01-01 00:00:00';

alter table movimentoitemdespensa add column versionEntity datetime not null default '2014-01-01 00:00:00';
update movimentoitemdespensa set versionEntity = '2014-01-01 00:00:00';

alter table moeda add column versionEntity datetime not null default '2014-01-01 00:00:00';
update moeda set versionEntity = '2014-01-01 00:00:00';

alter table meiopagamento add column versionEntity datetime not null default '2014-01-01 00:00:00';
update meiopagamento set versionEntity = '2014-01-01 00:00:00';

alter table lancamentoimportado add column versionEntity datetime not null default '2014-01-01 00:00:00';
update lancamentoimportado set versionEntity = '2014-01-01 00:00:00';

alter table lancamentoconta add column versionEntity datetime not null default '2014-01-01 00:00:00';
update lancamentoconta set versionEntity = '2014-01-01 00:00:00';

alter table itemdespensa add column versionEntity datetime not null default '2014-01-01 00:00:00';
update itemdespensa set versionEntity = '2014-01-01 00:00:00';

alter table identidade add column versionEntity datetime not null default '2014-01-01 00:00:00';
update identidade set versionEntity = '2014-01-01 00:00:00';

alter table fechamentoperiodo add column versionEntity datetime not null default '2014-01-01 00:00:00';
update fechamentoperiodo set versionEntity = '2014-01-01 00:00:00';

alter table favorecido add column versionEntity datetime not null default '2014-01-01 00:00:00';
update favorecido set versionEntity = '2014-01-01 00:00:00';

alter table faturacartao add column versionEntity datetime not null default '2014-01-01 00:00:00';
update faturacartao set versionEntity = '2014-01-01 00:00:00';

alter table endereco add column versionEntity datetime not null default '2014-01-01 00:00:00';
update endereco set versionEntity = '2014-01-01 00:00:00';

alter table documento add column versionEntity datetime not null default '2014-01-01 00:00:00';
update documento set versionEntity = '2014-01-01 00:00:00';

alter table despensa add column versionEntity datetime not null default '2014-01-01 00:00:00';
update despensa set versionEntity = '2014-01-01 00:00:00';

alter table conversaomoeda add column versionEntity datetime not null default '2014-01-01 00:00:00';
update conversaomoeda set versionEntity = '2014-01-01 00:00:00';

alter table conta add column versionEntity datetime not null default '2014-01-01 00:00:00';
update conta set versionEntity = '2014-01-01 00:00:00';

alter table categoriadocumento add column versionEntity datetime not null default '2014-01-01 00:00:00';
update categoriadocumento set versionEntity = '2014-01-01 00:00:00';

alter table categoria add column versionEntity datetime not null default '2014-01-01 00:00:00';
update categoria set versionEntity = '2014-01-01 00:00:00';

alter table cartaocredito add column versionEntity datetime not null default '2014-01-01 00:00:00';
update cartaocredito set versionEntity = '2014-01-01 00:00:00';

alter table buscasalva add column versionEntity datetime not null default '2014-01-01 00:00:00';
update buscasalva set versionEntity = '2014-01-01 00:00:00';

alter table banco add column versionEntity datetime not null default '2014-01-01 00:00:00';
update banco set versionEntity = '2014-01-01 00:00:00';

alter table arquivo add column versionEntity datetime not null default '2014-01-01 00:00:00';
update arquivo set versionEntity = '2014-01-01 00:00:00';

alter table aberturafechamentoconta add column versionEntity datetime not null default '2014-01-01 00:00:00';
update aberturafechamentoconta set versionEntity = '2014-01-01 00:00:00';

alter table auditoria change column `versionEntity` `versionAuditedEntity` datetime not null default '2014-01-01 00:00:00';

-- Previsão dos lançamentos da conta

delete from previsaolancamentoconta where agrupamento = 'FAVORECIDO';
delete from previsaolancamentoconta where agrupamento = 'MEIOPAGAMENTO';
alter table previsaolancamentoconta drop column agrupamento;

-- Agenda
create table agenda(
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	localAgendamento varchar(200) null,
	inicio datetime null,
	fim datetime null,
	tipoAgendamento varchar(15) not null,
	prioridadeTarefa varchar(10) null,
	diaInteiro boolean,
	concluido boolean,
	emitirAlerta boolean,
	notas text,
	idEntity bigint null,
	entity varchar(30) null,
	idUsuario bigint not null,
	versionEntity datetime not null default '2014-01-01 00:00:00',
	primary key(id)
) engine=InnoDB;

alter table agenda add constraint fk_agenda_usuario foreign key(idUsuario) references usuario(id);

-- Panorama dos lançamentos da conta
ALTER TABLE previsaolancamentoconta RENAME TO panoramalancamentoconta;
ALTER TABLE panoramalancamentoconta CHANGE COLUMN `descricaoPrevisao` `descricao` VARCHAR(255) NOT NULL;

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAR2014.1 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('MAR2014.1', true);

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAR2014.2 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('MAR2014.2', true);

-- Cadastro de favorecidos
alter table favorecido add column cpfCnpj varchar(14) null;

-- Cadastro de moedas
alter table moeda add column valorConversao decimal(18,4) not null default 0.0;