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

update lancamentoconta lc set lc.dataPagamento = (select fc.dataVencimento from faturacartao fc inner join detalhefatura df on df.idFaturaCartao = fc.id where df.idLancamento = lc.id);