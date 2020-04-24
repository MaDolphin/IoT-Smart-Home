echo create resources.xml file;
resources=$(nc datasource 12777);
echo $resources > resource.xml;
xmlstarlet fo --indent-tab --omit-decl resource.xml > resources.xml;
sed -i "s/|nl|/\n\t\t/g" resources.xml;
sed -i "s/|nl2|/\n\t/g" resources.xml;
if [ "$1" = true ];
then
    xmlstarlet fo --indent-tab --omit-decl resources.xml > WEB-INF/resources.xml;
    echo update war file;
    jar uvf webapps/montigem-be.war WEB-INF/resources.xml;
else
    xmlstarlet fo --indent-tab --omit-decl resources.xml > webapps/montigem-be/WEB-INF/resources.xml;
fi
rm resource*.*;