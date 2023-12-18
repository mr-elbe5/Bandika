# Bandika
A Java CMS and framework

#### Current version: 14.1
This is a sample project of the most important Bandika modules
#### Module dependency:
- BandikaBase 1.1,
- BandikaCms 1.1
## Installation
The following refers to a Linux installation, but it is very  similar on MacOS and Windows.
#### Install Java JDK with version 16 or higher:
~~~
me@myhost# sudo apt install openjdk-18-jdk  
~~~
testen mit:  
~~~
me@myhost# java --version
~~~
#### Install and configure Apache (optional):
~~~
$myhost>sudo apt install apache2  
me@myhost# sudo a2enmod proxy  
me@myhost# sudo a2enmod proxy_ajp  
~~~
Create a virtual host with a proxy line like 'ProxyPass "/" "ajp://localhost:8009/"'
#### Install PostgreSQL and create the database:
~~~
me@myhost# apt install postgresql  
me@myhost# sudo -u postgres -i  
postgres@myhost# psql  
postgres=# create user bandika with password 'mynextpassword';  
postgres=# \q  
postgres=# createdb -O bandika bandika;  
postgres@myhost# exit  
me@myhost#  
~~~
#### Initialize the database
First load the init.sql  
~~~
me@myhost# cd /tmp  
me@myhost#  curl https://git.elbe5cloud.de/miro/Bandika/raw/branch/master/sql/init.sql > ./init.sql > ./init.sql  
me@myhost# sudo -u postgres -i  
postgres@myhost# psql -U bandika -d bandika -f ./init.sql  
postgres@myhost# exit
~~~
#### Install and complete Tomcat
~~~
me@myhost# sudo adduser tomcat  
me@myhost# sudo su tomcat  
tomcat@myhost# cd  
tomcat@myhost# curl https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.17/bin/apache-tomcat-10.1.17.tar.gz > ./tomcat10.tar.gz  
tomcat@myhost# tar -xzvf ~/tomcat10.tar.gz  
tomcat@myhost# mv apache-tomcat-10.1.17 tomcat10  
tomcat@myhost# cd ~/tomcat10/lib  
tomcat@myhost# curl https://git.elbe5cloud.de/miro/BandikaBase/src/branch/main/lib/postgresql-42.2.18.jar > postgresql-42.2.18.jar 
~~~
#### Configure Tomcat
~~~
tomcat@myhost# cd ~/tomcat10/conf  
~~~
Open web.xml and in the jsp section add  
~~~
<init-param>  
<param-name>compilerSourceVM</param-name>  
<param-value>16</param-value>  
</init-param>  
<init-param>  
<param-name>compilerTargetVM</param-name>  
<param-value>16</param-value>  
</init-param>  
~~~
Open server.xml and make sure that the Connector section for HTTP/1.1 and AJP/1.3 are active (not commented).  

Add the database configuration:  
~~~
tomcat@myhost# cd ~/tomcat10/conf/Catalina
tomcat@myhost# mkdir localhost
tomcat@myhost# cd localhost
tomcat@myhost# touch ./context.xml.default
~~~
Open this file and add the following:
~~~
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <Resource
				name="jdbc/bandika"
				auth="Container"
				type="javax.sql.DataSource"
				driverClassName="org.postgresql.Driver"
				url="jdbc:postgresql://localhost:5432/bandika"
				username="bandika"
				password="yourPassword"
				maxActive="20"
				maxIdle="10"
				maxWait="-1"
				validationQuery="select 1"
				testOnBorrow="true"
			/>

</Context>
~~~

#### Install and start the app

Clear webapps folder and add the Bandika ROOT.war:  
~~~
tomcat@myhost# cd ~/tomcat10/webapps  
tomcat@myhost# rm -R ./*.*  
tomcat@myhost# curl https://git.elbe5cloud.de/miro/Bandika/src/branch/master/release/ROOT.war > ./ROOT.war  
~~~
Start Tomcat:  
~~~
tomcat@myhost# cd ~/tomcat10/bin
tomcat@myhost# ./startup.sh  
~~~
You should be able to open (the almost empty) home page on http://localhost:8080 or through your Apache's virtual host.  

Stop Tomcat:  
~~~
tomcat@myhost# ./shutdown.sh  
~~~

