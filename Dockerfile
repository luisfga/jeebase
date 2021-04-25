#RESUMO do processo inteiro
#1. mvn clean install tomee:exec
#2. docker build -f Dockerfile -t luisfga-jeebase:prd .
#3. docker tag luisfga-jeebase:prd registry.heroku.com/luisfga-jeebase/web
#4. docker push registry.heroku.com/luisfga-jeebase/web
#5. heroku container:release web -a luisfga-jeebase

FROM openjdk:11

MAINTAINER luisfga@gmail.com

RUN mkdir /app

COPY target/jeebase-exec.jar /app/jeebase-exec.jar

ENTRYPOINT [ "java", "-DadditionalSystemProperties=-Dhttp.port=$PORT", "-jar", "/app/jeebase-exec.jar", "run"]
