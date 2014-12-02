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

/*** Script de criação da base de dados ***/

-- MySQL dump 10.13  Distrib 5.5.40, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: orcamento
-- ------------------------------------------------------
-- Server version	5.5.40-0ubuntu0.12.04.1-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `agenda`
--

DROP TABLE IF EXISTS `agenda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agenda` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(200) NOT NULL,
  `localAgendamento` varchar(200) DEFAULT NULL,
  `inicio` datetime DEFAULT NULL,
  `fim` datetime DEFAULT NULL,
  `tipoAgendamento` varchar(15) NOT NULL,
  `prioridadeTarefa` varchar(10) DEFAULT NULL,
  `diaInteiro` tinyint(1) DEFAULT NULL,
  `concluido` tinyint(1) DEFAULT NULL,
  `emitirAlerta` tinyint(1) DEFAULT NULL,
  `notas` text,
  `idEntity` bigint(20) DEFAULT NULL,
  `entity` varchar(30) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_agenda_usuario` (`idUsuario`),
  CONSTRAINT `fk_agenda_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `arquivo`
--

DROP TABLE IF EXISTS `arquivo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arquivo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nomeArquivo` varchar(255) DEFAULT NULL,
  `contentType` varchar(255) DEFAULT NULL,
  `tamanho` bigint(20) DEFAULT NULL,
  `dados` mediumblob,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `auditoria`
--

DROP TABLE IF EXISTS `auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auditoria` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `classe` varchar(255) NOT NULL,
  `data` date NOT NULL,
  `dataHora` datetime NOT NULL,
  `ip` varchar(255) NOT NULL,
  `transacao` varchar(255) NOT NULL,
  `usuario` varchar(255) NOT NULL,
  `versionAuditedEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `dadosAuditados` mediumtext,
  `browser` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `banco`
--

DROP TABLE IF EXISTS `banco`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `banco` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `numero` varchar(5) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `ativo` tinyint(1) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `FK592C0BB7083BD82` (`idUsuario`),
  CONSTRAINT `FK592C0BB7083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `buscasalva`
--

DROP TABLE IF EXISTS `buscasalva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `buscasalva` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataFim` date DEFAULT NULL,
  `dataInicio` date DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `idConta` bigint(20) NOT NULL,
  `textoBusca` varchar(50) DEFAULT NULL,
  `tipoAgrupamentoBusca` varchar(15) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `pesquisarTermo` tinyint(1) DEFAULT '0',
  `idAgrupamento` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK129C7AEB7DD97C50` (`idConta`),
  CONSTRAINT `FK129C7AEB7DD97C50` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cartaocredito`
--

DROP TABLE IF EXISTS `cartaocredito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cartaocredito` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `abrangencia` varchar(15) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `bandeira` varchar(30) DEFAULT NULL,
  `diaFechamentoFatura` int(11) NOT NULL,
  `diaVencimentoFatura` int(11) NOT NULL,
  `juros` double DEFAULT NULL,
  `limiteCartao` decimal(18,2) NOT NULL,
  `limiteSaque` decimal(18,2) NOT NULL,
  `multa` double DEFAULT NULL,
  `tipoCartao` varchar(10) DEFAULT NULL,
  `validade` date NOT NULL,
  `idBanco` bigint(20) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `descricao` varchar(50) NOT NULL,
  `idCartaoSubstituto` bigint(20) DEFAULT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `numeroCartao` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9BFA41887083BD82` (`idUsuario`),
  KEY `FK9BFA4188A36EA1C` (`idBanco`),
  KEY `idCartaoSubstituto` (`idCartaoSubstituto`),
  CONSTRAINT `cartaocredito_ibfk_1` FOREIGN KEY (`idCartaoSubstituto`) REFERENCES `cartaocredito` (`id`),
  CONSTRAINT `cartaocredito_ibfk_2` FOREIGN KEY (`idCartaoSubstituto`) REFERENCES `cartaocredito` (`id`),
  CONSTRAINT `FK9BFA41887083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FK9BFA4188A36EA1C` FOREIGN KEY (`idBanco`) REFERENCES `banco` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `tipoCategoria` varchar(10) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `FK5D54E1337083BD82` (`idUsuario`),
  CONSTRAINT `FK5D54E1337083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `categoriadocumento`
