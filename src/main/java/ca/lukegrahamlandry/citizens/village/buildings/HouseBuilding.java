package ca.lukegrahamlandry.citizens.village.buildings;

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
        return this.totalBeds > 0;
    }

    // must call validate() first to calculate this.totalBeds
    public boolean hasAvailableBed(){
        return this.villagers.size() < this.totalBeds;
    }
}
