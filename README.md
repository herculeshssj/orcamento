Orçamento Doméstico
===================

Controle de orçamento doméstico familiar
-----------------------------------------

Destinado a controlar as despesas domésticas de uma família. Tem opções para controlar contas a pagar e receber, pagamentos, lançamentos bancários, saldos de contas, realizar fechamento de períodos e categorizar as informações. Permite que mais de uma pessoa possa gerenciar suas despesas, compartilhando com uma ou mais pessoas. Pode gerenciar as contas de micros empresas que buscam um sistema simples de controle de despesas.

### Ambiente de desenvolvimento

*Requisitos:*

* Oracle Java JDK SE 8 ou OpenJDK 8 (qualquer update);
* Eclipse Luna 4.4 ou superior;
* Git 1.9 ou superior;
* Tomcat 8.0.30 ou superior;
* MySQL 5.5 ou MariaBD 5.5 ou superior;
* astah Community 6 ou superior;
* Pencil 2.0.6 ou superior;
* Windows, Linux ou Mac, qualquer versão capaz de rodar os softwares acima.

*Links de download:*

* *Java JDK SE 8*: http://www.oracle.com/technetwork/java/javase/downloads/index.html
* *Eclipse Luna*: http://www.eclipse.org/downloads
* *Github for Windows/Mac*: https://github.com/ 
* *Tomcat 8*: http://tomcat.apache.org/download-80.cgi
* *MySQL 5.5*: http://dev.mysql.com/downloads/mysql/5.6.html
* *MariaDB 10*: https://downloads.mariadb.org
* *astah Community*: http://astah.net/editions/community
* *Pencil*: http://pencil.evolus.vn/Downloads.html

### Instalação do Java

A instalação do Java no Windows e Mac OS X não tem mistério. Basta baixar o pacote de acordo com a versão do SO e realizar a instalação através do assistente.

Para instalar o Oracle Java no Ubuntu, utilize os seguintes comandos:

``
sudo add-apt-repository ppa:webupd8team/java && sudo apt-get update && sudo apt-get install oracle-java8-installer
``

### Instalação do Eclipse Luna

A instalação do Eclipse Luna não tem mistério. Após realizar a instalação do Java, baixe o arquivo compactado do Eclipse Luna JavaEE de acordo com o SO e descompacte em uma pasta de sua preferência.

### Instalação do GIT

Para realizar a instalação do Git no Windows utiliza-se geralmente a ferramenta disponibiliza pelo Github.

Para realizar a instalação do Git no Linux, execute o seguinte comando:

``
sudo apt-get install git
``

Para realizar a instalação do Git no Mac, pode-se instalar a ferramenta disponibilizada pelo Github, ou utilizar o Xcode.

### Instalação do Tomcat

A instalação do Tomcat não tem mistério, basta baixar e descompactar o arquivo zipado para o diretório desejado.

Adicione no Eclipse pelo menu *Window -> Preferences*. Nas opções da lateral, vai em *Server -> Runtime Environment*. Clique em *Add*, selecione o Tomcat 8, selecione o diretório de instalação de Tomcat e clique em *Finnish*.

Depois clique na view *Server* e clique em *New -> Server*. Selecione o Tomcat 8 e clique em *Finnish*.

### Instalação do MySQL / MariaDB

O projeto utiliza o MySQL como servidor de banco de dados. Para realizar a instalação no Windows ou Mac, baixe e instale o programa de instalação do site do MySQL Community, ou do MariaDB.org.

Para realizar a instalação do MySQL, execute os seguintes comandos:

``
sudo apt-get install mysql-server
``

Durante a instalação será pedido uma nova senha para o usuário +root+ do MySQL.

### Configuração do projeto

Segue abaixo os passos para poder baixar e executar o projeto.

### Configuração do MySQL / MariaDB

Após instalar o MySQL, acesse via console ou usando uma ferramenta gráfica de administração e execute os seguintes comandos:

```sql
-- Criação da base de dados
create database orcamento;
create database orcamentotest;

-- Criação do usuário para acessar a base
create user 'orcamento'@'%' identified by 'd1nh31r0';
grant all privileges on orcamento.* to 'orcamento'@'%';
grant all privileges on orcamentotest.* to 'orcamento'@'%';

-- Criação da base de dados
use orcamento;
source /caminho/para/o/workspace/eclipse/orcamento/src/main/resources/script-create-all-db.sql;

use orcamentotest;
source /caminho/para/o/workspace/eclipse/orcamento/src/main/resources/script-create-all-db.sql;
```

### Configuração do projeto no Eclipse

Efetue o clone tanto do projeto para um diretório de sua preferência usando o comando:

``
git clone https://github.com/herculeshssj/orcamento
``

No Eclipse, abra a perspectiva Git. Clique em *Add an existing Git repository*. Selecione o diretório onde foi realizado o clone do repositório, e clique em *Finnish*.

Após isso clique no repositório e selecione *Import Projects from Git Repository*. Clique em *Next*, e na próxima tela em *Finnish*.

O projeto precisa ser compilado antes que possa ser executado. Para isso clique com o botão direito em cima do projeto, escolha *Run As -> Maven Build*. Na janela que se abre, digite "clean install" na linha "Goal", e depois clique em *Run*.

O projeto contém as mudanças mais recentes feitas na base de dados. Assim, é necessário atualizar a base antes de executar o projeto Orçamento. Para atualizar a base de testes, basta clicar com o botão direito em cima da classe MigrarDB, *Run As -> Java Application*. A classe está no pacote "br.com.hslife.orcamento.db".

Atualizado a base de teste, é necessário efetuar a atualização da base de produção. Clique com botão direito em cima da classe MigrarDB, selecione *Run As -> Run Configurations*. No lado esquerdo, selecione o *Migrar DB* no item *Java Application*. No lado direito, selecione a aba *Arguments*, e informe a palavra "producao" na seção "Program Arguments". Clique no botão *Run* para efetuar a atualização da base.

Atualizado as bases, clique com botão direito em cima do projeto, escolha *Run As -> Run on Server*. Na tela que se abre, selecione o Tomcat configurado e clique em *Finnish*.

Acesse o sistema pela URL http://localhost:8080/orcamento/ usando as seguintes credenciais:

* *Usuário:* admin
* *Senha:* admin

**Observação:** todas as funcionalidades já estão disponíveis para o usuário comum, basta realizar o cadastro no sistema usando o usuário admin. Para habilitar o registro é necessário fornecer uma configuração de servidor de e-mail para que a senha do usuário seja enviada e ele possa logar corretamente no sistema.

Sobre a HSlife
--------------

A HSlife Serviços de TI é uma empresa fictícia com princípios cristãos, que acredita em DEUS e na sua palavra; acredita que Jesus Cristo veio ao mundo como homem e morreu na cruz para pagar nossos pecados; acredita que devemos aceitar a Jesus Cristo como nosso único salvador e acredita que Jesus Cristo é o único caminho entre DEUS e o homem.

Nós cremos que "Jesus Cristo é a solução!" para a vida do homem. Trabalhamos com o objetivo de levar aos nossos clientes bons programas, bom atendimento e também levar a nossa mensagem: "Que os nossos programas, a nossa vida e o nosso trabalho sejam para a honra e glória de DEUS, o qual demonstrou seu amor por nós por meio de Jesus Cristo; a Ele seja o louvor, a honra, a glória, a soberania, o poder e a adoração, de hoje até o final de todos os tempos. Bendito seja DEUS."
