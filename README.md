# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games. The sequence diagram for the serve can be found [here](https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiALxZZD7KaqoAZj4ICAA0MKnA2NhhuGCpOZiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARIuUwQC2KBv9GzAblRu46snQHIfHpycou8BICLcnAL6Ywn0wXazsXEog022ygewORxOZwuqiuUBukPuG0ez1eiM+bE43Fgf2+okGUCiMUyUAAFJForFKJEAI4+NRgACUpjxKl+3Vk8iUKnUg3sKDAAFUlqTQeDmZzFAV1OyjDp+gAxJCcGDCyiSmA6MJi4D7TCS7mFX643r4mBoUoIL6mtl-A3S1T9ECEuQoNVk0GSiXaQ0yv7GfoKDgcVVLSXWkS2jk+h1Ol2ZBQ+Rqk4BJ1Je-UxnmqWUBoMhxONcOswztP6YwEDCJEqlQSKqUpYCvY43db5AmAgpbg46DO5nVONSYQADW6F7Jw+EZ+Ze6rXMgwATE4nKsuztdQcYH2oSdB6lh2O0BO7p9MDkFaifITKsqqMBkCGUS9mvOOm3eh3RhMZvNQWutm7Tc3iRGE4QRfsHieF4QM+dscW6ZsO3XMFgMRaF9Fha4QLOZ80VPTAkIQnpIxQAkaxJckKOpFA6QZZl4NbeBkAXGAABYVwA-Z1GAflDhOABRKBvCrPQQ0JOjYg2M8LyvG8YGSFBgBHGAwOuV8WPfEi+kGb8plmBYlgAnV9hwk41PhMzkWg-Cp0Y8sAWxYFAI3Uz0PMzDwKsvDYMIxzKFlEtyMpSiKWJGjJKZacAtnZi2jAQYONXdYNm41ReIhQThOgQYxJgCT6SkmSskvF5rxQSoTMMeQjw0+LAs-Ks9N-QzKGMoC3Mg85POw9zrNRXz7MQ-yqxQns+osiDd36mD0T8rEYo-UjgvCskwtrWlCqiktZXtbM+QFd1RQ6lBvS5B1c3lJUVXdDUtRgKrM3O7MmKC0N1W0adRCYojBnWkl60bebK1exrnLSjL+I2ISRMGB8XQ4MIavHODGqYt8EvYpwAGYuMKSG+xhnKYHhpTEZgZHj2K0qEHKyo0AgB6Trq8wGuoL9xn0v8jJSqrQJ6yy+p8uahv+BbRpc1DTIwy5eq64WCKI0H2YliG+MJ7Kq1JBmmdc07pOinE-gxwYAFZOJStXMuhzXBm1xmqsZA3zxKuSKvNRnJpZrT4N0zmWv-XmTqh6bJqRBW7LRhzxecvmd352XLNwmzfKV2Lfc7VL8fVrLYZge3VIFjgndRlX0c0zHzeSzYrahomtZ1yaS9MWSyvknW8O95gTRVv2fwMwOxrQycE6wwX5ZT0uZ2GmPM7jrqw6FyfgZbdOwcz2uNbzgu8Ob+zjYrs2LZr7Prfru2O5s5uzE4UxPG8PxAmgdh+RgAAZCBoiSAI0gybJcnyNmEoZRKjVFqPUOQTQsgtArmzHSwx-YGQMOoBIaA1xVUqJNKei0xaVmBBgwuicOAYhGnAs0CBP7KlJB-L+m16JfSjDILMhQDpChFI7J6UoXr+nlDAa6IZbraE1NqE6nDfQ5jXstd6UBiw2lLEtKgZpPTaBcCAVIKAQAjhGEXUkTcGHyKYc9FhMBnRKQTGmFMaYMx7SNDwuGwYYCFnTJ9HasVfrv0oWgQGCAmykMkfAoenVQ5F0GlHA+8UlzH0luNLq+5Dzjjmi7GmdNNTABDFVLucCOb93mMg1QqD0EnUwUXbBxF3FrAIVgleOC3oUK-tQzxdDYgMTkbtZhvIYD8jYZQY6eszpcNsd0AMfDlQCLDEI+6j0bF+h7oolQgwLRlH0T9EagwaHKm8b48WysAlZx4jnG2ecfDBDTNAJAAAvFANxSnlwiVjAAjHjfZZ9bYPROY0M5lzrkt1dm3d2eUvbQIxlkpqiDcmFAKesSpJTDaynKdCohJDtmzPIZ4hptDaJbRaaRNpRiOldKOhw6ZEihm8P4dIu6Ii9ZiIuv4s0gj5DLJRfM3WMiVFqI0VonRejXHRzwR4jFDYfHVKNgo5Cez0oHPPm8-cnyrkG33nOQ+DynmSpeUc95qQ5XfKSW7SouwYj5IcKy8EmTZnZK5nMPJkK1hVNFuUqpadzWq1PnXV5pIDUoONY7BVYSlV3KrqqgmudiYesNcqRwPrqZ6pgJ6o1jhAUwPqs6vulrrWJEKXrG5fKnKdiqkikG-jxWbxDVrONEbCFj2Lr6susUTYwEDZbV1W9Q3luNU3Z26AOB3y8L4fwAQvAoHQAK3wzAf7pEyC7PIDpgEVCqDUOohdGhmrFU1aQAk34CUmAJJBEKM2aviWgWFOaOxxNHOgEVZCWV1NHei0dTTtqtLtO0x0nSBROMlJYoc560D9PEZdQY5KP0TLCGeo8NLuHMrIo4qxLin3Rjxa+jgKBuCZFJMB+Qf7aWksGNIFDApqrqJgEUXwKoMPACZSeqsND71qCBk61dwJs3hNYsuau0a-m3jQPeR8JM0yHpXdpL867N3bt3Sg-dYGL32tWTB794G04KNRfYW9NGkwPuxXMgxxLWHka-QeH9WHuE4ZGWR2D8hhF8fkxe4lOyzSLKtLyme-K1NgE2ZeotLrnluo1bKqAFz5XMf9axDijym3eZbVWY5fmAs6tbrTdujMpNoEExnIYImt07vBRJo9MnZ7JY8ymjezbS0XyS-xn9qlvCKOQ9WoLcVWKNpPhF0r+cdbJaqxAGrVzr5dp7Q-fthIQxv2JDAAA4puHM46-5TsAYUWdoCF0QOXUC2BRWhhja3QZewm5VgFby-ypxh6PNKevcSahxINNMoQwM-F77zPAH04eozgy5SAdGXJ5xFn7oFds3Sll5HruGNu6+rpE3uKkhe36EzXT4DqPUODtQEHbFQcGMgWIpI4Dw9UIj1QmnvpuNkyN2I7mGNCdGicHb3F+JDE2FTlAABJaQ-F7mLmxmxM4v9MjuhiUiHQCBQAjh52hDYSJ6cADlNxHBrTOFjGBInV0p5NmndPNxM5Z2zjnJwudumDtLs4-PBfC86mLzckv9jS-eLMDjCX-lpKswZ2qq3k2MeGJtlq9O9sVfAwd3NR2f0ne0uQ87xO3OYvoa4m74jdMPae4Z5H0O3umYLA9yzv2X12ZZQ5yjznc2h9J34orzW1U+eJtF05-mvky5inLzGoWg1SteeXj5lfAs-OSYlh3Annes3W+77bm5j2547NnsnGdi-BsOaG9r3vh3YGq0gWre8-UNflw2qJJap8N3K9ZpI8+uuL56522+phe2PwCNga82BuDwHjIYRHKQJ3-2nUAy0i3wFLqgUm3vrvmoD-2F7rvmuBLpuNmsPlWMlsAWbqAYHm9KYq6JjnfldpHsDtHm+mAHpsllDiSknkBqnj9rPken9qjp9rIjis+ohoMPAZkIjuhg9i4AQiAfsNgQBiTA4ljoUA-pAEYNoHoAYAnhIiQYjpKC4GDmrtIJDjADnrgrmnAEgXRsKmPuvKrvsOroMKzuzvVvWmxgBPTmoTABoWxDbiknlMlqluvH-vMJ7gej+lAfsObigGATIaeoQXYSgA4QWqvKdtBtQSgIgWYigMgfBqgbGOgZgYQSwXYsnqQSBl3gHsQd4QspaNIe4nIQEQXsikXhKpPtKs3lqq3t8oqqvnXk4GFhPo3r5hXrFs7PFikm2o4GYT3j7BYWCnMNYWsEwY4UPs4aNJ0Z4TXq7uUeqq2uGsalgdXqKsUUfNXNkRUSMV6g0RETUb8rbvqqMY4J0eYb3Agjkm0btjYUeGuE4e4gVkodsUMaXmWusZ0tAcwRMbco1hviVlvnbPUTcfYZuL1ifvfH2oEJYChhQskDAAAFIQDKjjabiBCG6aKzYzpv7zof4NBf7ArrYjCCge77GQHrDX7AD-FQBwAQAUJQCVB6HSDHGyZYm2oC54kElEkkniH9HERvQABWYJaApIoJGy4ezSQOOmYRseWBAhrBeBRYsR6eiGme0GgOKBfJYh+wn6pJkRJmRQH2whQiRQ3guw7xjO0gQp-20GapjKKBb0hpwAqihJ0ACg94Lwug3Aui1JlAtJ0A+OjCfJppLgaSHAIwDpZIOJNJFpxJMR8gohh0J0kOSpSefgWgmQEJ8pQiyQGQqQ5oKAQJfptATmPRgwnJXiChWyhaReWhyqOh6wxh8kYkiOTOWx8Cow6J-+KAgBju6Aa4aZ+JAZ9JqhZJ3RJxrh2JPpTpgZpJjJV60GrJVC2ZQR5BUeoRXS4Ru+EZwyIpX2wAaehBepJB0pwRspAopppIipepypqpm4GoGpEAWppJa5iR5oyRGZ7i45uZhWgxsxwxok9uBUDI9xdayqSUDez5uUr53JYAyxHeduIYjR3+zR2xNZGJABBxTZvZuJjpbZ2pTO5J+WPZVJCFrZdJyFnZimQeLKo57Jd5kULp2mL6Meop8gceR485ZKH25GK5u+F5+F0G2eN5smd5QqeZq8WRm+uRmq2qH5teiUpRP5lxgweRglpZ7s9OJMCACMYQ1QOYqwLZMAEARQnW3WIYIAAZVZHMtZVhmJ6FLZ-Z7ZOpqF-KlJJlSFg5sBcigwhFHJbJE5WmuKIOFFS51F6AtF72ZmlFy5BBTFCRLFgwG5k5IR+06BO5e5f2B5KoppxGmpOFzFJpR5cG4VqVcZwZOlRJVpqItpfh1lRJpFKys8nF9Ghej5fFrypMaSSMykKMx6wlWMuM4WJekWcM8lZM9VR4QFMabxyWf54kAFeloKBlexMFGF-p2FtlvuHYzZfZNlDJD55O4Mzx0qYaCxcRNFH5wWa+TWT54l+cA1Sx0laxW1RVxMeUb5sQo1fs411hlJs1Uc4BcM6FtlZxuy1V28bxl1UAy+tae1lcTxLWLxx11xf118tR8kbxpJQ1+UI1TR3cv+aJ0F9ZsFaC8F010AFluaVli1RJQ5nma1oNG1sN4hAN08Ux6+Mx318x8aOFzcfWp+A2gQXguJzESksAC6yAIA8QiQj+M2OQL+828JYCi6SJd1wwGWYm8wxgXZsmyGqGfhzIeFb0StBGkOvJ5FJi3AeADKj2jIlQIAetUAO5RtutXNemPllt+t4yVFNtJtXN5t+5SeTteA1tKRsmiAXNGR+ZgxhZdyxZawnwfWQAA).
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
