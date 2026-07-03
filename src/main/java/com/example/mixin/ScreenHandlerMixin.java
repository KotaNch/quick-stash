package com.example.mixin;


import com.example.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Inject(method = "onSlotClick", at = @At("TAIL"))
    private void clearLockOutsideInventory(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ScreenHandler self = (ScreenHandler) (Object) this;

        int totalSlots = self.slots.size();
        //? if >=1.21.5 {
        int playerInvSize = player.getInventory().getMainStacks().size();
        //?} else {
        /*int playerInvSize = player.getInventory().main.size();
        *///?}
        int firstPlayerSlot = totalSlots - playerInvSize;

        for (int i = 0; i < firstPlayerSlot; i++){
            Slot slot = self.slots.get(i);
            ItemStack stack = slot.getStack();

            if (stack.isEmpty()){
                continue;
            }
            if (slot.inventory == player.getInventory()){
                continue;
            }
            if (stack.contains(ModComponents.LOCKED)){
                stack.remove(ModComponents.LOCKED);
            }
        }
    }
}
