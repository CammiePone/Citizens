package ca.lukegrahamlandry.citizens.goals;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalGetToBlock;
import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.LumberJackEntity;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.util.FetchType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
    private int posIndex = -1;

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
        CitizensMain.log("start finding trees");
    }

    @Override
    public void stop() {
        this.searchPositions.clear();
    }

    @Override
    public void tick() {
        if (!this.baritone.isActive()) {
            BlockPos tree = findTreesAround(this.villager.getBlockPos());
            if (tree == null){
                this.posIndex = (this.posIndex + 1) % this.searchPositions.size();
                BlockPos middle = this.searchPositions.get(this.posIndex).south(AREA_SIZE / 2).east(AREA_SIZE / 2);
                this.baritone.getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(middle));
                // CitizensMain.log("try again at "  + middle);
            } else {
                this.villager.treePos = tree;
                CitizensMain.log("found tree at " + tree);
            }
        }

    }

    // a RADIUS_IN_AREASxRADIUS_IN_AREAS square of AREA_SIZExAREA_SIZE squares of blocks
    int AREA_SIZE = 5;
    int RADIUS_IN_AREAS = 3;
    public List<BlockPos> calculateSearchPositions(BlockPos middle){
        CitizensMain.log("setup search pos");

        List<BlockPos> searchAt = new ArrayList<>();
        int totalAreas = (int) Math.pow(RADIUS_IN_AREAS * 2, 2);
        BlockPos pos = middle.north(AREA_SIZE * RADIUS_IN_AREAS).west(AREA_SIZE * RADIUS_IN_AREAS);
        for (int i=0;i<totalAreas;i++){
            searchAt.add(pos);

            if ((i + 1) % (RADIUS_IN_AREAS * 2) == 0){
                pos = pos.south(AREA_SIZE);
                pos = pos.west(AREA_SIZE * (RADIUS_IN_AREAS * 2 - 1));
            } else {
                pos = pos.east(AREA_SIZE);
            }
        }

        return searchAt;
    }

    private BlockPos findTreesAround(BlockPos pos) {
        CitizensMain.log("look for trees around " + pos);
        for (int x = 0; x< AREA_SIZE; x++){
            // for (int y=-1;y<1;y++){
            int y = 0;
                for (int z = 0; z< AREA_SIZE; z++){
                    BlockPos check = pos.add(x,y,z);

                    // CitizensMain.log("check " + check);

                    int height = 0;
                    BlockState state = this.villager.world.getBlockState(check);
                    while (state.isIn(BlockTags.LOGS)){
                        height++;
                        check = check.up();
                        state = this.villager.world.getBlockState(check);
                        // CitizensMain.log("check for wood " + check);
                    }

                    if (height > 0){
                        // CitizensMain.log("height " + height);

                        for (int i=0;i<6;i++){
                            Direction dir = Direction.byId(i);
                            BlockState checkState = this.villager.world.getBlockState(check.offset(dir));
                            if (checkState.isIn(BlockTags.LEAVES)) {
                                // CitizensMain.log("found leaves");
                                return check.down(height);
                            }
                        }
                    }
               // }
            }
        }

        CitizensMain.log("found no trees");
        return null;
    }
}
