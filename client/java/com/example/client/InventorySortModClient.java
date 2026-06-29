package com.example.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class InventorySortModClient implements ClientModInitializer {


	public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("inventory-sort-mod", "main"));
	public static KeyBinding moveKeyBinding;
	public static KeyBinding lockKeyBinding;

	@Override
	public void onInitializeClient() {
		ModComponents.LOCKED.toString(); //init kostil TODO: normal init

		moveKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.inventory-sort-mod.move",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_G,
				CATEGORY
		));

		lockKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.inventory-sort-mod.lock",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_L,
				CATEGORY
		));

		ScreenEvents.AFTER_INIT.register(((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof GenericContainerScreen containerScreen){
				ScreenKeyboardEvents.afterKeyPress(screen).register((scr,keyInput) -> {
					if (keyInput.key() == KeyBindingHelper.getBoundKeyOf(moveKeyBinding).getCode() && (keyInput.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0){
						ScreenHandler handler = containerScreen.getScreenHandler();
						moveIntoInventory(client,handler);
					}
					if (keyInput.key() == KeyBindingHelper.getBoundKeyOf(moveKeyBinding).getCode() && (keyInput.modifiers() & GLFW.GLFW_MOD_SHIFT) == 0){
						ScreenHandler handler = containerScreen.getScreenHandler();
						moveIntoChest(client,handler);
					}
					if (keyInput.key() ==  KeyBindingHelper.getBoundKeyOf(lockKeyBinding).getCode()){
						toggleLock(containerScreen);
					}
				});
			}
		}));
	}

	private void moveIntoChest(MinecraftClient client, ScreenHandler handler){
		int totalSlots = handler.slots.size();
		int playerInvSize = client.player.getInventory().getMainStacks().size();
		int firstPlslot = totalSlots - playerInvSize;

		for (int i = totalSlots - 1; i >= firstPlslot;i--){
			Slot slot = handler.slots.get(i);
			ItemStack stack = slot.getStack();

			if (stack.isEmpty()){
				continue;
			}

			boolean locked = stack.getOrDefault(ModComponents.LOCKED,false);
			if (locked){
				continue;
			}


			client.interactionManager.clickSlot(
					handler.syncId,
					slot.id,
					0,
					SlotActionType.QUICK_MOVE,
					client.player
			);


		}
	}

	private void moveIntoInventory(MinecraftClient client, ScreenHandler handler){
		int totalSlots = handler.slots.size();
		int chestInvSize = totalSlots- client.player.getInventory().getMainStacks().size();

		for (int i = chestInvSize-1; i>= 0;i --){
			Slot slot = handler.slots.get(i);
			ItemStack stack = slot.getStack();

			if (stack.isEmpty()){
				continue;
			}

			client.interactionManager.clickSlot(
					handler.syncId,
					slot.id,
					0,
					SlotActionType.QUICK_MOVE,
					client.player
			);
		}

	}


	private void toggleLock(HandledScreen<?> screen){
		Slot hovered = screen.focusedSlot;

		if (hovered == null || hovered.getStack().isEmpty()){
			return;
		}
		ItemStack stack = hovered.getStack();
		boolean currentlyLocked = stack.getOrDefault(ModComponents.LOCKED,false);

		stack.set(ModComponents.LOCKED, !currentlyLocked);
		System.out.println("Item locked = " + !currentlyLocked);
	}
}