Orçamento Doméstico
===================

[![Jenkins](https://img.shields.io/jenkins/s/https/jenkins.qa.ubuntu.com/view/Precise/view/All%20Precise/job/precise-desktop-amd64_default.svg)]()
[![Maven Central](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven.svg)]()
[![License (LGPL version 3.0)](https://img.shields.io/badge/license-GNU%20LGPL%20version%203.0-green.svg)](http://opensource.org/licenses/LGPL-3.0)
[![Conda](https://img.shields.io/conda/pn/conda-forge/python.svg)]()


Controle de orçamento doméstico familiar
-----------------------------------------

Destinado a controlar as despesas domésticas de uma família. Tem opções para controlar contas a pagar e receber, pagamentos, lançamentos bancários, saldos de contas, realizar fechamento de períodos, categorizar as informações, registrar os investimentos e acompanhar alguns itens do lar, como despensa e saúde. Permite que mais de uma pessoa possa gerenciar suas despesas, compartilhando com uma ou mais pessoas. Pode gerenciar também as contas de micros empresas que buscam um sistema simples de controle de despesas.

# Ambiente de desenvolvimento

*Requisitos:*

* Oracle Java JDK SE 8 ou OpenJDK 8 (qualquer update);
* Eclipse Neon 4.6 ou superior;
* Git 2.0 ou superior;
* Tomcat 8.5 ou Wildfly 10;
* MariaDB 10 ou superior;
* astah Community 7.0 ou superior;
* Pencil 3.0 ou superior;
* Windows, Linux ou Mac, qualquer versão capaz de rodar os softwares acima.

*Links de download:*

* *Java JDK SE 8*: http://www.oracle.com/technetwork/java/javase/downloads/index.html
* *Eclipse Neon*: http://www.eclipse.org/neon/
* *Git*: https://git-scm.com/downloads 
* *Tomcat 8.5*: http://tomcat.apache.org/download-80.cgi
* *Wildfly 10*: http://wildfly.org/downloads/
* *MariaDB 10*: https://downloads.mariadb.org
* *astah Community*: http://astah.net/editions/community
* *Pencil*: http://pencil.evolus.vn/Downloads.html

*Obs:* Siga as instruções de instalação e configuração básica disponíveis no site de cada ferramenta. 

*Git*

Realize o clone do projeto via linha de comando, ou através do Eclipse, pela Perspectiva "Git". 

Após realizar o clone do repositório, adicione o projeto no Eclipse (caso tenha feito o clone via linha de comando), e use a opção "Import Projects..." para importar o projeto. Não esqueça de deixar marcado somente o projeto "orcamento/orcamento", que ele é a versão Maven mais recente do projeto.

*Configuração do MariaDB*

Após instalar o MariaDB, acesse-o via linha de comando ou usando uma ferramenta gráfica de administração e execute os seguintes comandos:

```sql
-- Criação das base de dados
create database orcamento;
create database orcamentotest;

-- Criação do usuário para acessar a base
create user 'orcamento'@'%' identified by 'd1nh31r0';
grant all privileges on orcamento.* to 'orcamento'@'%';
grant all privileges on orcamento.* to 'orcamentotest'@'%';
```

Ainda na linha de comando ou na ferramenta de administração, rode o script "script-create-all-db.sql". O script encontra-se no repositório do projeto, em _orcamento/src/main/resources_.

*Configuração do servidores de aplicação*

Para aqueles que desejarem utilizar o Tomcat como servidor para rodar o projeto, adicione o Tomcat no Eclipse pelo menu *Window -> Preferences*. Nas opções da lateral esquerda, vai em *Server -> Runtime Environment*. Clique em *Add*, escolha o Tomcat 8.5, e, na tela seguinte, selecione o diretório de instalação de Tomcat e clique em *Finnish*.

Depois clique na view *Server* e clique em *New -> Server*. Selecione o Tomcat 8.5 e clique em *Finnish*.

Para aqueles que desejarem utilzar o Wildfly como servidor para rodar o projeto, instale antes o JBoss Tools pelo Eclipse Marketplace. Dos componentes disponíveis para instalar, escolha somente "JBoss AS, WildFly & EAP Server Tools". Prossiga com a instalação.

Após o Eclipse reiniciar, adicione o Wildfly no Eclipse pelo menu *Window -> Preferences*. Nas opções da lateral esquerda, vai em *Server -> Runtime Environment*. Clique em *Add*, escolha o Wildfly 10.x. Na tela seguinte, selecione o local de instalação do Wildfly, mantendo as opções mostradas na tela. Clique em *Finnish*.

Depois clique na view *Server*, clique em *New -> Server* e selecione o Wildfly 10. Na tela seguinte mantenha as opções mostradas e clique em *Finnish*.

*Configuração do Maven*

Antes de iniciar o sistema pelo Eclipse, é necessário aplicar as alterações na base que foram realizadas desde a inclusão da biblioteca Flyway. Para realizar esta operação, vá no menu *Run -> Run Configurations...*. No item "Maven Build", crie dois perfis de execução, um para a base "orcamento" e outro para a base "orcamentotest". Selecione o projeto na linha "Base directory", e nos alvos Maven (Goals), digite "flyway:baseline" primeiramente. Preencha o campo "Profiles" com:

|Profile   |Base         |
|----------|-------------|
|production|orcamento    |
|test      |orcamentotest|

Vá na aba "Environment" e inclua as seguintes variáveis de ambiente:

|Variable    |Value                                      |
|------------|-------------------------------------------|
|jdbcDriver  |org.mariadb.jdbc.Driver                    |
|jdbcUrl     |jdbc:mariadb://localhost:3306/orcamento    |
|jdbcUrlTest |jdbc:mariadb://localhost:3306/orcamentotest|
|jdbcUsername|orcamento                                  |
|jdbcPassword|d1nh31r0                                   |

Clique no botão "Run" para preparar ambas as bases para trabalhar com o Flyway.

Volte nos dois perfis Maven e substitua "flyway:baseline" por "flyway:migrate". Execute cada um dos perfis para atualizar as bases de dados.

*Executando o projeto*

Atualizado as bases, clique com botão direito em cima do projeto, escolha *Run As -> Run on Server*. Na tela que se abre, selecione o Tomcat 8.5 ou Wildfly 10.x e clique em *Finnish*.

Acesse o sistema pela URL http://localhost:8080/orcamento/ usando as seguintes credenciais:

* *Usuário:* admin
* *Senha:* admin

**Observação:** todas as funcionalidades já estão disponíveis para o usuário comum, basta realizar o cadastro no sistema usando o usuário admin. Para habilitar o registro é necessário fornecer uma configuração de servidor de e-mail para que a senha do usuário seja enviada e ele possa logar corretamente no sistema.

Sobre a HSlife
--------------

A HSlife Serviços de TI é uma empresa fictícia com princípios cristãos, que acredita em DEUS e na sua palavra; acredita que Jesus Cristo veio ao mundo como homem e morreu na cruz para pagar nossos pecados; acredita que devemos aceitar a Jesus Cristo como nosso único salvador e acredita que Jesus Cristo é o único caminho entre DEUS e o homem.

Nós cremos que "Jesus Cristo é a solução!" para a vida do homem. Trabalhamos com o objetivo de levar aos nossos clientes bons programas, bom atendimento e também levar a nossa mensagem: "Que os nossos programas, a nossa vida e o nosso trabalho sejam para a honra e glória de DEUS, o qual demonstrou seu amor por nós por meio de Jesus Cristo; a Ele seja o louvor, a honra, a glória, a soberania, o poder e a adoração, de hoje até o final de todos os tempos. Bendito seja DEUS."