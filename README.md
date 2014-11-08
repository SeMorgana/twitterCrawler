Sample results extracted from the database are in [samples](samples)/ directory. Those results are based on [ids.txt](ids.txt).

The file containing main() function is Collect.java.
Assuming all the .java files are in one directory, and we are inside the same directory in the command line:

To compile:

```
javac -cp "gson-2.2.4.jar:twitter4j-core-3.0.5.jar:mysql-connector-java-5.1.16-bin.jar:." Collect.java
```

to run the program:

```
java -cp "gson-2.2.4.jar:twitter4j-core-3.0.5.jar:mysql-connector-java-5.1.16-bin.jar:." Collect [database file] [token file] [twitter id file] [cmd]  
```
### Parameters in the command above: ###
#### [database file] ####
This file contains the information of a database in which the collected data are stored. The information includes username, password, database name, table names(optional), etc. One example of this file would look like:

```
host : localhost
database : tw_cross
username: xxxxx
password: xxxxx
port : 3306
tables : users tweets urls
```

So the database name is tw_cross, and the three tables are users, tweets, and urls. The last line in the file(i.e.tables) is optional, if not specified, the names of the tables would depend on the time when the program is executed. 

Make sure inside the database, the server characterset and DB chracterset is utf8mb4; client characterset can be utf8. When creating the database, using the statement "CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci" after the database name.(e.g. `CREATE DATABASE IF NOT EXISTS tw_cross CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`)

___THE DATABASE(tw_cross) NEED TO BE CREADTED BEFORE RUNNING THE PROGRAM(tables do not have to be created before running).___


#### [token file] ####
This is used for authentication. One example of this file would look like:

```
xxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx     
xxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx     
xxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx     
```

Each line has four parts: Consumer key, Consumer secret, Access token, and Access token secret, respectively. Each of them is separated by a space, and each group of them occupies one line. Those tokens can be easily obtained from [here](https://dev.twitter.com/oauth/overview/application-owner-access-tokens) if you have a Twitter account.


#### [twitter id file] ####
This is just a list of twitter ids you are interested in, each one of them occupies one line:

```
11111111111
2222222222
333333333333
```


#### [cmd] ####
This can be a single character or a string, which decides what commands to run:

u - user info<br>
t - tweets and urls in the tweets<br>
fr - random 25 friends ( left from previous version)<br>
fo - random 3000 followers ( left from previous version)<br>
p - update existing databases(i.e. collect only new tweets from the same users) ( not tested extensively,  inefficient )

Those commands can be used together, but using them separately is recommended(i.e. running the program multiple times with different commands).

#### Summary: ####
One possible command to execute the program could be:

```
java -cp "gson-2.2.4.jar:twitter4j-core-3.0.5.jar:mysql-connector-java-5.1.16-bin.jar:." Collect db_info.txt tokens.txt ids.txt u
```

which will collect the information of users whose ids are in the file ids.txt
