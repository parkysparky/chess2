# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games. The sequence diagram for the serve can be found [here](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-yRiY6DBMfktQQN05XIcuM9W1tdn7gOYs-We8MarOxk4DZJ8a6UpJtSmZUB5l0ppi7SouwYjRIcCi8Elzu4cx-Egwo9y1hpNFvizsaTSWStjhSq2JNSRCpQaK+29LHH7yZUfTZKUN7nxtjqkVjh9X8tOYK4VypHAXOgbFMJUqua3NlYkWJutlkvKQuVb5vkyVjPZZqzWVqnUfPArvQ1jKFyHyrhqzeWqo2io9tfdAHA75eF8P4AIXgUDoHfp-XwzAf7pEyE7PIDpgEVCqDUOohdGgSszl+aQ-E378UmPxGVKCfV7IPKOdAzy7JKq8SOtA6ScGZL+dk8tgLy0FJBT0phELHRlIFHoyUqihxTrOlwzRtT5T1OVAWNR2hkWTqPG07hmLBg7pxZGUsxSj0KI4CgbgmRSRPvkIesRuZT3SC-QKCqqRDBFF8CqP9wA119KVTQ5dxLzEzvRu24E-qjWJtcWuO1dM5J3gfEgEMN70BtuscMTt3be39uiYOsj07FXT10embx0705zoCgu5MS7kwroYWCt9YjWGwb3cOo8AGLont4ee1jRYr33UY3ezRD63aWngwGqsSH+MoaGT80Na8zXbKpUO2lRyGVziZexVlRnCYRsGFy-ZPLDmOxbgR122tGMUdGUMajPa+2RO9Uxxx46WPKdVRhsNWyJlby82xqdylvCRk-cXA1ZcnEHxNWymLHL65M0Y0liAKXFlZtvqYPNj8AiEhDG-YkMAADim5xGVr-jWwBhR62gKbRA1trrYGhqGA1ntel7CblWMp5jeD5MSdHZxn2WTiTUOJAJ0FuLwXvs3eUsTjGpPcJk2emDl7BFKYS7e9FEi-mwfg8Jh0rCmtcV-cd4ALgYUbqA4MIocnQQAHJxFwAg+oB7agVN+jU8gWIpIAeFGB6oNbL7elaYJStvTaGV5IROGNrifEhibCxygAAktIPiazFw41YmcX+mR3SDw2EiHQCBQAjhpx4pE+OABym4jjpcnthliLizaY+azjvHm4ick7JxTk4VO3S2Lp2cBnTOWftTZ5uTn+xufvFmPhgJ2VvP9Zqu6r8w2mr44m2d0dU3XkzfY2j8HS26uxFWzdmQsLRPPfE+xvbx65SyaOwpk7YRlMXZGWQrpVpZFI-fijhsqHIuUbs+anZZmXN8ueXzrGNnTX2dTVWJzqRzNuZOR5hmBWLdoB80rBqJvRubjHYNab4e7eGerrlhzNt4v7qPEVkraWsMJv59lxPJmtWd9m0kbAyWSOlcdtm3ND8C3YGvNgbg8AXSZEa5uFIVb-61qAZabr4CW1QJgYbwbNyzdDvY2uDnm5-Vhem4xm-au792644MJRP64Dr5QM7opruN13cA9gBPcD1Qc4UDsGlYNr1y9wDLsAprt-90V7tmsntgDXsTpvcalfcYAvsVRft-tAdVBYc4DQ8EdBgIcwAociDYd4cdoADNsP8f9gc0D9F5AMDdYsCICcCgkQxocgct9IAjBtA9ADBSC1N+DiDNwXB8cidNMH9rdv8ExMhBlm81UHJZDidBhSdyd+8rMcMq5NCJddDjl-E5Jsp8c3Yt9VhSR3RSYEAEYwgdAv1Eh7BxFlQykt9kgMhUgYAUp9grD9hGRK9wlpV5hL8n91hb99h78G9rdIi1hoiUBg1Z0Fs-lP8UAocf8-8hMGCRMt0wAdty8uCPtDsL1gCYCu9R0Q81NEDcjkCCjYdWDJQODUUSitE8Dgklg-t4AaDmtxD39yEltJDaD5C4iOwlDXRVD49Rlh9YsSZspxJ6EedUjnF2I3FW9jN5iRIgkcoIoi8zCzldjGNHMU9eVPYDc2Z1C-Ywi5gIjy9n99h1dkj68xYEikiUiFo3omDlDMjJiVDgVBN1tbt9oCiiiqi0B2jIC5NoDTsISBi0iAom9I8FCJjsjUcZiq9osti8tTiaVU8LN419D+cs8cscT2989C8dc5J01HB9dT8riote5wjxsoiX8YjXilVEj2SXjMTV5Nic8LVtVHVRVdsVj0NnEh8BSk8t5aTSZiiDiBUYA5SkiQiPVTdWSEjYi3jYC+Ss429c984VSeS40MsM9K4NiU0hTjSnjNwysc0KsF9AhLAv0KFkgYAAApCADw4HQIRXEAEcdrOtA-RtI-BoE-N1c-QUDU-Yc3CEtcFfYAF0qAOACAChKASoTQ7UidB49YRM5M1M9MzMsXaQT4sJSRAAK29LQFJC9NMUBLoNfTyLuzBI9121IOhP9zYKRThPHwRO+Jm2LHqLd0aNQNg1aPFA7JwM6JRR6JGP6JqMGMoOoJh2a0bN6RBNKU6JXIEP2BkJLOZDGLFjrKJVj30xDWuLWD0OcVw3WGpKOI4GByJzVOuWjNr1jKvynQTMZwLLTOgGLP2CJ2zPC1zPlR-MoELP-M8MAtLLf0RMGCrKoRPJyOBObNBO2zbOKKnLqSgOe0qL7MXPgsHOfXoIaPKSaPHLe021KJnIIN6NXK4n7JIgoOGL6K4nXPXUYNwPPR3KkL3M0KhKIuRNyOPOrOmMsUvOlJHx2LEkBPFNlDWOTXDUNMCVkv2PvMqD13LxfIQTfJZI-ISPzIgr-IzOgsJ1gqtw7EMvApTJMoAvMrLLU0QprOQobJdzIu3UwohMEpwphLwt7Nt0IoHOEvWyj1crPLUKizmNxKmXxPOPksy2sxZWzxlM5TONcw0pgDrlJFxkZDhgcPJiRmACqkuO9ko1GD0ruM1NAqMtsqLLMqAs5JAvjLzJssgtMqzLgoHOctrLErcqQJHIwuANAMk2wtPVwoqICqnSYrITqNQo8rAAouewnLtLGs+2+26MIIYpByCuYqGMh3nPYvcpHO3MOpQH3Jgo4oQxY3CpJUsRbytMmTJiCSKqPASvNOZTxjJMFKeoKpespmKpRlMKVLlJONUr2PoR0r9kqsvzAqTOMvqs6oGklKUoNOtJFLpIVMyrlNqvaqyl2KWIZChoqpjJQDjPH0eIcvrxRstOUvRt1WdTapMuvncwCRtPMvxrUshtKq7iZJGBhuqparht-PTNiJppSukqNIxoaukCbjn0dPzUCC8CTLnATFgCbWQBAHiESG3zaxyD306xDLAWbXDOJv81o3mGMCaum0-W-UyOZHmwHNtrA1JCus3IURAG4DwERVdsqE9rVuB13UZD9q9qgDEx8tPX9u9uxXkF9sUVDsDu0DjqjrDo9yuqj0QDVvEuGUM2vIPlvKvOBvtSyo1lJCPjyptk-VUGdGIAIB1ogCKBgEoGEmCOgTnyAA).
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
