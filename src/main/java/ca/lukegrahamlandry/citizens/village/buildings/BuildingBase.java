package ca.lukegrahamlandry.citizens.village.buildings;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BuildingBase {
    private final World world;
    private final BlockPos markerPos;

    // holds workers / residents that have claimed this building
    private final List<UUID> villagers = new ArrayList<>();

    public BuildingBase(World world, BlockPos markerPos){
        this.world = world;
        this.markerPos = markerPos;
    }

    public boolean validate(){
        // todo: check marker, roof, walls, door
        return false;
    }
}