--

DROP TABLE IF EXISTS `categoriadocumento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoriadocumento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `FKD0771AE17083BD82` (`idUsuario`),
  CONSTRAINT `FKD0771AE17083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conta`
--

DROP TABLE IF EXISTS `conta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agencia` varchar(10) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `contaCorrente` varchar(15) DEFAULT NULL,
  `contaPoupanca` varchar(15) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `variacao` varchar(10) DEFAULT NULL,
  `idBanco` bigint(20) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `idCartao` bigint(20) DEFAULT NULL,
  `tipoConta` varchar(10) NOT NULL,
  `dataAbertura` date NOT NULL,
  `dataFechamento` date DEFAULT NULL,
  `saldoInicial` decimal(18,2) NOT NULL,
  `saldoFinal` decimal(18,2) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `idMoeda` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5A7376F7083BD82` (`idUsuario`),
  KEY `FK5A7376FA36EA1C` (`idBanco`),
  KEY `FK5A7376F2CE1B172` (`idCartao`),
  KEY `fk_conta_moeda` (`idMoeda`),
  CONSTRAINT `FK5A7376F2CE1B172` FOREIGN KEY (`idCartao`) REFERENCES `cartaocredito` (`id`),
  CONSTRAINT `FK5A7376F7083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FK5A7376FA36EA1C` FOREIGN KEY (`idBanco`) REFERENCES `banco` (`id`),
  CONSTRAINT `fk_conta_moeda` FOREIGN KEY (`idMoeda`) REFERENCES `moeda` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conversaomoeda`
--

DROP TABLE IF EXISTS `conversaomoeda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `conversaomoeda` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `valor` decimal(18,2) NOT NULL,
  `taxaConversao` decimal(18,4) NOT NULL,
  `idMoeda` bigint(20) NOT NULL,
  `idFaturaCartao` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_faturacartao_conversaomoeda` (`idFaturaCartao`),
  CONSTRAINT `fk_faturacartao_conversaomoeda` FOREIGN KEY (`idFaturaCartao`) REFERENCES `faturacartao` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `despensa`
--

DROP TABLE IF EXISTS `despensa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `despensa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `localArmazenamento` varchar(255) DEFAULT NULL,
  `idUsuario` bigint(20) DEFAULT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_despensa` (`idUsuario`),
  CONSTRAINT `fk_despensa` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalhefatura`
--

DROP TABLE IF EXISTS `detalhefatura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `detalhefatura` (
  `idFaturaCartao` bigint(20) NOT NULL,
  `idLancamento` bigint(20) NOT NULL,
  KEY `fk_faturacartao_detalhefatura` (`idFaturaCartao`),
  KEY `fk_lancamentoconta_detalhefatura` (`idLancamento`),
  CONSTRAINT `fk_faturacartao_detalhefatura` FOREIGN KEY (`idFaturaCartao`) REFERENCES `faturacartao` (`id`),
  CONSTRAINT `fk_lancamentoconta_detalhefatura` FOREIGN KEY (`idLancamento`) REFERENCES `lancamentoconta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `detalheorcamento`
--

DROP TABLE IF EXISTS `detalheorcamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `detalheorcamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `idEntity` bigint(20) NOT NULL,
  `tipoCategoria` varchar(10) DEFAULT NULL,
  `previsao` decimal(18,2) DEFAULT '0.00',
  `previsaoCredito` decimal(18,2) DEFAULT '0.00',
  `previsaoDebito` decimal(18,2) DEFAULT '0.00',
  `realizado` decimal(18,2) DEFAULT '0.00',
  `realizadoCredito` decimal(18,2) DEFAULT '0.00',
  `realizadoDebito` decimal(18,2) DEFAULT '0.00',
  `versionEntity` datetime NOT NULL DEFAULT '2014-11-01 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `documento`
--

DROP TABLE IF EXISTS `documento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `documento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(255) DEFAULT NULL,
  `nome` varchar(50) NOT NULL,
  `idArquivo` bigint(20) NOT NULL,
  `idCategoriaDocumento` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `FK383D52B46CA8AABC` (`idCategoriaDocumento`),
  KEY `FK383D52B428C1FC80` (`idArquivo`),
  CONSTRAINT `FK383D52B428C1FC80` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`),
  CONSTRAINT `FK383D52B46CA8AABC` FOREIGN KEY (`idCategoriaDocumento`) REFERENCES `categoriadocumento` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `endereco`
