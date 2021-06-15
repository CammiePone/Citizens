package ca.lukegrahamlandry.citizens.entity;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.goals.CommuteGoal;
import ca.lukegrahamlandry.citizens.goals.VillagerSchedule;
import ca.lukegrahamlandry.citizens.village.Village;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.HouseBuilding;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

// todo: extend PlayerEntity for mod compatibility (guns!)
public abstract class VillagerBase extends PathAwareEntity {
    protected BuildingBase home;
    public BuildingBase work;
    protected Village village;
    protected VillagerBase(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder attributes() {
        return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D);
    }

    public abstract Identifier getTexture();

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

        this.goalSelector.add(2, new CommuteGoal(this));

        // have fear (same as normal villagers)
        this.goalSelector.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, ZoglinEntity.class, 10.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5D));
    }

    public enum Activity {
        WORK,
        SLEEP,
        COMMUTE,
        RESTOCK,
        PANIC,
        WANDER,
        HOME;
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isClient()){
            if (this.home == null) {
                this.tryFindAHome();
            } else if (this.work == null) {
                this.tryFindWork();
            }

            // todo: only check this every x ticks? idk if you want to be able to push them out of their home temporarily and have them wait a bit before running back

            Activity goal = this.getTargetActivity();
            boolean isCommuting = this.currentActivity == Activity.COMMUTE;

            if (!isCommuting && !this.atBuilding(this.home) && goal == Activity.HOME || goal == Activity.SLEEP){
                this.startCommuteTo(this.home);
            }

            // todo: allow restocking without immediately running back to work
            if (!isCommuting && !this.atBuilding(this.work) && goal == Activity.WORK){
                this.startCommuteTo(this.work);
            }

            if (!isCommuting && goal == Activity.WANDER && this.getRandom().nextInt(200) == 0){
                int index = this.getRandom().nextInt(this.village.buildings.size());
                BuildingBase building = this.village.buildings.get(index);
                this.startCommuteTo(building);
            }

            if (this.isAwake() && goal == Activity.SLEEP && this.atBuilding(this.home)){
                // todo: start sleep animation
            }
        }
    }

    protected boolean isAwake(){
        // todo
        return true;
    }

    public boolean atBuilding(BuildingBase building) {
        return building.getFloorSpace().contains(this.getBlockPos());
    }

    protected boolean tryFindAHome(){
        Village checkVillage = Village.findClosestVillage(this.getBlockPos());
        if (checkVillage == null) return false;
        for (BuildingBase building : checkVillage.buildings){
            if (building instanceof HouseBuilding){
                if (building.hasOpenSpace()) {
                    this.village = checkVillage;
                    building.villagers.add(this);
                    this.home = building;
                    checkVillage.villagers.add(this);
                    CitizensMain.log("found home at: " + building.getFirstInsidePos());
                    return true;
                }
            }
        }
        return false;
    }

    protected abstract boolean tryFindWork();

    public Activity currentActivity;
    public BuildingBase commuteLocation;

    VillagerSchedule schedule = new VillagerSchedule();
    public Activity getTargetActivity(){
        return schedule.getCurrent(this.world.getTimeOfDay());
    }

    public void startCommuteTo(BuildingBase building){
        this.commuteLocation = building;
        this.currentActivity = Activity.COMMUTE;
    }


}
