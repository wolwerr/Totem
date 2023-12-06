FROM amazoncorretto:21

# Definindo o diretório de trabalho
WORKDIR /app

EXPOSE 8081

# Copiando o JAR construído na primeira etapa
COPY target/Pagamento-0.0.1-SNAPSHOT.jar app.jar

# Definindo o ponto de entrada para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]

