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
create user 'orcamento'@'%' identified by 'senha'; -- informe uma senha para o usuário "orcamento"
grant all privileges on orcamento.* to 'orcamento'@'%';
grant all privileges on orcamento.* to 'orcamentotest'@'%';
```

Ainda na linha de comando ou na ferramenta de administração, rode o script "script-create-all-db.sql". O script encontra-se no repositório do projeto, em _orcamento/src/main/resources_.

*Configuração do Eclipse*

Para aqueles que desejarem utilizar o Tomcat como servidor para rodar o projeto, adicione o Tomcat no Eclipse pelo menu *Window -> Preferences*. Nas opções da lateral esquerda, vai em *Server -> Runtime Environment*. Clique em *Add*, escolha o Tomcat 8.5, e, na tela seguinte, selecione o diretório de instalação de Tomcat e clique em *Finnish*.

Depois clique na view *Server* e clique em *New -> Server*. Selecione o Tomcat 8.5 e clique em *Finnish*.

Para aqueles que desejarem utilzar o Wildfly com oservidor para rodar o projeto, instale antes o JBoss Tools pelo Eclipse Marketplace. Dos componentes disponíveis para instalar, escolha somente "JBoss AS, WildFly & EAP Server Tools". Prossiga com a instalação.

Após o Eclipse reiniciar, adicione o Wildfly no Eclipse pelo menu *Window -> Preferences*. Nas opções da lateral esquerda, vai em *Server -> Runtime Environment*. Clique em *Add*, escolha o Wildfly 10.x. Na tela seguinte, selecione o local de instalação do Wildfly, mantendo as opções mostradas na tela. Clique em *Finnish*.

Depois clique na view *Server*, clique em *New -> Server* e selecione o Wildfly 10. Na tela seguinte mantenha as opções mostradas e clique em *Finnish*.





























### Instalação do Tomcat

A instalação do Tomcat não tem mistério, basta baixar e descompactar o arquivo zipado para o diretório desejado.

Adicione no Eclipse pelo menu *Window -> Preferences*. Nas opções da lateral, vai em *Server -> Runtime Environment*. Clique em *Add*, selecione o Tomcat 8, selecione o diretório de instalação de Tomcat e clique em *Finnish*.

Depois clique na view *Server* e clique em *New -> Server*. Selecione o Tomcat 8 e clique em *Finnish*.

### Instalação do MariaDB

O projeto utiliza o MariaDB como servidor de banco de dados. Para realizar a instalação no Windows ou Mac, baixe e instale o programa de instalação do site do MariaDB.org.

Para realizar a instalação do MariaDB, execute os seguintes comandos:

``
sudo apt-get install mariadb-server
``

Durante a instalação execute o seguinte comando para poder configurar adequadamente o MariaDB:

``
sudo mysql_secure_installation
``

Pode-se usar também um container Docker. Para criar um container MariaDB use o seguinte comando:

``
docker run --name mariadb-container -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mariadb:latest
``

Use um software como DBeaver para se conectar ao MariaDB.

### Configuração do projeto

Segue abaixo os passos para poder baixar e executar o projeto.

### Configuração do MariaDB

Após instalar o MariaDB, acesse via console ou usando uma ferramenta gráfica de administração e execute os seguintes comandos:

```sql
-- Criação das base de dados
create database orcamento;
create database orcamentotest;

-- Criação do usuário para acessar a base
create user 'orcamento'@'%' identified by 'd1nh31r0';
grant all privileges on orcamento.* to 'orcamento'@'%';
grant all privileges on orcamento.* to 'orcamentotest'@'%';

-- Criação da base de dados
use orcamento;
source /caminho/para/o/workspace/eclipse/orcamento/src/main/resources/script-create-all-db.sql;

-- Criação da base de dados de test
use orcamentotest;
source /caminho/para/o/workspace/eclipse/orcamento/src/main/resources/script-create-all-db.sql;
``` 

### Configuração do projeto no Eclipse

Efetue o clone tanto do projeto para um diretório de sua preferência usando o comando:

``
git clone https://github.com/herculeshssj/orcamento
``

No Eclipse, abra a perspectiva Git. Clique em *Add an existing Git repository*. Selecione o diretório onde foi realizado o clone do repositório, e clique em *Finnish*.

Após isso clique no repositório e selecione *Import Projects from Git Repository*. Marque o projeto "orcamento/orcamento". Clique em *Next*, e na próxima tela em *Finnish*.

O projeto precisa ser compilado antes que possa ser executado. Para isso clique com o botão direito em cima do projeto, escolha *Run As -> Maven Build*. Na janela que se abre, digite "clean install" na linha "Goal", e depois clique em *Run*.

O projeto contém as mudanças mais recentes feitas na base de dados. Assim, é necessário atualizar a base antes de executar o projeto Orçamento. Para atualizar a base, vá em *Run As -> Maven Build...*. Na linha "Goal", digite "flyway:baseline", e em seguida clique em *Run*. Após finalizar, vá novamente em *Run As -> Maven Build...* e altere a linha "Goal" para "flyway:migrate".

Atualizado as bases, clique com botão direito em cima do projeto, escolha *Run As -> Run on Server*. Na tela que se abre, selecione o Tomcat configurado e clique em *Finnish*.

Acesse o sistema pela URL http://localhost:8080/orcamento/ usando as seguintes credenciais:

* *Usuário:* admin
* *Senha:* admin

**Observação:** todas as funcionalidades já estão disponíveis para o usuário comum, basta realizar o cadastro no sistema usando o usuário admin. Para habilitar o registro é necessário fornecer uma configuração de servidor de e-mail para que a senha do usuário seja enviada e ele possa logar corretamente no sistema.

Sobre a HSlife
--------------

A HSlife Serviços de TI é uma empresa fictícia com princípios cristãos, que acredita em DEUS e na sua palavra; acredita que Jesus Cristo veio ao mundo como homem e morreu na cruz para pagar nossos pecados; acredita que devemos aceitar a Jesus Cristo como nosso único salvador e acredita que Jesus Cristo é o único caminho entre DEUS e o homem.

Nós cremos que "Jesus Cristo é a solução!" para a vida do homem. Trabalhamos com o objetivo de levar aos nossos clientes bons programas, bom atendimento e também levar a nossa mensagem: "Que os nossos programas, a nossa vida e o nosso trabalho sejam para a honra e glória de DEUS, o qual demonstrou seu amor por nós por meio de Jesus Cristo; a Ele seja o louvor, a honra, a glória, a soberania, o poder e a adoração, de hoje até o final de todos os tempos. Bendito seja DEUS."
