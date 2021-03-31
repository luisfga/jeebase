#RESUMO do processo inteiro
#1. mvn clean install tomee:exec
#2. docker build -f Dockerfile -t luisfga-jeebase:prd . --build-arg APP_MAIL_SESSION_HOST --build-arg APP_MAIL_SESSION_PORT --build-arg APP_MAIL_SESSION_USERNAME --build-arg APP_MAIL_SESSION_PASSWORD
#3. docker tag luisfga-jeebase:prd registry.heroku.com/luisfga-jeebase/web
#4. docker push registry.heroku.com/luisfga-jeebase/web
#5. heroku container:release web -a luisfga-jeebase

FROM openjdk:11

MAINTAINER luisfga@gmail.com

RUN mkdir /app

COPY target/jeebase-exec.jar /app/jeebase-exec.jar

# O sistema precisa das seguintes variáveis de ambiente: 
# APP_MAIL_SESSION_HOST 
# APP_MAIL_SESSION_PORT
# APP_MAIL_SESSION_USERNAME
# APP_MAIL_SESSION_PASSWORD

# Elas devem estar no sistema que buildar o container e devem ser passadas ao container adicionando flags '--build-arg' ao comando 'build':
# --build-arg APP_MAIL_SESSION_HOST --build-arg APP_MAIL_SESSION_PORT --build-arg APP_MAIL_SESSION_USERNAME --build-arg APP_MAIL_SESSION_PASSWORD

# Exemplo de como poderá ser o comando 'build':
# -> docker build -f Dockerfile.dev -t luisfga-tomee-demo:dev . --build-arg APP_MAIL_SESSION_HOST --build-arg APP_MAIL_SESSION_PORT --build-arg APP_MAIL_SESSION_USERNAME --build-arg APP_MAIL_SESSION_PASSWORD


# OBS: essas variáveis podem ficar vazias e não serem informadas apenas se o sistema onde for feito o deploy ou release já tiver essas variáveis, por exemplo no Heroku, se elas estiverem configuradas como configVars

# Aqui os argumentos passados no comando 'build' com a flag '--build-arg' são consumidos (armazenados) durante a build
ARG APP_MAIL_SESSION_HOST
ARG APP_MAIL_SESSION_PORT
ARG APP_MAIL_SESSION_USERNAME
ARG APP_MAIL_SESSION_PASSWORD

# Agora, as variáveis consumidas nas linhas anteriores com o comando 'ARG' são colocadas no container mesmo
ENV APP_MAIL_SESSION_HOST=$APP_MAIL_SESSION_HOST
ENV APP_MAIL_SESSION_PORT=$APP_MAIL_SESSION_PORT
ENV APP_MAIL_SESSION_USERNAME=$APP_MAIL_SESSION_USERNAME
ENV APP_MAIL_SESSION_PASSWORD=$APP_MAIL_SESSION_PASSWORD

#Variáveis necessárias no servidor de produção (deixadas aqui pra fazer um último teste antes do deploy)
#os valores estão no heroku, nas configVars (tb no "postgres-addon", em "settings")
#ENV DB_HOST=???
#ENV DATABASE=???
#ENV DB_USER=???
#ENV DB_PASSWORD=???

#Heroku usa uma porta aleatória e a 'exporta' como PORT.
EXPOSE 8080

ENTRYPOINT [ "java", "-DadditionalSystemProperties=-Dhttp.port=$PORT", "-jar", "/app/jeebase-exec.jar", "run"]