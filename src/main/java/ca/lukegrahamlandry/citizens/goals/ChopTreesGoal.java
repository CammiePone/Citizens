package ca.lukegrahamlandry.citizens.goals;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalGetToBlock;
import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.LumberJackEntity;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.util.FetchType;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.StoreHouseBuilding;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class ChopTreesGoal extends Goal {
    private LumberJackEntity villager;
    private IBaritone baritone;

    public ChopTreesGoal(LumberJackEntity villager) {
        this.villager = villager;
    }

    @Override
    public boolean canStart() {
        return this.villager.currentActivity == VillagerBase.Activity.WORK && villager.treePos != null && this.villager.mustHold(FetchType.AXE);
    }

    @Override
    public void start() {
        this.baritone = BaritoneAPI.getProvider().getBaritone(this.villager);
        CitizensMain.log("path to tree: " + this.villager.treePos);
        this.baritone.getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(this.villager.treePos));
        theTree.clear();
        this.index = -1;
    }

    List<BlockPos> theTree = new ArrayList<>();

    int index = -1;
    int delay = 0;

    @Override
    public void tick() {
        if (!this.baritone.isActive()) {
            delay--;
            if (delay > 0) return;

            if (theTree.isEmpty()) {
                mapTreeAt(this.villager.treePos);
            }

            this.index++;
            if (this.index >= theTree.size()){
                replantSapling();
                this.villager.treePos = null; // done
                return;
            }

            breakBlock(theTree.get(this.index));
        }
    }

    private void breakBlock(BlockPos pos) {
        CitizensMain.log("break block at " +  pos);
        this.villager.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, Vec3d.ofCenter(pos));
        BlockState state = this.villager.world.getBlockState(pos);
        if (state.isIn(BlockTags.LOGS)){
            this.villager.world.breakBlock(pos, true, null);
            ItemStack stack = this.villager.getStackInHand(Hand.MAIN_HAND);
            stack.setDamage(stack.getDamage() + 1);
            if (stack.getDamage() >= stack.getMaxDamage()){
                stack.decrement(1);
            }
            CitizensMain.log("use durability");

            this.villager.swingHand(Hand.MAIN_HAND);
            delay = 20 - ((int) stack.getItem().getMiningSpeedMultiplier(stack, state));
        }
        if (state.isIn(BlockTags.LEAVES)){
            this.villager.world.breakBlock(pos, true, this.villager);

            this.villager.swingHand(Hand.MAIN_HAND);
            delay = 3;
        }
    }

    private void replantSapling() {
        Inventory inv = this.villager.inventory;
        int sapIndex = getSapling();
        if (sapIndex >= 0){
            ItemStack sapling = inv.getStack(sapIndex);
            System.out.println("found sapling " + sapling);
            Block block = ((BlockItem)sapling.getItem()).getBlock();

            for (int i=0;i<5;i++){
                BlockState state = this.villager.world.getBlockState(this.villager.treePos);
                if (state.isAir()){
                    BlockState groundState = this.villager.world.getBlockState(this.villager.treePos.down());
                    if (groundState.getBlock() == Blocks.DIRT){
                        this.villager.world.setBlockState(this.villager.treePos, block.getDefaultState());
                    }
                }
            }
        }
    }

    private int getSapling() {
        Inventory inv = this.villager.inventory;
        for (int i=0;i<inv.size();i++){
            ItemStack check = inv.getStack(i);
            if (check.isIn(ItemTags.SAPLINGS)) return i;
        }

        // go to store house and grab a stack of saplings
        this.villager.itemsToGet.add(FetchType.SAPLING);
        BuildingBase storehouse = StoreHouseBuilding.findStoreHouseWith(this.villager.village, FetchType.SAPLING);
        if (storehouse != null) this.villager.startCommuteTo(storehouse);
        CitizensMain.log("need saplings. from " + storehouse);

        return -1;
    }

    private static final BlockPos[] directions = new BlockPos[26];
    static {
        int index = 0;
        for (int x=-1;x<2;x++){
            for (int y=-1;y<2;y++){
                for (int z=-1;z<2;z++){
                    if (x == 0 && y == 0 && z == 0) continue;
                    directions[index] = new BlockPos(x,y,z);
                    index++;
                }
            }
        }
    }

    private void mapTreeAt(BlockPos base) {
        HashSet<BlockPos> logs = new HashSet<>();
        HashSet<BlockPos> leaves = new HashSet<>();

        Set<BlockPos> traversed = new HashSet<>();
        Deque<BlockPos> toBeChecked = new ArrayDeque<>();
        toBeChecked.add(base);
        traversed.add(base);

        while (!toBeChecked.isEmpty()) {
            BlockPos check = toBeChecked.pop();
            BlockState state = this.villager.world.getBlockState(check);
            if (state.isIn(BlockTags.LOGS) && logs.add(check)) {
                for (BlockPos side : directions) {
                    BlockPos offset = check.add(side);
                    if (traversed.add(offset)) {
                        toBeChecked.add(offset);
                    }
                }
            }
            if (state.isIn(BlockTags.LEAVES) && leaves.add(check)) {
                for (BlockPos side : directions) {
                    BlockPos offset = check.add(side);
                    if (traversed.add(offset)) {
                        toBeChecked.add(offset);
                    }
                }
            }
        }

        this.theTree.addAll(logs);
        this.theTree.addAll(leaves);
    }
}

