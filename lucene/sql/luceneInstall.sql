insert into t_module (name,properties,head_include_file) values('lucene','lucene','/_jsp/_master/include/lucene_head.jsp');
insert into t_servlet (name,class_name,pattern,module_name) values ('luceneSearchServlet','de.bandika.lucene.SearchServlet','/_lucenesearch','lucene');
insert into t_backend_link (link_key,link,ranking,module_name) values('link|search','/_lucenesearch?method=openSearchAdministration',75,'lucene');
insert into t_backend_link_right(link_key,group_id) values('link|search',1);
insert into t_timer_task (name,class_name,interval_type,execution_minute,active,module_name) values ('searchIndexTask','de.bandika.lucene.SearchIndexTask',0,60,false,'lucene');
