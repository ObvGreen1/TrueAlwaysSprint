package me.obvgreen.client.sprint;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModConfig {
	public static final KeyBinding.Category KEYBIND_CATEGORY =
			KeyBinding.Category.create(Identifier.of("truealwayssprint", "keybindings"));

	public static KeyBinding toggleKey;

	private static boolean enabled = true;
	private static boolean toggleKeyEnabled = true;
	private static boolean notificationsEnabled = true;
	private static int notificationDurationSeconds = 3;
	private static boolean notificationSoundEnabled = false;

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean value) {
		if (enabled == value) {
			return;
		}
		enabled = value;
		SprintToast.show(value);
	}

	public static boolean isToggleKeyEnabled() {
		return toggleKeyEnabled;
	}

	public static boolean isNotificationsEnabled() {
		return notificationsEnabled;
	}

	public static int getNotificationDurationSeconds() {
		return notificationDurationSeconds;
	}

	public static boolean isNotificationSoundEnabled() {
		return notificationSoundEnabled;
	}

	public static Screen openConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(Text.translatable("config.truealwayssprint.title"))
				.setSavingRunnable(() -> {
				});

		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.truealwayssprint.category.general"));
		general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.truealwayssprint.option.enabled"), enabled)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("config.truealwayssprint.option.enabled.tooltip"))
				.setSaveConsumer(ModConfig::setEnabled)
				.build());

		general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.truealwayssprint.option.toggleKey.enabled"), toggleKeyEnabled)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("config.truealwayssprint.option.toggleKey.enabled.tooltip"))
				.setSaveConsumer(value -> toggleKeyEnabled = value)
				.build());

		if (toggleKey != null) {
			general.addEntry(entryBuilder.fillKeybindingField(Text.translatable("config.truealwayssprint.option.toggleKey.binding"), toggleKey)
					.setTooltip(Text.translatable("config.truealwayssprint.option.toggleKey.binding.tooltip"))
					.build());
		} else {
			general.addEntry(entryBuilder.startKeyCodeField(Text.translatable("config.truealwayssprint.option.toggleKey.binding"), InputUtil.UNKNOWN_KEY)
					.build());
		}

		general.addEntry(entryBuilder.startTextDescription(Text.translatable("config.truealwayssprint.option.toggleKey.description"))
				.build());

		ConfigCategory notifications = builder.getOrCreateCategory(Text.translatable("config.truealwayssprint.category.notifications"));
		notifications.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.truealwayssprint.option.notifications"), notificationsEnabled)
				.setDefaultValue(true)
				.setTooltip(Text.translatable("config.truealwayssprint.option.notifications.tooltip"))
				.setSaveConsumer(value -> notificationsEnabled = value)
				.build());

		notifications.addEntry(entryBuilder.startIntSlider(Text.translatable("config.truealwayssprint.option.duration"), notificationDurationSeconds, 1, 10)
				.setDefaultValue(3)
				.setTooltip(Text.translatable("config.truealwayssprint.option.duration.tooltip"))
				.setTextGetter(value -> Text.translatable("config.truealwayssprint.option.duration.value", value))
				.setSaveConsumer(value -> notificationDurationSeconds = value)
				.build());

		notifications.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.truealwayssprint.option.sound"), notificationSoundEnabled)
				.setDefaultValue(false)
				.setTooltip(Text.translatable("config.truealwayssprint.option.sound.tooltip"))
				.setSaveConsumer(value -> notificationSoundEnabled = value)
				.build());

		return builder.build();
	}
}