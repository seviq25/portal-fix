package com.seviq.portalfix;

import net.minecraft.server.level.ServerLevel;

public class TrackedEntity {
    public final ServerLevel level;
    public final long expiry;

    public TrackedEntity(ServerLevel level, long expiry) {
        this.level = level;
        this.expiry = expiry;
    }
}
