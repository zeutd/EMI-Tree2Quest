package com.zeutd.tree2quest.fabric;

import net.fabricmc.api.ModInitializer;

import com.zeutd.tree2quest.Tree2QuestMod;

public final class Tree2QuestModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Tree2QuestMod.init();
    }
}
