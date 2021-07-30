package ca.lukegrahamlandry.citizens.entity;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.goals.FarmGoal;
import ca.lukegrahamlandry.citizens.goals.FindTreesGoal;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.FarmBuilding;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LumberJackEntity extends VillagerBase {
    public BlockPos treePos = null;

    public LumberJackEntity(EntityType<LumberJackEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(3, new FindTreesGoal(this));
        this.goalSelector.add(2, new ChopTreesGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
    }


    public boolean isValidWork(BuildingBase building){
        return false;
    }
}
