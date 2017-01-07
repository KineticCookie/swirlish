FROM alanszp/alpine-scala-sbt

ENV SWIRL_HOME=/usr/share/swirl
ENV SWIRL_JAR=target/scala-2.11/swirlish-assembly-1.0.jar

COPY . ${SWIRL_HOME}

WORKDIR ${SWIRL_HOME}

RUN sbt assembly
RUN cp ${SWIRL_JAR} swirl.jar

RUN chmod +x docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["./docker-entrypoint.sh"]
