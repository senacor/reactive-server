Reactive Server
===============

### start application
- build: mvn clean package (-DskipTests)
- start your local Mongo DB: mongod (--dbpath {path to db})
- start application: java -jar reactive-server-1.0-SNAPSHOT-fat.jar
- check mongo db:
  - shell: execute `use reactile` and `db.accounts.find().size()`.
  see http://docs.mongodb.org/master/tutorial/getting-started-with-the-mongo-shell/
  - client: connect to port 27017 on localhost
- perform sample request:
  - Open Browser at `http://localhost:8081/start?user=momann&customerId=cust-100000` or
  - curl `"http://localhost:8081/start?user=momann&customerId=cust-100000" | python -m json.tool`




### jmeter (load/performance tests)
- start jmeter (mvn exec:exec) or look at src/test/jmeter/README.md
- open a testplan (src/test/jmeter/testplan/*.jmx)
- execute the testplan with the green 'play' button

### hystrix
- start the hystrix dashboard: https://github.com/Netflix/Hystrix/tree/master/hystrix-dashboard
- connect to the application at http://localhost:8082/hystrix.stream

FAQ
===

### Beim ersten build gibt es Timeouts bzw. die embedded Mongodb kann nicht heruntergeladen werden
- lokale MongoDB instanzen herunterfahren!
- in VerticleDeployer den DEFAULT_TIMEOUT auf z.B. 300_000 setzen und in 
  Line 56 (return future.get(timeoutInMillis, TimeUnit.MILLISECONDS);) den DEFAULT_TIMEOUT setzen!
- Alternativ zu dem automatischen Download kann auch unter 
  <USER_HOME>/.embedmongo/<linux|windows|osx>/mongodb-<OS/ARCH>-<VERSION>.<tgz|bei windows=zip>
  die passende mongodb, die man online unter https://www.mongodb.org für sein OS bekommt, ablegen.

### Wie kann ich die MongoDB leeren?
- mongo reactile --eval "db.dropDatabase()"
  "reactile" ist der Datenbankname
  
  
TODO
====
- Embedded Mongo auf einem anderen Port laufen lassen.