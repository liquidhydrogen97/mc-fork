package com.liquidhydrogen.mcfork;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the mc-fork NeoForge mod. The body is intentionally minimal: this
 * skeleton contract delivers build infrastructure only. Subsequent contracts hang
 * registrations (density functions, features, biome modifiers) off the mod event bus
 * passed into the constructor.
 */
@Mod(McForkMod.MOD_ID)
public class McForkMod {

    public static final String MOD_ID = "mc_fork";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public McForkMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("[{}] mod constructor invoked; mc-fork skeleton loaded", MOD_ID);
    }
}
