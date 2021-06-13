package ca.lukegrahamlandry.citizens.village.buildings;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class BuildingBase {
    public static final int MAX_ROOF_HEIGHT = 16;
    public static final int MAX_FLOOR_SIZE = 64;

    protected final World world;
    protected final BlockPos markerPos;
    protected List<BlockPos> floorSpace = new ArrayList<>();

    // holds workers / residents that have claimed this building
    public final List<VillagerBase> villagers = new ArrayList<>();

    public BuildingBase(World world, BlockPos markerPos){
        this.world = world;
        this.markerPos = markerPos;
    }

    // checks requirements to be a valid building:
    // - door next to the marker
    // -
    public boolean validate(){
        BlockPos doorPos = getAdjacentDoor(this.markerPos);
        CitizensMain.log("doorPos: " + doorPos);
        if (doorPos == null) return false;

        BlockPos floorStart = getFirstFloorSpot(doorPos);
        CitizensMain.log("floorStart: " + floorStart);
        if (floorStart == null) return false;
        this.floorSpace.add(floorStart);

        crawlForFloorSpace(floorStart);

        CitizensMain.log("floor: " + floorSpace);

        // todo: check marker, walls
        return true;
    }

    protected BlockPos getAdjacentDoor(BlockPos start){
        for (int i=0;i<6;i++){
            Direction dir = Direction.byId(i);
            BlockPos checkPos = start.offset(dir);
            BlockState state = this.world.getBlockState(checkPos);
            if (state.getBlock() instanceof DoorBlock){
                if (state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER){
                    return checkPos;
                } else {
                    return checkPos.down();
                }
            }
        }
        return null;
    }

    protected BlockPos getFirstFloorSpot(BlockPos start){
        for (int i=0;i<4;i++){
            Direction dir = Direction.fromHorizontal(i);
            BlockPos checkPos = start.offset(dir);
            if (validateFloorSpot(checkPos)) return checkPos;
        }
        return null;
    }

    // checks for air at pos, roof above, solid below
    protected boolean validateFloorSpot(BlockPos floorSpot){
        BlockState floorSpotState = this.world.getBlockState(floorSpot);
        // todo: pressure plates / buttons should be valid here
        if (!floorSpotState.isAir()) return false;

        BlockState downState = this.world.getBlockState(floorSpot.down());
        if (downState.isAir()) return false;

        for (int i=1;i<MAX_ROOF_HEIGHT;i++){
            BlockState state = this.world.getBlockState(floorSpot.up(i));
            if (!state.isAir()) return true;
        }
        return false;
    }

    protected void crawlForFloorSpace(BlockPos knownFloor){
        if (this.floorSpace.size() > MAX_FLOOR_SIZE) return;
        for (int i=0;i<4;i++){
            Direction dir = Direction.fromHorizontal(i);
            BlockPos checkPos = knownFloor.offset(dir);
            if (!this.floorSpace.contains(checkPos) && validateFloorSpot(checkPos)){
                this.floorSpace.add(checkPos);
                this.crawlForFloorSpace(checkPos);
            }
        }
    }

    // for debugging. call validate() first to populate this.floorSpace
    public void displayFloorSpace(){
        if (!this.world.isClient()) return;
        for (BlockPos pos : this.floorSpace){
            ((ServerWorld)this.world).addParticle(ParticleTypes.GLOW_SQUID_INK, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0);
        }
    }

    public BlockPos getFirstInsidePos(){
        return this.floorSpace.get(0);
    }
}
