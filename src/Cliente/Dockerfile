FROM amazoncorretto:21

# Definindo o diretório de trabalho
WORKDIR /app

EXPOSE 8082

# Copiando o arquivo JAR para o contêiner
COPY target/Cliente-0.0.1-SNAPSHOT.jar app.jar

# Definindo o comando para executar o aplicativo
ENTRYPOINT ["java", "-jar", "app.jar"]

