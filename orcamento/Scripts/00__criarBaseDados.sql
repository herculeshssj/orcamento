-- Exclusão das bases
drop database orcamentotest;
drop database orcamento;

-- Criação da base de dados
create database orcamento;
create database orcamentotest;

-- Criação do usuário para acessar a base
create user 'orcamento'@'%' identified by 'd1nh31r0';
grant all privileges on orcamento.* to 'orcamento'@'%';
grant all privileges on orcamento.* to 'orcamentotest'@'%';