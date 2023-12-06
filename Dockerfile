# Primeira etapa: Construir o aplicativo
FROM maven:3.9.5-amazoncorretto-21 AS build

WORKDIR /workspace

# Restaurando as linhas para copiar o pom.xml e baixar as dependências
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiando o código fonte e construindo o JAR
COPY src src
RUN mvn clean package

# Segunda etapa: Rodar a aplicação
FROM amazoncorretto:21-alpine-jdk

# Definindo metadados da imagem
LABEL maintainer="ricardo@ricardo.net"
LABEL version="1.0"
LABEL description="Totem - Pagamento"
LABEL name="Pagamento"

# Expondo a porta em que a aplicação vai rodar
EXPOSE 8081

# Definindo variáveis de ambiente para configuração do banco de dados
# Os valores reais devem ser fornecidos durante a execução do contêiner
ENV DATABASE_HOST localhost
ENV DATABASE_PORT 3307
ENV DATABASE_NAME mydatabase
ENV DATABASE_USER root
ENV DATABASE_PASSWORD my-secret-pw

# Copiando o JAR construído na primeira etapa
COPY --from=build /workspace/target/Pagamento-0.0.1-SNAPSHOT.jar app.jar

# Definindo o ponto de entrada para executar a aplicação
ENTRYPOINT ["java", "-jar", "/app.jar"]
