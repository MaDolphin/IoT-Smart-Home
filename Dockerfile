FROM java:8-jdk
MAINTAINER "Software Engineering, RWTH Aachen University"

ENV PATH /usr/local/tomee/bin:$PATH

RUN mkdir -p /usr/local/tomee

WORKDIR /usr/local/tomee

# curl -fsSL 'https://www.apache.org/dist/tomee/KEYS' | awk -F ' = ' '$1 ~ /^ +Key fingerprint$/ { gsub(" ", "", $2); print $2 }' | sort -u
# ENV GPG_KEYS \
# 	223D3A74B068ECA354DC385CE126833F9CF64915 \
#     7A2744A8A9AAF063C23EB7868EBE7DBE8D050EEF \
#     82D8419BA697F0E7FB85916EE91287822FDB81B1 \
#     9056B710F1E332780DE7AF34CBAEBE39A46C4CA1 \
#     A57DAF81C1B69921F4BA8723A8DE0A4DB863A7C1 \
#     B7574789F5018690043E6DD9C212662E12F3E1DD \
#     B8B301E6105DF628076BD92C5483E55897ABD9B9 \
#     DBCCD103B8B24F86FFAAB025C8BB472CD297D428 \
#     F067B8140F5DD80E1D3B5D92318242FE9A0B1183 \
#     FAA603D58B1BA4EDF65896D0ED340E0E6D545F97
#
# RUN set -xe \
#     && for key in $GPG_KEYS; do \
#       gpg  --keyserver p80.pool.sks-keyservers.net  --recv-keys  "$key"; \
#     done

#    && curl -fsSL 'https://www.apache.org/dist/tomee/KEYS' | awk -F ' = ' '$1 ~ /^ +Key fingerprint$/ { gsub(" ", "", $2); print $2 }' | sort -u | xargs -L 1 gpg --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys \

RUN set -x \
	&& curl -fSL https://archive.apache.org/dist/tomee/tomee-7.0.5/apache-tomee-7.0.5-plus.tar.gz -o tomee.tar.gz \
	&& tar -zxf tomee.tar.gz \
	&& mv apache-tomee-plus-7.0.5/* /usr/local/tomee \
	&& rm -Rf apache-tomee-plus-7.0.5 \
	&& rm bin/*.bat \
	&& rm tomee.tar.gz*

RUN printf "deb [check-valid-until=no] http://archive.debian.org/debian jessie-backports main" > /etc/apt/sources.list.d/jessie-backports.list

RUN sed -i '/deb http:\/\/deb.debian.org\/debian jessie-updates main/d' /etc/apt/sources.list

RUN apt -o Acquire::Check-Valid-Until=false update  && apt install -y netcat-openbsd && apt-get install xmlstarlet -y

# add tomee config files to container
RUN mkdir -p /usr/local/tomee/webapps

COPY 05.Dockerconfig/tomcat-users.xml /usr/local/tomee/conf/

# add montigem libs and jar to container
COPY 04.BackendRTE/lib/ /usr/local/tomee/lib/

COPY 04.BackendRTE/lib/hibernate-jpa-2.1-api-1.0.0.Final.jar /usr/local/tomee/lib/hibernate-jpa-2.1-api-1.0.0.Final.jar

COPY 04.BackendRTE/target/montigem-be.war /usr/local/tomee/webapps/montigem-be.war

RUN mkdir -p /usr/local/tomee/WEB-INF

# move scripts to container
RUN mkdir -p /usr/local/tomee/scripts

COPY 07.Scripts/Tools/createDummy.sh /usr/local/tomee/scripts
COPY 07.Scripts/Tools/resetDB.sh /usr/local/tomee/scripts
COPY 07.Scripts/Tools/exportData.sh /usr/local/tomee/scripts
COPY 07.Scripts/Tools/exportUser.sh /usr/local/tomee/scripts

COPY 07.Scripts/Tomee/tomee-entrypoint.sh /usr/local/tomee/bin
RUN chmod +x /usr/local/tomee/bin/tomee-entrypoint.sh

COPY 07.Scripts/Tomee/receiveResourceXML.sh /usr/local/tomee/bin
RUN chmod +x /usr/local/tomee/bin/receiveResourceXML.sh

# COPY db-schema.sql /usr/local/tomee/db-schema.sql

EXPOSE 8080 4200

ENTRYPOINT ["tomee-entrypoint.sh"]

CMD ["catalina.sh", "run"]
