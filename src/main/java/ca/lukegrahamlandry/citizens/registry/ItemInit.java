package ca.lukegrahamlandry.citizens.registry;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.FarmerEntity;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.items.BuildingItem;
import ca.lukegrahamlandry.citizens.items.DebugItem;
import ca.lukegrahamlandry.citizens.village.buildings.FarmBuilding;
import ca.lukegrahamlandry.citizens.village.buildings.HouseBuilding;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;

public class ItemInit {
    public static final Map<Item, Identifier> ITEMS = new HashMap<>();

    public static final Item DEBUG = create("debug", new DebugItem(props()));

    public static final Item FARM_MARKER = create("farm_marker", new BuildingItem(FarmBuilding::new, props()));
    public static final Item HOUSE_MARKER = create("house_marker", new BuildingItem(HouseBuilding::new, props()));

    private static Item.Settings props() {
        return new Item.Settings();
    }

    private static Item create(String name, Item item) {
        ITEMS.put(item, new Identifier(CitizensMain.MOD_ID, name));
        return item;
    }

    public static void registerAll() {
        ITEMS.keySet().forEach(item -> Registry.register(Registry.ITEM, ITEMS.get(item), item));
    }
}
