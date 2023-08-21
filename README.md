# Gateway

## Development

### Required tools

- JDK11
- Gradle

You can use the `shell.nix` script to get a shell with all required dependencies, should work on most linux distributions and on Windows Subsystem for Linux (WSL) [see how to install Nix.](https://nixos.org/download)

```sh
nix-shell
```

Otherwise you need to install `jdk11` and use the **gradle wrapper script** `./gradlew` instead of `gradle`. For example:

```sh
./gradlew build
```

### Building

To generate a single JAR run the command below. It'll be located at `./app/build/libs/app-all.jar`.

```sh
gradle build
```

### Testing

To run all tests:

```sh
gradle test
```

Generate coverage report which can be found at `app/build/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml`.

```sh
gradle testCodeCoverageReport
```

### Running

Use docker compose to start the service locally. SOAP will be exposed at `http://localhost:8080/sampleservice?wsdl`.

```sh
docker-compose up
```

