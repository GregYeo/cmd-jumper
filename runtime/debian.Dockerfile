FROM index.docker.io/library/debian:13-slim

## =====================
## Build env setup =====
## =====================

RUN apt update -y && \
    apt install -y curl

ARG JDK_VERSION=21.0.2
ARG ARCHITECTURE=aarch64
ARG JAVA_HOME=/usr/local/graalvm

RUN curl https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-${JDK_VERSION}/graalvm-community-jdk-${JDK_VERSION}_linux-${ARCHITECTURE}_bin.tar.gz \
        -L \
        -o /tmp/graalvm-jdk.tar.gz && \
    mkdir -p /tmp/graalvm/ && \
    tar -zxvf /tmp/graalvm-jdk.tar.gz -C /tmp/graalvm/ && \
    rm -rf /tmp/graalvm-jdk.tar.gz && \
    \
    cd /tmp/graalvm/ && \
    ls -lh /tmp/graalvm/ && \
    export GRAALVM_DIR=$(find /tmp/graalvm -maxdepth 1 -type d -name "graalvm-community-*" -print -quit) && \
    echo "GRAALVM_DIR: $GRAALVM_DIR" && \
    mkdir -p $JAVA_HOME && \
    mv $GRAALVM_DIR/* $JAVA_HOME && \
    rm -rf $GRAALVM_DIR

ENV JAVA_HOME=/usr/local/graalvm
ENV GRAALVM_HOME=/usr/local/graalvm
ENV PATH=$JAVA_HOME/bin:$PATH

RUN apt install -y build-essential
RUN apt install -y zlib1g-dev


## =====================
## Build source setup ==
## =====================

RUN mkdir -p /builder

WORKDIR /builder

COPY build.gradle.kts \
     gradle.properties \
     gradlew \
     settings.gradle.kts \
     /builder
COPY gradle /builder/gradle
RUN ./gradlew dependencies

COPY src /builder/src

## =====================
## Build ===============
## =====================

RUN ./gradlew nativeCompile && \
    ls -lh /builder/build/native/nativeCompile/
