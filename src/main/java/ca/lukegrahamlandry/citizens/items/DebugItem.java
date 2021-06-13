package ca.lukegrahamlandry.citizens.items;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.village.Village;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.HouseBuilding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class DebugItem extends Item {
    public DebugItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient()){
            CitizensMain.log("click");
            BuildingBase building = new HouseBuilding(context.getWorld(), context.getBlockPos());
            boolean valid = building.validate();
            if (valid){
                building.displayFloorSpace();
                Village.INSTANCE.buildings.add(building);
            }
        }

        return ActionResult.SUCCESS;
    }
}
