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

***/package br.com.hslife.orcamento.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.facade.IContaCompartilhada;

@Component("confirmacaoMB")
@Scope("request")
public class ConfirmacaoController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8835536442428196447L;

	private static final Logger logger = LogManager.getLogger(ConfirmacaoController.class);
	
	@Autowired
	private IContaCompartilhada contaCompartilhadaService;
	
	private String operacao;
	
	private String hash;

	@Override
	protected void initializeEntity() {
			
	}
	
	public String getMensagemAviso() {
		String mensagemAviso;
		
		try {
			switch (operacao) {
				case "compartilhamento" :
					getContaCompartilhadaService().habilitarCompartilhamento(hash);
					mensagemAviso = "Compartilhamento habilitado com sucesso!";
					break;
				default : mensagemAviso = "Operação não disponível!";
			}
		} catch (Throwable e) {
			logger.catching(e);
			mensagemAviso = e.getMessage();
		}
		
		return mensagemAviso;
	}

	@Override
	public String startUp() {
		return null;
	}

	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public IContaCompartilhada getContaCompartilhadaService() {
		return contaCompartilhadaService;
	}
}
