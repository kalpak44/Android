 
# Android messaging application (not edited ReadMe.md)

Simple java application. Simple client-server comunication with sockets.

  - History saving in local database
  - Authentification/Registratin
  - Simple using


A simple software to exchange messages. She works in the likeness of mail programs to work with an email. Messages are stored on a remote server for mysql database, which is pumped through the serialized objects. android app and easy to use interface you can view them. Android client processes them and displays in a convenient interface. Also supports sending messages to the server, which can be read by users who have addressed.

### Tech

Client - Android Studio Project:

* OpenJDK Runtime Environment (IcedTea 2.5.6) (7u79-2.5.6-0ubuntu1.14.04.1)
* OpenJDK 64-Bit Server VM (build 24.79-b02, mixed mode)
* java version "1.7.0_79"t

Server - Eclipse project


And of course Dillinger itself is open source with a [public repository](https://github.com/joemccann/dillinger) on GitHub.

### Installation

You need install mysql server:

```sh
$ apt-get install mysql-server
```
Create tables in database
```sql
INSERT INTO table_name (column1,column2,column3,...)
VALUES (value1,value2,value3,...);
```

### Running
Run server:
```sh
$ java -jar Server.jar
```
