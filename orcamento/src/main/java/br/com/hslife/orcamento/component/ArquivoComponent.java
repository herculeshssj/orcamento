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
package br.com.hslife.orcamento.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.facade.IArquivo;

@Component
public class ArquivoComponent {
	
	@Autowired
	private IArquivo service;

	public IArquivo getService() {
		return service;
	}
	
	public Long carregarArquivo(Arquivo entity) {		
		getService().salvar(entity);
		return entity.getId();
	}
	
	public Long substituirArquivo(Arquivo novoArquivo, Long arquivoAnterior) {
		// Verifica se o arquivo anterior existe
		Arquivo a = this.buscarArquivo(arquivoAnterior);
		if (a != null) {
			// Exclui o arquivo anterior
			this.excluirArquivo(arquivoAnterior);
		}
		
		// Salva o novo arquivo
		this.carregarArquivo(novoArquivo);
		return novoArquivo.getId();
	}
	
	public Arquivo buscarArquivo(Long idArquivo) {
		Arquivo arquivo = getService().buscarPorID(idArquivo);
		if (arquivo == null || arquivo.getDados() == null || arquivo.getDados().length == 0) {
			return null;
		} else {
			return arquivo;
		}
	}
	
	public void excluirArquivo(Long idArquivo) {
		getService().excluir(getService().buscarPorID(idArquivo));
	}
}
