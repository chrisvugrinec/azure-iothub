FROM ubuntu
MAINTAINER chvugrin@microsoft.com
RUN apt-get update
RUN apt-get -y install default-jdk
EXPOSE 8080
COPY build/libs/iot-client-app-0.1.0.jar /etc
CMD ["/usr/bin/java","-jar","/etc/iot-client-app-0.1.0.jar"]
