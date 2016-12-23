# Swirlish

[![Build Status](https://travis-ci.org/KineticCookie/swirlish.svg?branch=graph-execution)](https://travis-ci.org/KineticCookie/swirlish)

Apache Spark jobs orchestrator for [Mist](https://github.com/Hydrospheredata/mist).

## How to run
First, you need to compile Spark jobs:

```bash
$ cd jobs
$ sbt package
```

Configuration for jobs is already defined, but if you change job files, you need to recompile them and check configurations.

Swirl depends on Mist and MQTT broker. The easiest way to launch is to use Docker Compose.

```bash
$ docker-compose up
```