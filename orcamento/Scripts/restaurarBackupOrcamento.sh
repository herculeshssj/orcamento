#!/bin/bash
echo "***** Script de restauração da base. Exclusivo do desenvolvedor do projeto *****"
echo ""
echo "Excluindo os arquivos existentes..."
echo ""
rm -rf ~/Downloads/orcamento-estrutura.sql.bz2
rm -rf ~/Downloads/orcamento-dados.sql.bz2
rm -rf ~/Downloads/orcamento-estrutura.sql
rm -rf ~/Downloads/orcamento-dados.sql
echo "OK"
echo ""
echo "Baixando o backup da base de produção..."
echo ""
scp hslife@hslife.com.br:/orcamento/db/orcamento-estrutura.sql.bz2 ~/Downloads
scp hslife@hslife.com.br:/orcamento/db/orcamento-dados.sql.bz2 ~/Downloads
echo "Download concluído!"
echo ""
echo "Parando o container do MariaDB..."
echo ""
docker stop mariadb-container
echo "Container parado!"
echo ""
echo "Iniciando o container do MariaDB..."
echo ""
docker start mariadb-container
echo "Container iniciado!"
echo ""
echo "Descompactando a base de produção..."
echo ""
cd ~/Downloads
bunzip2 orcamento-estrutura.sql.bz2
bunzip2 orcamento-dados.sql.bz2
echo "Arquivo descompactado"
echo ""
echo "Recriando as bases de dados..."
echo ""
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'drop database orcamento'
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'drop database orcamentotest'
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'create database orcamento'
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'create database orcamentotest'
echo "Bases recriadas!"
echo ""
echo "Restaurando as bases de dados..."
echo ""
cat orcamento-estrutura.sql | docker exec -i mariadb-container /usr/bin/mysql -u root --password=root orcamento
cat orcamento-dados.sql | docker exec -i mariadb-container /usr/bin/mysql -u root --password=root orcamento
cat orcamento-estrutura.sql | docker exec -i mariadb-container /usr/bin/mysql -u root --password=root orcamentotest
cat orcamento-dados.sql | docker exec -i mariadb-container /usr/bin/mysql -u root --password=root orcamentotest
echo "Bases restauradas!"
echo ""
echo "Executando rotinas pós-restauração..."
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update faturacartao set idArquivo = null' orcamento
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update lancamentoconta set idArquivo = null' orcamento
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update lancamentoperiodico set idArquivo = null' orcamento
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update dividaterceiro set idArquivoTermoDivida = null' orcamento
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update dividaterceiro set idArquivoTermoQuitacao = null' orcamento
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update pagamentodividaterceiro set idArquivoComprovante = null' orcamento
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update usuario set senha = sha2(login, 256)' orcamento
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update logs set sendToAdmin = true where sendToAdmin = false' orcamento
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update faturacartao set idArquivo = null' orcamentotest
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update lancamentoconta set idArquivo = null' orcamentotest
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update lancamentoperiodico set idArquivo = null' orcamentotest
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update dividaterceiro set idArquivoTermoDivida = null' orcamentotest
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update dividaterceiro set idArquivoTermoQuitacao = null' orcamentotest
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update pagamentodividaterceiro set idArquivoComprovante = null' orcamentotest
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update usuario set senha = sha2(login, 256)' orcamentotest
docker exec -i mariadb-container /usr/bin/mysql -u root --password=root -e 'update logs set sendToAdmin = true where sendToAdmin = false' orcamentotest
echo "Operação concluída!"