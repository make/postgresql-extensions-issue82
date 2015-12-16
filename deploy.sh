rm -r build
grails clean
grails war
APP=${PWD##*/}

sudo stop tomcat
sleep 10
sudo rm -r /opt/tomcat/webapps/${APP}*
sudo cp build/libs/*.war /opt/tomcat/webapps/${APP}.war
sudo start tomcat

