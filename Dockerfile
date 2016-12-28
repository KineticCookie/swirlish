FROM alanszp/alpine-scala-sbt

ENV SWIRL_HOME=/usr/share/swirl

COPY . ${SWIRL_HOME}

WORKDIR ${SWIRL_HOME}

RUN chmod +x docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["./docker-entrypoint.sh"]