--

DROP TABLE IF EXISTS `endereco`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `endereco` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `tipoLogradouro` varchar(30) NOT NULL,
  `logradouro` varchar(150) NOT NULL,
  `numero` varchar(10) DEFAULT NULL,
  `complemento` varchar(50) DEFAULT NULL,
  `bairro` varchar(50) NOT NULL,
  `cidade` varchar(100) NOT NULL,
  `estado` varchar(2) NOT NULL,
  `cep` varchar(8) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_endereco_usuario` (`idUsuario`),
  CONSTRAINT `fk_endereco_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `faturacartao`
--

DROP TABLE IF EXISTS `faturacartao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `faturacartao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idConta` bigint(20) NOT NULL,
  `idMoeda` bigint(20) NOT NULL,
  `idLancamento` bigint(20) DEFAULT NULL,
  `idArquivo` bigint(20) DEFAULT NULL,
  `valorFatura` decimal(18,2) NOT NULL,
  `statusFaturaCartao` varchar(10) NOT NULL,
  `valorMinimo` decimal(18,2) NOT NULL,
  `dataFechamento` date DEFAULT NULL,
  `dataVencimento` date DEFAULT NULL,
  `dataPagamento` date DEFAULT NULL,
  `valorPago` decimal(18,2) NOT NULL,
  `parcelado` tinyint(1) DEFAULT NULL,
  `saldoDevedor` decimal(18,2) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `mes` int(11) NOT NULL,
  `ano` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_conta_faturacartao` (`idConta`),
  KEY `fk_moeda_faturacartao` (`idMoeda`),
  KEY `fk_lancamentoconta_faturacartao` (`idLancamento`),
  KEY `fk_arquivo_faturacartao` (`idArquivo`),
  CONSTRAINT `fk_arquivo_faturacartao` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`),
  CONSTRAINT `fk_conta_faturacartao` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`),
  CONSTRAINT `fk_lancamentoconta_faturacartao` FOREIGN KEY (`idLancamento`) REFERENCES `lancamentoconta` (`id`),
  CONSTRAINT `fk_moeda_faturacartao` FOREIGN KEY (`idMoeda`) REFERENCES `moeda` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `favorecido`
--

DROP TABLE IF EXISTS `favorecido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `favorecido` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `contato` text,
  `nome` varchar(100) NOT NULL,
  `tipoPessoa` varchar(10) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `padrao` tinyint(1) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `cpfCnpj` varchar(14) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1D15D5D87083BD82` (`idUsuario`),
  CONSTRAINT `FK1D15D5D87083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fechamentoperiodo`
--

