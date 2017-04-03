insert into t_module (name,properties) values('reusable','reusable');
insert into t_servlet (name,class_name,pattern,module_name) values ('reusablePartServlet','de.bandika.reusable.ReusablePartServlet','/_reusable','reusable');
insert into t_template_type (name,template_path,template_level,module_name) values('wrapperLayout','/_jsp/_wrapperlayout/',2,'reusable');
insert into t_template (name,description,type_name,match_types,class_name,module_name) values('defaultWrapper','Default Wrapper','wrapperLayout','','de.bandika.reusable.ReusablePartContainer','reusable');
insert into t_template (name,description,type_name,match_types,class_name,module_name) values('viewerPart','Part for viewing other parts','part','','de.bandika.reusable.ReusableViewerPartData','reusable');
insert into t_backend_link (link_key,link,ranking,module_name) values('link|reusableParts','/_reusable?method=openEditReusableParts',75,'reusable');
insert into t_backend_link_right(link_key,group_id) values('link|reusableParts',3);
insert into t_backend_link_right(link_key,group_id) values('link|reusableParts',4);
