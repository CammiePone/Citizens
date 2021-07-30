package ca.lukegrahamlandry.citizens.goals;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalGetToBlock;
import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.LumberJackEntity;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.util.FetchType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class FindTreesGoal extends Goal {
    private LumberJackEntity villager;
    private List<BlockPos> searchPositions;
    IBaritone baritone;
    private int posIndex;

    public FindTreesGoal(LumberJackEntity villager) {
        this.villager = villager;
    }

    @Override
    public boolean canStart() {
        return this.villager.currentActivity == VillagerBase.Activity.WORK && villager.treePos == null && this.villager.home != null && this.villager.mustHold(FetchType.AXE);
    }

    @Override
    public void start() {
        this.searchPositions = calculateSearchPositions(this.villager.home.getFirstInsidePos());
        this.baritone = BaritoneAPI.getProvider().getBaritone(this.villager);
        this.baritone.getCustomGoalProcess().setGoalAndPath(null);
        this.posIndex = -1;
    }

    @Override
    public void tick() {
        if (!this.baritone.isActive()) {
            BlockPos tree = findTreesAround(this.villager.getBlockPos());
            if (tree == null){
                this.posIndex = (this.posIndex + 1) % this.searchPositions.size();
                BlockPos middle = this.searchPositions.get(this.posIndex).south(DIVISION_SIZE / 2).east(DIVISION_SIZE / 2);
                this.baritone.getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(middle));
            } else {
                this.villager.treePos = tree;
                CitizensMain.log("found tree at " + tree);
            }
        }

    }

    int DIVISION_SIZE = 5;
    int DIVISION_RADIUS = 3;
    public List<BlockPos> calculateSearchPositions(BlockPos middle){
        List<BlockPos> searchAt = new ArrayList<>();
        int area = (int) Math.pow(DIVISION_RADIUS * 2, 2);
        BlockPos pos = middle.north(DIVISION_SIZE * DIVISION_RADIUS).west(DIVISION_SIZE * DIVISION_RADIUS);
        for (int i=0;i<area;i++){
            searchAt.add(pos);

            if (i % (DIVISION_RADIUS * 2) == 0){
                pos = pos.south(DIVISION_SIZE);
                pos = pos.north(DIVISION_SIZE * DIVISION_RADIUS * 2);
            } else {
                pos = pos.east(DIVISION_SIZE);
            }
        }

        return searchAt;
    }

    private BlockPos findTreesAround(BlockPos pos) {
        for (int x=0;x<DIVISION_SIZE;x++){
            for (int y=-2;y<3;y++){
                for (int z=0;z<DIVISION_SIZE;z++){
                    BlockPos check = pos.add(z,y,z);

                    int height = 0;
                    BlockState state = this.villager.world.getBlockState(check);
                    while (state.isIn(BlockTags.LOGS)){
                        height++;
                        check = check.up();
                        state = this.villager.world.getBlockState(check);
                    }

                    if (height > 0){
                        for (int i=0;i<6;i++){
                            Direction dir = Direction.byId(i);
                            BlockState checkState = this.villager.world.getBlockState(check.offset(dir));
                            if (checkState.isIn(BlockTags.LEAVES)) {
                                return check.down(height);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
