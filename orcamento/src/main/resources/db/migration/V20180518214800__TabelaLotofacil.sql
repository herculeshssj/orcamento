create table lotofacil (
	id bigint not null auto_increment,
	concurso int not null,
	datasorteio date not null,
	bola1 int not null,
	bola2 int not null,	
	bola3 int not null,
	bola4 int not null,
	bola5 int not null,
	bola6 int not null,
	bola7 int not null,
	bola8 int not null,
	bola9 int not null,
	bola10 int not null,
	bola11 int not null,
	bola12 int not null,
	bola13 int not null,
	bola14 int not null,
	bola15 int not null,
	arrecadacaototal decimal(18,2) not null,
	ganhadores15numeros int not null,
	ganhadores14numeros int not null,
	ganhadores13numeros int not null,
	ganhadores12numeros int not null,
	ganhadores11numeros int not null,
	valorrateio15numeros decimal(18,2) not null,
	valorrateio14numeros decimal(18,2) not null,
	valorrateio13numeros decimal(18,2) not null,
	valorrateio12numeros decimal(18,2) not null,
	valorrateio11numeros decimal(18,2) not null,
	acumulado15numeros decimal(18,2) not null,
	estimativapremio decimal(18,2) not null,
	valoracumuladoespecial decimal(18,2) not null,
	primary key (id)
) Engine=InnoDB;

create table cidadeuf (
	id bigint not null auto_increment,
	cidade varchar(100) null,
	uf varchar(2) not null,
	primary key (id)
) Engine=InnoDB; 

create table lotofacil_cidadeuf (
	id_lotofacil bigint not null,
	id_cidadeuf bigint not null
) Engine=InnoDB;

alter table lotofacil_cidadeuf add constraint fk_lotofacil foreign key (id_lotofacil) references lotofacil (id);

alter table lotofacil_cidadeuf add constraint fk_cidadeuf foreign key (id_cidadeuf) references cidadeuf (id);