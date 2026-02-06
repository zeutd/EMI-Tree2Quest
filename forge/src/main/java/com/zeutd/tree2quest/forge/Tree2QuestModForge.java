package com.zeutd.tree2quest.forge;

import net.minecraftforge.fml.common.Mod;

import com.zeutd.tree2quest.Tree2QuestMod;

@Mod(Tree2QuestMod.MOD_ID)
public final class Tree2QuestModForge {
    public Tree2QuestModForge() {
        // Run our common setup.
        Tree2QuestMod.init();
    }
}
