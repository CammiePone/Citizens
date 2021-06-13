package ca.lukegrahamlandry.citizens.entity;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalXZ;
import ca.lukegrahamlandry.citizens.CitizensMain;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FarmerEntity extends VillagerBase {
    public FarmerEntity(EntityType<FarmerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public Identifier getTexture() {
        return new Identifier(CitizensMain.MOD_ID, "textures/entity/farmer");
    }
}
