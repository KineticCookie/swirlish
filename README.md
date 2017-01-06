# Swirlish

[![Build Status](https://travis-ci.org/KineticCookie/swirlish.svg?branch=dev)](https://travis-ci.org/KineticCookie/swirlish)

Apache Spark jobs orchestrator for [Mist](https://github.com/Hydrospheredata/mist).

## API
Swirl provides REST api for submitting job graph and retrieving list of
available jobs.

Result of jobs evaluation will be published in Mqtt with `swirlish/${JOB_ID}`.

There is also a frontend avaiable, but it's currently in early integration stage.

## How to run
Swirl depends on Mist and MQTT broker.

1. Run Mosquitto (with WebSockets configured)

    ```bash
    docker run --name swirl-mosquitto -d -p 1883:1883 -p 9001:9001 toke/mosquitto
    ```

2. Run Mist and link it to Mosquitto

    ```bash
    docker run -d --link swirl-mosquitto:mosquitto -p 2003:2003 --name swirl-mist -v $PWD/jobs/target/scala-2.11/:/jobs -v $PWD/models/:/models -v $PWD/configs/:/usr/share/mist/configs -v $PWD/configs/twitter4j.properties:/usr/share/spark/conf/twitter4j.properties -t hydrosphere/mist:master-2.0.0 mist
    ```

3. Compile jobs

  ```bash
  cd jobs
  sbt package
  ```

4. Run streaming jobs

  To launch Twitter job you need to create `configs/twitter4j.properties` file with your Twitter OAuth data.

  1. Run Twitter Sentiment job
  
  ```bash
  docker exec -it swirl-mist bash -c "/usr/share/mist/bin/mist start job --config /usr/share/mist/configs/docker.conf --route twittersentiment"
  ```

  2. Run Swirl streaming job
  
  ```bash
  docker exec -it swirl-mist bash -c "/usr/share/mist/bin/mist start job --config /usr/share/mist/configs/docker.conf --route swirlishjob"
  ```

5. Run Swirl

  1. Build
  
    ```bash
    docker build -t swirl:latest .
    ```

  2. Run
  
    ```bash
    docker run -d --link swirl-mosquitto:mosquitto -p 8080:8080 --name swirl -v $PWD/configs/:/usr/share/swirl/configs swirl
    ```

  Now Swirl API is ready to go.
  It will listen broker for streaming data from Mist.
  And send it to the job graph.

6. Frontend
  Frontend is available in https://github.com/IceKhan13/swirlish_frontend
