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
import br.com.hslife.orcamento.exception.ApplicationException;
import br.com.hslife.orcamento.exception.ValidationException;
import br.com.hslife.orcamento.rest.json.AgendaJson;
import br.com.hslife.orcamento.util.EntityPersistenceUtil;
import br.com.hslife.orcamento.util.Util;

@Entity
@Table(name="agenda")
@SuppressWarnings("serial")
public class Agenda extends EntityPersistence {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length=200, nullable=false)
	private String descricao;
	
	@Column(length=200, nullable=true)
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
	
	@ManyToOne
	@JoinColumn(name="idUsuario", nullable=false)
	private Usuario usuario;
	
	@Column(name="entityID", nullable=true)
	private Long idEntity;
	
	@Column(length=30, nullable=true)
	private String entity;
	
	public Agenda() {		
		inicio = new Date();
		fim = new Date();
	}
	
	@Override
	public void validate() {
		EntityPersistenceUtil.validaTamanhoCampoStringObrigatorio("Descrição", this.descricao, 200);
		EntityPersistenceUtil.validaTamanhoCampoStringOpcional("Local", this.localAgendamento, 200);
		EntityPersistenceUtil.validaCampoNulo("Tipo de agendamento", this.tipoAgendamento);
		EntityPersistenceUtil.validaCampoNulo("Data de início", this.inicio);
		
		if (this.fim != null && this.fim.before(this.inicio)) {
			throw new ValidationException("Data de término não pode ser anterior a data de início!");
		}
	}

	@Override
	public String getLabel() {
		return this.descricao;
	}
	
	public String getDateLabel() {
		try {
			return comporTextoAgendamento();
		} catch (ApplicationException be) {
			return "";
		}
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
	
	public Date comporData(Date data, int hora, int minuto) {
		return this.comporData(data, hora, minuto, 0);		
	}
	
	@SuppressWarnings("deprecation")
	public Date comporData(Date data, int hora, int minuto, int segundo) {
		Calendar dataExtraida = Calendar.getInstance();
		dataExtraida.set(data.getYear() + 1900, data.getMonth(), data.getDate(), hora, minuto, segundo);		
		return dataExtraida.getTime();
		
	}
	
	public String comporTextoAgendamento() throws ApplicationException {
		return this.comporTextoAgendamento(this.inicio, this.fim, this.diaInteiro, this.tipoAgendamento);
	}
	
	public String comporTextoAgendamento(Date inicio, Date fim, boolean diaInteiro, TipoAgendamento tipoAgendamento) throws ApplicationException {
		switch (tipoAgendamento) {
			case PREVISAO : return this.comporTextoAgendamentoPrevisao(inicio, fim, diaInteiro, tipoAgendamento);
			case TAREFA : return this.comporTextoAgendamentoTarefa(inicio, fim, diaInteiro, tipoAgendamento);
			case COMPROMISSO : return this.comporTextoAgendamentoCompromisso(inicio, fim, diaInteiro, tipoAgendamento);							
		}
		return "";
	}
	
	private String comporTextoAgendamentoPrevisao(Date inicio, Date fim, boolean diaInteiro, TipoAgendamento tipoAgendamento) {
		if (inicio == null) {
			throw new ValidationException("Informe a data de início!");
		} else {
			return Util.formataDataHora(inicio, Util.DATA);
		}
	}
	
	private String comporTextoAgendamentoTarefa(Date inicio, Date fim, boolean diaInteiro, TipoAgendamento tipoAgendamento) {
		if (inicio == null) {
			throw new ValidationException("Informe a data de início!");
		}
		if (fim == null) {
			if (this.extrairHora(inicio) == 0 && this.extrairMinuto(inicio) == 0) {
				return Util.formataDataHora(inicio, Util.DATA);
			} else {
				return Util.formataDataHora(inicio, Util.DATAHORA);
			}
		} else {
			if (this.extrairHora(inicio) == 0 && this.extrairMinuto(inicio) == 0 && this.extrairHora(fim) == 0 && this.extrairMinuto(fim) == 0) {
				return new StringBuilder().append(Util.formataDataHora(inicio, Util.DATA)).append("\nà\n").append(Util.formataDataHora(fim, Util.DATA)).toString();
			} else {
				return new StringBuilder().append(Util.formataDataHora(inicio, Util.DATAHORA)).append("\nà\n").append(Util.formataDataHora(fim, Util.DATAHORA)).toString();
			}
		}				
	}
	
	private String comporTextoAgendamentoCompromisso(Date inicio, Date fim, boolean diaInteiro, TipoAgendamento tipoAgendamento) {
		if (inicio == null) {
			throw new ValidationException("Informe a data de início!");
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
	
	@Override
	public AgendaJson toJson() {
		return new AgendaJson();
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

	public Long getIdEntity() {
		return idEntity;
	}

	public void setIdEntity(Long idEntity) {
		this.idEntity = idEntity;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}
}