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

***/
package br.com.hslife.orcamento.repository;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Arquivo;
import br.com.hslife.orcamento.entity.Documento;
import br.com.hslife.orcamento.entity.FaturaCartao;
import br.com.hslife.orcamento.entity.LancamentoConta;
import br.com.hslife.orcamento.entity.LancamentoPeriodico;
import br.com.hslife.orcamento.model.CriterioArquivo;

@Repository
public class ArquivoRepository extends AbstractRepository {
	
	@SuppressWarnings("unchecked")
	public List<Arquivo> findByCriterioArquivo(CriterioArquivo criterio) {
		// FIXME refatorar o método
		Criteria criteria = getSession().createCriteria(Arquivo.class);
		
		if (criterio.getNome() != null && !criterio.getNome().isEmpty()) {
			criteria.add(Restrictions.ilike("nomeArquivo", criterio.getNome(), MatchMode.ANYWHERE));
		}
		
		if (criterio.getInicio() != null) {
			criteria.add(Restrictions.ge("dataCriacao", criterio.getInicio()));
		}
		
		if (criterio.getFim() != null) {
			criteria.add(Restrictions.le("dataCriacao", criterio.getFim()));
		}
		
		if (criterio.getContainer() != null) {
			criteria.add(Restrictions.eq("container", criterio.getContainer()));
		}
		
		criteria.add(Restrictions.eq("usuario.id", criterio.getUsuario().getId()));
		
		return criteria.addOrder(Order.asc("nomeArquivo")).list();
	}
	
	public void delete(Arquivo arquivo) {
		getSession().delete(arquivo);
	}
	
	public void save(Arquivo arquivo) {
		getSession().persist(arquivo);
	}
	
	public Arquivo findByID(Long id) {
		return (Arquivo)getSession()
				.createQuery("SELECT arquivo FROM Arquivo arquivo WHERE arquivo.id = :id")
				.setParameter("id", id)
				.uniqueResult();
	}
	
	public boolean deleteFromLancamentoConta(Arquivo arquivo) {
		LancamentoConta lancamento = (LancamentoConta)getSession().createQuery("SELECT lancamento FROM LancamentoConta lancamento WHERE lancamento.arquivo.id = :idArquivo")
				.setParameter("idArquivo", arquivo.getId())
				.uniqueResult();
		
		if (lancamento == null)
			return false;
		else {
			lancamento.setArquivo(null);
			getSession().update(lancamento);
			return true;
		}
	}
	
	public boolean deleteFromFaturaCartao(Arquivo arquivo) {
		FaturaCartao fatura = (FaturaCartao)getSession().createQuery("SELECT fatura FROM FaturaCartao fatura WHERE fatura.arquivo.id = :idArquivo")
				.setParameter("idArquivo", arquivo.getId())
				.uniqueResult();
		
		if (fatura == null)
			return false;
		else {
			fatura.setArquivo(null);
			getSession().update(fatura);
			return true;
		}
	}
	
	public boolean deleteFromDocumento(Arquivo arquivo) {
		Documento documento = (Documento)getSession().createQuery("SELECT documento FROM Documento documento WHERE documento.arquivo.id = :idArquivo")
				.setParameter("idArquivo", arquivo.getId())
				.uniqueResult();
		
		if (documento == null)
			return false;
		else {
			getSession().delete(documento);
			return true;
		}
	}
	
	public boolean deleteFromLancamentoPeriodico(Arquivo arquivo) {
		LancamentoPeriodico lancamento = (LancamentoPeriodico)getSession().createQuery("SELECT lancamento FROM LancamentoPeriodico lancamento WHERE lancamento.arquivo.id = :idArquivo")
				.setParameter("idArquivo", arquivo.getId())
				.uniqueResult();
		
		if (lancamento == null)
			return false;
		else {
			lancamento.setArquivo(null);
			getSession().update(lancamento);
			return true;
		}
	}
}
