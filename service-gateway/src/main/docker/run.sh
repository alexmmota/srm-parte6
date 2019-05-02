#!/bin/sh

echo "********************************************************"
echo "Waiting for the configuration server to start on port 8080"
echo "********************************************************"
while ! `nc -z config-server 8080 `; do sleep 3; done
echo ">>>>>>>>>>>> Configuration Server has started"

echo "********************************************************"
echo "Waiting for the service discovery to start on port 8080"
echo "********************************************************"
while ! `nc -z service-discovery 8080 `; do sleep 3; done
echo ">>>>>>>>>>>> Service Discovery has started"

java -Dserver.port=$SERVER_PORT                                 \
     -Dspring.cloud.config.uri=$CONFIGSERVER_URI                \
     -Dspring.profiles.active=$PROFILE                          \
     -Dfile.encoding=UTF-8       \
     -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n \
     -jar /usr/local/servicegateway/@project.build.finalName@.jar

