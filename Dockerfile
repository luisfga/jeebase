#RESUMO do processo inteiro
#1. mvn clean package -Plate-setup-bootable-jar
#2. docker build -f Dockerfile -t luisfga-jeebase:prd .
#3. docker tag luisfga-jeebase:prd registry.heroku.com/luisfga-jeebase/web
#4. docker push registry.heroku.com/luisfga-jeebase/web
#5. heroku container:release web -a luisfga-jeebase

FROM openjdk:11

MAINTAINER luisfga@gmail.com

RUN mkdir /app

COPY target/jeebase-bootable.jar /app/jeebase-bootable.jar
COPY scripts/wildfly-setup.cli /app/scripts/wildfly-setup.cli

ENTRYPOINT "java -Xmx128M -Xms64M -Xss512k -XX:MaxRAM=128M -jar /app/jeebase-bootable.jar --cli-script=/app/scripts/wildfly-setup.cli -b=0.0.0.0 -Djboss.http.port=$PORT"