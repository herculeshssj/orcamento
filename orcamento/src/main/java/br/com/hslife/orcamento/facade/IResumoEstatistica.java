/***

Copyright (c) 2012 - 2021 Hércules S. S. José

Este arquivo é parte do programa Orçamento Doméstico.


Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

Licença.


Este programa é distribuído na esperança que possa ser útil, mas SEM

NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer

MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor

GNU em português para maiores detalhes.


Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob

o nome de "LICENSE" junto com este programa, se não, acesse o site do

projeto no endereco https://github.com/herculeshssj/orcamento ou escreva

para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor,

Boston, MA  02110-1301, USA.


Para mais informações sobre o programa Orçamento Doméstico e seu autor

entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/
package br.com.hslife.orcamento.facade;

import java.util.Date;
import java.util.List;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.CadastroSistema;
import br.com.hslife.orcamento.model.CriterioBuscaLancamentoConta;
import br.com.hslife.orcamento.model.PanoramaLancamentoCartao;
import br.com.hslife.orcamento.model.PanoramaLancamentoConta;
import br.com.hslife.orcamento.model.ResumoMensalContas;
import br.com.hslife.orcamento.model.SaldoAtualConta;

public interface IResumoEstatistica {
	
	List<SaldoAtualConta> gerarSaldoAtualContas(boolean agendado, Usuario usuario);
	
	List<PanoramaLancamentoConta> gerarRelatorioPanoramaLancamentoConta(CriterioBuscaLancamentoConta criterioBusca, int ano);
	
	List<PanoramaLancamentoCartao> gerarRelatorioPanoramaLancamentoCartao(CriterioBuscaLancamentoConta criterioBusca, int ano);
	
	List<Conta> gerarRelatorioPanoramaCadastro(CadastroSistema cadastro, Long idRegistro);
	
	ResumoMensalContas gerarRelatorioResumoMensalContas(Conta conta, FaturaCartao faturaCartao);
	
	ResumoMensalContas gerarRelatorioResumoMensalContas(Conta conta, Date dataInicio, Date dataFim);
	
}
