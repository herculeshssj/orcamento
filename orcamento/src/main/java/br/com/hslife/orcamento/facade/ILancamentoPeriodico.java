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
import java.util.Map;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.EntityPersistence;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusLancamento;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.enumeration.TipoLancamentoPeriodico;

public interface ILancamentoPeriodico extends ICRUDService<LancamentoPeriodico> {
	
	List<LancamentoPeriodico> buscarPorTipoLancamentoContaEStatusLancamento(TipoLancamentoPeriodico tipo, Conta conta, StatusLancamento statusLancamento);
	
	List<LancamentoPeriodico> buscarPorTipoLancamentoETipoContaEStatusLancamento(TipoLancamentoPeriodico tipo, TipoConta tipoConta, StatusLancamento statusLancamento);
	
	void alterarStatusLancamento(LancamentoPeriodico entity, StatusLancamento novoStatus);
	
	void registrarPagamento(LancamentoConta pagamentoPeriodo);
	
	List<LancamentoPeriodico> buscarPorTipoLancamentoEStatusLancamentoPorUsuario(TipoLancamentoPeriodico tipo, StatusLancamento status, Usuario usuario);
	
	void mesclarLancamentos(LancamentoConta pagamentoPeriodo, LancamentoConta lancamentoAMesclar);
	
	void vincularLancamentos(LancamentoPeriodico lancamentoPeriodico, List<LancamentoConta> lancamentosAVincular);

	void removerLancamentos(List<LancamentoConta> lancamentosARemover);
		
	void gerarParcelas(LancamentoPeriodico lancamentoPeriodico);
	
	void gerarMensalidade(LancamentoPeriodico entity);
	
	List<LancamentoPeriodico> buscarDescricaoEDataAquisicaoPorUsuario(String descricao, Date dataInicio, Date dataFim, Usuario usuario);
	
	void reclassificarLancamento(LancamentoPeriodico entity, Map<String, ? super EntityPersistence> parametros);
}
