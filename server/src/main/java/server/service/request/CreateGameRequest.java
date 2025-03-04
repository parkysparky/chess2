package server.service.request;

public record CreateGameRequest(String authToken, String gameName) {
}
