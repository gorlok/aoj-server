FROM adoptopenjdk/openjdk11:alpine
RUN mkdir /app
COPY . /app
WORKDIR /app
EXPOSE 7666
EXPOSE 9999
CMD ["java", "-jar", "target/server-0.12.3.jar"]
