package com.seviq.portalfix;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.seviq.portalfix.mixin.MobAccessor;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class PortalFix implements ModInitializer {
	public static final String MOD_ID = "portal-fix";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<UUID, TrackedEntity> TRACKED = new HashMap<>();
	private int tickCounter = 0;

	@Override
	public void onInitialize() {
		PortalFixConfigManager.load();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("portalfix")
					.then(LiteralArgumentBuilder.<CommandSourceStack>literal("reload")
							.requires(source -> Permissions.check(source, "portalfix.reload", 2))
							.executes(context -> {
								boolean success = PortalFixConfigManager.load(true);
								if (success) {
									context.getSource().sendSuccess(() -> Component.literal("We will let there be Portal Farms! (config reloaded)."), true);
									return 1;
								} else {
									context.getSource().sendFailure(Component.literal("We didn't let there be farms! - portal-fix-config.json is malformed. Check server console for details. The old config is still active."));
									return 0;
								}
							})
					)
			);
		});
		ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register((originalEntity, newEntity, origin, destination) -> {
			if (!(newEntity instanceof Mob mob)) return;
			if (mob.isPersistenceRequired()) return;
			if (!isWhitelisted(mob)) return;

			mob.setPersistenceRequired();
			long expiry = destination.getGameTime() + (PortalFixConfigManager.CONFIG.persistenceDurationSeconds * 20L);
			mob.setAttached(PortalFixAttachments.PORTAL_PERSISTENCE_EXPIRY, expiry);

			TRACKED.put(mob.getUUID(), new TrackedEntity(destination, expiry));
		});

		ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
			if (!(entity instanceof Mob mob)) return;
			if (!mob.hasAttached(PortalFixAttachments.PORTAL_PERSISTENCE_EXPIRY)) return;

			long expiry = mob.getAttachedOrElse(PortalFixAttachments.PORTAL_PERSISTENCE_EXPIRY, -1L);
			TRACKED.put(mob.getUUID(), new TrackedEntity(level, expiry));

		});
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			tickCounter++;
			if (tickCounter < 20) return;
			tickCounter = 0;

			TRACKED.entrySet().removeIf(entry -> {
				UUID uuid = entry.getKey();
				TrackedEntity tracked = entry.getValue();

				if (tracked.level.getGameTime() < tracked.expiry) return false;

				Entity entity = tracked.level.getEntity(uuid);
				if (entity == null) return true;
				if (entity.hasCustomName()) return true;

				if (entity instanceof Mob mob) {
					((MobAccessor) mob).portalfix$setPersistenceRequired(false);
				}
				return true;
			});

		});
	}
	private boolean isWhitelisted(Mob mob) {
		EntityType<?> type = mob.getType();
		for (String entry : PortalFixConfigManager.CONFIG.whitelist) {
			if (entry.startsWith("#")) {
				Identifier tagId = Identifier.parse(entry.substring(1));
				TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, tagId);
				if (mob.is(tag)) return true;
			} else {
				Identifier id = Identifier.parse(entry);
				if (id.equals(BuiltInRegistries.ENTITY_TYPE.getKey(type))) return true;
			}
		}
        return false;
    }
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
