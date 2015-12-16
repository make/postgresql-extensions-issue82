# postgresql-extensions-issue82
Example for https://github.com/kaleidos/grails-postgresql-extensions/issues/82

Tested with
 * Ubuntu 14.04
 * Grails 3.0.10
 * Oracle JVM 1.8.0_66 (from ppa:webupd8team/java)
 * Tomcat 8.0.29

---

Steps for reproducing the problem (tested with Ubuntu 14.04 and jdk):

1. Create user and db
  ```SQL
CREATE USER issue82 PASSWORD 'issue82';
CREATE DATABASE issue82;
GRANT ALL PRIVILEGES ON DATABASE issue82 TO issue82;
```
2. Install tomcat
  ```bash
cd /opt
sudo wget http://www.nic.funet.fi/pub/mirrors/apache.org/tomcat/tomcat-8/v8.0.29/bin/apache-tomcat-8.0.29.tar.gz
sudo tar -xzf apache-tomcat-8.0.29.tar.gz
sudo ln -s apache-tomcat-8.0.29 tomcat

sudo adduser --system --no-create-home --group tomcat
sudo chown -R tomcat:tomcat /opt/apache-tomcat-*
echo '
# tomcat - Apache Tomcat 7  - Web Application Server
#
#
# upstart docs: http://upstart.ubuntu.com/getting-started.html
#               http://manpages.ubuntu.com/manpages/karmic/man5/init.5.html
#
# (note that embedding a script and pre-start and post-start actions are supported)
#

start on runlevel [2345]
stop on runlevel [!2345]

respawn
respawn limit 10 5

setuid tomcat
setgid tomcat

env JAVA_HOME=/usr/lib/jvm/java-8-oracle
env CATALINA_HOME=/opt/tomcat

pre-start script
    /opt/tomcat/bin/startup.sh
end script

post-stop script
    /opt/tomcat/bin/shutdown.sh 10 -force
end script
' | sudo tee /etc/init/tomcat.conf
sudo chmod 644 /etc/init/tomcat.conf
```
3. Deploy war
  ```bash
rm -r build
grails clean
grails war
APP=${PWD##*/}

sudo stop tomcat
sleep 10
sudo rm -r /opt/tomcat/webapps/${APP}*
sudo cp build/libs/*.war /opt/tomcat/webapps/${APP}.war
sudo start tomcat
```
4. See if the error occurs
  ```bash
less +F /opt/tomcat/logs/catalina.out
```
