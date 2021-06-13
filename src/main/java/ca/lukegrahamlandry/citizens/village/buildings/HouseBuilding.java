package ca.lukegrahamlandry.citizens.village.buildings;

import ca.lukegrahamlandry.citizens.CitizensMain;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HouseBuilding extends BuildingBase {
    protected int totalBeds = 0;

    public HouseBuilding(World world, BlockPos markerPos) {
        super(world, markerPos);
    }

    @Override
    public boolean validate() {
        if (!super.validate()) return false;
        int bedParts = 0;
        for (BlockPos pos : this.floorSpace){
            BlockState state = this.world.getBlockState(pos);
            if (state.getBlock() instanceof BedBlock){
                bedParts++;
            }
        }

        this.totalBeds = bedParts / 2;
        CitizensMain.log("totalBeds: " + this.totalBeds);
        return this.totalBeds > 0;
    }

    @Override
    protected boolean validateFloorSpot(BlockPos floorSpot){
        BlockState floorSpotState = this.world.getBlockState(floorSpot);
        // todo: pressure plates / buttons should be valid here
        if (!floorSpotState.isAir() && !(floorSpotState.getBlock() instanceof BedBlock)) return false;

        BlockState downState = this.world.getBlockState(floorSpot.down());
        if (downState.isAir()) return false;

        for (int i=1;i<MAX_ROOF_HEIGHT;i++){
            BlockState state = this.world.getBlockState(floorSpot.up(i));
            if (!state.isAir()) return true;
        }
        return false;
    }

    // must call validate() first to calculate this.totalBeds
    public boolean hasAvailableBed(){
        return this.villagers.size() < this.totalBeds;
    }
}
