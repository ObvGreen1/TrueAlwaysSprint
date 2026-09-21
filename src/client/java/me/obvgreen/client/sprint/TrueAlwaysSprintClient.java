package me.obvgreen.client.sprint;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class TrueAlwaysSprintClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModConfig.toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.truealwayssprint.toggle",
				InputUtil.Type.KEYSYM,
				InputUtil.UNKNOWN_KEY.getCode(),
				ModConfig.KEYBIND_CATEGORY
		));
		ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
	}

	private void onClientTick(MinecraftClient client) {
		handleToggleKey();
		if (client.player == null || client.currentScreen != null) {
			return;
		}
		if (ModConfig.isEnabled()) {
			client.options.sprintKey.setPressed(true);
		}
	}

	private void handleToggleKey() {
		if (!ModConfig.isToggleKeyEnabled()) {
			return;
		}
		KeyBinding key = ModConfig.toggleKey;
		if (key == null) {
			return;
		}
		while (key.wasPressed()) {
			ModConfig.setEnabled(!ModConfig.isEnabled());
		}
	}
}