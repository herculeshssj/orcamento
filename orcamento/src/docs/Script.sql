-- Criação da base de dados
create database veiculo;

-- A criação das tabelas da base será via ferramenta de migração que o framework oferece
-- Para cada framework, um esquema em particular

-- Criação dos schemas
create schema javaee;
create schema grails;
create schema play;
create schema spring;
create schema dotnet;
create schema vraptor;

-- O exemplo encontrado de aplicação Spring MVC não tem opção de geração automática da base. O script SQL de criação das tabelas precisou ser feito à mão.


-- drop table spring.viagem;