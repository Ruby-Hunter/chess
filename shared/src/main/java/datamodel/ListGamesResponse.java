package datamodel;

import java.util.HashSet;

public record ListGamesResponse(HashSet<GameData> games) {
}
