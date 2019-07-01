#!/bin/sh

echo "********************************************************"
echo "Waiting for the database server to start on port 3306"
echo "********************************************************"
while ! `nc -z database 3306`; do sleep 3; done
echo ">>>>>>>>>>>> Database Server has started"

echo "********************************************************"
echo "Waiting for the configuration server to start on port 8080"
echo "********************************************************"
while ! `nc -z config-server 8080 `; do sleep 3; done
echo ">>>>>>>>>>>> Configuration Server has started"

java -Dserver.port=$SERVER_PORT                                 \
     -Dspring.cloud.config.uri=$CONFIGSERVER_URI                \
     -Dspring.profiles.active=$PROFILE                          \
     -Dfile.encoding=UTF-8       \
     -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n \
     -jar /usr/local/estoque/@project.build.finalName@.jar