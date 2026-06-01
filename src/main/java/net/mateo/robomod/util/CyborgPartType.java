package net.mateo.robomod.util;

public enum CyborgPartType {

    HEAD("head"),
    BODY("body"),
    RIGHT_ARM("right_arm"),
    LEFT_ARM("left_arm"),
    RIGHT_LEG("right_leg"),
    LEFT_LEG("left_leg");

    final String typeName;

    CyborgPartType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public static void forEach(CyborgPartTypeRunnable runnable) {
        for (CyborgPartType partType : CyborgPartType.values()) {
            runnable.run(partType);
        }
    }

    public interface CyborgPartTypeRunnable {
        void run(CyborgPartType partType);
    }
}
