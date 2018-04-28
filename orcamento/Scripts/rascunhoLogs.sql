select count(*) from logs where sendToAdmin = false and `level` = 'ERROR';

update logs set sendToAdmin = true where sendToAdmin = false and `level` = 'ERROR';

SELECT * FROM logs order by `date` desc;

select max(date) from logs;
select * from logs order by date desc limit 10;

select * from logs order by date desc;
select * from logrequisicao order by dataHora desc;