DROP TABLE IF EXISTS `fechamentoperiodo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fechamentoperiodo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `saldo` decimal(18,2) NOT NULL,
  `idContaBancaria` bigint(20) NOT NULL,
  `operacao` varchar(10) NOT NULL,
  `usuario` varchar(255) NOT NULL,
  `dataAlteracao` datetime NOT NULL,
  `mes` int(11) NOT NULL,
  `ano` int(11) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `FKEC523EC8BA3900C1` (`idContaBancaria`),
  CONSTRAINT `FKEC523EC8BA3900C1` FOREIGN KEY (`idContaBancaria`) REFERENCES `conta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identidade`
--

DROP TABLE IF EXISTS `identidade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identidade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tipoIdentidade` varchar(30) NOT NULL,
  `numero` varchar(20) DEFAULT NULL,
  `orgaoExpedidor` varchar(50) DEFAULT NULL,
  `dataExpedicao` date DEFAULT NULL,
  `municipio` varchar(100) DEFAULT NULL,
  `uf` varchar(2) DEFAULT NULL,
  `secao` varchar(4) DEFAULT NULL,
  `zona` varchar(3) DEFAULT NULL,
  `serie` varchar(5) DEFAULT NULL,
  `livro` varchar(5) DEFAULT NULL,
  `folha` varchar(5) DEFAULT NULL,
  `dataValidade` date DEFAULT NULL,
  `dataPrimeiraHabilitacao` date DEFAULT NULL,
  `categoria` varchar(5) DEFAULT NULL,
  `pais` varchar(30) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_identidade` (`idUsuario`),
  CONSTRAINT `fk_identidade` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itemdespensa`
--

DROP TABLE IF EXISTS `itemdespensa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `itemdespensa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `arquivado` tinyint(1) DEFAULT NULL,
  `caracteristicas` varchar(255) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `quantidadeAmarelo` int(11) NOT NULL,
  `quantidadeAtual` int(11) NOT NULL,
  `quantidadeVerde` int(11) NOT NULL,
  `quantidadeVermelho` int(11) NOT NULL,
  `idUnidadeMedida` bigint(20) DEFAULT NULL,
  `idUsuario` bigint(20) DEFAULT NULL,
  `idDespensa` bigint(20) NOT NULL,
  `validade` datetime DEFAULT NULL,
  `valor` decimal(18,2) NOT NULL,
  `perecivel` tinyint(1) DEFAULT '0',
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `FK3D6214555F6482A2` (`idUnidadeMedida`),
  KEY `FK3D621455EC275F4E` (`idUsuario`),
  CONSTRAINT `FK3D6214555F6482A2` FOREIGN KEY (`idUnidadeMedida`) REFERENCES `unidademedida` (`id`),
  CONSTRAINT `FK3D621455EC275F4E` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `itemdespensa_movimentoitemdespensa`
--

DROP TABLE IF EXISTS `itemdespensa_movimentoitemdespensa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `itemdespensa_movimentoitemdespensa` (
  `itemDespensa_id` bigint(20) NOT NULL,
  `movimentacao_id` bigint(20) NOT NULL,
  UNIQUE KEY `movimentacao_id` (`movimentacao_id`),
  KEY `fk_itemdespensa` (`itemDespensa_id`),
  CONSTRAINT `fk_itemdespensa` FOREIGN KEY (`itemDespensa_id`) REFERENCES `itemdespensa` (`id`),
  CONSTRAINT `fk_movimentoitemdespensa` FOREIGN KEY (`movimentacao_id`) REFERENCES `movimentoitemdespensa` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lancamentoconta`
--

DROP TABLE IF EXISTS `lancamentoconta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lancamentoconta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataPagamento` date DEFAULT NULL,
  `descricao` varchar(200) NOT NULL,
  `historico` varchar(255) DEFAULT NULL,
  `origem` varchar(20) DEFAULT NULL,
  `numeroDocumento` varchar(50) DEFAULT NULL,
  `observacao` text,
  `valorPago` decimal(18,2) NOT NULL,
  `tipoLancamento` varchar(10) NOT NULL,
  `hashImportacao` varchar(32) DEFAULT NULL,
  `idConta` bigint(20) NOT NULL,
  `idCategoria` bigint(20) DEFAULT NULL,
  `idFavorecido` bigint(20) DEFAULT NULL,
  `idMeioPagamento` bigint(20) DEFAULT NULL,
  `idArquivo` bigint(20) DEFAULT NULL,
  `parcela` int(11) DEFAULT '0',
  `idMoeda` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `ano` int(11) DEFAULT '0',
  `periodo` int(11) DEFAULT '0',
  `dataVencimento` date DEFAULT NULL,
  `idLancamentoPeriodico` bigint(20) DEFAULT NULL,
  `statusLancamentoConta` varchar(15) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_conta_lancamentoconta` (`idConta`),
  KEY `fk_categoria_lancamentoconta` (`idCategoria`),
  KEY `fk_favorecido_lancamentoconta` (`idFavorecido`),
  KEY `fk_meiopagamento_lancamentoconta` (`idMeioPagamento`),
  KEY `fk_arquivo_lancamentoconta` (`idArquivo`),
  KEY `fk_moeda_lancamentoconta` (`idMoeda`),
  KEY `fk_lancamentoperiodico_lancamentoconta` (`idLancamentoPeriodico`),
  CONSTRAINT `fk_arquivo_lancamentoconta` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`),
  CONSTRAINT `fk_categoria_lancamentoconta` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`id`),
  CONSTRAINT `fk_conta_lancamentoconta` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`),
  CONSTRAINT `fk_favorecido_lancamentoconta` FOREIGN KEY (`idFavorecido`) REFERENCES `favorecido` (`id`),
  CONSTRAINT `fk_lancamentoperiodico_lancamentoconta` FOREIGN KEY (`idLancamentoPeriodico`) REFERENCES `lancamentoperiodico` (`id`),
  CONSTRAINT `fk_meiopagamento_lancamentoconta` FOREIGN KEY (`idMeioPagamento`) REFERENCES `meiopagamento` (`id`),
  CONSTRAINT `fk_moeda_lancamentoconta` FOREIGN KEY (`idMoeda`) REFERENCES `moeda` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lancamentoimportado`
--

DROP TABLE IF EXISTS `lancamentoimportado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lancamentoimportado` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `historico` varchar(200) DEFAULT NULL,
  `documento` varchar(50) DEFAULT NULL,
  `valor` decimal(18,2) NOT NULL,
  `hash` varchar(32) NOT NULL,
  `idConta` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `moeda` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_conta_lancamentoimportado` (`idConta`),
  CONSTRAINT `fk_conta_lancamentoimportado` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `lancamentoperiodico`
