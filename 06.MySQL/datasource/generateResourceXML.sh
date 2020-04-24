#!/bin/bash
addResource(){
    res=$(echo $res | xmlstarlet ed -P -S -L -s /resources -t elem -n ResourceTMP \
        -i //ResourceTMP -t attr -n "id" -v "$1" \
        -i //ResourceTMP -t attr -n "type" -v "javax.sql.DataSource" \
        -u //ResourceTMP -v "|nl| jdbcUrl = jdbc:mysql://db:3306/${1,,} |nl| jdbcDriver = com.mysql.cj.jdbc.Driver |nl| userName = $3 |nl| password = $4 |nl2|" \
        -r //ResourceTMP -v Resource)
}

createResourcesXML(){
    res="<resources>
    </resources>"
}

addDBRouter(){
    res=$(echo $res | xmlstarlet ed -P -S -L -s /resources -t elem -n ResourceTMP \
        -i //ResourceTMP -t attr -n "id" -v "DBRouter" \
        -i //ResourceTMP -t attr -n "type" -v "de.macoco.be.database.DatabaseRouter" \
        -i //ResourceTMP -t attr -n "provider" -v "db.router#DatabaseRouter" \
        -u //ResourceTMP -v "|nl| DatasourceNames = TestDB |nl| DefaultDataSourceName = TestDB |nl2|" \
        -r //ResourceTMP -v Resource)
}

addRoutedDatasource(){
     res=$(echo $res | xmlstarlet ed -P -S -L -s /resources -t elem -n ResourceTMP \
        -i //ResourceTMP -t attr -n "id" -v "Routed Datasource" \
        -i //ResourceTMP -t attr -n "type" -v "org.apache.openejb.resource.jdbc.Router" \
        -i //ResourceTMP -t attr -n "provider" -v "RoutedDataSource" \
        -u //ResourceTMP -v "|nl| Router = DBRouter |nl2|" \
        -r //ResourceTMP -v Resource)
}

addTestDBSource(){
     res=$(echo $res | xmlstarlet ed -P -S -L -s /resources -t elem -n ResourceTMP \
        -i //ResourceTMP -t attr -n "id" -v "TestDB" \
        -i //ResourceTMP -t attr -n "type" -v "javax.sql.DataSource" \
        -u //ResourceTMP -v "|nl| jdbcUrl = jdbc:mysql://db:3306/testdb |nl| jdbcDriver = com.mysql.cj.jdbc.Driver |nl| userName = admin |nl| password = pass |nl2|" \
        -r //ResourceTMP -v Resource)
}

addDatasourceDB(){
    res=$(echo $res | xmlstarlet ed -P -S -L -s /resources -t elem -n ResourceTMP \
        -i //ResourceTMP -t attr -n "id" -v "DataSource" \
        -i //ResourceTMP -t attr -n "type" -v "javax.sql.DataSource" \
        -u //ResourceTMP -v "|nl| jdbcUrl = jdbc:mysql://datasource:3306/datasource |nl| jdbcDriver = com.mysql.cj.jdbc.Driver |nl| userName = admin |nl| password = pass |nl2|" \
        -r //ResourceTMP -v Resource)
}

addDatabaseEntries2XML(){
    result=$(mysql -u admin -ppass datasource -e 'select * from datasource.DataSource')
    while read dbname bezeichnung ip username password;
    do
        if [ $dbname != "dbname" -a $dbname != "TestDB" ]
        then
            addResource $dbname $bezeichnung $username $password
        fi;
    done <<< $result
}

adaptDBRouter(){
    res=$(echo $res | xmlstarlet ed -P -S -L -u "//resources/Resource[@id = 'DBRouter']" -v "|nl| DatasourceNames = TestDB $1 |nl| DefaultDataSourceName = TestDB |nl2|")
}

collectExistingResourcesAndAdd(){
    names=$(echo $res | xmlstarlet sel -t -m '/resources/Resource[@id != "DBRouter" and @id != "Routed Datasource" and @id != "DataSource"and @id != "TestDB"]/@id' -v . -o " ")
    adaptDBRouter "$names"
}

format_resource_xml(){
    res=$(echo $res | sed -e "s/|nl|/\n\t\t/g")
    res=$(echo $res | sed -e "s/|nl2|/\n\t/g")
    res=$(echo $res | xmlstarlet fo -t)
}

createResourcesXML
addDBRouter
addRoutedDatasource
addTestDBSource
addDatasourceDB
addDatabaseEntries2XML
collectExistingResourcesAndAdd
#format_resource_xml
echo $res