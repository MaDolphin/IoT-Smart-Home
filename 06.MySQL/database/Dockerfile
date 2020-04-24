FROM mysql
COPY default.cnf   /etc/mysql/my.cnf
COPY createDBWithSchema.sh ./usr/local/bin/createDBWithSchema.sh
RUN chmod +x /usr/local/bin/createDBWithSchema.sh
ENV MYSQL_ROOT_PASSWORD=pwd MYSQL_DATABASE=testdb MYSQL_USER=admin MYSQL_PASSWORD=pass CONNECT_TIMEOUT=31536000 WAIT_TIMEOUT=31536000;
RUN mkdir -p /var/lib/mysql-files
RUN chown -R mysql:mysql /var/lib/mysql-files