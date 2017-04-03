update t_jsp set path='/_jsp/user/login.jsp',module_name='base' where name='login';
delete from t_jsp where module_name='registration';
delete from t_module where name='registration';

