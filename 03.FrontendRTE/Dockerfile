#FROM nginx
#
#RUN rm /etc/nginx/conf.d/default.conf
#
#COPY default.conf   /etc/nginx/conf.d/default.conf
#COPY certif.pem   /etc/nginx/certs/cert.pem
#COPY private.pem   /etc/nginx/certs/key.pem
#COPY MaCoCoLive/dist/macoco /usr/share/nginx/html

FROM unicon/shibboleth-sp

RUN yum -y update \
    && yum -y install httpd php mod_ssl mod_ldap \
    && yum -y clean all

COPY httpd-foreground /usr/local/bin/
COPY httpd.conf /etc/httpd/conf.d/httpd.conf

COPY certif.pem   /etc/httpd/ssl/cert.crt
COPY private.pem   /etc/httpd/ssl/key.pem

COPY var-www-html/ /var/www/html/protected/
COPY MaCoCoLive/dist/macoco /var/www/html/

RUN chmod +x /usr/local/bin/httpd-foreground

#Shibbolet SP
COPY etc-shibboleth /etc/shibboleth/


EXPOSE 80 443

CMD ["httpd-foreground"]
