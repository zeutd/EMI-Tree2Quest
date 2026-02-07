package com.zeutd.tree2quest;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.ftb.mods.ftbquests.command.FTBQuestsCommands;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Tree2QuestMod {
    public static final String MOD_ID = "emi_tree2quest";
    public static final Logger LOGGER = LogManager.getLogger("Tree2Quest");

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx, Commands.CommandSelection selection) {
        Tree2QuestCommands.register(dispatcher);
    }

    public static void init() {
        CommandRegistrationEvent.EVENT.register(Tree2QuestMod::registerCommands);
    }
}
