package server.result;

import java.util.HashSet;

public record ListGamesResult(HashSet<model.GameData> games) {
}
