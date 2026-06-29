package com.example;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

public class TemplateMod implements ModInitializer {
	public static final String MOD_ID = "template-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ModComponents.init();

		PayloadTypeRegistry.playC2S().register(LockTogglePayload.ID, LockTogglePayload.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(LockTogglePayload.ID, ((payload, context) -> {
			ScreenHandler handler = context.player().currentScreenHandler;

			if (handler.syncId != payload.syncId()){
				return;
			}
			if (payload.slotIndex() < 0 || payload.slotIndex() >= handler.slots.size()){
				return;
			}
			Slot slot = handler.slots.get(payload.slotIndex());
			ItemStack stack = slot.getStack();

			if (stack.isEmpty()){
				return;
			}
			boolean currentlyLocked = stack.getOrDefault(ModComponents.LOCKED,false);
			stack.set(ModComponents.LOCKED, !currentlyLocked);
		}));
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
