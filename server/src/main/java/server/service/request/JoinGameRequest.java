package server.service.request;

import chess.ChessGame;

public record JoinGameRequest(String username, ChessGame.TeamColor playerColor, int gameID){
}
