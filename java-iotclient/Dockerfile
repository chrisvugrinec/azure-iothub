FROM ubuntu
MAINTAINER chvugrin@microsoft.com
RUN apt-get update
RUN apt-get -y install default-jdk
RUN apt-get -y install bc
RUN mkdir /opt/lib/
COPY libs /opt/lib/

COPY *.class /opt/
ENV CLASSPATH="/opt/lib/commons-codec-1.9.jar:${CLASSCLASSPATH}"
ENV CLASSPATH="/opt/lib/gson-2.5.jar:${CLASSPATH}"
ENV CLASSPATH="/opt/lib/iothub-java-device-client-1.0.16.jar:${CLASSPATH}"
ENV CLASSPATH="/opt/lib/org.eclipse.paho.client.mqttv3-1.0.2.jar:${CLASSPATH}"
ENV CLASSPATH="/opt/lib/pi4j-core-1.1.jar:${CLASSPATH}"

WORKDIR /opt/

CMD [ "/usr/bin/java" , "AzureIotHubClient" ]
