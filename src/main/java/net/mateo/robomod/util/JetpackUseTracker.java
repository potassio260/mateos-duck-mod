package net.mateo.robomod.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JetpackUseTracker {
    private static final Map<UUID, Boolean> playerUseJetpack = new HashMap<>();

    public static void setUseJetpack(UUID playerId, boolean useJetpack) {
        playerUseJetpack.put(playerId, useJetpack);
    }

    public static boolean usesJetpack(UUID playerId) {
        Boolean usesJetpack = playerUseJetpack.get(playerId);
        if (usesJetpack == null) return false;
        return usesJetpack;
    }

    public static void removePlayer(UUID playerId) {
        playerUseJetpack.remove(playerId);
    }
}
