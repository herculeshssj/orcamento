/***
  
  	Copyright (c) 2012 - 2021 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, mas SEM

    NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer
    
    MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral Menor GNU
    
    em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob
	
	o nome de "LICENSE" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento-maven ou 
	
	escreva para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth 
	
	Floor, Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor
	
	entre em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva

    para Hércules S. S. José, Rua José dos Anjos, 160 - Bl. 3 Apto. 304 - 
	
	Jardim Alvorada - CEP: 26261-130 - Nova Iguaçu, RJ, Brasil.

***/

package br.com.hslife.orcamento.component;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.enumeration.TipoConta;
import br.com.hslife.orcamento.service.ContaService;

@Component
@Transactional(propagation=Propagation.SUPPORTS)
public class OpcaoFaturaComponent {
	
	private static final Logger logger = LogManager.getLogger(OpcaoFaturaComponent.class);
	
	@Autowired
	private ContaService contaService;
	
	@Autowired
	private UsuarioComponent usuarioComponent;
	
	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}
	
	public ContaService getContaService() {
		return contaService;
	}
	
	/*
	 * Retorna a lista de contas disponíveis para receber os pagamentos
	 * das faturas do cartão de crédito
	 */
	public List<SelectItem> getListaContaPadrao() {
		List<SelectItem> listaConta = new ArrayList<>();
		try {
			for (Conta conta : getContaService().buscarDescricaoOuTipoContaOuAtivoPorUsuario("", new TipoConta[]{TipoConta.CORRENTE, TipoConta.POUPANCA, TipoConta.OUTROS}, getUsuarioComponent().getUsuarioLogado(), true)) {
				listaConta.add(new SelectItem(conta.getId().toString(), conta.getLabel()));
			}
			return listaConta;
		} catch (Exception e) {
			logger.catching(e);
		}
		
		return new ArrayList<>();
	}
}
