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

-- Veículos - Github Issue orcamento-maven #8
create table caracteristicaveiculo (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    ativo boolean,
    primary key (id)
) Engine=InnoDB;

create table combustivel (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    distribuidora varchar(50) not null,
    primary key(id)
) Engine=InnoDB;

create table tipoveiculo (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    primary key(id)
) Engine=InnoDB;

create table montadora (
	id bigint not null,
    descricao varchar(50) not null,
    primary key(id)
) Engine=InnoDB;

create table modeloveiculo (
	id bigint not null auto_increment,
	descricao varchar(50) not null,
	potencia double,
	anoModelo integer,
	cilindradas integer,
	numeroCilindros integer,
	numeroPortas integer,
	detalhesModeloVeiculo varchar(255),
	idTipoVeiculo bigint not null,
	idMontadora bigint not null,
	idCombustivel bigint not null,
	idUsuario bigint not null,
	primary key(id)
);

alter table modeloveiculo add constraint fk_modeloveiculo_tipoveiculo foreign key (idTipoVeiculo) references tipoveiculo(id);
alter table modeloveiculo add constraint fk_modeloveiculo_montadora foreign key (idMontadora) references montadora(id);
alter table modeloveiculo add constraint fk_modeloveiculo_combustivel foreign key (idCombustivel) references combustivel(id);
alter table modeloveiculo add constraint fk_modeloveiculo_usuario foreign key (idUsuario) references usuario(id);

create table seguro (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    dataAquisicao date not null,
    validade date null,
    cobertura varchar(100) null,
    valorCobertura decimal(18,2) not null,
    valorSeguro decimal(18,2) not null,
    observacao varchar(255) null,
    idUsuario bigint not null,
    primary key(id)    
) Engine=InnoDB;

alter table seguro add constraint fk_seguro_usuario foreign key (idUsuario) references usuario(id);

create table tipolancamentoveiculo (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    primary key(id)
) Engine=InnoDB;

create table veiculo (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    placa varchar(10) null,
    cor varchar(10) null,
    anoFabricacao integer not null,
    odometro decimal(18,2) null,
    observacao varchar(255) null,
    renavam varchar(30) null,
    chassis varchar(35) null,
    numeroMotor varchar(20) null,
    dataCompra date not null,
    dataVenda date null,
    valorPago decimal(18,2) not null,
    valorVenda decimal(18,2) null,
    odometroCompra decimal(18,2) null,
    odometroVenda decimal(18,2) null,
    garantia date null,
    observacaoCompra varchar(255) null,
    observacaoVenda varchar(255) null,
    idComprador bigint null,
    idVendedor bigint not null,
    idMeioPagamentoCompra bigint not null,
    idMeioPagamentoVenda bigint null,
    idSeguro bigint null,
    idModeloVeiculo bigint not null,
    idUsuario bigint not null,
    primary key (id)
) Engine=InnoDB;

alter table veiculo add constraint fk_veiculo_vendedor foreign key(idVendedor) references favorecido(id);
alter table veiculo add constraint fk_veiculo_meiopagamentovenda foreign key(idMeioPagamentoVenda) references meiopagamento(id);
alter table veiculo add constraint fk_veiculo_meiopagamentocompra foreign key(idMeioPagamentoCompra) references meiopagamento(id);
alter table veiculo add constraint fk_veiculo_comprador foreign key(idComprador) references favorecido(id);
alter table veiculo add constraint fk_veiculo_modelo_veiculo foreign key(idModeloVeiculo) references modeloveiculo(id);
alter table veiculo add constraint fk_veiculo_usuario foreign key(idUsuario) references usuario(id);
alter table veiculo add constraint fk_veiculo_seguro foreign key(idSeguro) references seguro(id);

create table caracteristicas (
	idVeiculo bigint not null,
    idCaracteristica bigint not null,
    unique (idVeiculo),
    primary key (idVeiculo, idCaracteristica)
) Engine=InnoDB;

alter table caracteristicas add constraint fk_caracteristicas_veiculo foreign key(idVeiculo) references veiculo(id);
alter table caracteristicas add constraint fk_caracteristicas_caracteristica foreign key(idCaracteristica) references caracteristicaveiculo(id);

create table lancamentoveiculo (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    kilometragem decimal(18,2) null,
    valor decimal(18,2) not null,
    quantidadeCombustivel decimal(18,2) null,
    tanqueCheio boolean,
    dataVencimento date null,
    dataPagamento date not null,
    idCombustivel bigint null,
    idTipoLancamentoVeiculo bigint not null,
    primary key (id)
) Engine=InnoDB;

alter table lancamentoveiculo add constraint fk_lancamentoveiculo_combustivel foreign key(idCombustivel) references combustivel(id);
alter table lancamentoveiculo add constraint fk_lancamentoveiculo_tipolancamentoveiculo foreign key(idTipoLancamentoVeiculo) references tipolancamentoveiculo(id);

create table viagem (
	id bigint not null auto_increment,
    descricao varchar(50) not null,
    dataHoraSaida datetime null,
    dataHoraChegada datetime null,
    origem varchar(100) null,
    destino varchar(100) null,
    observacao varchar(255) null,
    odometroSaida decimal(18,2) null,
    odometroChegada decimal(18,2) null,
    quantidadeCombustivel decimal(18,2) null,
    idVeiculo bigint not null,
    primary key (id)
) Engine=InnoDB;

alter table viagem add constraint fk_viagem_veiculo foreign key (idVeiculo) references veiculo(id);