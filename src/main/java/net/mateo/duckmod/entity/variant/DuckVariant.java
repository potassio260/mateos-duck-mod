package net.mateo.duckmod.entity.variant;

public enum DuckVariant { // Duck variants
    MALLARD(0),
    CALL(1),
    WOOD(2),
    CRESTED(3),
    RUNNER(4);

    private final int id;

    DuckVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public static DuckVariant byId(int id) {
        for (DuckVariant variant : values()) {
            if (variant.id == id) {
                return variant;
            }
        }
        return MALLARD;
    }
}
