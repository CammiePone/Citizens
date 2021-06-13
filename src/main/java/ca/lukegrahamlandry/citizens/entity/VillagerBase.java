package ca.lukegrahamlandry.citizens.entity;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.goals.CommuteGoal;
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
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;

public abstract class VillagerBase extends PathAwareEntity {
    protected BuildingBase home;
    protected BuildingBase work;
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

    // instead of these, maybe I should use a time based task system like normal villagers

    public void onDayStart(){
        // go to work
    }

    public void onDayEnd(){
        // go home and sleep
    }

    public enum Activity {
        WORK,
        SLEEP,
        COMMUTE,
        RESTOCK,
        PANIC,
        WANDER;
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isClient() && this.home == null){
            this.tryFindAHome();
        }

        if (!world.isClient()){
            // todo: dont hard code the time
            // todo: have some complicated system for telling which times are work and sleep
            if (world.getTimeOfDay() > 12000 && !atOrGoingHome() && !(this.home == null)){
                CitizensMain.log("home time");
                this.queueActivity(Activity.COMMUTE);
                this.commuteLocation = this.home;
                this.queueActivity(Activity.SLEEP);
            }

            if (this.currentActivity == null){
                Activity todo = getNextActivity();
                if (todo != null){
                    this.currentActivity = todo;
                }
            }

        }
    }

    protected boolean tryFindAHome(){
        Village village = Village.findClosestVillage(this.getBlockPos());
        if (village == null) return false;
        for (BuildingBase building : village.buildings){
            if (building instanceof HouseBuilding){
                if (((HouseBuilding) building).hasAvailableBed()) {
                    building.villagers.add(this);
                    this.home = building;
                    village.villagers.add(this);
                    CitizensMain.log("found home at: " + building.getFirstInsidePos());
                    return true;
                }
            }
        }
        return false;
    }

    private boolean atOrGoingHome() {
        return this.commuteLocation == this.home || this.currentActivity == Activity.SLEEP;
    }

    public Activity currentActivity;
    protected List<Activity> next = new ArrayList<>();
    public BuildingBase commuteLocation;

    protected void queueActivity(Activity todo){
        this.next.add(todo);
    }

    public Activity getNextActivity(){
        if (this.next.size() == 0) return null;
        Activity todo = this.next.get(0);
        this.next.remove(0);
        return todo;
    }


}
