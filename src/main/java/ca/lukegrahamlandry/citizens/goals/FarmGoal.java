package ca.lukegrahamlandry.citizens.goals;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalGetToBlock;
import ca.lukegrahamlandry.citizens.CitizensConfig;
import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.FarmerEntity;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.util.FetchType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class FarmGoal extends Goal {
    FarmerEntity villager;
    IBaritone baritone;
    BlockPos target;
    private boolean done;
    private int delay = 10;
    ServerWorld world;

    public FarmGoal(FarmerEntity me) {
        this.villager = me;
        this.world = (ServerWorld) this.villager.getEntityWorld();
    }

    @Override
    public boolean canStart() {
        return this.villager.currentActivity == VillagerBase.Activity.WORK && this.villager.atBuilding(villager.work);
    }

    @Override
    public void start() {
        System.out.println("FarmGoal start()");
    }

    @Override
    public boolean shouldContinue() {
        return !this.done && this.villager.currentActivity == VillagerBase.Activity.WORK;
    }


    // todo: restock when out of seeds
    // probably better as a shouldRestock method on the villager
    // same for putting away when inventory full or end of day

    // todo: break this into methods so it's less confusing and easier to add to

    @Override
    public void tick() {
        if (this.delay > 0){
            this.delay--;
            return;
        }

        if (this.target == null) {
            findACrop();
            if (this.target == null) {
                this.delay = 20;
            } else {
                this.baritone = BaritoneAPI.getProvider().getBaritone(this.villager);
                baritone.getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(this.target));
            }
            return;
        }

        if (!this.baritone.isActive()){
            System.out.println("begin farm");
            BlockState state = this.world.getBlockState(this.target);

            if (state.getBlock() instanceof CropBlock && ((CropBlock) state.getBlock()).isMature(state)){
                System.out.println("break");
                this.world.breakBlock(this.target, true, this.villager);
                // todo: collect drops
            }

            if (state.isAir()){
                BlockState soil = this.world.getBlockState(this.target.down());

                // todo: support for double crops like melons and pumpkins
                // todo: support for non-farmland crops like cactus and sugar cane

                int seedIndex = pickSeeds();
                if (seedIndex >= 0){
                    Inventory inv = this.villager.inventory;
                    ItemStack seeds = inv.getStack(seedIndex);
                    System.out.println("found seed " + seeds );

                    if (seeds.getItem() instanceof BlockItem && ((BlockItem)seeds.getItem()).getBlock() instanceof CropBlock){
                        CropBlock block = (CropBlock) ((BlockItem)seeds.getItem()).getBlock();
                        if (block.canPlaceAt(soil, world, this.target)){
                            world.setBlockState(this.target, block.getDefaultState(), 3);
                            seeds.decrement(1);
                            System.out.println("is farm");
                        } else {
                            // todo: check that is dirt even tho the building should have checked already it might have changed
                            // todo: require having a hoe and do animation and use durability
                            world.setBlockState(this.target.down(), Blocks.FARMLAND.getDefaultState(), 3);
                            System.out.println("make farm");
                            if (block.canPlaceAt(soil, world, this.target)){
                                System.out.println("use farm");
                                world.setBlockState(this.target, block.getDefaultState(), 3);
                                seeds.decrement(1);
                            }
                        }
                    }

                    inv.setStack(seedIndex, seeds);
                }
            }

            this.target = null;
        }
    }

    // todo: add some random so it uses different seeds
    // todo: only plant seeds next to same seed type or have gui let you select which seeds to use
    private int pickSeeds() {
        Inventory inv = this.villager.inventory;
        for (int i=0;i<inv.size();i++){
            ItemStack check = inv.getStack(i);
            if (CitizensConfig.isSeed(check.getItem())) return i;
        }

        // go to store house and grab a stack of seeds
        this.villager.itemsToGet.add(FetchType.SEEDS);


        return -1;
    }

    private void findACrop() {
        int index = this.villager.getRandom().nextInt(this.villager.work.getFloorSpace().size());
        this.target = this.villager.work.getFloorSpace().get(index);

        CitizensMain.log("start farming " + target);

    }

    @Override
    public void stop() {
        CitizensMain.log("done farm");
        this.villager.currentActivity = null;
        this.villager.commuteLocation = null;
    }
}
