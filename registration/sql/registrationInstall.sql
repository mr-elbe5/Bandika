insert into t_module (name,properties) values('registration','registration');
insert into t_servlet (name,class_name,pattern,module_name) values ('registrationServlet','de.bandika.registration.RegistrationServlet','/_registration','registration');
update t_jsp set path='/_jsp/registration/login.jsp',module_name='registration' where name='login';
insert into t_jsp (name,path,module_name) values('registerUser','/_jsp/registration/registerUser.jsp','registration');
insert into t_jsp (name,path,module_name) values('userRegistered','/_jsp/registration/userRegistered.jsp','registration');
insert into t_jsp (name,path,module_name) values('approveRegistration','/_jsp/registration/approveRegistration.jsp','registration');
insert into t_jsp (name,path,module_name) values('registrationApproved','/_jsp/registration/registrationApproved.jsp','registration');
        