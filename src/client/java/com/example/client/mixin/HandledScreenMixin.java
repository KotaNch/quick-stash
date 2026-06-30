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

    private static final Identifier LOCK_OVERLAY = Identifier.of("inventory-sort-mod", "gui/lock_overlay");
    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void drawLockOverlay(DrawContext context, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {

        if (slot.getStack().isEmpty()) {
            return;
        }

        boolean locked = slot.getStack().getOrDefault(ModComponents.LOCKED, false);
        if (!locked) {
            return;
        }

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, LOCK_OVERLAY, slot.x-1, slot.y-1, 18, 18, 0x80FFFFFF);
    }
}