Orçamento Doméstico
===================

Controle de orçamento doméstico familiar
-----------------------------------------

Destinado a controlar as despesas domésticas de uma família. Tem opções para controlar contas a pagar e receber, pagamentos, lançamentos bancários, saldos de contas, realizar fechamento de períodos e categorizar as informações. Permite que mais de uma pessoa possa gerenciar suas despesas, compartilhando com uma ou mais pessoas. Pode gerenciar as contas de micros empresas que buscam um sistema simples de controle de despesas.

### Ambiente de desenvolvimento

*Requisitos:*

* Java JDK SE 8 update 25 ou superior;
* Eclipse Luna 4.4 ou superior;
* Git 1.7 ou superior;
* Tomcat 8.0.14 ou superior;
* MySQL 5.5 ou superior;
* astah Community 6 ou superior;
* Pencil 2.0.6 ou superior;
* Windows, Linux ou Mac, qualquer versão capaz de rodar os softwares acima.

*Links de download:*

* *Java JDK SE 8*: http://www.oracle.com/technetwork/java/javase/downloads/index.html
* *Eclipse Luna*: http://www.eclipse.org/downloads
* *Github for Windows/Mac*: https://github.com/ 
* *Tomcat 8*: http://tomcat.apache.org/download-80.cgi
* *MySQL 5.5*: http://dev.mysql.com/downloads/mysql/5.6.html
* *astah Community*: http://astah.net/editions/community
* *Pencil*: http://pencil.evolus.vn/Downloads.html

### Instalação do Java

A instalação do Java no Windows e Mac OS X não tem mistério. Basta baixar o pacote de acordo com a versão do SO e realizar a instalação através do assistente.

Para o Ubuntu, utilize os seguintes comandos:

sudo add-apt-repository ppa:webupd8team/java && sudo apt-get update
sudo apt-get install oracle-java8-installer


### Instalação do Eclipse Luna

A instalação do Eclipse Luna não tem mistério. Após realizar a instalação do Java, baixe o arquivo compactado do Eclipse Luna JavaEE de acordo com o SO e descompacte em uma pasta de sua preferência.

### Instalação do GIT

Para realizar a instalação do Git no Windows utiliza-se geralmente a ferramenta disponibiliza pelo Github.

Para realizar a instalação do Git no Linux, execute o seguinte comando:

sudo apt-get install git

Para realizar a instalação do Git no Mac, pode-se instalar a ferramenta disponibilizada pelo Github, ou utilizar o Xcode.

### Instalação do Tomcat

A instalação do Tomcat não tem mistério, basta baixar e descompactar o arquivo zipado para o diretório desejado.

Adicione no Eclipse pelo menu Window -> Preferences. Nas opções da lateral, vai em Server -> Runtime Environment. Clique em Add, selecione o Tomcat 8, selecione o diretório de instalação de Tomcat e clique em Finnish.

Depois clique na view Server e clique em New -> Server. Selecione o Tomcat 8 e clique em Finnish.

### Instalação do MySQL

O projeto utiliza o MySQL como servidor de banco de dados. Para realizar a instalação no Windows ou Mac, baixe e instale o programa de instalação do site da Oracle.

Para realizar a instalação do MySQL, execute os seguintes comandos:

sudo apt-get install mysql-server

Durante a instalação será pedido uma nova senha para o usuário +root+ do MySQL.

### Configuração do projeto

Segue abaixo os passos para poder baixar e executar o projeto.

### Configuração do MySQL

Após instalar o MySQL, acesse via console ou usando uma ferramenta gráfica de administração e execute os seguintes comandos:

```sql
-- Criação da base de dados
create database orcamento;
create database orcamentotest;

-- Criação do usuário para acessar a base
create user 'orcamento'@'localhost' identified by 'd1nh31r0';
grant all privileges on orcamento.* to 'orcamento'@'localhost';
grant all privileges on orcamentotest.* to 'orcamento'@'localhost';

-- Criação da base de dados
use orcamento;
source /caminho/para/o/workspace/eclipse/Orcamento/docs/script-update-all-db.sql;

use orcamentoTest;
source /caminho/para/o/workspace/eclipse/Orcamento/docs/script-update-all-db.sql;
```

*Usuário:* admin
*Senha:* admin

**Observação:** todas as funcionalidades já estão disponíveis para o usuário comum, basta realizar o registro no sistema.

### Configuração do projeto no Eclipse

Efetue o clone do projeto para um diretório de sua preferência usando o comando:

git clone https://github.com/herculeshssj/orcamento

No Eclipse, abra a perspectiva Git. Clique em Add an existing Git repository. Selecione o diretório onde foi realizado o clone do repositório, e clique em Finnish.

Após isso clique no repositório e selecione Import Projects from Git Repository. Clique em Next, e na próxima tela em Finnish.

Para executar o projeto, clique com botão direito em cima do projeto, escolha Run As -> Run on Server. Na tela que se abre, selecione o Tomcat configurado e clique em Finnish.
