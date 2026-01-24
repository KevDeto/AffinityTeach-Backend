# 1. FASE DE CONSTRUCCIÓN - Crear el JAR de la aplicación
# =========================================================

# Define la imagen base para la fase de construcción
# maven:3.9.11-eclipse-temurin-21 = Maven 3.9.11 + Java 21 Eclipse Temurin
# AS build = Le da nombre a esta etapa para referenciarla después
FROM maven:3.9.11-eclipse-temurin-21 AS build

# Establece el directorio de trabajo dentro del contenedor
# Todas las instrucciones siguientes se ejecutarán en /app
WORKDIR /app

# Copia el archivo pom.xml desde tu máquina local al contenedor
# El primer punto (.) es el directorio actual en tu máquina (contexto de Docker)
# El segundo punto (./) es /app dentro del contenedor
COPY pom.xml ./

# Descarga todas las dependencias de Maven
# - Esto se hace ANTES de copiar el código fuente para aprovechar la cache de Docker
# - Si el pom.xml no cambia, Docker reutiliza esta capa cacheada
RUN mvn dependency:go-offline

# Copia el código fuente (carpeta src/) al contenedor
# Esto se hace después de las dependencias porque el código cambia más frecuentemente
COPY src ./src

# Compila el proyecto y crea el JAR ejecutable
# - clean: elimina compilaciones anteriores
# - package: crea el JAR
# - DskipTests: omite las pruebas para acelerar la construcción (en producción, ¡ejecuta tests!)
RUN mvn clean package -DskipTests


# 2. FASE DE EJECUCIÓN - Imagen final para producción
# ====================================================

# Nueva imagen base, más pequeña (solo JRE, sin Maven ni herramientas de desarrollo)
# eclipse-temurin:21-jre = Java 21 Runtime Environment (más ligero que JDK)
FROM eclipse-temurin:21-jre

# Directorio de trabajo para la aplicación ejecutándose
WORKDIR /app

# MEJORA DE SEGURIDAD: Crear usuario no-root
# - groupadd -r spring: crea grupo 'spring' con ID de sistema (-r)
# - useradd -r -g spring spring: crea usuario 'spring' en grupo 'spring'
RUN groupadd -r spring && useradd -r -g spring spring

# Cambia al usuario no-root (principio de privilegio mínimo)
# La aplicación se ejecutará como usuario 'spring', no como root
USER spring:spring

# Copia SOLO el JAR desde la fase de construcción
# --from=build: toma el archivo de la etapa llamada 'build'
# /app/target/affinityteach-backend-*.jar: ruta en la etapa de construcción
# affinityteach-backend.jar: nombre final en la imagen de ejecución
COPY --from=build /app/target/affinityteach-backend-*.jar affinityteach-backend.jar

# Informa a Docker que el contenedor escuchará en el puerto 8080
# Esto es documentación, NO abre el puerto (eso se hace con docker run -p)
EXPOSE 8080

# Comando que se ejecutará al iniciar el contenedor
# ["java", "-jar", "/app/affinityteach-backend.jar"] = ejecuta el JAR con Java
ENTRYPOINT ["java", "-jar", "/app/affinityteach-backend.jar"]


# Comentado excesivamente para el aprendisaje.
# Yyy para no olvidarme que ****** hice aca jaja.