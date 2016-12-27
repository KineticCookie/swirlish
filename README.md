# Swirlish

[![Build Status](https://travis-ci.org/KineticCookie/swirlish.svg?branch=dev)](https://travis-ci.org/KineticCookie/swirlish)

Apache Spark jobs orchestrator for [Mist](https://github.com/Hydrospheredata/mist).

## API
Swirl provides REST api for submitting job graph and retrieving list of
available jobs.

Result of jobs evaluation will be published in Mqtt with `swirlish/${JOB_ID}`.

There is also a frontend avaiable, but it's currently in early integration stage.

## How to run
Swirl depends on Mist and MQTT broker. The easiest way to launch is to use Docker Compose.

```bash
$ docker-compose up
```
But there is some issues with it...

So the true way is to manually launch MQTT broker, Mist and Swirl and link them.

1. Run Mosquitto (with WebSockets configured)
```bash
$ docker run --name swirl-mosquitto -d -p 1883:1883 -p 9001:9001 toke/mosquitto
```

2. Run Mist and link it to Mosquitto
```bash
$ docker run -d --link swirl-mosquitto:mosquitto -p 2003:2003 --name swirl-mist -v  $PWD/jobs/target/scala-2.11/:/jobs -v $PWD/configs/:/usr/share/mist/configs -v $PWD/configs/twitter4j.properties:/usr/share/spark/conf/twitter4j.properties -t hydrosphere/mist:master-2.0.0 mist
```

3. Run Swirl
  1. Build
    ```bash
    $ docker build -t swirl:latest .
    ```
  2. Run
  ```bash
  $ docker run -d --link swirl-mosquitto:mosquitto -p 8080:8080 --name swirl -v $PWD/configs/:/usr/share/swirl/configs swirl
  ```

  Now Swirl API is ready to go.
  It will listen broker for streaming data from Mist.
  And send it to the job graph.

## Example
First, you need to compile Spark jobs for mist:

```bash
$ cd jobs
$ sbt package
```

Configuration for jobs is already defined, but if you change job files,
you need to recompile them and check configurations.

Then follow **How to run** section.
