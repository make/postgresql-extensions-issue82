# postgresql-extensions-issue82
Example for https://github.com/kaleidos/grails-postgresql-extensions/issues/82

Tested with
 * Ubuntu 14.04
 * Grails 3.0.10
 * Oracle JVM 1.8.0_66 (from ppa:webupd8team/java)
 * Tomcat 8.0.29
 * psql (PostgreSQL) 9.4.5

---

Steps for reproducing the problem:

1. Create user and db into postgresql
  ```sql

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

Output should seem something like:
```
16-Dec-2015 20:10:03.955 INFO [main] org.apache.coyote.AbstractProtocol.init Initializing ProtocolHandler ["http-nio-8080"]
16-Dec-2015 20:10:03.964 INFO [main] org.apache.tomcat.util.net.NioSelectorPool.getSharedSelector Using a shared selector for servlet write/read
16-Dec-2015 20:10:03.965 INFO [main] org.apache.coyote.AbstractProtocol.init Initializing ProtocolHandler ["ajp-nio-8009"]
16-Dec-2015 20:10:03.966 INFO [main] org.apache.tomcat.util.net.NioSelectorPool.getSharedSelector Using a shared selector for servlet write/read
16-Dec-2015 20:10:03.967 INFO [main] org.apache.catalina.startup.Catalina.load Initialization processed in 311 ms
16-Dec-2015 20:10:03.979 INFO [main] org.apache.catalina.core.StandardService.startInternal Starting service Catalina
16-Dec-2015 20:10:03.979 INFO [main] org.apache.catalina.core.StandardEngine.startInternal Starting Servlet Engine: Apache Tomcat/8.0.29
16-Dec-2015 20:10:03.995 INFO [localhost-startStop-1] org.apache.catalina.startup.HostConfig.deployWAR Deploying web application archive /opt/apache-tomcat-8.0.29/webapps/postgresql-extensions-issue82.war
16-Dec-2015 20:10:06.440 INFO [localhost-startStop-1] org.apache.jasper.servlet.TldScanner.scanJars At least one JAR was scanned for TLDs yet contained no TLDs. Enable debug logging for this logger for a c
omplete list of JARs that were scanned but no TLDs were found in them. Skipping unneeded JARs during scanning can improve startup time and JSP compilation time.
2015-12-16 20:10:14,276 [localhost-startStop-1] INFO grails.app.init.BootStrap - Checking job statuses
2015-12-16 20:10:14,314 [localhost-startStop-1] ERROR grails.boot.GrailsApp - Application startup failed
java.lang.IllegalStateException: Method on class [postgresql.extensions.issue82.JobStatus] was used outside of a Grails application. If running in the context of a test using the mocking API or bootstrap G
rails correctly.
        at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method) ~[na:1.8.0_66]
        at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62) ~[na:1.8.0_66]
        at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45) ~[na:1.8.0_66]
        at java.lang.reflect.Constructor.newInstance(Constructor.java:422) ~[na:1.8.0_66]
        at org.codehaus.groovy.reflection.CachedConstructor.invoke(CachedConstructor.java:80) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.reflection.CachedConstructor.doConstructorInvoke(CachedConstructor.java:74) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.ConstructorSite$ConstructorSiteNoUnwrap.callConstructor(ConstructorSite.java:84) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallConstructor(CallSiteArray.java:60) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:235) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callConstructor(AbstractCallSite.java:247) ~[groovy-2.4.5.jar:2.4.5]
        at org.grails.datastore.gorm.GormEntity$Trait$Helper.currentGormStaticApi(GormEntity.groovy:69) ~[grails-datastore-gorm-4.0.7.RELEASE.jar:na]
        at org.grails.datastore.gorm.GormEntity$Trait$Helper$currentGormStaticApi$0.call(Unknown Source) ~[na:na]
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:48) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:113) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:125) ~[groovy-2.4.5.jar:2.4.5]
        at postgresql.extensions.issue82.JobStatus.currentGormStaticApi(JobStatus.groovy) ~[classes/:na]
        at postgresql.extensions.issue82.JobStatus$currentGormStaticApi$0.call(Unknown Source) ~[na:na]
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:48) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:113) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:117) ~[groovy-2.4.5.jar:2.4.5]
        at org.grails.datastore.gorm.GormEntity$Trait$Helper.withNewSession(GormEntity.groovy:810) ~[grails-datastore-gorm-4.0.7.RELEASE.jar:na]
        at org.grails.datastore.gorm.GormEntity$Trait$Helper$withNewSession.call(Unknown Source) ~[na:na]
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:48) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:113) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:133) ~[groovy-2.4.5.jar:2.4.5]
        at postgresql.extensions.issue82.JobStatus.withNewSession(JobStatus.groovy) ~[classes/:na]
        at postgresql.extensions.issue82.JobStatus$withNewSession.call(Unknown Source) ~[na:na]
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:48) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:113) ~[groovy-2.4.5.jar:2.4.5]
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:125) ~[groovy-2.4.5.jar:2.4.5]
        at BootStrap$_closure1.doCall(BootStrap.groovy:7) ~[classes/:na]
...
```
