package server.service.request;

import chess.ChessGame;

public record JoinGameRequestBody(ChessGame.TeamColor playerColor, int gameID) {
}
