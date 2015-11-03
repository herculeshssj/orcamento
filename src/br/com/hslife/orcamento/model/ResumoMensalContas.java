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

package br.com.hslife.orcamento.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.hslife.orcamento.entity.Categoria;
import br.com.hslife.orcamento.entity.Conta;
import br.com.hslife.orcamento.entity.Favorecido;
import br.com.hslife.orcamento.entity.MeioPagamento;
import br.com.hslife.orcamento.enumeration.TipoCategoria;
import br.com.hslife.orcamento.util.Util;

public class ResumoMensalContas {

	private List<Categoria> categorias = new ArrayList<Categoria>();
	
	private List<Favorecido> favorecidos = new ArrayList<Favorecido>();
	
	private List<MeioPagamento> meiosPagamento = new ArrayList<MeioPagamento>();
	
	private Conta conta;
	
	private Date inicio;
	
	private Date fim;
	
	public List<Categoria> getCategorias() {
		return categorias;
	}
	
	public List<Favorecido> getFavorecidos() {
		return favorecidos;
	}
	
	public List<MeioPagamento> getMeiosPagamento() {
		return meiosPagamento;
	}

	public void setCategorias(List<Categoria> categorias, double saldoAnterior, double saldoAtual) {
		Categoria saldoAnteriorCategorias = new Categoria();
		Categoria saldoAtualCategorias = new Categoria();
		
		saldoAnteriorCategorias.setDescricao("Saldo anterior");
		saldoAtualCategorias.setDescricao("Saldo atual");
		
		saldoAnteriorCategorias.setSaldoPago(Util.arredondar(saldoAnterior));
		saldoAtualCategorias.setSaldoPago(Util.arredondar(saldoAtual));
		
		if (saldoAnterior > 0) {
			saldoAnteriorCategorias.setTipoCategoria(TipoCategoria.CREDITO);
		} else {
			saldoAnteriorCategorias.setTipoCategoria(TipoCategoria.DEBITO);
		}
		
		if (saldoAtual > 0) {
			saldoAtualCategorias.setTipoCategoria(TipoCategoria.CREDITO);
		} else {
			saldoAtualCategorias.setTipoCategoria(TipoCategoria.DEBITO);
		}
		
		this.categorias.add(saldoAnteriorCategorias);
		this.categorias.addAll(categorias);
		this.categorias.add(saldoAtualCategorias);
	}
	
	public void setCategoriasCartao(List<Categoria> categorias) {
		this.categorias = categorias;
	}

	public void setFavorecidos(List<Favorecido> favorecidos) {
		this.favorecidos = favorecidos;
	}

	public void setMeiosPagamento(List<MeioPagamento> meiosPagamento) {
		this.meiosPagamento = meiosPagamento; 
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}
}