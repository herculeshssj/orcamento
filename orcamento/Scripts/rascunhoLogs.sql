select count(*) from logs where sendToAdmin = false and `level` = 'ERROR';

update logs set sendToAdmin = true where sendToAdmin = false and `level` = 'ERROR';

SELECT * FROM logs order by `date` desc;

select max(date) from logs;