--

DROP TABLE IF EXISTS `lancamentoperiodico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lancamentoperiodico` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataAquisicao` date NOT NULL,
  `descricao` varchar(50) NOT NULL,
  `observacao` text,
  `valorParcela` decimal(18,2) DEFAULT '0.00',
  `valorCompra` decimal(18,2) DEFAULT NULL,
  `tipoLancamento` varchar(10) NOT NULL,
  `statusLancamento` varchar(15) NOT NULL,
  `idConta` bigint(20) NOT NULL,
  `idCategoria` bigint(20) DEFAULT NULL,
  `idFavorecido` bigint(20) DEFAULT NULL,
  `idMeioPagamento` bigint(20) DEFAULT NULL,
  `idArquivo` bigint(20) DEFAULT NULL,
  `idMoeda` bigint(20) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `totalParcela` int(11) DEFAULT NULL,
  `diaVencimento` int(11) NOT NULL,
  `tipoLancamentoPeriodico` varchar(10) NOT NULL,
  `periodoLancamento` varchar(20) DEFAULT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-05-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_lancamentoperiodico_conta` (`idConta`),
  KEY `fk_lancamentoperiodico_categoria` (`idCategoria`),
  KEY `fk_lancamentoperiodico_favorecido` (`idFavorecido`),
  KEY `fk_lancamentoperiodico_meiopagamento` (`idMeioPagamento`),
  KEY `fk_lancamentoperiodico_arquivo` (`idArquivo`),
  KEY `fk_lancamentoperiodico_moeda` (`idMoeda`),
  KEY `fk_lancamentoperiodico_usuario` (`idUsuario`),
  CONSTRAINT `fk_lancamentoperiodico_arquivo` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`),
  CONSTRAINT `fk_lancamentoperiodico_categoria` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`id`),
  CONSTRAINT `fk_lancamentoperiodico_conta` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`),
  CONSTRAINT `fk_lancamentoperiodico_favorecido` FOREIGN KEY (`idFavorecido`) REFERENCES `favorecido` (`id`),
  CONSTRAINT `fk_lancamentoperiodico_meiopagamento` FOREIGN KEY (`idMeioPagamento`) REFERENCES `meiopagamento` (`id`),
  CONSTRAINT `fk_lancamentoperiodico_moeda` FOREIGN KEY (`idMoeda`) REFERENCES `moeda` (`id`),
  CONSTRAINT `fk_lancamentoperiodico_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `meiopagamento`
--

DROP TABLE IF EXISTS `meiopagamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meiopagamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `FKC31F74287083BD82` (`idUsuario`),
  CONSTRAINT `FKC31F74287083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `moeda`
--

DROP TABLE IF EXISTS `moeda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `moeda` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `pais` varchar(50) NOT NULL,
  `siglaPais` varchar(2) NOT NULL,
  `simboloMonetario` varchar(5) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  `valorConversao` decimal(18,4) NOT NULL DEFAULT '0.0000',
  `codigoMonetario` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_usuario_moeda` (`idUsuario`),
  CONSTRAINT `fk_usuario_moeda` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `movimentoitemdespensa`
--

DROP TABLE IF EXISTS `movimentoitemdespensa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `movimentoitemdespensa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataOperacao` date DEFAULT NULL,
  `operacaoDespensa` varchar(10) DEFAULT NULL,
  `quantidade` int(11) NOT NULL,
  `valor` decimal(18,2) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `opcaosistema`
