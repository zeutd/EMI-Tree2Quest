package com.zeutd.tree2quest.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.zeutd.tree2quest.TreeSaver;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.bom.BoM;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.screen.BoMScreen;
import dev.emi.emi.screen.tooltip.EmiTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BoMScreen.class, remap = false)
public abstract class BoMScreenMixin {
    @Shadow private double offX;
    @Shadow private double offY;

    //@Shadow public abstract BoMScreen.Hover getHoveredStack(int mx, int my);

    @Unique
    private Bounds emi_tree2quest$export = new Bounds(16, 0, 16, 16);
    @Inject(method = "recalculateTree", at = @At("HEAD"))
    private void recaculateTreeMixin(CallbackInfo ci){
        BoMScreen self = ((BoMScreen) (Object) this);
        emi_tree2quest$export = new Bounds(self.width - 18, self.height - 18, 16, 16);
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Ldev/emi/emi/runtime/EmiDrawContext;drawTexture(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", ordinal = 0))
    private void renderMixin(GuiGraphics raw, int mouseX, int mouseY, float delta, CallbackInfo ci, @Local(ordinal = 0) EmiDrawContext context){
        BoMScreen self = ((BoMScreen) (Object) this);
        if (BoM.tree != null) {
            if (emi_tree2quest$export.contains(mouseX, mouseY)) {
                List<ClientTooltipComponent> list = EmiTooltip.splitTranslate("tooltip.emi_tree2quest.export");
                EmiRenderHelper.drawTooltip(self, context, list, mouseX, mouseY);
                context.setColor(0.5F, 0.6F, 1.0F, 1.0F);
            }
            context.drawTexture(EmiRenderHelper.WIDGETS, this.emi_tree2quest$export.x(), this.emi_tree2quest$export.y(), 0, 200, this.emi_tree2quest$export.width(), this.emi_tree2quest$export.height());
            context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Ldev/emi/emi/screen/BoMScreen;getHoveredStack(II)Ldev/emi/emi/screen/BoMScreen$Hover;", shift = At.Shift.AFTER))
    private void mouseClickedMixin(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir){
        BoMScreen self = ((BoMScreen) (Object) this);
        float scale = self.getScale();
        int mx = (int)((mouseX - (double)(self.width / 2)) / (double)scale - offX);
        int my = (int)((mouseY - (double)(self.height / 2)) / (double)scale - offY);
        if (emi_tree2quest$export.contains(mx, my)) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            TreeSaver.save();
        }
    }
}
