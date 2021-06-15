package ca.lukegrahamlandry.citizens.village.buildings;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FarmBuilding extends BuildingBase {
    public FarmBuilding(World world, BlockPos markerPos) {
        super(world, markerPos);
    }

    // finds a fence instead of door
    @Override
    protected BlockPos getAdjacentDoor(BlockPos start){
        for (int i=0;i<6;i++){
            Direction dir = Direction.byId(i);
            BlockPos checkPos = start.offset(dir);
            BlockState state = this.world.getBlockState(checkPos);
            if (state.getBlock() instanceof FenceGateBlock){
                return checkPos;
            }
        }
        return null;
    }

    // checks that pos is air or plant and down is dirt or farm land
    @Override
    protected boolean validateFloorSpot(BlockPos floorSpot) {
        BlockState floorSpotState = this.world.getBlockState(floorSpot);
        if (!(floorSpotState.getBlock() instanceof PlantBlock || floorSpotState.isAir())) return false;

        BlockState downState = this.world.getBlockState(floorSpot.down());
        return downState.isIn(BlockTags.DIRT) || downState.isOf(Blocks.FARMLAND);
    }

    @Override
    public boolean hasOpenSpace() {
        return this.villagers.size() == 0;
    }


}
