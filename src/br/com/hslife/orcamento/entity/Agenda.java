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

package br.com.hslife.orcamento.entity;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.hslife.orcamento.enumeration.PrioridadeTarefa;
import br.com.hslife.orcamento.enumeration.TipoAgendamento;
import br.com.hslife.orcamento.exception.BusinessException;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="agenda")
@SuppressWarnings("serial")
public class Agenda extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=50, nullable=false)
	private String descricao;
	
	@Column(length=200, nullable=false)
	private String localAgendamento;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date inicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date fim;
	
	@Column(length=15)
	@Enumerated(EnumType.STRING)
	private TipoAgendamento tipoAgendamento;
	
	@Column(length=10)
	@Enumerated(EnumType.STRING)
	private PrioridadeTarefa prioridadeTarefa;
	
	@Column
	private boolean diaInteiro;
	
	@Column
	private boolean concluido;
	
	@Column
	private boolean emitirAlerta;
	
	@Column(columnDefinition="text", nullable=true)
	private String notas;
	
	@Transient
	private Date dataInicio;
	
	@Transient
	private Date horaInicio;
	
	@Transient
	private Date dataFim;
	
	@Transient
	private Date horaFim;
	
	@Transient
	private LancamentoConta lancamentoAgendado;
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	public Agenda() {		
		inicio = new Date();
		fim = new Date();
	}
	
	@Override
	public void validate() throws BusinessException {
		if (this.descricao.trim().length() > 50) {
			throw new BusinessException("Campo aceita no máximo 50 caracteres!");
		}
		
		if (this.localAgendamento.trim().length() > 200) {
			throw new BusinessException("Campo aceita no máximo 200 caracteres!");
		}
		
		if (this.tipoAgendamento == null) {
			throw new BusinessException("Informe o tipo de agendamento!");
		}
		
		if (this.fim != null && this.fim.before(this.inicio)) {
			throw new BusinessException("Data de término não pode ser anterior a data de início!");
		}
		
		if (this.usuario == null) {
			throw new BusinessException("Informe o usuário!");
		}
	}

	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	public String getDateLabel() {
		try {
			return comporTextoAgendamento();
		} catch (BusinessException be) {
			return "";
		}
	}
	
	public Date extrairData(Date data) {
		Calendar dataExtraida = Calendar.getInstance();
		dataExtraida.setTime((Date)data.clone());		
		dataExtraida.set(Calendar.HOUR, 0);
		dataExtraida.set(Calendar.MINUTE, 0);
		dataExtraida.set(Calendar.SECOND, 0);
		dataExtraida.set(Calendar.MILLISECOND, 0);
		return dataExtraida.getTime();
	}
	
	public int extrairHora(Date data) {
		Calendar dataExtraida = Calendar.getInstance();
		dataExtraida.setTime((Date)data.clone());
		return dataExtraida.get(Calendar.HOUR_OF_DAY);
	}
	
	public int extrairMinuto(Date data) {
		Calendar dataExtraida = Calendar.getInstance();
		dataExtraida.setTime((Date)data.clone());
		return dataExtraida.get(Calendar.MINUTE);
	}
	
	public int extrairSegundo(Date data) {
		Calendar dataExtraida = Calendar.getInstance();
		dataExtraida.setTime((Date)data.clone());
		return dataExtraida.get(Calendar.SECOND);
	}
	
	public Date comporData(Date data, int hora, int minuto) {
		return this.comporData(data, hora, minuto, 0);		
	}
	
	public Date comporData(Date data, int hora, int minuto, int segundo) {
		Calendar dataExtraida = Calendar.getInstance();
		dataExtraida.setTime((Date)data.clone());		
		dataExtraida.set(Calendar.HOUR, hora);
		dataExtraida.set(Calendar.MINUTE, minuto);
		dataExtraida.set(Calendar.SECOND, segundo);
		dataExtraida.set(Calendar.MILLISECOND, 0);
		return dataExtraida.getTime();
	}
	
	public String comporTextoAgendamento() throws BusinessException {
		return this.comporTextoAgendamento(this.inicio, this.fim, this.diaInteiro, this.tipoAgendamento);
	}
	
	public String comporTextoAgendamento(Date inicio, Date fim, boolean diaInteiro, TipoAgendamento tipoAgendamento) throws BusinessException {
		switch (tipoAgendamento) {
		case PREVISAO :
			if (inicio == null) {
				throw new BusinessException("Informe a data de início!");
			} else {
				return Util.formataDataHora(inicio, Util.DATA);
			}
		case TAREFA :
			if (inicio == null) {
				throw new BusinessException("Informe a data de início!");
			}
			if (fim == null) {
				if (this.extrairHora(inicio) == 0 || this.extrairMinuto(inicio) == 0) {
					return Util.formataDataHora(inicio, Util.DATA);
				} else {
					return Util.formataDataHora(inicio, Util.DATAHORA);
				}
			} else {
				if (this.extrairHora(inicio) == 0 || this.extrairMinuto(inicio) == 0 || this.extrairHora(fim) == 0 || this.extrairMinuto(fim) == 0) {
					return new StringBuilder().append(Util.formataDataHora(inicio, Util.DATAHORA)).append("\nà\n").append(Util.formataDataHora(fim, Util.DATAHORA)).toString();
				} else {
					return new StringBuilder().append(Util.formataDataHora(inicio, Util.DATA)).append("\nà\n").append(Util.formataDataHora(fim, Util.DATA)).toString();
				}
			}				
		case COMPROMISSO :
			if (inicio == null) {
				throw new BusinessException("Informe a data de início!");
			}
			if (diaInteiro) {
				if (fim == null) {
					return Util.formataDataHora(inicio, Util.DATA);
				} else {
					return new StringBuilder().append(Util.formataDataHora(inicio, Util.DATA)).append("\nà\n").append(Util.formataDataHora(fim, Util.DATA)).toString();
				}
			} else {
				if (fim == null) {
					return Util.formataDataHora(inicio, Util.DATAHORA);
				} else {
					return new StringBuilder().append(Util.formataDataHora(inicio, Util.DATAHORA)).append("\nà\n").append(Util.formataDataHora(fim, Util.DATAHORA)).toString();
				}
			}				
	}
	return "";
	}
	
	public Long getId() {
		return id;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	public LancamentoConta getLancamentoAgendado() {
		return lancamentoAgendado;
	}

	public void setLancamentoAgendado(LancamentoConta lancamentoAgendado) {
		this.lancamentoAgendado = lancamentoAgendado;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setId(Long id) {
		this.id = id;
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

	public TipoAgendamento getTipoAgendamento() {
		return tipoAgendamento;
	}

	public void setTipoAgendamento(TipoAgendamento tipoAgendamento) {
		this.tipoAgendamento = tipoAgendamento;
	}

	public PrioridadeTarefa getPrioridadeTarefa() {
		return prioridadeTarefa;
	}

	public void setPrioridadeTarefa(PrioridadeTarefa prioridadeTarefa) {
		this.prioridadeTarefa = prioridadeTarefa;
	}

	public boolean isDiaInteiro() {
		return diaInteiro;
	}

	public void setDiaInteiro(boolean diaInteiro) {
		this.diaInteiro = diaInteiro;
	}

	public boolean isConcluido() {
		return concluido;
	}

	public void setConcluido(boolean concluido) {
		this.concluido = concluido;
	}

	public boolean isEmitirAlerta() {
		return emitirAlerta;
	}

	public void setEmitirAlerta(boolean emitirAlerta) {
		this.emitirAlerta = emitirAlerta;
	}

	public String getNotas() {
		return notas;
	}

	public void setNotas(String notas) {
		this.notas = notas;
	}

	public String getLocalAgendamento() {
		return localAgendamento;
	}

	public void setLocalAgendamento(String localAgendamento) {
		this.localAgendamento = localAgendamento;
	}
}