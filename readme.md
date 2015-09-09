 
# Android messaging application

##Simple java application. 
Simple client-server comunication with sockets

  - History saving in local database
  - Authentification/Registratin
  - Simple configuration


A simple software to exchange messages with android.

### Tech
* OpenJDK Runtime Environment (IcedTea 2.5.6) (7u79-2.5.6-0ubuntu1.14.04.1)
* OpenJDK 64-Bit Server VM (build 24.79-b02, mixed mode)
* java version "1.7.0_79"t
* jdbc connector

**Client** - Android Studio Project:

Tested on:
Genymotion Custom phone - 5.1.10 - Api22 168x1200dpi
4 Cores
RAM 1024mb

**Server** - Eclipse project
Multi thread server with mysql jdbc connector. Init database before using server.

### Installation and configuration

You need install mysql server:

```sh
$ apt-get install mysql-server
```
install mysql client:

```sh
$ apt-get install mysql-client
```
create database (example [mychat])

```sql
$ create database mychat;
```

Create tables in database
```sql
CREATE TABLE clients (
id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
username VARCHAR(30) NOT NULL,
password VARCHAR(30) NOT NULL,
avatar VARCHAR(50)
);
```

```sql
CREATE TABLE IF NOT EXISTS `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from` varchar(50) COLLATE utf8_bin NOT NULL,
  `to` varchar(50) COLLATE utf8_bin NOT NULL,
  `date_time` varchar(50) COLLATE utf8_bin NOT NULL,
  `is_readed` int(11) DEFAULT '0',
  `message` text COLLATE utf8_bin,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=15 ;
```

### Server
Compile or run currient server:
```sh
$ java -jar Server.jar
```
Params:
* -config - open server configuration menu
* -l      - load configuration file

**Default config:**
>Port number 2222
>Max thread connection 100
>DB: localhost:3306/mychat
>DB User:     root
>DB Password: root

###PS It's my first android app