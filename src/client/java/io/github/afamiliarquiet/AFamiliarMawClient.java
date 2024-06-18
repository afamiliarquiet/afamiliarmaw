package io.github.afamiliarquiet;

import io.github.afamiliarquiet.entity.MawEntities;
import io.github.afamiliarquiet.network.BreathPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.afamiliarquiet.MawKeybinds.breatheKey;

public class AFamiliarMawClient implements ClientModInitializer {



	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		MawKeybinds.register();

		AtomicBoolean breathingLastTick = new AtomicBoolean(false);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean breathingNow = breatheKey.isPressed();

			if (ClientPlayNetworking.canSend(BreathPacket.ID) && breathingNow != breathingLastTick.get()) {
				BreathPacket.Mode mode = breathingNow ? BreathPacket.Mode.START_BREATHING : BreathPacket.Mode.STOP_BREATHING;

				ClientPlayNetworking.send(new BreathPacket(mode));
			}

			breathingLastTick.set(breathingNow);
		});

		EntityRendererRegistry.register(MawEntities.BREATH_PROJECTILE_TYPE, EmptyEntityRenderer::new);

	}
}