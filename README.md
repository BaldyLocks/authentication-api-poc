# authentication-api-poc

1. Build with `mvn package`
2. Run mock using `start-wiremock.sh` (uses port 8081 and 8444 )
3. Run API using `mvn spring-boot:run` (uses port 8084)
4. Run smoke test using `smoke-test.sh`. It will register one account and authenticate on it. Printout is a base64 encoded JWT.
