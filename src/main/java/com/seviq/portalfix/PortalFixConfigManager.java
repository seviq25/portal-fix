package com.seviq.portalfix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PortalFixConfigManager {
    public static PortalFixConfig CONFIG = new PortalFixConfig();
    private static final Logger LOGGER = LoggerFactory.getLogger("portal-fix");

    public static void load() {
        load(false);
    }
    public static boolean load(boolean isReload) {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("portal-fix-config.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                Gson gson = new Gson();
                PortalFixConfig parsed = gson.fromJson(json, PortalFixConfig.class);
                CONFIG = parsed;
                return true;
            } catch (JsonSyntaxException e) {
                LOGGER.error("portal-fix-config.json is malformed: {}", e.getMessage());
                if (!isReload) {
                    throw new RuntimeException("Invalid portal-fix-config.json - fix the file and restart the server.", e);
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try{
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(CONFIG);
                Files.writeString(configPath, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}