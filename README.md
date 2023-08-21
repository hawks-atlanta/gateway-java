# Gateway

[![codecov](https://codecov.io/gh/hawks-atlanta/gateway-java/graph/badge.svg?token=0FSRVUD6AD)](https://codecov.io/gh/hawks-atlanta/gateway-java)
[![Coverage](https://github.com/hawks-atlanta/gateway-java/actions/workflows/coverage.yml/badge.svg)](https://github.com/hawks-atlanta/gateway-java/actions/workflows/coverage.yml)
[![Release](https://github.com/hawks-atlanta/gateway-java/actions/workflows/release.yaml/badge.svg)](https://github.com/hawks-atlanta/gateway-java/actions/workflows/release.yaml)
[![Tagging](https://github.com/hawks-atlanta/gateway-java/actions/workflows/tagging.yaml/badge.svg)](https://github.com/hawks-atlanta/gateway-java/actions/workflows/tagging.yaml)
[![Test](https://github.com/hawks-atlanta/gateway-java/actions/workflows/testing.yml/badge.svg)](https://github.com/hawks-atlanta/gateway-java/actions/workflows/testing.yml)

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

SOAP will be exposed at `http://localhost:8080/sampleservice?wsdl`.

Run directly on host:

```sh
gradle run
```

Or use docker compose (preferred):

```sh
docker-compose up
```

## Coverage

|![coverage](https://codecov.io/gh/hawks-atlanta/gateway-java/graphs/sunburst.svg?token=0FSRVUD6AD)|![coverage](https://codecov.io/gh/hawks-atlanta/gateway-java/graphs/tree.svg?token=0FSRVUD6AD)|
|---|---|
