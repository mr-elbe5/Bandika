insert into t_module (name,dependencies) values('btfrontend','cms');

insert into t_template (name,type_name,module_name) values('BTLayoutMaster','master','btfrontend');

insert into t_template (name,type_name,class_name,module_name) values('BTHomeLayout','layout','de.bandika.page.PageData','btfrontend');
insert into t_template (name,type_name,class_name,module_name) values('BTIndexLayout','layout','de.bandika.page.PageData','btfrontend');
insert into t_template (name,type_name,class_name,module_name) values('BTArticleLayout','layout','de.bandika.page.PageData','btfrontend');

insert into t_template (name,type_name,match_types,class_name,module_name) values('H1','part','stage,content','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('H2','part','content,teaser','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('H3','part','content,teaser,related','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('Html-Full','part','stage,content','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('Html-Small','part','teaser,related','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('Image','part','','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('Legend','part','content','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('Link','part','','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('TextArea','part','content,related','de.bandika.cms.CmsPartData','btfrontend');
insert into t_template (name,type_name,match_types,class_name,module_name) values('TextLine','part','','de.bandika.cms.CmsPartData','btfrontend');

