package ru.autoresell;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AutoResellClient implements ClientModInitializer {
    private static final int INTERVAL_TICKS = 60 * 20;
    private static KeyBinding toggleKey;
    private static boolean enabled = false;
    private static int ticksUntilNextCommand = INTERVAL_TICKS;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autoresell.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                ticksUntilNextCommand = INTERVAL_TICKS;
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("AutoResell: " + (enabled ? "ON" : "OFF")),
                            true
                    );
                }
            }

            if (!enabled || client.player == null || client.getNetworkHandler() == null) {
                return;
            }

            if (--ticksUntilNextCommand <= 0) {
                client.getNetworkHandler().sendChatCommand("ah resell");
                ticksUntilNextCommand = INTERVAL_TICKS;
            }
        });
    }
}
