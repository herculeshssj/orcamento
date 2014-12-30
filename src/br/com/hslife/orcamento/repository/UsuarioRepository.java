/***
  
  	Copyright (c) 2012 - 2015 Hércules S. S. José

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

    em contato pelo e-mail herculeshssj@gmail.com, ou ainda escreva para 

    Hércules S. S. José, Av. Ministro Lafaeyte de Andrade, 1683 - Bl. 3 Apt 404, 

    Marco II - Nova Iguaçu, RJ, Brasil.
  
***/

package br.com.hslife.orcamento.repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import br.com.hslife.orcamento.entity.Usuario;

@Repository
public class UsuarioRepository extends AbstractCRUDRepository<Usuario> {
	
	public UsuarioRepository() {
		super(new Usuario());
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> findAll() {
		return getSession().createCriteria(Usuario.class).addOrder(Order.asc("nome")).list();
	}
	
	public Usuario findByLogin(String login) {
		Criteria criteria = getSession().createCriteria(Usuario.class);
		criteria.add(Restrictions.eq("login", login));
		return (Usuario)criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Usuario> findAllByLogin(String login) {
		Criteria criteria = getSession().createCriteria(Usuario.class);
		criteria.add(Restrictions.ilike("login", login, MatchMode.ANYWHERE));
		return criteria.addOrder(Order.asc("nome")).list();
	}
	
	public Map<String, Long> findUserActivity(Usuario usuario) {
		Map<String, Long> usuarioAtividade = new HashMap<String, Long>();
		BigInteger resultado = new BigInteger("0");
		
		// Traz a quantidade de bancos do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from banco where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("BANCO", resultado.longValue());
		
		// Traz a quantidade de categorias do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from categoria where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("CATEGORIA", resultado.longValue());
		
		// Traz a quantidade de categorias de documento do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from categoriadocumento where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("CATEGORIA_DOCUMENTO", resultado.longValue());
		
		// Traz a quantidade de contas do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from conta where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("CONTA", resultado.longValue());
		
		// Traz a quantidade de despensas do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from despensa where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("DESPENSA", resultado.longValue());
		
		// Traz a quantidade de favorecidos do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from favorecido where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("FAVORECIDO", resultado.longValue());
		
		// Traz a quantidade de lançamentos periódicos do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from lancamentoperiodico where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("LANCAMENTO_PERIODICO", resultado.longValue());
		
		// Traz a quantidade de meios de pagemento do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from meiopagamento where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("MEIO_PAGAMENTO", resultado.longValue());
		
		// Traz a quantidade de moedas do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from moeda where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("MOEDA", resultado.longValue());
		
		// Traz a quantidade de opções do sistema do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from opcaosistema where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("OPCAO_SISTEMA", resultado.longValue());
		
		// Traz a quantidade de documentos de identidade do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from identidade where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("IDENTIDADE", resultado.longValue());
		
		// Traz a quantidade de registros de auditoria do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from auditoria where usuario = '" + usuario.getLogin() + "'").uniqueResult(); 
		usuarioAtividade.put("AUDITORIA", resultado.longValue());
		
		// Traz a quantidade de unidades de medida do usuário
		resultado = (BigInteger)getSession().createSQLQuery("select count(*) from unidademedida where idUsuario = " + usuario.getId()).uniqueResult(); 
		usuarioAtividade.put("UNIDADE_MEDIDA", resultado.longValue());
		
		return usuarioAtividade;
	}
}