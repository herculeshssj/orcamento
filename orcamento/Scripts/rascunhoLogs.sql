select count(*) from logs where sendToAdmin = false and `level` = 'ERROR';

update logs set sendToAdmin = true where sendToAdmin = false and `level` = 'ERROR';