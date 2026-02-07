package com.zeutd.tree2quest;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.ftb.mods.ftbquests.integration.PermissionsHelper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class Tree2QuestCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        //noinspection ConstantValue
        dispatcher.register(Commands.literal("tree2quest")
                .requires(s -> s.getServer() != null && s.getServer().isSingleplayer() || hasEditorPermission(s))
                .then(Commands.literal("generate_tree_chapter")
                        .executes(context -> {
                            int result = TreeGenerator.generate(context.getSource().getServer());
                            if (result == 1) context.getSource().sendSuccess(() -> Component.literal("Done!"), false);
                            else if (result == 0) context.getSource().sendFailure(Component.literal("You doesn't have an recipe tree!"));
                            return result;
                        })
                )
        );
    }

    private static boolean hasEditorPermission(CommandSourceStack stack) {
        //noinspection DataFlowIssue
        return stack.hasPermission(2)
                || stack.isPlayer() && PermissionsHelper.hasEditorPermission(stack.getPlayer(), false);
    }
}
