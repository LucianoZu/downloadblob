# Download BLOB utility for Oracle

This utility allows to download a BLOB list into a client folder.

Required parameters are:

>* -Dconnection.url=<JDBC Connection>
>* -Dconnection.user=<Oracle user name>
>* -Dconnection.password=<Oracle user password>
>* -Dsql.filename=<path to sql query to run>

Optional paramenters are:

>* -Ddownload.folder=<download folder path>

Default value is "D:/outputScript/DWH/scartati/" (please remember always final slash)

Example:

    java -Dconnection.url=jdbc:oracle:thin:@127.0.0.1:1521:ORC -Dconnection.user=ZU -Dconnection.password=TRYME -Dsql.filename=/home/zu/query.sql -Ddownload.folder=/home/zu/blobs/ -jar downloadblob-1.0.0.jar

File with query SQL has to return 7 columns: 

1. the BLOB
2. the File Name
3. the first string called partner that identify better the BLOB and used to compose the downloaded file name
4. the second string called reference that identify better the BLOB and used to compose the downloaded file name
5. a status just to log purposes
6. a timestamp just to log purposes
7. another date just to log purposes

for instance you can try with the following query:

    SELECT 'CIAO' as CONTENT
          ,'hello.txt' as ORIGINALFILENAME
          ,'ZU' as PARTNER
          ,'001' as REFERENCE
          ,'OK' as STATUS
          ,sysdate as TIMESTAMP
          ,sysdate as ANOTHER_DATE
      FROM DUAL

For further reference, please consider the following sections:

* [Luciano Zu's github repositories](https://github.com/LucianoZu)

