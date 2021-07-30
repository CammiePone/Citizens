package ca.lukegrahamlandry.citizens.registry;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.FarmerEntity;
import ca.lukegrahamlandry.citizens.entity.LumberJackEntity;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class EntityInit {
    public static final Map<EntityType, Identifier> ENTITIES = new HashMap<>();

    public static final EntityType<FarmerEntity> FARMER = create("farmer", FarmerEntity::new, VillagerBase.attributes(), 0.6F, 1.95F);
    public static final EntityType<FarmerEntity> LUMBER_JACK = create("lumber_jack", LumberJackEntity::new, VillagerBase.attributes(), 0.6F, 1.95F);

    private static EntityType create(String name, EntityType.EntityFactory supplier, DefaultAttributeContainer.Builder attributes, float width, float height) {
        EntityType type = FabricEntityTypeBuilder.create(SpawnGroup.MISC, supplier).dimensions(EntityDimensions.fixed(width, height)).build();
        ENTITIES.put(type, new Identifier(CitizensMain.MOD_ID, name));
        FabricDefaultAttributeRegistry.register(type, attributes);
        return type;
    }

    public static void registerAll() {
        ENTITIES.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
    }
}
