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

alter table seguro add column tipoSeguro varchar(15) not null;
alter table seguro add column periodicidadeRenovacao varchar(15) not null;
alter table seguro add column periodicidadePagamento varchar(15) not null;
alter table seguro add column premioSeguro varchar(15) not null;
alter table seguro add column dataRenovacao date not null;
alter table seguro change column `valorCobertura` `valorCobertura` decimal(18,2) null;
alter table seguro change column `cobertura` `cobertura` text null;
alter table seguro add column ativo boolean;
alter table seguro add column idArquivo bigint null;
alter table seguro add column idLancamentoPeriodico bigint not null;

alter table seguro drop foreign key fk_seguro_usuario;
alter table seguro drop column idUsuario;

alter table seguro add constraint fk_arquivo_seguro foreign key (idArquivo) references arquivo (id);
alter table seguro add constraint fk_lancamento_periodico_seguro foreign key (idLancamentoPeriodico) references lancamentoperiodico (id);