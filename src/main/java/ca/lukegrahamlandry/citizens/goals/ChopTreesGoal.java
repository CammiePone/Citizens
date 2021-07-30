package ca.lukegrahamlandry.citizens.goals;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalNear;
import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.LumberJackEntity;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.util.FetchType;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.StoreHouseBuilding;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class ChopTreesGoal extends Goal {
    private LumberJackEntity villager;
    private IBaritone baritone;

    public ChopTreesGoal(LumberJackEntity villager) {
        this.villager = villager;
    }

    @Override
    public boolean canStart() {
        return this.villager.currentActivity == VillagerBase.Activity.WORK && villager.treePos != null && this.villager.mustHold(FetchType.AXE);
    }

    @Override
    public void start() {
        this.baritone = BaritoneAPI.getProvider().getBaritone(this.villager);
        this.baritone.getCustomGoalProcess().setGoalAndPath(new GoalNear(this.villager.treePos, 1));
        theTree.clear();
        alreadyChecked.clear();
        this.index = -1;
    }

    List<BlockPos> theTree = new ArrayList<>();
    List<BlockPos> alreadyChecked = new ArrayList<>();

    int index = -1;
    int delay = 0;

    @Override
    public void tick() {
        if (!this.baritone.isActive()) {
            delay--;
            if (delay > 0) return;

            if (theTree.isEmpty()) {
                mapTheTreeAt(this.villager.treePos);
                alreadyChecked.clear();
            }

            this.index++;
            if (this.index >= theTree.size()){
                this.villager.treePos = null; // done
            }

            BlockPos pos = theTree.get(this.index);
            BlockState state = this.villager.world.getBlockState(pos);
            if (state.isIn(BlockTags.LOGS)){
                this.villager.world.breakBlock(pos, true, this.villager);
                ItemStack stack = this.villager.getStackInHand(Hand.MAIN_HAND);
                stack.setDamage(stack.getDamage() + 1);
                if (stack.getDamage() >= stack.getMaxDamage()){
                    stack.decrement(1);
                }

                this.villager.swingHand(Hand.MAIN_HAND);
                delay = 10;
            }
            if (state.isIn(BlockTags.LEAVES)){
                this.villager.world.breakBlock(pos, true, this.villager);

                this.villager.swingHand(Hand.MAIN_HAND);
                delay = 10;
            }


        }
    }

    private void mapTheTreeAt(BlockPos treePos) {
        for (int i=0;i<6;i++){
            Direction dir = Direction.byId(i);
            BlockPos check = treePos.offset(dir);
            if (theTree.contains(check) || alreadyChecked.contains(check)) return;
            BlockState state = this.villager.world.getBlockState(check);
            if (state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.LEAVES)){
                theTree.add(check);
                mapTheTreeAt(check);
            } else {
                alreadyChecked.add(check);
            }
        }
    }
}

