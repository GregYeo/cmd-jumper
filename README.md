# **CMD-Jumper: Remote Command Execution Gateway**

**CMD-Jumper** is a lightweight REST server designed to execute arbitrary shell commands on its host container and return the detailed results—including exit code, standard output, and standard error—as a structured JSON response.  
It acts as a secure RPC (Remote Procedure Call) endpoint, enabling external services to remotely manage or query the state of a running container without SSH access.

* Single binary file size is 90MB
  * Started within under second.
     ```shell
    root@b74d56e308b8:/# export SERVER_PORT=7777
    root@b74d56e308b8:/# time ./cmd-jumper-debian-aarch64
    
    2025-11-25T06:25:18.264Z  INFO 169 --- [           main] com.postvisioning.CmdJumperApplicationKt : Starting AOT-processed CmdJumperApplicationKt using Java 21.0.2 with PID 169 (/cmd-jumper-debian-aarch64 started by root in /)
    
    ...
    
    2025-11-25T06:25:18.278Z  INFO 169 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 14 ms
    2025-11-25T06:25:18.298Z  INFO 169 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 7777 (http) with context path '/'
    2025-11-25T06:25:18.300Z  INFO 169 --- [           main] com.postvisioning.CmdJumperApplicationKt : Started CmdJumperApplicationKt in 0.044 seconds (process running for 0.049)
    
    ^C
    
    2025-11-25T06:25:19.114Z  INFO 169 --- [ionShutdownHook] o.s.b.w.e.tomcat.GracefulShutdown        : Commencing graceful shutdown. Waiting for active requests to complete
    2025-11-25T06:25:19.115Z  INFO 169 --- [tomcat-shutdown] o.s.b.w.e.tomcat.GracefulShutdown        : Graceful shutdown complete
    
    real    0m0.875s
    user    0m0.035s
    sys     0m0.030s
    ```

## **🚀 Key Features**

* **RESTful Interface:** Simple POST request to execute commands.
* **Structured Output:** Provides commands, exit code, stdout, and stderr in a predictable JSON format.
* **Container-Native:** Built using Spring Native/GraalVM for minimal size and fast startup within containerized environments.
* **Architecture Agnostic:** Built and tested for both x64 (AMD64) and aarch64 (ARM64) Linux architectures.

## **📦 Deployment and Execution**

* CMD-Jumper is intended to be run as a sidecar or main application within a Docker container.
Download and run in your container simply.
* This is single binary package

## **💡 Usage**

The service exposes a single core endpoint for command execution.

### **Endpoint**

| Method | Path      | Request Body                                    | Response Body                                                   |
|:-------|:----------|:------------------------------------------------|:----------------------------------------------------------------|
| POST   | /cmd/exec | ```{ "commands": ["echo", "'Hello World'"] }``` | ```{ "exitCode": 0, "stdout": ["Hello World"], "stderr": []}``` |


## **🛠️ Installation**

### **Prerequisites**

* AMD64/ARM64
* Linux (Glibc)
  
    | Artifact Name             | OS Base           | Architecture | Download ([Releases](https://github.com/GregYeo/cmd-jumper/releases)) |
    |:--------------------------|:------------------|:-------------|:----------------------------------------------------------------------|
    | CMD-Jumper-debian-x64     | Debian (Glibc)    | AMD64        | cmd-jumper-debian-x64                                                 |
    | CMD-Jumper-debian-aarch64 | Debian (Glibc)    | ARM64        | cmd-jumper-debian-aarch64                                             |
    | ~~CMD-Jumper-alpine-x64~~ | ~~Alpine (Musl)~~ | ~~AMD64~~    | ~~cmd-jumper-alpine-x64~~                                             | 

### Deployment setup
#### A. To include in dockerfile

```dockerfile
# Dockerfile

# Your docker image having CLI tool
FROM index.docker.io/library/debian:13-slim

EXPOSE 8080

ADD ./cmd-jumper-debian-aarch64 /app/cmd-jumper-debian-aarch64
CMD /app/cmd-jumper-debian-aarch64
```

#### B. To mount the bin and run it
```yaml
# compose.yml

services:
  cli-tool:
    # Your docker image having CLI tool
    image: index.docker.io/library/debian:13-slim
    volumes:
      ./cmd-jumper-debian-aarch64:/app/cmd-jumper-debian-aarch64
    expose:
      - 8080
    command: /app/cmd-jumper-debian-aarch64
  main-app:
    image: ubuntu:24.04
    depends_on:
      - cli_tool
    command: ["curl", "http://cli-tool:8080/cmd/exec", "-XPOST", "-H", "Content-Type: application/json" "-d", "{\"commands\": [\"echo\", \"'Hello World'\"]}"]
```