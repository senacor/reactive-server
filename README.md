Reactive Server
===============

### start application
- build (mvn clean package) (maybe -DskipTests because some *Transaction*Tests will fail)
- start your local Mongo DB
- start application (java -jar reactive-server-1.0-SNAPSHOT-fat.jar)

### jmeter (load/performance tests)
- start jmeter (mvn exec:exec) (or look at src/test/jmeter/README.md)
- open a testplan (src/test/jmeter/testplan/*.jmx)
- execute the testplan with the green 'play' button

### hystrix
- start the hystrix dashboard (https://github.com/Netflix/Hystrix/tree/master/hystrix-dashboard)
- connect to the application (http://localhost:8082/hystrix.stream)

FAQ
===

### Beim ersten build gibt es Timeouts bzw. die embedded Mongodb kann nicht heruntergeladen werden
- lokale MongoDB instanzen herunterfahren!
- in VerticleDeployer den DEFAULT_TIMEOUT auf z.B. 300_000 setzen und in 
  Line 56 (return future.get(timeoutInMillis, TimeUnit.MILLISECONDS);) den DEFAULT_TIMEOUT setzen!
- Alternativ zu dem automatischen Download kann auch unter 
  <USER_HOEM>/.embedmongo/<linux|windows|osx>/mongodb-<OS/ARCH>-<VERSION>.<tgz|bei windows=zip>
  die passende mongodb, die man online unter https://www.mongodb.org für sein OS bekommt, ablegen.
  
  
TODO
====
- Embedded Mongo auf einem anderen Port laufen lassen