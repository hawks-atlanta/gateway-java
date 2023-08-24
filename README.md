# Gateway

[![codecov](https://codecov.io/gh/hawks-atlanta/gateway-java/graph/badge.svg?token=0FSRVUD6AD)](https://codecov.io/gh/hawks-atlanta/gateway-java)
[![Coverage](https://github.com/hawks-atlanta/gateway-java/actions/workflows/coverage.yml/badge.svg)](https://github.com/hawks-atlanta/gateway-java/actions/workflows/coverage.yml)
[![Release](https://github.com/hawks-atlanta/gateway-java/actions/workflows/release.yaml/badge.svg)](https://github.com/hawks-atlanta/gateway-java/actions/workflows/release.yaml)
[![Tagging](https://github.com/hawks-atlanta/gateway-java/actions/workflows/tagging.yaml/badge.svg)](https://github.com/hawks-atlanta/gateway-java/actions/workflows/tagging.yaml)
[![Test](https://github.com/hawks-atlanta/gateway-java/actions/workflows/testing.yml/badge.svg)](https://github.com/hawks-atlanta/gateway-java/actions/workflows/testing.yml)

## Development

### Submodules

Fetch submodules after cloning:

```sh
git clone https://github.com/hawks-atlanta/gateway-java
git submodule update --init
```

### Tools

- Have `jdk11` or newer installed.
- (Optional) Use the **gradle wrapper script** (`./gradlew`) for all `gradle` commands. For example:

    ```sh
    ./gradlew build
    ```

- (Optional) Use the provided `nix-shell` to get into a shell with all required dependecies [[install Nix](https://nixos.org/download)].

    ```sh
    nix-shell
    ```

### Run

```sh
gradle run
```

### Run tests

```sh
gradle test
```

### Format

You need to have `clang-format` installed.

```sh
./format.sh clang-check # check (doesn't write)
./format.sh clang-format # apply (writes)
```

## Coverage

|![coverage](https://codecov.io/gh/hawks-atlanta/gateway-java/graphs/sunburst.svg?token=0FSRVUD6AD)|![coverage](https://codecov.io/gh/hawks-atlanta/gateway-java/graphs/tree.svg?token=0FSRVUD6AD)|
|---|---|