--

DROP TABLE IF EXISTS `opcaosistema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `opcaosistema` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chave` varchar(100) NOT NULL,
  `valor` text,
  `tipoOpcaoSistema` varchar(15) NOT NULL,
  `enabled` tinyint(1) DEFAULT NULL,
  `visible` tinyint(1) DEFAULT NULL,
  `required` tinyint(1) DEFAULT '0',
  `tipoValor` varchar(20) NOT NULL,
  `casoDeUso` varchar(100) NOT NULL,
  `idUsuario` bigint(20) DEFAULT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_opcaosistema` (`idUsuario`),
  CONSTRAINT `fk_opcaosistema` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orcamento`
--

DROP TABLE IF EXISTS `orcamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orcamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `tipoOrcamento` varchar(15) NOT NULL,
  `tipoConta` varchar(10) DEFAULT NULL,
  `inicio` date NOT NULL,
  `fim` date NOT NULL,
  `periodoLancamento` varchar(15) NOT NULL,
  `automatico` tinyint(1) DEFAULT NULL,
  `arquivar` tinyint(1) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `abrangenciaOrcamento` varchar(15) NOT NULL,
  `idConta` bigint(20) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-11-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_conta_orcamento` (`idConta`),
  KEY `fk_usuario_orcamento` (`idUsuario`),
  CONSTRAINT `fk_usuario_orcamento` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`),
  CONSTRAINT `fk_conta_orcamento` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orcamento_detalheorcamento`
--

