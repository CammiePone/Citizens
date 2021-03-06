package ca.lukegrahamlandry.citizens.entity;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalXZ;
import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.goals.CommuteGoal;
import ca.lukegrahamlandry.citizens.goals.FarmGoal;
import ca.lukegrahamlandry.citizens.village.Village;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.FarmBuilding;
import ca.lukegrahamlandry.citizens.village.buildings.HouseBuilding;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class FarmerEntity extends VillagerBase {
    public FarmerEntity(EntityType<FarmerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(3, new FarmGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isValidWork(BuildingBase building){
        return building instanceof FarmBuilding;
    }
}
