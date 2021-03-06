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
package br.com.hslife.orcamento.util;

import java.text.Collator;
import java.util.Comparator;

import br.com.hslife.orcamento.entity.ItemDespensa;

public class ItemDespensaComparator implements Comparator<ItemDespensa>{

	@Override
	public int compare(ItemDespensa oneEntity, ItemDespensa otherEntity) {
		try {
			
			if (oneEntity != null && otherEntity != null) {
				if (oneEntity.getDescricao() != null && otherEntity.getDescricao() != null) {
					// Dica para ordenar, deixando o Á junto com A, a, e não no final da lista
					// http://www.guj.com.br/t/listagem-em-ordem-alfabetica-com-palavras-com-acento/40369/15
					//return oneEntity.getDescricao().toLowerCase().compareTo(otherEntity.getDescricao().toLowerCase());
					return Collator.getInstance().compare(oneEntity.getDescricao(), otherEntity.getDescricao());
				}
			}			
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		}
		return 0;
	}
}
