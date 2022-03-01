FROM registry.access.redhat.com/ubi8/ubi-minimal:8.5

ARG JAVA_PACKAGE=java-11-openjdk-headless

ARG JMETER_VERSION=apache-jmeter-5.4.1
ARG JMETER_PACKAGE_URL=https://archive.apache.org/dist/jmeter/binaries/${JMETER_VERSION}.tgz

ARG DRIVER_JAR=acmeair-jmeter-2.0.0-SNAPSHOT.jar
ARG DRIVER_GITHUB=https://github.com/blueperf/acmeair-driver/raw/main
ARG DRIVER_JAR_URL=${DRIVER_GITHUB}/${DRIVER_JAR}
ARG DRIVER_JMX_URL=${DRIVER_GITHUB}/acmeair-jmeter/scripts/{AcmeAir-microservices-mpJwt}.jmx
ARG DRIVER_CSV_URL=${DRIVER_GITHUB}/acmeair-jmeter/scripts/{Airports,Airports2,hosts}.csv

ARG JSON_JAR=json-simple-1.1.1.jar
ARG JSON_JAR_URL=https://repo1.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/${JSON_JAR}

WORKDIR /deployments

RUN microdnf install ${JAVA_PACKAGE} curl tar gzip rsync less \
    && microdnf update \
    && microdnf clean all \
    && chmod "g+rwX" /deployments \
    && curl -sS ${JMETER_PACKAGE_URL} | tar -xzf - \
    && curl -sS ${JSON_JAR_URL} -o ${JMETER_VERSION}/lib/ext/${JSON_JAR} \
    && curl -sSL ${DRIVER_JAR_URL} -o ${JMETER_VERSION}/lib/ext/${DRIVER_JAR} \
    && curl -sSL ${DRIVER_JMX_URL} -o scripts/#1.jmx --create-dirs \
    && curl -sSL ${DRIVER_CSV_URL} -o scripts/#1.csv --create-dirs \
    && chown -R 1001:root /deployments

COPY --chown=1001:root ./driver.sh ./

ENV JRE_HOME=/usr/lib/jvm/jre-11-openjdk
ENV JMETER_HOME=/deployments/${JMETER_VERSION}
ENV TZ=UTC LANG=C

ENV HOST=localhost
ENV PORT=80
ENV THREADS=1
ENV DURATION=60
ENV SUMMARY_INTERVAL=10
ENV LOG_RESPONSE=

USER 1001

ENTRYPOINT [ "./driver.sh" ]
