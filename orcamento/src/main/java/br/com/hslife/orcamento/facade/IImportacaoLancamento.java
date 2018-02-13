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

	para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth

	Floor, Boston, MA  02110-1301, USA.


	Para mais informações sobre o programa Orçamento Doméstico e seu autor

	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

	para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 -

	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/package br.com.hslife.orcamento.facade;

import java.util.List;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoImportado;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.model.InfoOFX;

public interface IImportacaoLancamento {
	
	public LancamentoImportado buscarPorID(Long id);

	public List<LancamentoImportado> buscarLancamentoImportadoPorConta(Conta conta);

	public void atualizarLancamentoImportado(LancamentoImportado entity);
	
	public void excluirLancamentoImportado(LancamentoImportado entity);
	
	public void  processarArquivoImportado(Arquivo arquivo, Conta conta) throws ApplicationException;
	
	public void processarArquivoCSVImportado(Arquivo arquivo, Conta conta) throws ApplicationException; 
	
	public List<LancamentoConta> buscarLancamentoContaACriarAtualizar(Conta conta, List<LancamentoImportado> lancamentosImportados);
	
	public List<LancamentoConta> buscarLancamentoContaACriarAtualizar(Conta conta, List<LancamentoImportado> lancamentosImportados, boolean quitarAutomaticamente);
	
	public void processarLancamentos(Conta conta, List<LancamentoConta> lancamentos);
	
	public void importarLancamento(LancamentoImportado entity);
	
	public void importarLancamento(LancamentoImportado entity, boolean quitarAutomaticamente);
	
	public InfoOFX obterInformacaoArquivoImportado(Arquivo arquivo, Conta conta) throws ApplicationException;
	
	public void apagarLancamentosImportados(Conta conta);
}
