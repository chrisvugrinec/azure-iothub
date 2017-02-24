FROM alpine
#FROM ubuntu
MAINTAINER chvugrin@microsoft.com
RUN apk --update add openjdk7-jre
#RUN apt-get update
#RUN apt-get -y install default-jdk
EXPOSE 8080
COPY build/libs/gs-spring-boot-0.1.0.jar /etc
CMD ["/usr/bin/java","-jar","/etc/gs-spring-boot-0.1.0.jar"]
