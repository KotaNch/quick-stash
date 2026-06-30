package com.example.client;

import com.example.LockTogglePayload;
import com.example.ModComponents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InventorySortModClient implements ClientModInitializer {


	public static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("inventory-sort-mod", "main"));
	public static KeyBinding moveKeyBinding;
	public static KeyBinding lockKeyBinding;

	@Override
	public void onInitializeClient() {
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

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

			if (screen instanceof HandledScreen<?> handledScreen) {

				ScreenKeyboardEvents.afterKeyPress(screen).register((scr, keyInput) -> {

					if (keyInput.key() == KeyBindingHelper.getBoundKeyOf(lockKeyBinding).getCode()) {
						ScreenHandler handler = handledScreen.getScreenHandler();
						toggleLockIfPlayerSlot(handledScreen, handler);
					}

					boolean isAllowedContainer = screen instanceof GenericContainerScreen
							|| screen instanceof ShulkerBoxScreen
							|| screen instanceof HopperScreen
							|| screen instanceof Generic3x3ContainerScreen;

					if (isAllowedContainer) {
						ScreenHandler handler = handledScreen.getScreenHandler();

						if (keyInput.key() == KeyBindingHelper.getBoundKeyOf(moveKeyBinding).getCode()
								&& (keyInput.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0) {
							moveIntoInventory(client, handler);
						}
						if (keyInput.key() == KeyBindingHelper.getBoundKeyOf(moveKeyBinding).getCode()
								&& (keyInput.modifiers() & GLFW.GLFW_MOD_SHIFT) == 0) {
							moveIntoChest(client, handler);
						}
					}
				});
			}
		});
	}

	private void moveIntoChest(MinecraftClient client, ScreenHandler handler){
		int totalSlots = handler.slots.size();
		int playerInvSize = client.player.getInventory().getMainStacks().size();
		int firstPlslot = totalSlots - playerInvSize;

		List<Slot> playerSlots = new ArrayList<>();
		for (int i = firstPlslot; i < totalSlots; i ++){
			playerSlots.add(handler.slots.get(i));
		}

		playerSlots.sort((a,b) ->{
			ItemStack stackA = a.getStack();
			ItemStack stackB = b.getStack();

			if (stackA.isEmpty() && stackB.isEmpty()){
				return 0;
			}
			if (stackA.isEmpty()){
				return  1;
			}
			if (stackB.isEmpty()){
				return  -1;
			}

			String nameA = stackA.getItemName().toString();
			String nameB = stackB.getItemName().toString();
			return nameA.compareTo(nameB);
		});

		for (Slot slot:playerSlots){
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

	private void toggleLockIfPlayerSlot(HandledScreen<?> screen, ScreenHandler handler){
		Slot hovered = screen.focusedSlot;

		if (hovered == null || hovered.getStack().isEmpty()){
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		if (!(hovered.inventory == client.player.getInventory())){
			return;
		}

		ClientPlayNetworking.send(new LockTogglePayload(handler.syncId, hovered.id));
	}
}