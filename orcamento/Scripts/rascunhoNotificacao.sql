create table notificacaosistema (
    id bigint not null auto_increment,
    dataHora datetime not null,
    titulo varchar(100) not null,
    detalhes text not null,
    visualizado boolean,
    idUsuario bigint null,
    primary key(id)
) Engine=InnoDB;

alter table notificacaosistema add constraint fk_notificacaosistema_usuario foreign key (idUsuario) references usuario (id);
