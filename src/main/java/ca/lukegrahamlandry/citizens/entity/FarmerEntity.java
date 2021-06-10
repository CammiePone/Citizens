package ca.lukegrahamlandry.citizens.entity;

import ca.lukegrahamlandry.citizens.CitizensMain;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FarmerEntity extends VillagerBase{
    public FarmerEntity(EntityType<? extends VillagerBase> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return new Identifier(CitizensMain.MOD_ID, "textures/entity/farmer");
    }
}
