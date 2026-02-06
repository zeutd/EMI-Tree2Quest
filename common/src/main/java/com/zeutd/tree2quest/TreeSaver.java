package com.zeutd.tree2quest;

import dev.emi.emi.api.stack.FluidEmiStack;
import dev.emi.emi.api.stack.ItemEmiStack;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.bom.MaterialNode;
import dev.ftb.mods.ftblibrary.snbt.SNBT;
import dev.ftb.mods.ftblibrary.snbt.SNBTCompoundTag;
import dev.ftb.mods.ftbquests.quest.BaseQuestFile;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.ListTag;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class TreeSaver {
    public static void save(){

        Chapter chapter = new Chapter(ServerQuestFile.INSTANCE.newID(), ServerQuestFile.INSTANCE, ServerQuestFile.INSTANCE.getDefaultChapterGroup());
//        SNBTCompoundTag nbt = new SNBTCompoundTag();
//        nbt.putString("id", UUID.randomUUID().toString());
//        nbt.putString("group", "");
//        nbt.putInt("order_index", 0);
//        nbt.putString("filename", "generated");
//        ListTag questList = new ListTag();
//        eachInChildren(BoM.tree.goal, (parent, node, depth) -> {
//            SNBTCompoundTag questNBT = new SNBTCompoundTag();
//            quest.writeData(questNBT);
//            questNBT.putString("id", getNodeName(node));
//            questList.add(questNBT);
//        });
//
//        try {
//            Path folder = new File(Minecraft.getInstance().gameDirectory, "local/tree2quest/saved/").getCanonicalFile().toPath();
//            nbt.putInt("version", BaseQuestFile.VERSION);
//            SNBT.write(folder.resolve("data.snbt"), nbt);
//        } catch (IOException e) {
//            Tree2QuestMod.LOGGER.error(e);
//        }
    }
    public static void eachInChildren(MaterialNode root, int depth, TriConsumer<MaterialNode, MaterialNode, Integer> operation){
        if (root.children == null) return;
        root.children.forEach(node -> {
            operation.accept(root, node, depth + 1);
            eachInChildren(node, depth + 1, operation);
        });
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
}
