package ca.lukegrahamlandry.citizens.client;

import ca.lukegrahamlandry.citizens.client.render.VillagerRenderer;
import ca.lukegrahamlandry.citizens.registry.EntityInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class CitizensClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(EntityInit.FARMER, VillagerRenderer::new);
    }
}