/***
  
  	Copyright (c) 2012 - 2016 Hércules S. S. José

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

    nome de "LICENSE.TXT" junto com este programa, se não, acesse o site do
    
    projeto no endereco https://github.com/herculeshssj/orcamento ou escreva 
    
    para a Fundação do Software Livre(FSF) Inc., 51 Franklin St, Fifth Floor, 
    
    Boston, MA  02110-1301, USA.
    

    Para mais informações sobre o programa Orçamento Doméstico e seu autor entre  

    em contato pelo e-mail herculeshssj@outlook.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hslife.orcamento.entity.DividaTerceiro;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.PagamentoDividaTerceiro;
import br.com.hslife.orcamento.entity.Usuario;
import br.com.hslife.orcamento.enumeration.StatusDivida;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IDividaTerceiro;
import br.com.hslife.orcamento.repository.DividaTerceiroRepository;
import br.com.hslife.orcamento.util.Util;

@Service("dividaTerceiroService")
public class DividaTerceiroService extends AbstractCRUDService<DividaTerceiro> implements IDividaTerceiro {
	
	@Autowired
	private DividaTerceiroRepository repository;

	public DividaTerceiroRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}
	
	@Override
	public List<DividaTerceiro> buscarFavorecidoOuTipoCategoriaOuStatusDividaPorUsuario(Favorecido favorecido, 
			TipoCategoria tipoCategoria, StatusDivida statusDivida, Usuario usuario) {
		return getRepository().findFavorecidoOrTipoCategoriaOrStatusDividaByUsuario(favorecido, tipoCategoria, statusDivida, usuario);
	}
	
	@Override
	public void vigorarDividaTerceiro(DividaTerceiro entity) {
		if (entity.getModeloDocumento() == null) {
			throw new BusinessException("Selecione um modelo de termo/contrato!");
		}		
		entity.validate();
		entity.setTermoDivida(entity.getModeloDocumento().getConteudo());
		entity.setStatusDivida(StatusDivida.VIGENTE);
		getRepository().update(entity);
	}
	
	@Override
	public void renegociarDividaTerceiro(DividaTerceiro entity, String justificativa) {
		if (justificativa == null || justificativa.isEmpty()) {
			throw new BusinessException("Informe a justificativa para renegociar a dívida!");
		}
		
		DividaTerceiro dividaAntiga = getRepository().findById(entity.getId());
		dividaAntiga.setStatusDivida(StatusDivida.RENEGOCIADO);
		getRepository().update(dividaAntiga);
		
		// Anexa a justificativa informada à existente
		StringBuilder j = new StringBuilder();
		j.append("Dívida renegociada em ");
		j.append(Util.formataDataHora(new Date(), Util.DATAHORA));
		j.append(".<br/>");
		j.append(justificativa);
		
		DividaTerceiro novaDivida = new DividaTerceiro();
		novaDivida.setDataNegociacao(entity.getDataNegociacao());
		novaDivida.setFavorecido(entity.getFavorecido());
		novaDivida.setJustificativa(j.toString());
		novaDivida.setMoeda(entity.getMoeda());
		novaDivida.setStatusDivida(StatusDivida.VIGENTE);
		novaDivida.setTermoDivida(entity.getModeloDocumento().getConteudo());
		novaDivida.setTipoCategoria(entity.getTipoCategoria());
		novaDivida.setUsuario(entity.getUsuario());
		novaDivida.setValorDivida(entity.getValorDivida());
		getRepository().save(novaDivida);
	}
	
	public void registrarPagamentoDivida(DividaTerceiro entity, PagamentoDividaTerceiro pagamento) {
		if (pagamento.getModeloDocumento() == null) {
			throw new BusinessException("Selecione um modelo de comprovante de pagamento!");
		}
		
		if (entity.getTotalPago() + pagamento.getValorPagoConvertido() >= entity.getValorDivida() && entity.getModeloDocumento() == null) {
			throw new BusinessException("Selecione um modelo de termo de quitação!");
		}
		
		DividaTerceiro divida = getRepository().findById(entity.getId());
		pagamento.setDividaTerceiro(divida);
		pagamento.setComprovantePagamento(pagamento.getModeloDocumento().getConteudo());
		divida.getPagamentos().add(pagamento);
		
		if (divida.getTotalPago() >= divida.getValorDivida()) {
			divida.setStatusDivida(StatusDivida.QUITADO);
			divida.setTermoQuitacao(entity.getModeloDocumento().getConteudo());
		}
		
		getRepository().update(divida);
	}
	
	public void encerrarDividaTerceiro(DividaTerceiro entity, String justificativa) {
		if (justificativa == null || justificativa.isEmpty()) {
			throw new BusinessException("Informe a justificativa para encerrar a dívida!");
		}
		
		if (justificativa.length() < 100) {
			throw new BusinessException("Justificativa deve ter no mínimo 100 caracteres!");
		}
		
		// Anexa a justificativa informada à existente
		StringBuilder j = new StringBuilder(entity.getJustificativa());
		j.append("<br/>");
		j.append("Dívida encerrada em ");
		j.append(Util.formataDataHora(new Date(), Util.DATAHORA));
		j.append(".<br/>");
		j.append(justificativa);
		
		entity.setJustificativa(j.toString());
		entity.setStatusDivida(StatusDivida.ENCERRADO);
		getRepository().update(entity);
	}
}