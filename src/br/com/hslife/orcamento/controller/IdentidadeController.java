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

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Identidade;
import br.com.hslife.orcamento.enumeration.TipoIdentidade;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IIdentidade;

@Component("identidadeMB")
@Scope("session")
public class IdentidadeController extends AbstractController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1966416536585261603L;

	@Autowired
	private IIdentidade service;
	
	// Documentos de identidade do usuário
	private Identidade cpf;
	private Identidade rg;
	private Identidade tituloEleitor;
	private Identidade pisPasep;
	private Identidade carteiraTrabalho;
	private Identidade certidaoNascimento;
	private Identidade cnh;
	private Identidade docMilitar;
	private Identidade passaporte;
	
	private List<Identidade> documentos = new ArrayList<>();

	public IdentidadeController() {
		moduleTitle = "Documentos de Identidade";
	}
	
	@Override
	protected void initializeEntity() {
		
	}

	@Override
	public String startUp() {
		try {
			// Carrega o CPF
			cpf = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.CPF);
			if (cpf == null) cpf = new Identidade(getUsuarioLogado(), TipoIdentidade.CPF);
			
			// Carrega o RG
			rg = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.RG);
			if (rg == null) rg = new Identidade(getUsuarioLogado(), TipoIdentidade.RG);
			
			// Carrega o título de eleitor
			tituloEleitor = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.TITULO_ELEITOR);
			if (tituloEleitor == null) tituloEleitor = new Identidade(getUsuarioLogado(), TipoIdentidade.TITULO_ELEITOR);
			
			// Carrega o PIS/PASEP
			pisPasep = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.PIS_PASEP);
			if (pisPasep == null) pisPasep = new Identidade(getUsuarioLogado(), TipoIdentidade.PIS_PASEP);
			
			// Carrega a carteira de trabalho
			carteiraTrabalho = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.CARTEIRA_TRABALHO);
			if (carteiraTrabalho == null) carteiraTrabalho = new Identidade(getUsuarioLogado(), TipoIdentidade.CARTEIRA_TRABALHO);
			
			// Carrega a certidão de nascimento
			certidaoNascimento = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.CERTIDAO_NASCIMENTO);
			if (certidaoNascimento == null) certidaoNascimento = new Identidade(getUsuarioLogado(), TipoIdentidade.CERTIDAO_NASCIMENTO);
			
			// Carrega a carteira de motorista
			cnh = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.CNH);
			if (cnh == null) cnh = new Identidade(getUsuarioLogado(), TipoIdentidade.CNH);
			
			// Carrega o certificado de reservista
			docMilitar = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.DOC_MILITAR);
			if (docMilitar == null) docMilitar = new Identidade(getUsuarioLogado(), TipoIdentidade.DOC_MILITAR);
			
			// Carrega o passaporte
			passaporte = getService().buscarPorUsuarioETipoIdentidade(getUsuarioLogado(), TipoIdentidade.PASSAPORTE);
			if (passaporte == null) passaporte = new Identidade(getUsuarioLogado(), TipoIdentidade.PASSAPORTE);
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}		
		return "/pages/Identidade/formIdentidade";
	}
	
	public void salvarDocumentos() {
		try {			
			// Adiciona os documentos no arraylist
			documentos.clear();
			documentos.add(cpf);
			documentos.add(rg);
			documentos.add(tituloEleitor);
			documentos.add(pisPasep);
			documentos.add(carteiraTrabalho);
			documentos.add(certidaoNascimento);
			documentos.add(cnh);
			documentos.add(docMilitar);
			documentos.add(passaporte);
			
			// Valida os documentos de identidade
			for (Identidade identidade : documentos) {
				getService().validar(identidade);
			}
			
			// Salva os documentos
			getService().salvarDocumentos(documentos);
			
			infoMessage("Documentos salvos com sucesso!");
		} catch (BusinessException be) {
			errorMessage(be.getMessage());
		}
	}
	
	public IIdentidade getService() {
		return service;
	}

	public void setService(IIdentidade service) {
		this.service = service;
	}

	public Identidade getCpf() {
		return cpf;
	}

	public void setCpf(Identidade cpf) {
		this.cpf = cpf;
	}

	public Identidade getRg() {
		return rg;
	}

	public void setRg(Identidade rg) {
		this.rg = rg;
	}

	public Identidade getTituloEleitor() {
		return tituloEleitor;
	}

	public void setTituloEleitor(Identidade tituloEleitor) {
		this.tituloEleitor = tituloEleitor;
	}

	public Identidade getPisPasep() {
		return pisPasep;
	}

	public void setPisPasep(Identidade pisPasep) {
		this.pisPasep = pisPasep;
	}

	public Identidade getCarteiraTrabalho() {
		return carteiraTrabalho;
	}

	public void setCarteiraTrabalho(Identidade carteiraTrabalho) {
		this.carteiraTrabalho = carteiraTrabalho;
	}

	public Identidade getCertidaoNascimento() {
		return certidaoNascimento;
	}

	public void setCertidaoNascimento(Identidade certidaoNascimento) {
		this.certidaoNascimento = certidaoNascimento;
	}

	public Identidade getCnh() {
		return cnh;
	}

	public void setCnh(Identidade cnh) {
		this.cnh = cnh;
	}

	public Identidade getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(Identidade passaporte) {
		this.passaporte = passaporte;
	}

	public Identidade getDocMilitar() {
		return docMilitar;
	}

	public void setDocMilitar(Identidade docMilitar) {
		this.docMilitar = docMilitar;
	}
}