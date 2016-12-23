FROM frolvlad/alpine-scala:2.11

ENV SWIRL_HOME=/usr/share/swirl

ENV SBT_VERSION 0.13.8
ENV SBT_HOME /usr/local/sbt
ENV PATH ${PATH}:${SBT_HOME}/bin

# Install sbt
RUN curl -sL "http://dl.bintray.com/sbt/native-packages/sbt/$SBT_VERSION/sbt-$SBT_VERSION.tgz" | gunzip | tar -x -C /usr/local && \
    echo -ne "- with sbt $SBT_VERSION\n" >> /root/.built

COPY . ${SWIRL_HOME}

RUN cd ${SWIRL_HOME} && \
    sbt assembly </dev/null && \
    chmod +x /docker-entrypoint.sh # Make jar executable

ENTRYPOINT "${SWIRL_HOME}/target/scala-2.11" # run jar