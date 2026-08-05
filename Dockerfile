FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x mvnw gradlew || true
RUN if [ -f "pom.xml" ]; then ./mvnw clean package -DskipTests && mv target/*.jar app.jar; else ./gradlew build -x test && mv build/libs/*.jar app.jar; fi
ENTRYPOINT ["java", "-jar", "app.jar"]
