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
package br.com.hslife.orcamento.enumeration;

import org.springframework.context.annotation.DependsOn;

import java.util.Calendar;
import java.util.Date;

public enum Periodicidade {
    FIXO("Fixo"), MENSAL("Mensal"), BIMESTRAL("Bimestral"), TRIMESTRAL("Trimestral"), QUADRIMESTRAL("Quadrimestral"), SEMESTRAL("Semestral"), ANUAL("Anual");

    private String descricao;

    private Periodicidade(String descricao) {
        this.descricao = descricao;
    }

    public String toString() {
        return descricao;
    }

    public Date getDataPeriodo(Date dataPeriodo) {
        Calendar periodo = Calendar.getInstance();
        periodo.setTime(dataPeriodo);

        switch (this) {
            case MENSAL : periodo.add(Calendar.MONTH, 1); break;
            case BIMESTRAL : periodo.add(Calendar.MONTH, 2); break;
            case TRIMESTRAL : periodo.add(Calendar.MONTH, 3); break;
            case QUADRIMESTRAL : periodo.add(Calendar.MONTH, 4); break;
            case SEMESTRAL : periodo.add(Calendar.MONTH, 6); break;
            case ANUAL : periodo.add(Calendar.YEAR, 1); break;
            default : throw new IllegalArgumentException("Valor incorreto para o método: 'FIXO'");
        }

        return periodo.getTime();
    }

    public Calendar getDataPeriodo(Calendar dataPeriodo) {
        Calendar periodo = Calendar.getInstance();

        switch (this) {
            case MENSAL : periodo.add(Calendar.MONTH, 1); break;
            case BIMESTRAL : periodo.add(Calendar.MONTH, 2); break;
            case TRIMESTRAL : periodo.add(Calendar.MONTH, 3); break;
            case QUADRIMESTRAL : periodo.add(Calendar.MONTH, 4); break;
            case SEMESTRAL : periodo.add(Calendar.MONTH, 6); break;
            case ANUAL : periodo.add(Calendar.YEAR, 1); break;
            default : throw new IllegalArgumentException("Valor incorreto para o método: 'FIXO'");
        }

        return periodo;
    }
}
