package ca.lukegrahamlandry.citizens.goals;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class CommuteGoal extends Goal {
    VillagerBase villager;
    IBaritone baritone;
    public CommuteGoal(VillagerBase me) {
        this.villager = me;
    }

    @Override
    public boolean canStart() {
        return this.villager.currentActivity == VillagerBase.Activity.COMMUTE && this.villager.commuteLocation != null;
    }

    @Override
    public void start() {
        BlockPos target = this.villager.commuteLocation.getFirstInsidePos();

        CitizensMain.log("start going to " + target);


        this.baritone = BaritoneAPI.getProvider().getBaritone(this.villager);
        baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(target));
    }

    @Override
    public boolean shouldContinue() {
        return baritone.isActive();
    }

    @Override
    public void stop() {
        CitizensMain.log("done");
        this.villager.currentActivity = null;
        this.villager.commuteLocation = null;
    }
}
