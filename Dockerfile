# VARIÁVEIS DE AMBIENTE

# P/ Java Mail Session: MAIL_HOST, MAIL_PORT, MAIL_USER, MAIL_PASS

# P/ Banco de dados(DESENV): DB_HOST, DB_PORT, DB_USER, DB_PASS, JEEBASE_DATABASE
# * esses nomes é que são usados na configuração interna do Servidor de Aplicações 
# ** são usados tanto no ambiente de desenvolvimento quanto no container em produção

# P/ Banco de dados(PRODUÇÃO): JEEBASE_PRD_DB_HOST, JEEBASE_PRD_DB_PORT, JEEBASE_PRD_DB_USER, JEEBASE_PRD_DB_PASS, JEEBASE_PRD_DATABASE
# * essas variáveis devem estar no ambiente que gerar o container de produção, para não colocar os valores no repositório de códigos-fonte
# ** são passadas no build do container e armazenadas nas variáveis usadas internamente

# EXEMPLO resumido de build e deploy do container
#1. mvn clean package liberty:package -Dinclude=runnable
#TEST: java -jar target/jeebase.jar --default.http.port=9876 --default.https.port=9877
#2 docker build -m 400M -f Dockerfile -t luisfga-jeebase:prd . --build-arg MAIL_HOST --build-arg MAIL_PORT --build-arg MAIL_USER --build-arg MAIL_PASS --build-arg JEEBASE_PRD_DB_HOST --build-arg JEEBASE_PRD_DB_PORT --build-arg JEEBASE_PRD_DB_USER --build-arg JEEBASE_PRD_DB_PASS --build-arg JEEBASE_PRD_DATABASE
#3. docker tag luisfga-jeebase:prd registry.heroku.com/luisfga-jeebase/web
#4. docker push registry.heroku.com/luisfga-jeebase/web
#5. heroku container:release web -a luisfga-jeebase

FROM openjdk:11

MAINTAINER luisfga@gmail.com

RUN mkdir /app

COPY target/jeebase.jar /app/jeebase.jar

# argumentos passados no comando 'build' com a flag '--build-arg' são consumidos (armazenados) durante a build
ARG MAIL_HOST
ARG MAIL_PORT
ARG MAIL_USER
ARG MAIL_PASS
ARG JEEBASE_PRD_DB_HOST
ARG JEEBASE_PRD_DB_PORT
ARG JEEBASE_PRD_DB_USER
ARG JEEBASE_PRD_DB_PASS
ARG JEEBASE_PRD_DATABASE

# Agora, as variáveis consumidas nas linhas anteriores com o comando 'ARG' são colocadas no container mesmo
ENV MAIL_HOST=$MAIL_HOST
ENV MAIL_PORT=$MAIL_PORT
ENV MAIL_USER=$MAIL_USER
ENV MAIL_PASS=$MAIL_PASS
ENV DB_HOST=$JEEBASE_PRD_DB_HOST
ENV DB_PORT=$JEEBASE_PRD_DB_PORT
ENV DB_USER=$JEEBASE_PRD_DB_USER
ENV DB_PASS=$JEEBASE_PRD_DB_PASS
ENV JEEBASE_DATABASE=$JEEBASE_PRD_DATABASE

CMD java -Xms64M -Xmx128M -Xss4M -XX:MaxRAM=128M -jar /app/jeebase.jar --default.http.port=$PORT --default.https.port=8443