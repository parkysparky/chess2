package server.result;

import model.GameInfo;

import java.util.HashSet;

public record ListGamesResult(HashSet<GameInfo> games) {
}
