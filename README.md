# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games. The sequence diagram for the serve can be found [here](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-gAheBQOgd+n9fDMB-ukTITs8gOmARUKoNQ6iF0aFKzOX5pD8TfvxSY-FbkoIVXsg8o50DPLsiqrxY60DpJwZkrF-zK2AsrQUkFfS5EQsdGUgUejJSqKHDOs6XDNHwt4Ui3RajUVhGnUeURF1flYr3QYvFxST0KI4CgbgmRSTPvkMesRuYEXSC-QKCqqRDBFF8CqP90jDFBqrDQ1dQzzFzvRp24EgaTXJtcWuB1dM5J3gfEgEMt70AduscMbtvb+2DuicOsjs7lXT0vYeu96cF0BSXcmFdyY10MNfTIWFrDYMHtHUeADF0z2Ipg1ewR91GP3u4Y+gKFoygboQ4SitfGUOkp+eGteFrtk0pHfSo5TK5wsvYuywzhMo2DB5fsvlhzHYtwI67bWjGKPwKoz2vtA65VDqY44ydLHFPqowxGrV6b65M0Y8pbwkZP3FyNWXJxB8zUcq2RMrenn0zeKSNgRLJHFk5tvqYAtj8AiEhDG-YkMAADim5xHVr-nWwBhRG2gJbRA9t7rYHhqGA1vtel7CblWIp5jeDWPifHRxn2fziTUOJPx0F-lwXvu3eU0TjHJPcOkw02DLTFMYvJZGJ9cm4OCY6awprXFf2XZcDCrdQHBhFAvaCAA5OIuAEH1B3bUEpzRKnBjIFiKSX7hQAeqDW+d2Rmn34rd02hleSEThja4nxIYmwMcoAAJLSD4msxcONWJnF-pkd0g8NhIh0AgUAI4qceKRLjgAcpuI4qXJ7YZYi4s26PmtY5x5uAnROSdk5OBTt0tiadnDpwzpn7UWebnZ-sTn7xZj4YCdlLz-Waqeq-MNpquOJv5ZnROwa029EFZRyD8hS26uxFWxut9YiROXbEwVvbp65Tntk0Wa9pMzd3tO3btTVp4Ohem47sAJLbcGertlrlVZHOpDM1znBPOsbWfNXZmLDnTPOYFcc-xbc4vB-I3rtmGqGpG9G5uC3Ytw-x5r1FpP9mbZ5bY2WorEAkulYz+h5xmXbOWsmQXeLvf+8pbPLm-ND8i3YGvNgbg8AXSZEa5uFINb-71qAZabr4C21QJgfrwbES5gm5HQVtcbPNyBqj68oP3e0HrDv-sUN86FtYqUT+uA6+UBncikhMt13cA95BPcj0gcalfcZMCxLtjsK9Z1Q9ONBhYMXcQDNtbtmsHtwDgAnsTpvcYC6l3sVQvsfs-tVBodoDOk7cwcwAIdKDodYcdpMC3dFEACAdcD9F5ACDdYiC4VYCgkQxId-st9IAjBtA9ADAaDMUApRCqDNwXBccCcNNH8Ox-8ExMg48ItKNhd9hRdBhidScsMk1edcNEoVDCcjDxctc5Jspcc3Yt9VhSR3RSYEAEYwgdAv1Eh7BxFlQykt9kgMhUgYAUp9gnD9hGRvMKU-ZZVL9xtr8Z1b8Vd79G8p0kCUj9hVcUBP8Fo3pBhf8UAIcACgDrthMd0wAdskCBDXs4CZs7ob0kDZCzssl0DgCbtKjoduDJQ+C2laitFSDgklhvt4AmDmsWi6ClsFDmC1DLcn9NDXQdDLEE800rVspxJ6FB9ZRnF2I3FE8jMcsSYNjgUwBXMTl3NKgdckCC86Ui9PYq9vZKNe55gr9GMsiUAciH95iOx3i39UiP9bdUCOCtDijFjtDTiWDSxXcHQwCeDgBICJMWiDsL0jsFNmiUDv9VNelI8fiqxwTADkddCfNR9jNuVC9+VzNE1LNk0c8stDjk9bjeVKTzjS9XZM1HBddT9q9IsXiEj9gPivj0iWM1h39ciW9ItSSjjo1nVxVdttj0tTVU1osrVdVZTOSajWShUYAOTAjsjNwYjwl4i3jMj1hvixZwsVjW8pTGTrV1S9TPjNwE00ss9K59i1jx9dSxSys80KsF9AhLAv0KFkgYAAApCAAIgHQIeXEAEcdrBtA-ZtI-BoE-D1c-QUY3RIv4tYFfYAQMqAOACAChKASoKw80jIl-RVenfMws4s0skXaQPIsJLJAAKwjNyXDNMUhIwM6O2w9122RNgMOwQPRJf0mOBPaPKNAK6JwNgz6PFEHJII+xGIoKhwmMxIKPt3BxmOayhNkRhP2hgCGMYLXP2GUIbOZDmLFk7OJSJKtMlNMJpPMLNjsLOSCQBwJ0NI5gzPrwFKSKPCrLzMoFrOgHrIMMbOFOm2zNzJrKLNAodIJybLtzbKoRvLKPW03SwMqOqJfwGJRP93hMQLHI3O6QaJfQwoPNKXKW6LnOe02zqKGNaVGJ3K4nHKxNB2mPGK4j3I23YOPJYpQHPPArwvYrdhxME2vPbOWLJWtIOLz3WKCRygigVLMOz2VPb3z0CTElOK1MdS0uf1mzQC-K-BGB-NeKzNNJzOrOArgpLIQogqmyf2gusoLNsrAvxwgvm03JQo7Kku7I6IqL7LwMRPQBErqWHLwKIsMrYs3Ob1xMkq7IbFQ2JNiJtI71T3T0fN2LZVzzHxMzuJZNfMqDrlJFxkZDhg8PJiRmACqkeK7l5NMszL-OcqAtcrrPsvLLC0spgpsvarLKBNEp8tJDQv8qnKwqCvhJCrQDCoRQisItHOipIrIUnIorYNhJnPu1osIMXIRUYvILGNPMByWrhw4u3K4rUB4swr4qRRPLELPKsMuoRxGqSr0zDVko9OtjJiCWqqPAVNdNZTxnpPksmS+opipl0suJ1PtMYyykUs2IZGMoQTMv5JQEAtgr6obO+OH3UoZI7zVP1Q1NwohoCV1J6rauOLhtOMRr9mRpNMrP+PAqxoyxxuBq3lJpcpAqgGvjcxJvtKsNhu0oimptGFposvpqstas5qZqVPdJVM9L5ovP1jnz9MLUCC8DzLnATFgBbWQBAHiESG3zaxyD3060TLAVbRTOFuo38z0mMEgqf0-W-WKOZC8tIsdrA1JEusooURAG4DwCkU9sqF9q1oB33UZCDr9qgFExmsKMjoDvDsUUjtDu0EDsTq1ujqvJVUQC1ukv02tKyoPgsLWCKpgBKqPnKptk-VUGdGIAIANogCKBgEoGEmiOgTnyAA).
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
