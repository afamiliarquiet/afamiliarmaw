package io.github.afamiliarquiet;

import io.github.afamiliarquiet.network.BreathPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import static io.github.afamiliarquiet.MawKeybinds.breathKey;

public class AFamiliarMawClient implements ClientModInitializer {



	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		MawKeybinds.register();

		BreathHandler breathHandler = BreathHandler.init();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (ClientPlayNetworking.canSend(BreathPayload.ID) && breathKey.wasPressed()) {
				ClientPlayNetworking.send(new BreathPayload(BreathPayload.Mode.START_BREATHING));
			}
		});
	}
}