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
package br.com.hslife.orcamento.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.hslife.orcamento.component.UsuarioComponent;
import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Documento;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.enumeration.Container;
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.facade.IArquivo;
import br.com.hslife.orcamento.model.AnexoEntidade;
import br.com.hslife.orcamento.model.CriterioArquivo;
import br.com.hslife.orcamento.repository.ArquivoRepository;
import br.com.hslife.orcamento.repository.DocumentoRepository;
import br.com.hslife.orcamento.repository.FaturaCartaoRepository;
import br.com.hslife.orcamento.repository.LancamentoContaRepository;
import br.com.hslife.orcamento.repository.LancamentoPeriodicoRepository;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor={ApplicationException.class})
public class ArquivoService implements IArquivo {
	
	@Autowired
	public SessionFactory sessionFactory;
	
	@Autowired
	private ArquivoRepository repository;
	
	@Autowired
	private DocumentoRepository documentoRepository;
	
	@Autowired
	private FaturaCartaoRepository faturaCartaoRepository;
	
	@Autowired
	private LancamentoContaRepository lancamentoContaRepository;
	
	@Autowired
	private LancamentoPeriodicoRepository lancamentoPeriodicoRepository;
	
	@Autowired
	private UsuarioComponent usuarioComponent;

	public ArquivoRepository getRepository() {
		this.repository.setSessionFactory(this.sessionFactory);
		return repository;
	}

	public DocumentoRepository getDocumentoRepository() {
		this.documentoRepository.setSessionFactory(this.sessionFactory);
		return documentoRepository;
	}
	
	public FaturaCartaoRepository getFaturaCartaoRepository() {
		this.faturaCartaoRepository.setSessionFactory(this.sessionFactory);
		return faturaCartaoRepository;
	}

	public LancamentoContaRepository getLancamentoContaRepository() {
		this.lancamentoContaRepository.setSessionFactory(this.sessionFactory);
		return lancamentoContaRepository;
	}

	public LancamentoPeriodicoRepository getLancamentoPeriodicoRepository() {
		this.lancamentoPeriodicoRepository.setSessionFactory(this.sessionFactory);
		return lancamentoPeriodicoRepository;
	}

	public UsuarioComponent getUsuarioComponent() {
		return usuarioComponent;
	}

	@Override
	public List<Arquivo> buscarPorCriterioArquivo(CriterioArquivo criterio) {
		return getRepository().findByCriterioArquivo(criterio);
	}
	
	@Override
	public void salvar(Arquivo arquivo) {
		getRepository().save(arquivo);
	}
	
	@Override
	public Arquivo buscarPorID(Long id) {
		return getRepository().findByID(id);
	}
	
	@Override
	public void excluir(Arquivo arquivo) {
		switch (arquivo.getContainer()) {
			case DOCUMENTOS :
				if (getRepository().deleteFromDocumento(arquivo)) {
					// Nada a fazer
				} else {
					throw new BusinessException("Não foi possível excluir o arquivo.");
				}
				break;
			case FATURACARTAO :
				if (getRepository().deleteFromFaturaCartao(arquivo)) {
					// Nada a fazer
				} else {
					throw new BusinessException("Não foi possível excluir o arquivo.");
				}
				break;
			case LANCAMENTOCONTA : 
				if (getRepository().deleteFromLancamentoConta(arquivo)) {
					// Nada a fazer
				} else {
					throw new BusinessException("Não foi possível excluir o arquivo.");
				}
				break;
			case LANCAMENTOPERIODICO :
				if (getRepository().deleteFromLancamentoPeriodico(arquivo)) {
					// Nada a fazer
				} else {
					throw new BusinessException("Não foi possível excluir o arquivo.");
				}
				break;
			default :
				throw new BusinessException("A exclusão não está disponível para este arquivo!");
		}
		
		// Exclui o arquivo 
		getRepository().delete(arquivo);
	}
	
	@Override
	public List<AnexoEntidade> buscarEntidadesPorDescricao(String descricao, Container container) {
		List<AnexoEntidade> listaAnexos = new ArrayList<>();
		AnexoEntidade anexo;
		switch (container) {
			case DOCUMENTOS:
				for (Documento d : getDocumentoRepository().findByNomeAndUsuario(descricao, getUsuarioComponent().getUsuarioLogado())) {
					anexo = new AnexoEntidade();
					anexo.setId(d.getId());
					anexo.setDescricao(d.getLabel());
					listaAnexos.add(anexo);
				}
				break;
			case FATURACARTAO:
				for (FaturaCartao f : getFaturaCartaoRepository().findAllByUsuario(getUsuarioComponent().getUsuarioLogado())) {
					if (f.getLabel().contains(descricao)) {
						anexo = new AnexoEntidade();
						anexo.setId(f.getId());
						anexo.setDescricao(f.getLabel());
						anexo.setContemAnexo(f.isPossuiAnexo());
						listaAnexos.add(anexo);
					}					
				}
				break;
			case LANCAMENTOCONTA:
				for (LancamentoConta l : getLancamentoContaRepository().findByDescricaoAndUsuario(descricao, getUsuarioComponent().getUsuarioLogado())) {
					anexo = new AnexoEntidade();
					anexo.setId(l.getId());
					anexo.setDescricao(l.getLabel());
					anexo.setContemAnexo(l.isPossuiAnexo());
					listaAnexos.add(anexo);
				}
				break;
			case LANCAMENTOPERIODICO:
				for (LancamentoPeriodico l : getLancamentoPeriodicoRepository().findByDescricaoAndUsuario(descricao, getUsuarioComponent().getUsuarioLogado())) {
					anexo = new AnexoEntidade();
					anexo.setId(l.getId());
					anexo.setDescricao(l.getLabel());
					anexo.setContemAnexo(l.isPossuiAnexo());
					listaAnexos.add(anexo);
				}
				break;
			default :
				throw new BusinessException("Esta opção não está disponível para esta entidade!");
		}
		return listaAnexos;
	}
	
	@Override
	public void salvarAnexo(Long idEntity, Container container, Arquivo anexo) {
		switch (container) {
			case DOCUMENTOS:
				Documento d = getDocumentoRepository().findById(idEntity);
				getRepository().save(anexo);
				d.setIdArquivo(anexo.getId());
				documentoRepository.update(d);
				break;
			case FATURACARTAO: 
				FaturaCartao f = getFaturaCartaoRepository().findById(idEntity);
				getRepository().save(anexo);
				f.setIdArquivo(anexo.getId());
				faturaCartaoRepository.update(f);
				break;
			case LANCAMENTOCONTA:
				LancamentoConta l = getLancamentoContaRepository().findById(idEntity);
				getRepository().save(anexo);
				l.setIdArquivo(anexo.getId());
				lancamentoContaRepository.update(l);
				break;
			case LANCAMENTOPERIODICO:
				LancamentoPeriodico lp = getLancamentoPeriodicoRepository().findById(idEntity);
				getRepository().save(anexo);
				lp.setIdArquivo(anexo.getId());
				lancamentoPeriodicoRepository.update(lp);
				break;
			default :
				throw new BusinessException("Esta opção não está disponível para esta entidade!");
		}
		
	}
}
