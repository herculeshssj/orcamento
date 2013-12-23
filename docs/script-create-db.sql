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

-- phpMyAdmin SQL Dump
-- version 3.3.3
-- http://www.phpmyadmin.net
--
-- Servidor: mysql06.kinghost.net
-- Tempo de Geração: Dez 22, 2013 as 10:19 PM
-- Versão do Servidor: 5.1.70
-- Versão do PHP: 5.2.9

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Banco de Dados: `hslife02`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `aberturafechamentoconta`
--

CREATE TABLE IF NOT EXISTS `aberturafechamentoconta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `dataAlteracao` datetime NOT NULL,
  `operacao` varchar(10) NOT NULL,
  `saldo` decimal(18,2) NOT NULL,
  `usuario` varchar(255) NOT NULL,
  `idConta` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKB806F0BD7DD97C50` (`idConta`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `arquivo`
--

CREATE TABLE IF NOT EXISTS `arquivo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nomeArquivo` varchar(255) DEFAULT NULL,
  `contentType` varchar(255) DEFAULT NULL,
  `tamanho` bigint(20) DEFAULT NULL,
  `dados` mediumblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `auditoria`
--

CREATE TABLE IF NOT EXISTS `auditoria` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `classe` varchar(255) NOT NULL,
  `data` date NOT NULL,
  `dataHora` datetime NOT NULL,
  `ip` varchar(255) NOT NULL,
  `transacao` varchar(255) NOT NULL,
  `usuario` varchar(255) NOT NULL,
  `versionEntity` datetime NOT NULL,
  `dadosAuditados` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `auditoriadados`
--

CREATE TABLE IF NOT EXISTS `auditoriadados` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nomeAtributo` varchar(255) NOT NULL,
  `situacaoOperacao` varchar(255) NOT NULL,
  `valorAtributo` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `auditoria_auditoriadados`
--

CREATE TABLE IF NOT EXISTS `auditoria_auditoriadados` (
  `auditoria_id` bigint(20) NOT NULL,
  `dadosAuditoria_id` bigint(20) NOT NULL,
  UNIQUE KEY `dadosAuditoria_id` (`dadosAuditoria_id`),
  KEY `FKF2BF5F9E8841B637` (`dadosAuditoria_id`),
  KEY `FKF2BF5F9E7D80A527` (`auditoria_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estrutura da tabela `banco`
--

CREATE TABLE IF NOT EXISTS `banco` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `numero` varchar(5) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `ativo` tinyint(1) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK592C0BB7083BD82` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `buscasalva`
--

CREATE TABLE IF NOT EXISTS `buscasalva` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agendados` tinyint(1) DEFAULT NULL,
  `dataFim` date DEFAULT NULL,
  `dataInicio` date DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `exibirSaldoAnterior` tinyint(1) DEFAULT NULL,
  `idConta` bigint(20) NOT NULL,
  `textoBusca` varchar(50) DEFAULT NULL,
  `tipoAgrupamentoBusca` varchar(15) NOT NULL,
  `textoParcela` varchar(20) DEFAULT NULL,
  `lancadoEm` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK129C7AEB7DD97C50` (`idConta`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `cartaocredito`
--

CREATE TABLE IF NOT EXISTS `cartaocredito` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `abrangencia` varchar(15) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `bandeira` varchar(30) DEFAULT NULL,
  `codigoSeguranca` varchar(3) DEFAULT NULL,
  `diaFechamentoFatura` int(11) NOT NULL,
  `diaVencimentoFatura` int(11) NOT NULL,
  `juros` double DEFAULT NULL,
  `limiteCartao` decimal(18,2) NOT NULL,
  `limiteSaque` decimal(18,2) NOT NULL,
  `multa` double DEFAULT NULL,
  `nomeCliente` varchar(50) DEFAULT NULL,
  `numeroCartao` varchar(30) DEFAULT NULL,
  `tipoCartao` varchar(10) DEFAULT NULL,
  `validade` date NOT NULL,
  `idBanco` bigint(20) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `descricao` varchar(50) NOT NULL,
  `idCartaoSubstituto` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9BFA41887083BD82` (`idUsuario`),
  KEY `FK9BFA4188A36EA1C` (`idBanco`),
  KEY `idCartaoSubstituto` (`idCartaoSubstituto`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `categoria`
--

CREATE TABLE IF NOT EXISTS `categoria` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `tipoCategoria` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5D54E1337083BD82` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `categoriadocumento`
--

CREATE TABLE IF NOT EXISTS `categoriadocumento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKD0771AE17083BD82` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `conta`
--

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
  `idCartao` bigint(20) DEFAULT NULL,
  `tipoConta` varchar(10) NOT NULL,
  `dataAbertura` date NOT NULL,
  `dataFechamento` date DEFAULT NULL,
  `saldoInicial` decimal(18,2) NOT NULL,
  `saldoFinal` decimal(18,2) NOT NULL,
  `arquivado` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK5A7376F7083BD82` (`idUsuario`),
  KEY `FK5A7376FA36EA1C` (`idBanco`),
  KEY `FK5A7376F2CE1B172` (`idCartao`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `conversaomoeda`
--

CREATE TABLE IF NOT EXISTS `conversaomoeda` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `valor` decimal(18,2) NOT NULL,
  `taxaConversao` decimal(18,4) NOT NULL,
  `idMoeda` bigint(20) NOT NULL,
  `idFaturaCartao` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_faturacartao_conversaomoeda` (`idFaturaCartao`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `despensa`
--

CREATE TABLE IF NOT EXISTS `despensa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `localArmazenamento` varchar(255) DEFAULT NULL,
  `idUsuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_despensa` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `detalhefatura`
--

CREATE TABLE IF NOT EXISTS `detalhefatura` (
  `idFaturaCartao` bigint(20) NOT NULL,
  `idLancamento` bigint(20) NOT NULL,
  KEY `fk_faturacartao_detalhefatura` (`idFaturaCartao`),
  KEY `fk_lancamentoconta_detalhefatura` (`idLancamento`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estrutura da tabela `documento`
--

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
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `faturacartao`
--

CREATE TABLE IF NOT EXISTS `faturacartao` (
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
  PRIMARY KEY (`id`),
  KEY `fk_conta_faturacartao` (`idConta`),
  KEY `fk_moeda_faturacartao` (`idMoeda`),
  KEY `fk_lancamentoconta_faturacartao` (`idLancamento`),
  KEY `fk_arquivo_faturacartao` (`idArquivo`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `favorecido`
--

CREATE TABLE IF NOT EXISTS `favorecido` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `contato` text,
  `nome` varchar(100) NOT NULL,
  `tipoPessoa` varchar(10) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  `padrao` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1D15D5D87083BD82` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `fechamentoperiodo`
--

CREATE TABLE IF NOT EXISTS `fechamentoperiodo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `saldo` decimal(18,2) NOT NULL,
  `idContaBancaria` bigint(20) NOT NULL,
  `operacao` varchar(10) NOT NULL,
  `usuario` varchar(255) NOT NULL,
  `dataAlteracao` datetime NOT NULL,
  `mes` int(11) NOT NULL,
  `ano` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKEC523EC8BA3900C1` (`idContaBancaria`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `identidade`
--

CREATE TABLE IF NOT EXISTS `identidade` (
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
  PRIMARY KEY (`id`),
  KEY `fk_identidade` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `itemdespensa`
--

CREATE TABLE IF NOT EXISTS `itemdespensa` (
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
  PRIMARY KEY (`id`),
  KEY `FK3D6214555F6482A2` (`idUnidadeMedida`),
  KEY `FK3D621455EC275F4E` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `itemdespensa_movimentoitemdespensa`
--

CREATE TABLE IF NOT EXISTS `itemdespensa_movimentoitemdespensa` (
  `itemDespensa_id` bigint(20) NOT NULL,
  `movimentacao_id` bigint(20) NOT NULL,
  UNIQUE KEY `movimentacao_id` (`movimentacao_id`),
  KEY `fk_itemdespensa` (`itemDespensa_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Estrutura da tabela `lancamentoconta`
--

CREATE TABLE IF NOT EXISTS `lancamentoconta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataLancamento` date DEFAULT NULL,
  `dataPagamento` date DEFAULT NULL,
  `descricao` varchar(100) NOT NULL,
  `historico` varchar(255) DEFAULT NULL,
  `origem` varchar(20) DEFAULT NULL,
  `numeroDocumento` varchar(50) DEFAULT NULL,
  `observacao` text,
  `valorPago` decimal(18,2) NOT NULL,
  `tipoLancamento` varchar(10) NOT NULL,
  `agendado` tinyint(1) DEFAULT '0',
  `quitado` tinyint(1) DEFAULT '0',
  `hashImportacao` varchar(32) DEFAULT NULL,
  `idConta` bigint(20) NOT NULL,
  `idCategoria` bigint(20) DEFAULT NULL,
  `idFavorecido` bigint(20) DEFAULT NULL,
  `idMeioPagamento` bigint(20) DEFAULT NULL,
  `idArquivo` bigint(20) DEFAULT NULL,
  `descricaoCategoria` varchar(255) DEFAULT NULL,
  `descricaoFavorecido` varchar(255) DEFAULT NULL,
  `descricaoMeioPagamento` varchar(255) DEFAULT NULL,
  `parcela` varchar(20) DEFAULT NULL,
  `idMoeda` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_conta_lancamentoconta` (`idConta`),
  KEY `fk_categoria_lancamentoconta` (`idCategoria`),
  KEY `fk_favorecido_lancamentoconta` (`idFavorecido`),
  KEY `fk_meiopagamento_lancamentoconta` (`idMeioPagamento`),
  KEY `fk_arquivo_lancamentoconta` (`idArquivo`),
  KEY `fk_moeda_lancamentoconta` (`idMoeda`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `lancamentoimportado`
--

CREATE TABLE IF NOT EXISTS `lancamentoimportado` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `historico` varchar(200) DEFAULT NULL,
  `documento` varchar(50) DEFAULT NULL,
  `valor` decimal(18,2) NOT NULL,
  `hash` varchar(32) NOT NULL,
  `idConta` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_conta_lancamentoimportado` (`idConta`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `meiopagamento`
--

CREATE TABLE IF NOT EXISTS `meiopagamento` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `descricao` varchar(50) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC31F74287083BD82` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `moeda`
--

CREATE TABLE IF NOT EXISTS `moeda` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `pais` varchar(50) NOT NULL,
  `siglaPais` varchar(2) NOT NULL,
  `simboloMonetario` varchar(5) NOT NULL,
  `padrao` tinyint(1) DEFAULT NULL,
  `ativo` tinyint(1) DEFAULT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_usuario_moeda` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `movimentoitemdespensa`
--

CREATE TABLE IF NOT EXISTS `movimentoitemdespensa` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dataOperacao` date DEFAULT NULL,
  `operacaoDespensa` varchar(10) DEFAULT NULL,
  `quantidade` int(11) NOT NULL,
  `valor` decimal(18,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `opcaosistema`
--

CREATE TABLE IF NOT EXISTS `opcaosistema` (
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
  PRIMARY KEY (`id`),
  KEY `fk_opcaosistema` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `previsaolancamentoconta`
--

CREATE TABLE IF NOT EXISTS `previsaolancamentoconta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ano` int(11) DEFAULT NULL,
  `descricaoPrevisao` varchar(255) NOT NULL,
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
  `indice` int(11) NOT NULL,
  `agrupamento` varchar(15) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_conta_previsaolancamentoconta` (`idConta`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `unidademedida`
--

CREATE TABLE IF NOT EXISTS `unidademedida` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `sigla` varchar(10) NOT NULL,
  `idUsuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2F2C0D88EC275F4E` (`idUsuario`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `usuario`
--

CREATE TABLE IF NOT EXISTS `usuario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ativo` tinyint(1) DEFAULT NULL,
  `dataCriacao` datetime DEFAULT NULL,
  `login` varchar(50) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `senha` varchar(40) NOT NULL,
  `tipoUsuario` varchar(10) DEFAULT NULL,
  `email` varchar(40) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Estrutura da tabela `versao`
--

CREATE TABLE IF NOT EXISTS `versao` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `versao` varchar(30) NOT NULL,
  `ativo` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

--
-- Restrições para as tabelas dumpadas
--

--
-- Restrições para a tabela `aberturafechamentoconta`
--
ALTER TABLE `aberturafechamentoconta`
  ADD CONSTRAINT `FKB806F0BD7DD97C50` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`);

--
-- Restrições para a tabela `auditoria_auditoriadados`
--
ALTER TABLE `auditoria_auditoriadados`
  ADD CONSTRAINT `FKF2BF5F9E7D80A527` FOREIGN KEY (`auditoria_id`) REFERENCES `auditoria` (`id`),
  ADD CONSTRAINT `FKF2BF5F9E8841B637` FOREIGN KEY (`dadosAuditoria_id`) REFERENCES `auditoriadados` (`id`);

--
-- Restrições para a tabela `banco`
--
ALTER TABLE `banco`
  ADD CONSTRAINT `FK592C0BB7083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `buscasalva`
--
ALTER TABLE `buscasalva`
  ADD CONSTRAINT `FK129C7AEB7DD97C50` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`);

--
-- Restrições para a tabela `cartaocredito`
--
ALTER TABLE `cartaocredito`
  ADD CONSTRAINT `cartaocredito_ibfk_1` FOREIGN KEY (`idCartaoSubstituto`) REFERENCES `cartaocredito` (`id`),
  ADD CONSTRAINT `cartaocredito_ibfk_2` FOREIGN KEY (`idCartaoSubstituto`) REFERENCES `cartaocredito` (`id`),
  ADD CONSTRAINT `FK9BFA41887083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`),
  ADD CONSTRAINT `FK9BFA4188A36EA1C` FOREIGN KEY (`idBanco`) REFERENCES `banco` (`id`);

--
-- Restrições para a tabela `categoria`
--
ALTER TABLE `categoria`
  ADD CONSTRAINT `FK5D54E1337083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `categoriadocumento`
--
ALTER TABLE `categoriadocumento`
  ADD CONSTRAINT `FKD0771AE17083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `conta`
--
ALTER TABLE `conta`
  ADD CONSTRAINT `FK5A7376F2CE1B172` FOREIGN KEY (`idCartao`) REFERENCES `cartaocredito` (`id`),
  ADD CONSTRAINT `FK5A7376F7083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`),
  ADD CONSTRAINT `FK5A7376FA36EA1C` FOREIGN KEY (`idBanco`) REFERENCES `banco` (`id`);

--
-- Restrições para a tabela `conversaomoeda`
--
ALTER TABLE `conversaomoeda`
  ADD CONSTRAINT `fk_faturacartao_conversaomoeda` FOREIGN KEY (`idFaturaCartao`) REFERENCES `faturacartao` (`id`);

--
-- Restrições para a tabela `despensa`
--
ALTER TABLE `despensa`
  ADD CONSTRAINT `fk_despensa` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `detalhefatura`
--
ALTER TABLE `detalhefatura`
  ADD CONSTRAINT `fk_lancamentoconta_detalhefatura` FOREIGN KEY (`idLancamento`) REFERENCES `lancamentoconta` (`id`),
  ADD CONSTRAINT `fk_faturacartao_detalhefatura` FOREIGN KEY (`idFaturaCartao`) REFERENCES `faturacartao` (`id`);

--
-- Restrições para a tabela `documento`
--
ALTER TABLE `documento`
  ADD CONSTRAINT `FK383D52B428C1FC80` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`),
  ADD CONSTRAINT `FK383D52B46CA8AABC` FOREIGN KEY (`idCategoriaDocumento`) REFERENCES `categoriadocumento` (`id`),
  ADD CONSTRAINT `FK383D52B47083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `faturacartao`
--
ALTER TABLE `faturacartao`
  ADD CONSTRAINT `fk_arquivo_faturacartao` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`),
  ADD CONSTRAINT `fk_conta_faturacartao` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`),
  ADD CONSTRAINT `fk_lancamentoconta_faturacartao` FOREIGN KEY (`idLancamento`) REFERENCES `lancamentoconta` (`id`),
  ADD CONSTRAINT `fk_moeda_faturacartao` FOREIGN KEY (`idMoeda`) REFERENCES `moeda` (`id`);

--
-- Restrições para a tabela `favorecido`
--
ALTER TABLE `favorecido`
  ADD CONSTRAINT `FK1D15D5D87083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `fechamentoperiodo`
--
ALTER TABLE `fechamentoperiodo`
  ADD CONSTRAINT `FKEC523EC8BA3900C1` FOREIGN KEY (`idContaBancaria`) REFERENCES `conta` (`id`);

--
-- Restrições para a tabela `identidade`
--
ALTER TABLE `identidade`
  ADD CONSTRAINT `fk_identidade` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `itemdespensa`
--
ALTER TABLE `itemdespensa`
  ADD CONSTRAINT `FK3D6214555F6482A2` FOREIGN KEY (`idUnidadeMedida`) REFERENCES `unidademedida` (`id`),
  ADD CONSTRAINT `FK3D621455EC275F4E` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `itemdespensa_movimentoitemdespensa`
--
ALTER TABLE `itemdespensa_movimentoitemdespensa`
  ADD CONSTRAINT `fk_itemdespensa` FOREIGN KEY (`itemDespensa_id`) REFERENCES `itemdespensa` (`id`),
  ADD CONSTRAINT `fk_movimentoitemdespensa` FOREIGN KEY (`movimentacao_id`) REFERENCES `movimentoitemdespensa` (`id`);

--
-- Restrições para a tabela `lancamentoconta`
--
ALTER TABLE `lancamentoconta`
  ADD CONSTRAINT `fk_arquivo_lancamentoconta` FOREIGN KEY (`idArquivo`) REFERENCES `arquivo` (`id`),
  ADD CONSTRAINT `fk_categoria_lancamentoconta` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`id`),
  ADD CONSTRAINT `fk_conta_lancamentoconta` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`),
  ADD CONSTRAINT `fk_favorecido_lancamentoconta` FOREIGN KEY (`idFavorecido`) REFERENCES `favorecido` (`id`),
  ADD CONSTRAINT `fk_meiopagamento_lancamentoconta` FOREIGN KEY (`idMeioPagamento`) REFERENCES `meiopagamento` (`id`),
  ADD CONSTRAINT `fk_moeda_lancamentoconta` FOREIGN KEY (`idMoeda`) REFERENCES `moeda` (`id`);

--
-- Restrições para a tabela `lancamentoimportado`
--
ALTER TABLE `lancamentoimportado`
  ADD CONSTRAINT `fk_conta_lancamentoimportado` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`);

--
-- Restrições para a tabela `meiopagamento`
--
ALTER TABLE `meiopagamento`
  ADD CONSTRAINT `FKC31F74287083BD82` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `moeda`
--
ALTER TABLE `moeda`
  ADD CONSTRAINT `fk_usuario_moeda` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `opcaosistema`
--
ALTER TABLE `opcaosistema`
  ADD CONSTRAINT `fk_opcaosistema` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

--
-- Restrições para a tabela `previsaolancamentoconta`
--
ALTER TABLE `previsaolancamentoconta`
  ADD CONSTRAINT `fk_conta_previsaolancamentoconta` FOREIGN KEY (`idConta`) REFERENCES `conta` (`id`);

--
-- Restrições para a tabela `unidademedida`
--
ALTER TABLE `unidademedida`
  ADD CONSTRAINT `FK2F2C0D88EC275F4E` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`id`);

  
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
('DEZ2013', 1);

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