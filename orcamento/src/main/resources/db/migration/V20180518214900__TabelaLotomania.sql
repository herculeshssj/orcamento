create table lotomania (
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
	bola16 int not null,
	bola17 int not null,
	bola18 int not null,
	bola19 int not null,
	bola20 int not null,
	arrecadacaototal decimal(18,2) not null,
	ganhadores20numeros int not null,
	ganhadores19numeros int not null,
	ganhadores18numeros int not null,
	ganhadores17numeros int not null,
	ganhadores16numeros int not null,
	ganhadoresnenhumnumero int not null,
	valorrateio20numeros decimal(18,2) not null,
	valorrateio19numeros decimal(18,2) not null,
	valorrateio18numeros decimal(18,2) not null,
	valorrateio17numeros decimal(18,2) not null,
	valorrateio16numeros decimal(18,2) not null,
	valorrateionenhumnumero decimal(18,2) not null,
	acumulado20numeros decimal(18,2) not null,
	acumulado19numeros decimal(18,2) not null,
	acumulado18numeros decimal(18,2) not null,
	acumulado17numeros decimal(18,2) not null,
	acumulado16numeros decimal(18,2) not null,
	acumuladonenhumnumero decimal(18,2) not null,
	estimativapremio decimal(18,2) not null,
	valoracumuladoespecial decimal(18,2) not null,
	primary key (id)
) Engine=InnoDB;

create table lotomania_cidadeuf (
	id_lotomania bigint not null,
	id_cidadeuf bigint not null
) Engine=InnoDB;

alter table lotomania_cidadeuf add constraint fk_lotomania foreign key (id_lotomania) references lotomania (id);

alter table lotomania_cidadeuf add constraint fk_cidadeuf_lotomania foreign key (id_cidadeuf) references cidadeuf (id);