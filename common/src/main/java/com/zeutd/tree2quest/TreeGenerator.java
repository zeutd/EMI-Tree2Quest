package com.zeutd.tree2quest;

import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.ftb.mods.ftblibrary.config.Tristate;
import dev.ftb.mods.ftbquests.net.CreateObjectResponseMessage;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

public class TreeGenerator {
    public static Map<MaterialNode, Quest> questMap = new HashMap<>();
    public static int generate(MinecraftServer server){
        try {
            Chapter chapter = new Chapter(ServerQuestFile.INSTANCE.newID(), ServerQuestFile.INSTANCE, ServerQuestFile.INSTANCE.getDefaultChapterGroup());
            chapter.onCreated();

            chapter.setRawTitle("Generated chapter of all items in EMI's recipe tree");
            chapter.setRawIcon(new ItemStack(Items.OAK_SAPLING));
            chapter.setDefaultQuestShape("circle");

            new CreateObjectResponseMessage(chapter, null).sendToAll(server);

            Quest rootQuest = new Quest(chapter.file.newID(), chapter);
            rootQuest.onCreated();
            questMap.put(BoM.tree.goal, rootQuest);
            new CreateObjectResponseMessage(rootQuest, null).sendToAll(server);
            ItemTask rootTask = new ItemTask(chapter.file.newID(), rootQuest);
            rootTask.onCreated();
            rootTask.setStackAndCount(getNodeItem(BoM.tree.goal), 1);

            CompoundTag rootExtra = new CompoundTag();
            rootExtra.putString("type", rootTask.getType().getTypeForNBT());
            new CreateObjectResponseMessage(rootTask, rootExtra).sendToAll(server);

            eachInChildren(BoM.tree.goal, (parent, node, depth, index) -> {
                Quest quest = new Quest(chapter.file.newID(), chapter);
                quest.onCreated();
                quest.setY(depth * 2);
                quest.setX(index * 2);
                questMap.put(node, quest);
                if (questMap.containsKey(parent)) {
                    quest.addDependency(questMap.get(parent));
                }
                new CreateObjectResponseMessage(quest, null).sendToAll(server);

                ItemTask task = new ItemTask(chapter.file.newID(), quest);
                task.onCreated();
                task.setStackAndCount(getNodeItem(node), 1);

                CompoundTag extra = new CompoundTag();
                extra.putString("type", task.getType().getTypeForNBT());
                new CreateObjectResponseMessage(task, extra).sendToAll(server);

            });
            ServerQuestFile.INSTANCE.markDirty();
            ServerQuestFile.INSTANCE.saveNow();
        } catch(NullPointerException e){
            return 0;
        }
        return 1;
    }
    public static void eachInChildren(MaterialNode root, int depth, QuadConsumer<MaterialNode, MaterialNode, Integer, Integer> operation){
        if (root.children == null) return;
        for (int i = 0; i < root.children.size(); i++) {
            MaterialNode node = root.children.get(i);
            operation.accept(root, node, depth + 1, i);
            eachInChildren(node, depth + 1, operation);
        }

    }
    public static void eachInChildren(MaterialNode root, QuadConsumer<MaterialNode, MaterialNode, Integer, Integer> operation){
        eachInChildren(root, 0, operation);
    }
    public static String getNodeName(MaterialNode node){
        if (node.ingredient instanceof ItemEmiStack itemEmiStack) {
            return itemEmiStack.getId().getPath();
        }
        if (node.ingredient instanceof FluidEmiStack fluidEmiStack) {
            return fluidEmiStack.getId().getPath();
        }
        return "";
    }

    public static ItemStack getNodeItem(MaterialNode node){
        if (node.ingredient instanceof ItemEmiStack itemEmiStack) {
            return itemEmiStack.getItemStack();
        }
        if (node.ingredient instanceof FluidEmiStack fluidEmiStack) {
            return ((Fluid) fluidEmiStack.getKey()).getBucket().getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }
}
