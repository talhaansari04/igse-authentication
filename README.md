# iGSE (Great Shangri-La Energy)

## Running the app locally

### To run the app locally run the boot run gradle task

```
./gradlew bootRun
```
### To start the stub server

```
./gradlew stubRun
```
## Debugging the app locally
To debug the app locally run the below cmd.After that follow these step. 
- Intellij -> edit configuration
- Click on top left + (plus)
- select remote debug to jvm

```
./gradlew bootRun --debug-jvm 
```
### To start the tests 
```
./gradlew test
./gradlew componentTest
./gradlew blackboxTest
```

## Docker

### To up docker
```
./gradlew composeUp
```
### To up docker
```
./gradlew composeDown
```
## Note
### Before following app should be up.
-  docker.
- Zookeeper, Kafka
- MySql