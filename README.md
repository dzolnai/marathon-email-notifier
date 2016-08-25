marathon-email-notifier
=======================

### Quickstart

#### Building the project

To build the project, execute the following line in its directory:

```
sbt assembly
```

The jar output will be in the `target/scala-2.11/` directory.

#### Deployment

Let's assume the Mesos master you want to deploy to is `mesos-master.example.com`

First copy the example config and the built jar to the machine:

```
scp target/scala-2.11/marathon-email-notifier-assembly-*.jar mesos-master.example.com:
scp src/test/resources/application.conf mesos-master.example.com:
```

Now SSH into the machine, and do the following:

* Edit `application.conf` so that it points to the correct ZooKeeper URL and email addresses, and make sure that the email sender configuration is valid as well.
* Create a new file called `log4j.properties` with the content below (this will print debug logs on your console):

```
log4j.rootLogger=DEBUG, stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
```

#### Running the notifier

Assuming that your home directory is `/home/user/`, you can run the notifier by executing:

```
java -Dconfig.file=/home/user/application.conf -Dlog4j.configuration=file:/home/user/log4j.properties -cp /home/user/marathon-email-notifier-assembly-1.0.jar daniel.zolnai.marathon.MarathonEmailNotifier
```

Note that the jar name might be different depending on the version number.

### Contributing

Feel free to fork and make a pull request :)
