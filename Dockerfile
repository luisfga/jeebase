#RESUMO do processo inteiro
#1. mvn clean package -Plate-setup-bootable-jar
#2. docker build -f Dockerfile -t luisfga-jee7base:prd .
#3. docker tag luisfga-jee7base:prd registry.heroku.com/luisfga-jee7base/web
#4. docker push registry.heroku.com/luisfga-jee7base/web
#5. heroku container:release web -a luisfga-jee7base

FROM openjdk:11

MAINTAINER luisfga@gmail.com

RUN mkdir /app

COPY target/jee7base-bootable.jar /app/jee7base-bootable.jar
COPY scripts/wildfly-setup.cli /app/scripts/wildfly-setup.cli

ENTRYPOINT "java -Xmx128M -Xms64M -Xss512k -XX:MaxRAM=128M -jar /app/jee7base-bootable.jar --cli-script=/app/scripts/wildfly-setup.cli -b=0.0.0.0 -Djboss.http.port=$PORT"