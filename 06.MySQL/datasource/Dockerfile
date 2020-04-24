FROM mysql
COPY default.cnf   /etc/mysql/my.cnf
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh
COPY generateResourceXML.sh /usr/local/bin/generateResourceXML.sh
RUN chmod +x /usr/local/bin/generateResourceXML.sh

COPY createInstanceDBEntry.sh /usr/local/bin/createInstanceDBEntry.sh
RUN chmod +x /usr/local/bin/createInstanceDBEntry.sh

ENV MYSQL_ROOT_PASSWORD=pwd MYSQL_DATABASE=datasource MYSQL_USER=admin MYSQL_PASSWORD=pass CONNECT_TIMEOUT=31536000 WAIT_TIMEOUT=31536000;
RUN mkdir -p /var/lib/mysql-files
RUN chown -R mysql:mysql /var/lib/mysql-files
RUN apt-get update && apt-get install socat -y && apt-get install xmlstarlet -y

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["mysqld"]