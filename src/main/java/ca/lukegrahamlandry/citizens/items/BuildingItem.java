package ca.lukegrahamlandry.citizens.items;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.village.Village;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.HouseBuilding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BuildingItem extends Item {
    private BuildingFactory factory;

    public BuildingItem(BuildingFactory factory, Settings settings) {
        super(settings);
        this.factory = factory;
    }

    public BuildingBase getBuilding(World world, BlockPos markerPos){
        return this.factory.create(world, markerPos);
    }


    public interface BuildingFactory {
        BuildingBase create(World world, BlockPos markerPos);
    }
}
