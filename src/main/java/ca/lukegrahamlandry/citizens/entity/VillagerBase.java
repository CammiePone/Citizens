package ca.lukegrahamlandry.citizens.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public abstract class VillagerBase extends PathAwareEntity {
    protected VillagerBase(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder attributes() {
        return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20);
    }

    public abstract Identifier getTexture();

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

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
}
