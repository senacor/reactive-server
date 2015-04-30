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