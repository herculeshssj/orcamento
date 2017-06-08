/***
  
  	Copyright (c) 2012 - 2020 Hércules S. S. José

    Este arquivo é parte do programa Orçamento Doméstico.
    

    Orçamento Doméstico é um software livre; você pode redistribui-lo e/ou 

    modificá-lo dentro dos termos da Licença Pública Geral Menor GNU como 

    publicada pela Fundação do Software Livre (FSF); na versão 3.0 da 

    Licença.
    

    Este programa é distribuído na esperança que possa ser útil, 

    mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÂO a 
    
    qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a Licença Pública 
    
    Geral Menor GNU em português para maiores detalhes.
    

    Você deve ter recebido uma cópia da Licença Pública Geral Menor GNU sob o 

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.hslife.orcamento.entity.CategoriaDocumento;
import br.com.hslife.orcamento.entity.HistoricoSaude;
import br.com.hslife.orcamento.entity.Saude;
import br.com.hslife.orcamento.entity.TratamentoSaude;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.facade.ICategoriaDocumento;
import br.com.hslife.orcamento.facade.ISaude;

@Component
@Scope("session")
public class SaudeController extends AbstractCRUDController<Saude> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5076023665451990660L;
	
	@Autowired
	private ISaude service; 
	
	@Autowired
	private ICategoriaDocumento categoriaDocumentoService;
	
	private HistoricoSaude historicoSaude;
	private TratamentoSaude tratamentoSaude;
	
	public SaudeController() {
		super(new Saude());
		moduleTitle = "Saúde";
	}
	
	@Override
	protected void initializeEntity() {
		entity = new Saude();
		listEntity = new ArrayList<Saude>();		
	}
	
	@Override
	public String startUp() {
		find();
		return super.startUp();
	}
	
	@Override
	public void find() {
		listEntity = getService().buscarTodosAtivosPorUsuario(getUsuarioLogado());
	}
	
	@Override
	public String save() {
		entity.setUsuario(getUsuarioLogado());
		return super.save();
	}
	
	public void adicionarHistorico() {
		
	}
	
	public void editarHistorico() {
		
	}
	
	public void excluirHistorico() {
		
	}
	
	public void adicionarTratamento() {
		
	}
	
	public void editarTratamento() {
		
	}
	
	public void excluirTratamento() {
		
	}
	
	public List<CategoriaDocumento> getListaCategoriaDocumento() {
		try {
			return getCategoriaDocumentoService().buscarPorUsuario(getUsuarioLogado());
		} catch (ValidationException | BusinessException be) {
			errorMessage(be.getMessage());
		}
		return new ArrayList<CategoriaDocumento>();
	}
	
	public ISaude getService() {
		return service;
	}

	public ICategoriaDocumento getCategoriaDocumentoService() {
		return categoriaDocumentoService;
	}

	public HistoricoSaude getHistoricoSaude() {
		return historicoSaude;
	}

	public void setHistoricoSaude(HistoricoSaude historicoSaude) {
		this.historicoSaude = historicoSaude;
	}

	public TratamentoSaude getTratamentoSaude() {
		return tratamentoSaude;
	}

	public void setTratamentoSaude(TratamentoSaude tratamentoSaude) {
		this.tratamentoSaude = tratamentoSaude;
	}
}