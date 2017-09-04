/*** Executar após restaurar a base ***/

update usuario set senha = sha2(login, 256);