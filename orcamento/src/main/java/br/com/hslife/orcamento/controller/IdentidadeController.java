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

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.Identidade;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.TipoIdentidade;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
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

	private Identidade carregarIdentidade(Usuario usuario, TipoIdentidade tipoIdentidade) throws ApplicationException{
		Identidade documento = getService().buscarPorUsuarioETipoIdentidade(usuario, tipoIdentidade);
		if (documento == null) {
			return new Identidade(usuario, tipoIdentidade);
		} else {
			return documento;
		}
	}
	
	@Override
	@PostConstruct
	public String startUp() {
		try {
			cpf = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.CPF);
			rg = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.RG);
			tituloEleitor = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.TITULO_ELEITOR);
			pisPasep = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.PIS_PASEP);
			carteiraTrabalho = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.CARTEIRA_TRABALHO);
			certidaoNascimento = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.CERTIDAO_NASCIMENTO);
			cnh = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.CNH);
			docMilitar = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.DOC_MILITAR);
			passaporte = this.carregarIdentidade(getUsuarioLogado(), TipoIdentidade.PASSAPORTE);
		} catch (ApplicationException be) {
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
				identidade.validate();
			}
			
			// Salva os documentos
			getService().salvarDocumentos(documentos);
			
			infoMessage("Documentos salvos com sucesso!");
		} catch (ValidationException | BusinessException be) {
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