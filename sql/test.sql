delete from t_page where id >999;

insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id) values(1000,1,100,1,'Index A','','',1,0,1,1);
insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id) values(1001,1,1000,1,'Page A.1','','',1,0,1,1);
insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id) values(1002,1,1000,2,'Page A.2','','',1,0,1,1);
insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id) values(1020,1,1002,1,'Page A.2.1','','',1,0,1,1);
insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id) values(1021,1,1002,2,'Page A.2.2','','',1,0,1,1);
insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id) values(1003,1,1000,3,'Page A.3','','',1,0,1,1);
insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id) values(1100,1,100,1,'Index B','','',1,0,1,1);
insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id) values(1101,1,1100,1,'Page B.1','','',1,0,1,1);
