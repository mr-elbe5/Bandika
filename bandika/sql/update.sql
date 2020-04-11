drop table t_editor_page;

-- salt is V3xfgDrxdl8=
-- root user
update t_user set pwd='A0y3+ZmqpMhWA21VFQMkyY6v74Y=' where id=1;

-- root page
SELECT ADDSECTIONPAGE(1, null, 'home', 'Home', 'Startseite', 1, 'defaultSectionPage');
UPDATE t_page set publish_date=now() where id =1;





--local ok
