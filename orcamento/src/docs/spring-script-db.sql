create table spring.caracteristica_veiculo(
	id bigserial not null,
	descricao varchar(50) not null,
	ativo boolean,
	primary key(id)
);

create table spring.combustivel(
	id bigserial not null,
	descricao varchar(50) not null,
	distribuidora varchar(50) not null,
	primary key(id)
);

create table spring.usuario(
	id bigserial not null,
	nome varchar(50) not null,
	login varchar(50) not null unique,
	senha varchar(40) not null, -- SHA1
	data_criacao date not null,
	email varchar(30) not null,
	ativo boolean,
	tipo_usuario varchar(15) not null,
	primary key(id)
);

create table spring.favorecido(
	id bigserial not null,
	nome varchar(50) not null,
	contato varchar(255) null,
	cpfCnpj varchar(14) not null,
	ativo boolean,
	id_usuario bigint not null,
	primary key(id)
);

alter table spring.favorecido add constraint fk_favorecido_usuario foreign key (id_usuario) references spring.usuario(id) on update no action on delete no action;

create table spring.meio_pagamento(
	id bigserial not null,
	descricao varchar(50) not null,
	ativo boolean,
	primary key(id)
);

create table spring.tipo_veiculo(
	id bigserial not null,
	descricao varchar(50) not null,
	primary key(id)
);

create table spring.montadora(
	id bigserial not null,
	descricao varchar(50) not null,
	primary key(id)
);

create table spring.modelo_veiculo(
	id bigserial not null,
	descricao varchar(50) not null,
	potencia real,
	ano_modelo int,
	cilindradas int,
	numero_cilindros int,
	numero_portas int,
	detalhes_modelo_veiculo varchar(255),
	id_tipo_veiculo bigint not null,
	id_montadora bigint not null,
	id_combustivel bigint not null,
	id_usuario bigint not null,
	primary key(id)
);

alter table spring.modelo_veiculo add constraint fk_modelo_veiculo_tipo_veiculo foreign key (id_tipo_veiculo) references spring.tipo_veiculo(id) on update no action on delete no action;
alter table spring.modelo_veiculo add constraint fk_modelo_veiculo_montadora foreign key (id_montadora) references spring.montadora(id) on update no action on delete no action;
alter table spring.modelo_veiculo add constraint fk_modelo_veiculo_combustivel foreign key (id_combustivel) references spring.combustivel(id) on update no action on delete no action;
alter table spring.modelo_veiculo add constraint fk_modelo_veiculo_usuario foreign key (id_usuario) references spring.usuario(id) on update no action on delete no action;

create table spring.seguro(
	id bigserial not null,
	descricao varchar(50) not null,
	validade date not null,
	cobertura varchar(100) null,
	valor real,
	observacao varchar(255) null,
	id_usuario bigint not null,
	primary key(id)
);

alter table spring.seguro add constraint fk_seguro_usuario foreign key (id_usuario) references spring.usuario(id) on update no action on delete no action;

create table spring.tipo_lancamento_veiculo(
	id bigserial not null,
	descricao varchar(50) not null,
	primary key(id)
);

create table spring.veiculo(
	id bigserial not null,
	descricao varchar(50) not null,
	placa varchar(10),
	cor varchar(10),
	ano_fabricacao int,
	odometro real,
	observacao varchar(255) null,
	renavam varchar(30) null,
	chassis varchar(35) null,
	numero_motor varchar(20) null,
	data_compra date not null,
	valor_pago real,
	odometro_compra real,
	garantia date,
	observacao_compra varchar(255) null,
	id_vendedor bigint not null,
	data_venda date not null,
	valor_venda real,
	id_meio_pagamento_compra bigint null,
	id_meio_pagamento_venda bigint not null,
	id_comprador bigint null,
	observacao_venda varchar(255) null,
	odometro_venda real,
	id_modelo_veiculo bigint not null, 
	id_usuario bigint not null,
	id_seguro bigint null,
	primary key(id)
);

alter table spring.veiculo add constraint fk_veiculo_vendedor foreign key(id_vendedor) references spring.favorecido(id) on update no action on delete no action;
alter table spring.veiculo add constraint fk_veiculo_meio_pagamento_venda foreign key(id_meio_pagamento_venda) references spring.meio_pagamento(id) on update no action on delete no action;
alter table spring.veiculo add constraint fk_veiculo_meio_pagamento_compra foreign key(id_meio_pagamento_compra) references spring.meio_pagamento(id) on update no action on delete no action;
alter table spring.veiculo add constraint fk_veiculo_comprador foreign key(id_comprador) references spring.favorecido(id) on update no action on delete no action;
alter table spring.veiculo add constraint fk_veiculo_modelo_veiculo foreign key(id_modelo_veiculo) references spring.modelo_veiculo(id) on update no action on delete no action;
alter table spring.veiculo add constraint fk_veiculo_usuario foreign key(id_usuario) references spring.usuario(id) on update no action on delete no action;
alter table spring.veiculo add constraint fk_veiculo_seguro foreign key(id_seguro) references spring.seguro(id) on update no action on delete no action;

create table spring.caracteristicas(
	id_veiculo bigint not null,
	id_caracteristica bigint not null,
	unique(id_veiculo),
	primary key (id_veiculo, id_caracteristica)
);

alter table spring.caracteristicas add constraint fk_caracteristicas_veiculo foreign key(id_veiculo) references spring.veiculo(id) on update no action on delete no action;
alter table spring.caracteristicas add constraint fk_caracteristicas_caracteristica foreign key(id_caracteristica) references spring.caracteristica_veiculo(id) on update no action on delete no action;

create table spring.lancamento_veiculo(
	id bigserial not null,
	descricao varchar(50) not null,
	kilometragem real,
	valor real,
	quantidade_combustivel real,
	tanque_cheio boolean,
	data_vencimento date null,
	data_pagamento date not null,
	id_combustivel bigint null,
	id_tipo_lancamento_veiculo bigint not null,
	primary key(id)
);

alter table spring.lancamento_veiculo add constraint fk_lancamento_veiculo_combustivel foreign key(id_combustivel) references spring.combustivel(id) on update no action on delete no action;
alter table spring.lancamento_veiculo add constraint fk_lancamento_veiculo_tipo_lancamento_veiculo foreign key(id_tipo_lancamento_veiculo) references spring.tipo_lancamento_veiculo(id) on update no action on delete no action;

create table spring.viagem(
	id bigserial not null,
	descricao varchar(50) not null,
	datahora_saida timestamp null,
	datahora_chegada timestamp null,
	origem varchar(100) null,
	destino varchar(100) null,
	observacao varchar(255) null,
	odometro_saida real,
	odometro_chegada real,
	quantidade_combustivel real,
	id_veiculo bigint not null,
	primary key(id)
);

alter table spring.viagem add constraint fk_viagem_veiculo foreign key (id_veiculo) references spring.veiculo(id) on update no action on delete no action;