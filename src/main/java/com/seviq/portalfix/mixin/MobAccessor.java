package com.seviq.portalfix.mixin;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)

public interface MobAccessor {
    @Accessor("persistenceRequired")
    void portalfix$setPersistenceRequired(boolean value);
}
