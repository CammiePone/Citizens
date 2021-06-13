package ca.lukegrahamlandry.citizens;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;

public class AutomatoneSetup implements EntityComponentInitializer {
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(VillagerBase.class, IBaritone.KEY, BaritoneAPI.getProvider().componentFactory());
    }
}