DROP TABLE IF EXISTS `orcamento_detalheorcamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orcamento_detalheorcamento` (
  `orcamento_id` bigint(20) NOT NULL,
  `detalhes_id` bigint(20) NOT NULL,
  UNIQUE KEY `detalhes_id` (`detalhes_id`),
  KEY `fk_orcamento` (`orcamento_id`),
  CONSTRAINT `fk_detalheorcamento` FOREIGN KEY (`detalhes_id`) REFERENCES `detalheorcamento` (`id`),
  CONSTRAINT `fk_orcamento` FOREIGN KEY (`orcamento_id`) REFERENCES `orcamento` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pessoal`
--

DROP TABLE IF EXISTS `pessoal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pessoal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `genero` char(1) NOT NULL DEFAULT 'M',
  `etnia` varchar(50) DEFAULT NULL,
  `tipoSanguineo` varchar(5) DEFAULT NULL,
  `dataNascimento` date DEFAULT NULL,
  `nacionalidade` varchar(50) DEFAULT NULL,
  `naturalidade` varchar(50) DEFAULT NULL,
  `escolaridade` varchar(50) DEFAULT NULL,
  `filiacaoPai` varchar(100) DEFAULT NULL,
  `filiacaoMae` varchar(100) DEFAULT NULL,
  `estadoCivil` varchar(50) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_pessoal_usuario` (`idUsuario`),
  CONSTRAINT `fk_pessoal_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `telefone`
--

DROP TABLE IF EXISTS `telefone`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `telefone` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `ddd` varchar(5) DEFAULT NULL,
  `numero` varchar(15) NOT NULL,
  `ramal` varchar(5) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `fk_telefone_usuario` (`idUsuario`),
  CONSTRAINT `fk_telefone_usuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `unidademedida`
--

DROP TABLE IF EXISTS `unidademedida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unidademedida` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `sigla` varchar(10) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  KEY `FK2F2C0D88EC275F4E` (`idUsuario`),
  CONSTRAINT `FK2F2C0D88EC275F4E` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `dataCriacao` datetime DEFAULT NULL,
  `login` varchar(50) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `senha` varchar(64) NOT NULL,
  `tipoUsuario` varchar(10) DEFAULT NULL,
  `email` varchar(40) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `versao`
--

DROP TABLE IF EXISTS `versao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `versao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `versao` varchar(30) NOT NULL,
  `ativo` tinyint(1) NOT NULL,
  `versionEntity` datetime NOT NULL DEFAULT '2014-01-01 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-11-30 12:11:42
  
--
-- Extraindo dados da tabela `opcaosistema`
--

INSERT INTO `opcaosistema` (`chave`, `valor`, `tipoOpcaoSistema`, `enabled`, `visible`, `required`, `tipoValor`, `casoDeUso`, `idUsuario`) VALUES
('EMAIL_SERVIDOR', 'smtp.hslife.com.br', 'GLOBAL_ADMIN', 1, 1, 1, 'STRING', 'email', NULL),
('EMAIL_PORTA', '465', 'GLOBAL_ADMIN', 1, 1, 1, 'INTEGER', 'email', NULL),
('EMAIL_USUARIO', 'nao-responde@hslife.com.br', 'GLOBAL_ADMIN', 1, 1, 1, 'STRING', 'email', NULL),
('EMAIL_SENHA', 'n0r3ply1@3', 'GLOBAL_ADMIN', 1, 1, 1, 'STRING', 'email', NULL),
('EMAIL_REMETENTE', 'HSlife Serviços de TI', 'GLOBAL_ADMIN', 1, 1, 1, 'STRING', 'email', NULL),
('EMAIL_EMAIL_REMETENTE', 'contato@hslife.com.br', 'GLOBAL_ADMIN', 1, 1, 1, 'STRING', 'email', NULL),
('EMAIL_SSL_TLS', 'true', 'GLOBAL_ADMIN', 1, 1, 1, 'BOOLEAN', 'email', NULL),
('EMAIL_CHARSET', 'UTF-8', 'GLOBAL_ADMIN', 1, 1, 1, 'STRING', 'email', NULL);

-- --------------------------------------------------------

--
-- Extraindo dados da tabela `usuario`
--

INSERT INTO `usuario` (`ativo`, `dataCriacao`, `login`, `nome`, `senha`, `tipoUsuario`, `email`) VALUES
(1, '2012-01-01 00:00:00', 'admin', 'Administrador do sistema', 'd033e22ae348aeb5660fc2140aec35850c4da997', 'ROLE_ADMIN', 'contato@hslife.com.br'); -- senha: admin

-- --------------------------------------------------------

--
-- Extraindo dados da tabela `versao`
--

INSERT INTO `versao` (`versao`, `ativo`) VALUES
('DEZ2014', 1);

--
-- Extraindo dados da tabela `categoria`
--

INSERT INTO `categoria` VALUES (1,1,'Categoria Padrão',1,1,'CREDITO'),(2,1,'Categoria Padrão',1,1,'DEBITO');

-- --------------------------------------------------------

--
-- Extraindo dados da tabela `favorecido`
--

INSERT INTO `favorecido` VALUES (1,1,NULL,'Favorecido Padrão','FISICA',1,1);

-- --------------------------------------------------------

--
-- Extraindo dados da tabela `meiopagamento`
--

INSERT INTO `meiopagamento` VALUES (1,1,'Meio de Pagamento Padrão',1,1);

-- --------------------------------------------------------

--
-- Extraindo dados da tabela `moeda`
--

INSERT INTO `moeda` VALUES (1,'Real','Brasil','BR','R$',1,1,1);

-- --------------------------------------------------------