# Use a base image with Java (ajuste a versão conforme necessário)
FROM amazoncorretto:21-alpine-jdk

# Copia o arquivo JAR do seu serviço para o contêiner
COPY ./target/Totem.jar /usr/app/

# Define o diretório de trabalho
WORKDIR /usr/app

# Comando para rodar o aplicativo
CMD ["java", "-jar", "Totem-0.0.1-SNAPSHOT.jar"]