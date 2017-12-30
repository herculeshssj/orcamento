-- Criação da base de dados
create database orcamento;

-- Criação do usuário para acessar a base
create user 'orcamento'@'%' identified by 'd1nh31r0';
grant all privileges on orcamento.* to 'orcamento'@'%';