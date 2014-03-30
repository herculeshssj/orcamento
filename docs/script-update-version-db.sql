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

/*** Script de atualização da base de dados ***/

/*** ATUALIZAÇÃO DA BASE DE DADOS PARA A VERSÃO MAR2014.2 ***/

-- Atualização de versão
update versao set ativo = false;
insert into versao (versao, ativo) values ('MAI2014', true);

-- Alterações na tabela lancamentoconta;
alter table lancamentoconta add column mes integer null;
alter table lancamentoconta add column ano integer null;
alter table lancamentoconta add column diaVencimento integer null;
alter table lancamentoconta add column totalParcela integer null;

alter table lancamentoconta add column dataAquisicao date null;
alter table lancamentoconta add column valorParcela decimal(18,2) null;

alter table lancamentoconta add column naturezaLancamento varchar(15) not null;
alter table lancamentoconta add column periodoLancamento varchar(15) null;
alter table lancamentoconta add column statusLancamento varchar(15) not null;

alter table lancamentoconta add column lancamentoPai bigint null;

alter table lancamentoconta add constraint fk_lancamentopai_lancamentoconta foreign key(lancamentoPai) references lancamentoconta(id);

update lancamentoconta set statusLancamento = 'REGISTRADO';
update lancamentoconta set statusLancamento = 'AGENDADO' where agendado = true;
update lancamentoconta set statusLancamento = 'QUITADO' where quitado = true;