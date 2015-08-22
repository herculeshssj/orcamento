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

package br.com.hslife.orcamento.repository;

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
		
		// Traz a quantidade de bancos do usuário
		usuarioAtividade.put("BANCO", 
				(Long)getQuery("SELECT COUNT(*) FROM Banco banco WHERE banco.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);

		// Traz a quantidade de categorias do usuário
		usuarioAtividade.put("CATEGORIA", 
				(Long)getQuery("SELECT COUNT(*) FROM Categoria categoria WHERE categoria.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
				
		// Traz a quantidade de categorias de documento do usuário
		usuarioAtividade.put("CATEGORIA_DOCUMENTO", 
				(Long)getQuery("SELECT COUNT(*) FROM CategoriaDocumento categoriaDocumento WHERE categoriaDocumento.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
		
		// Traz a quantidade de contas do usuário
		usuarioAtividade.put("CONTA", 
				(Long)getQuery("SELECT COUNT(*) FROM Conta conta WHERE conta.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);

		// Traz a quantidade de despensas do usuário
		usuarioAtividade.put("DESPENSA", 
				(Long)getQuery("SELECT COUNT(*) FROM Despensa despensa WHERE despensa.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
		
		// Traz a quantidade de favorecidos do usuário
		usuarioAtividade.put("FAVORECIDO", 
				(Long)getQuery("SELECT COUNT(*) FROM Favorecido favorecido WHERE favorecido.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
		
		// Traz a quantidade de lançamentos periódicos do usuário
		usuarioAtividade.put("LANCAMENTO_PERIODICO", 
				(Long)getQuery("SELECT COUNT(*) FROM LancamentoPeriodico lancamentoPeriodico WHERE lancamentoPeriodico.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
		
		// Traz a quantidade de meios de pagemento do usuário
		usuarioAtividade.put("MEIO_PAGAMENTO", 
				(Long)getQuery("SELECT COUNT(*) FROM MeioPagamento meioPagamento WHERE meioPagamento.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
		
		// Traz a quantidade de moedas do usuário
		usuarioAtividade.put("MOEDA", 
				(Long)getQuery("SELECT COUNT(*) FROM Moeda moeda WHERE moeda.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
		
		// Traz a quantidade de opções do sistema do usuário
		usuarioAtividade.put("OPCAO_SISTEMA", 
				(Long)getQuery("SELECT COUNT(*) FROM OpcaoSistema opcao WHERE opcao.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
		
		// Traz a quantidade de orçamentos cadastrados pelo usuário
		usuarioAtividade.put("ORCAMENTO", 
				(Long)getQuery("SELECT COUNT(*) FROM Orcamento orcamento WHERE orcamento.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
		
		// Traz a quantidade de documentos de identidade do usuário
		usuarioAtividade.put("IDENTIDADE", 
				(Long)getQuery("SELECT COUNT(*) FROM Identidade identidade WHERE identidade.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);		
		
		// Traz a quantidade de registros de auditoria do usuário
		usuarioAtividade.put("AUDITORIA", 
				(Long)getQuery("SELECT COUNT(*) FROM Auditoria auditoria WHERE auditoria.usuario = :idUsuario")
				.setString("idUsuario", usuario.getLogin())
				.uniqueResult()
		);		
		
		// Traz a quantidade de unidades de medida do usuário
		usuarioAtividade.put("UNIDADE_MEDIDA", 
				(Long)getQuery("SELECT COUNT(*) FROM UnidadeMedida unidadeMedida WHERE unidadeMedida.usuario.id = :idUsuario")
				.setLong("idUsuario", usuario.getId())
				.uniqueResult()
		);
				
		return usuarioAtividade;
	}
}