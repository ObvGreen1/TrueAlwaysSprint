package me.obvgreen.client.sprint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SprintToast implements Toast {
	private static final Object TYPE = new Object();
	private static SprintToast current;

	private static final int WIDTH = 190;
	private static final int HEIGHT = 42;

	private static final int COLOR_ENABLED = 0xFF34D399;
	private static final int COLOR_ENABLED_SOFT = 0xFF86EFAC;
	private static final int COLOR_DISABLED = 0xFFF87171;
	private static final int COLOR_DISABLED_SOFT = 0xFFFCA5A5;
	private static final int COLOR_BG_TOP = 0xEB222630;
	private static final int COLOR_BG_BOTTOM = 0xEB141822;
	private static final int COLOR_BORDER = 0xFF3A4356;
	private static final int COLOR_TITLE = 0xFFFFFFFF;
	private static final int COLOR_TRACK = 0x33FFFFFF;

	private boolean enabled;
	private boolean justUpdated = true;
	private long startTime;
	private long durationMs = 3000L;
	private Toast.Visibility visibility = Toast.Visibility.SHOW;

	private SprintToast(boolean enabled) {
		this.enabled = enabled;
	}

	public static void show(boolean enabled) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!ModConfig.isNotificationsEnabled()) {
			return;
		}
		if (current == null) {
			current = new SprintToast(enabled);
			client.getToastManager().add(current);
		} else {
			current.enabled = enabled;
			current.justUpdated = true;
			current.visibility = Toast.Visibility.SHOW;
		}
		if (ModConfig.isNotificationSoundEnabled()) {
			SoundEvent sound = enabled ? SoundEvents.UI_TOAST_IN : SoundEvents.UI_TOAST_OUT;
			client.getSoundManager().play(PositionedSoundInstance.master(sound, 1.0f));
		}
	}

	@Override
	public Object getType() {
		return TYPE;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public SoundEvent getSoundEvent() {
		if (!ModConfig.isNotificationSoundEnabled()) {
			return null;
		}
		return enabled ? SoundEvents.UI_TOAST_IN : SoundEvents.UI_TOAST_OUT;
	}

	@Override
	public Toast.Visibility getVisibility() {
		return visibility;
	}

	@Override
	public void update(ToastManager manager, long time) {
		if (justUpdated) {
			startTime = time;
			durationMs = (long) (ModConfig.getNotificationDurationSeconds() * 1000L * manager.getNotificationDisplayTimeMultiplier());
			visibility = Toast.Visibility.SHOW;
			justUpdated = false;
		}
		long elapsed = time - startTime;
		visibility = elapsed < durationMs ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
	}

	@Override
	public void onFinishedRendering() {
		if (current == this) {
			current = null;
		}
	}

	@Override
	public void draw(DrawContext context, TextRenderer textRenderer, long time) {
		long elapsed = Math.max(0L, time - startTime);
		float progress = 1.0f;
		float fade = 1.0f;
		if (durationMs > 0L) {
			progress = Math.max(0.0f, Math.min(1.0f, 1.0f - (float) elapsed / (float) durationMs));
			long remaining = durationMs - elapsed;
			if (remaining < 350L) {
				fade = Math.max(0.0f, (float) remaining / 350.0f);
			}
		}
		if (fade <= 0.0f) {
			return;
		}

		boolean active = enabled;
		int accent = active ? COLOR_ENABLED : COLOR_DISABLED;
		int accentSoft = active ? COLOR_ENABLED_SOFT : COLOR_DISABLED_SOFT;

		context.fillGradient(0, 0, WIDTH, HEIGHT, fadeColor(COLOR_BG_TOP, fade), fadeColor(COLOR_BG_BOTTOM, fade));
		context.fill(0, 0, WIDTH, 1, fadeColor(COLOR_BORDER, fade));
		context.fill(0, HEIGHT - 1, WIDTH, HEIGHT, fadeColor(COLOR_BORDER, fade));
		context.fill(0, 1, 1, HEIGHT - 1, fadeColor(COLOR_BORDER, fade));
		context.fill(WIDTH - 1, 1, WIDTH, HEIGHT - 1, fadeColor(COLOR_BORDER, fade));
		context.fill(0, 1, WIDTH, 2, withAlpha(0xFFFFFF, 0x24, fade));

		int pulse = (int) (0x2E + 0x1E * (0.5 + 0.5 * Math.sin(elapsed / 850.0 * Math.PI * 2.0)));
		context.fill(0, 0, 5, HEIGHT, fadeColor(accent, fade));
		context.fill(5, 0, 9, HEIGHT, (pulse << 24) | (accent & 0x00FFFFFF));

		int iconCX = 25;
		int iconCY = HEIGHT / 2;
		int iconSize = 22;
		float popT = Math.min((float) elapsed / 260.0f, 1.0f);
		float scale = 0.55f + 0.45f * easeOutBack(popT);
		context.getMatrices().pushMatrix();
		context.getMatrices().translate(iconCX, iconCY);
		context.getMatrices().scale(scale, scale);
		context.getMatrices().translate(-iconCX, -iconCY);
		int ix = iconCX - iconSize / 2;
		int iy = iconCY - iconSize / 2;
		context.fill(ix, iy, ix + iconSize, iy + iconSize, fadeColor(accent, fade));
		context.fill(ix + 5, iy + 5, ix + 11, iy + 8, fadeColor(0xFF111827, fade));
		context.fill(ix + 5, iy + 9, ix + 16, iy + 12, fadeColor(0xFF111827, fade));
		context.fill(ix + 5, iy + 13, ix + 8, iy + 16, fadeColor(0xFF111827, fade));
		context.getMatrices().popMatrix();

		MutableText title = Text.translatable("toast.truealwayssprint.title").formatted(Formatting.BOLD);
		context.drawTextWithShadow(textRenderer, title, 46, 7, fadeColor(COLOR_TITLE, fade));

		MutableText subtitle = Text.translatable(active ? "toast.truealwayssprint.enabled" : "toast.truealwayssprint.disabled");
		if (ModConfig.toggleKey != null && !ModConfig.toggleKey.isUnbound()) {
			subtitle.append(Text.literal("  ·  ")).append(ModConfig.toggleKey.getBoundKeyLocalizedText());
		}
		context.drawTextWithShadow(textRenderer, subtitle, 46, 20, fadeColor(accentSoft, fade));

		int barY = HEIGHT - 6;
		int barW = WIDTH - 16;
		context.fill(8, barY, 8 + barW, barY + 2, fadeColor(COLOR_TRACK, fade));
		if (progress > 0.0f) {
			context.fill(8, barY, 8 + Math.round(barW * progress), barY + 2, fadeColor(accent, fade));
		}
	}

	private static float easeOutBack(float t) {
		float c1 = 1.70158f;
		float c3 = c1 + 1.0f;
		float u = t - 1.0f;
		return 1.0f + c3 * u * u * u + c1 * u * u;
	}

	private static int fadeColor(int argb, float factor) {
		return withAlpha(argb, (argb >>> 24) & 0xFF, factor);
	}

	private static int withAlpha(int argb, int alpha, float factor) {
		return ((int) (alpha * factor) << 24) | (argb & 0x00FFFFFF);
	}
}