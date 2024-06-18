package io.github.afamiliarquiet;

import io.github.afamiliarquiet.entity.MawEntities;
import io.github.afamiliarquiet.network.BreathPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

import java.util.concurrent.atomic.AtomicInteger;

import static io.github.afamiliarquiet.MawKeybinds.breatheKey;

public class AFamiliarMawClient implements ClientModInitializer {



	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.


		// todo - make default ticking rate configy and also change consumption rate based on ticky fire rate
		MawKeybinds.register();
		AtomicInteger cooldown = new AtomicInteger();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (cooldown.get() > 0) {
				cooldown.getAndDecrement();
			}

			if (cooldown.get() == 0 && ClientPlayNetworking.canSend(BreathPacket.ID) && breatheKey.isPressed()) {
				ClientPlayNetworking.send(new BreathPacket(BreathPacket.Mode.START_BREATHING));
				cooldown.set(2);
			}
		});

		EntityRendererRegistry.register(MawEntities.BREATH_PROJECTILE_TYPE, EmptyEntityRenderer::new);

	}
}