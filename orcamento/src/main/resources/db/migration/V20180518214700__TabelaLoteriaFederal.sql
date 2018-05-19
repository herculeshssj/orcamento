create table federal (
	id bigint not null auto_increment,
	concurso int not null,
	dataSorteio date not null,
	primeiroPremio int not null,
	segundoPremio int not null,
	terceiroPremio int not null,
	quartoPremio int not null,
	quintaPremio int not null,
	valorPrimeiroPremio decimal(18,2) not null,
	valorSegundoPremio decimal(18,2) not null,
	valorTerceiroPremio decimal(18,2) not null,
	valorQuartoPremio decimal(18,2) not null,
	valorQuintoPremio decimal(18,2) not null,
	primary key (id)
) Engine=InnoDB;