package ca.lukegrahamlandry.citizens.village.buildings;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.village.Village;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class StoreHouseBuilding extends BuildingBase {
    List<Inventory> chests = new ArrayList<>();

    public StoreHouseBuilding(World world, BlockPos markerPos) {
        super(world, markerPos);
    }

    @Override
    public boolean validate() {
        if (!super.validate()) return false;

        for (BlockPos pos : this.floorSpace){
            BlockEntity tile = this.world.getBlockEntity(pos);
            if (tile instanceof Inventory){
                chests.add((Inventory) tile);
            }
        }

        CitizensMain.log("totalChests: " + this.chests.size());
        return this.chests.size() > 0;
    }

    @Override
    protected boolean validateFloorSpot(BlockPos floorSpot){
        BlockState floorSpotState = this.world.getBlockState(floorSpot);
        // todo: pressure plates / buttons should be valid here
        boolean hasInventory = this.world.getBlockEntity(floorSpot) instanceof Inventory;
        if (!floorSpotState.isAir() && !hasInventory) return false;

        BlockState downState = this.world.getBlockState(floorSpot.down());
        if (downState.isAir()) return false;

        for (int i=1;i<MAX_ROOF_HEIGHT;i++){
            BlockState state = this.world.getBlockState(floorSpot.up(i));
            if (!state.isAir()) return true;
        }
        return false;
    }

    public boolean hasOpenSpace(){
        return true;
    }

    // Util: accessing chests

    public boolean storeItem(ItemStack stack){
        if (stack == null || stack == ItemStack.EMPTY) return true;

        for (Inventory chest : this.chests){
            for (int i=0;i<chest.size();i++){
                ItemStack check = chest.getStack(i);
                if (check == ItemStack.EMPTY) {
                    chest.setStack(i, stack);
                    return true;
                }

                int newCount = check.getCount() + stack.getCount();
                if (check.getItem() == stack.getItem() && newCount <= stack.getItem().getMaxCount() && newCount < chest.getMaxCountPerStack()){
                    stack.setCount(newCount);
                    chest.setStack(i, stack);
                    return true;
                }
            }
        }

        return false;
    }

    public ItemStack getItem(Item item){
        for (Inventory chest : this.chests){
            for (int i=0;i<chest.size();i++){
                ItemStack check = chest.getStack(i);

                if (check.getItem() == item){
                    chest.setStack(i, ItemStack.EMPTY);
                    return check;
                }
            }
        }

        return ItemStack.EMPTY;
    }


    // Util: pathfinding to the right store house. allows villages to have multiple store houses

    public static StoreHouseBuilding findStoreHouseFor(Village village, ItemStack stack){
        if (village == null || stack == null || stack == ItemStack.EMPTY) return null;

        for (BuildingBase building : village.buildings){
            if (building instanceof StoreHouseBuilding){
                for (Inventory chest : ((StoreHouseBuilding) building).chests){
                    for (int i=0;i<chest.size();i++){
                        ItemStack check = chest.getStack(i);
                        if (check == ItemStack.EMPTY) return (StoreHouseBuilding) building;
                        if (check.getItem() == stack.getItem() && ((check.getCount() + stack.getCount()) <= stack.getItem().getMaxCount())){
                            return (StoreHouseBuilding) building;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static StoreHouseBuilding findStoreHouseWith(Village village, Item item){
        if (village == null || item == null) return null;

        for (BuildingBase building : village.buildings){
            if (building instanceof StoreHouseBuilding){
                for (Inventory chest : ((StoreHouseBuilding) building).chests){
                    for (int i=0;i<chest.size();i++){
                        ItemStack check = chest.getStack(i);
                        if (check.getItem() == item){
                            return ((StoreHouseBuilding) building);
                        }
                    }
                }
            }
        }

        return null;
    }




}
