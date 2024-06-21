package io.github.afamiliarquiet;

import io.github.afamiliarquiet.entity.MawEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

public class MagnificentMawClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MawKeybinds.register();

		EntityRendererRegistry.register(MawEntities.BREATH_PROJECTILE_TYPE, EmptyEntityRenderer::new);
	}
}