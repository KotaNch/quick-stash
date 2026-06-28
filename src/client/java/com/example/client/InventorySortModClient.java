package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class InventorySortModClient implements ClientModInitializer {


	public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("inventory-sort-mod", "main"));
	public static KeyBinding sortKeyBinding;

	@Override
	public void onInitializeClient() {
		sortKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.inventory-sort-mod.sort",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				CATEGORY
		));

		ScreenEvents.AFTER_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof GenericContainerScreen containerScreen){
				ScreenKeyboardEvents.afterKeyPress(screen).register((scr,keyInput) -> {
					if (keyInput.key() == sortKeyBinding.getDefaultKey().getCode()){
						ScreenHandler handler = containerScreen.getScreenHandler();
						System.out.println("Chest opened, number of slots: " + handler.slots.size());
						sortIntoChest(client,handler);
					}
				});
			}
		}));
	}

	private void sortIntoChest(MinecraftClient client, ScreenHandler handler){
		int totalSlots = handler.slots.size();
		int invSize = client.player.getInventory().getMainStacks().size();

		int firstPlslot = totalSlots - invSize;

		for (int i = totalSlots - 1; i >= firstPlslot;i--){
			Slot slot = handler.slots.get(i);

			if (!slot.getStack().isEmpty()){
				client.interactionManager.clickSlot(handler.syncId,slot.id,0, SlotActionType.QUICK_MOVE, client.player);
			}
		}
	}
}