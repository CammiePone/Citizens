package ca.lukegrahamlandry.citizens.village;

import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Village {
    // I'll have to save this data somewhere unless i have a clever way not to need it.
    // I could have the markers regenerate the village on world load instead:
    // buildings addOrCreate a village, villagers remember their home and work building in nbt.
    // yeah lets go with that
    // need mixin on item frames or add a Building Marker Frame to map out the village on world load

    public List<BuildingBase> buildings = new ArrayList<>();
    public List<VillagerBase> villagers = new ArrayList<>();

    public Village(){

    }

    public static List<Village> all = new ArrayList<>();

    // todo: config max range
    // todo: dimemnsion sensitive
    public static Village findClosestVillage(BlockPos blockPos) {
        Village closest = null;
        double bestDistSq = Integer.MAX_VALUE;
        for (Village village : all){
            for (BuildingBase building : village.buildings){
                BlockPos check = building.getFirstInsidePos();
                double distSq = Math.pow(check.getX() - blockPos.getX(), 2) + Math.pow(check.getZ() - blockPos.getZ(), 2);
                if (distSq < bestDistSq){
                    bestDistSq = distSq;
                    closest = village;
                }
            }
        }

        return closest;
    }
}
