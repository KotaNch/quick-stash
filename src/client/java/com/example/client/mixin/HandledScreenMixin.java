package com.example.client.mixin;

import com.example.ModComponents;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    private static final Identifier BG_TEXTURE = Identifier.of(
            "inventory-sort-mod",  "gui/star_bg");
    private static final Identifier ICON_TEXTURE = Identifier.of(
            "inventory-sort-mod", "gui/lock_icon");

    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void drawLockBackground(DrawContext context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!isLocked(slot)) return;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BG_TEXTURE, slot.x-1, slot.y-1, 18, 18, 0x80FFFFFF);
    }

    @Inject(method = "drawSlot", at = @At("TAIL"))
    private void drawLockIcon(DrawContext context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
        if (!isLocked(slot)) return;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ICON_TEXTURE, slot.x-10, slot.y-10, 18, 16);
    }

    private boolean isLocked(Slot slot) {
        if (slot.getStack().isEmpty()) return false;
        return slot.getStack().getOrDefault(ModComponents.LOCKED, false);
    }
}