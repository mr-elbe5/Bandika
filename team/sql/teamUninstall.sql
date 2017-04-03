delete from t_template where module_name='team';
delete from t_servlet where module_name='team';
drop table if exists t_teamblogentry;
drop table if exists t_teamfile_current;
drop table if exists t_teamfile_version;
drop table if exists t_teamfile;
delete from t_module where name='team';
