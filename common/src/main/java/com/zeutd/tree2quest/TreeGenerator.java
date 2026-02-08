package com.zeutd.tree2quest;

import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.ftb.mods.ftbquests.net.CreateObjectResponseMessage;
import dev.ftb.mods.ftbquests.net.EditObjectResponseMessage;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;

import java.util.*;

public class TreeGenerator {
    public static Map<MaterialNode, Quest> questMap = new HashMap<>();
    public static List<Integer> indices = new ArrayList<>();
    public static int generate(MinecraftServer server){
        try {
            indices.clear();
            if (BoM.tree == null) return 0;
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

            eachInChildren(BoM.tree.goal, (parent, node, depth) -> {
                while (indices.size() < depth + 1){
                     indices.add(0);
                }
                if (getNodeItem(node).isEmpty()) return;
                Quest quest = new Quest(chapter.file.newID(), chapter);
                quest.onCreated();
                quest.setY(depth * 2);
                quest.setX(indices.get(depth) * 2);
                questMap.put(node, quest);
                new CreateObjectResponseMessage(quest, null).sendToAll(server);
                if (questMap.containsKey(parent)) {
                    questMap.get(parent).addDependency(quest);
                }
                new EditObjectResponseMessage(questMap.get(parent)).sendToAll(server);

                ItemTask task = new ItemTask(chapter.file.newID(), quest);
                task.onCreated();
                task.setStackAndCount(getNodeItem(node), 1);

                CompoundTag extra = new CompoundTag();
                extra.putString("type", task.getType().getTypeForNBT());
                new CreateObjectResponseMessage(task, extra).sendToAll(server);
                indices.set(depth, indices.get(depth) + 1);
            });
            ServerQuestFile.INSTANCE.markDirty();
            ServerQuestFile.INSTANCE.saveNow();
            return 1;
        } catch (Exception e){
            Tree2QuestMod.LOGGER.error(e);
        }
        return 0;
    }
    //size:2
    //depth:1
    //0 1
    public static void eachInChildren(MaterialNode root, int depth, TriConsumer<MaterialNode, MaterialNode, Integer> operation){
        if (root.children == null) return;
        for (int i = 0; i < root.children.size(); i++) {
            MaterialNode node = root.children.get(i);
            operation.accept(root, node, depth + 1);
            eachInChildren(node, depth + 1, operation);
        }
    }
    public static void eachInChildren(MaterialNode root, TriConsumer<MaterialNode, MaterialNode, Integer> operation){
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
