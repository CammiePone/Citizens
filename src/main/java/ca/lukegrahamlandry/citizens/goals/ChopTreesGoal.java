package ca.lukegrahamlandry.citizens.goals;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalNear;
import ca.lukegrahamlandry.citizens.CitizensConfig;
import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.LumberJackEntity;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.util.FetchType;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.StoreHouseBuilding;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;

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
        alreadyChecked.clear();
        this.index = -1;
    }

    List<BlockPos> theTree = new ArrayList<>();
    List<BlockPos> alreadyChecked = new ArrayList<>();

    int index = -1;
    int delay = 0;

    @Override
    public void tick() {
        if (!this.baritone.isActive()) {
            delay--;
            if (delay > 0) return;

            if (theTree.isEmpty()) {
                CitizensMain.log("start mapping tree");
                mapTheTreeAt(this.villager.treePos);
                alreadyChecked.clear();
                CitizensMain.log("done mapping tree");
            }

            this.index++;
            if (this.index >= theTree.size()){
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

                this.villager.treePos = null;
                // done
                return;
            }

            BlockPos pos = theTree.get(this.index);
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
                delay = 10;
            }
            if (state.isIn(BlockTags.LEAVES)){
                this.villager.world.breakBlock(pos, true, this.villager);

                this.villager.swingHand(Hand.MAIN_HAND);
                delay = 10;
            }


        }
    }


    private void mapTheTreeAt(BlockPos treePos) {
        theTree.addAll(AOETreeUtil.getBlocks(this.villager.world, treePos));
        /*
        for (int i=0;i<6;i++){
            Direction dir = Direction.byId(i);
            BlockPos check = treePos.offset(dir);
            if (theTree.contains(check) || alreadyChecked.contains(check)) return;
            BlockState state = this.villager.world.getBlockState(check);
            if (state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.LEAVES)){
                theTree.add(check);
                mapTheTreeAt(check);
            } else {
                alreadyChecked.add(check);
            }
        }

         */
    }

    private int getSapling() {
        Inventory inv = this.villager.inventory;
        for (int i=0;i<inv.size();i++){
            ItemStack check = inv.getStack(i);
            if (CitizensConfig.isSeed(check.getItem())) return i;
        }

        // go to store house and grab a stack of seeds
        this.villager.itemsToGet.add(FetchType.SAPLING);
        BuildingBase storehouse = StoreHouseBuilding.findStoreHouseWith(this.villager.village, FetchType.SAPLING);
        this.villager.startCommuteTo(storehouse);
        CitizensMain.log("need saplings. from " + storehouse);

        return -1;
    }

    // from Actually Additions (MIT License)
    class AOETreeUtil {
        private static final BlockPos[] NEIGHBOR_POSITIONS = new BlockPos[26];

        static {
            NEIGHBOR_POSITIONS[0] = new BlockPos(1, 0, 0);
            NEIGHBOR_POSITIONS[1] = new BlockPos(-1, 0, 0);
            NEIGHBOR_POSITIONS[2] = new BlockPos(0, 0, 1);
            NEIGHBOR_POSITIONS[3] = new BlockPos(0, 0, -1);
            NEIGHBOR_POSITIONS[4] = new BlockPos(0, 1, 0);
            NEIGHBOR_POSITIONS[5] = new BlockPos(0, -1, 0);

            NEIGHBOR_POSITIONS[6] = new BlockPos(1, 0, 1);
            NEIGHBOR_POSITIONS[7] = new BlockPos(1, 0, -1);
            NEIGHBOR_POSITIONS[8] = new BlockPos(-1, 0, 1);
            NEIGHBOR_POSITIONS[9] = new BlockPos(-1, 0, -1);

            NEIGHBOR_POSITIONS[10] = new BlockPos(1, 1, 0);
            NEIGHBOR_POSITIONS[11] = new BlockPos(-1, 1, 0);
            NEIGHBOR_POSITIONS[12] = new BlockPos(0, 1, 1);
            NEIGHBOR_POSITIONS[13] = new BlockPos(0, 1, -1);

            NEIGHBOR_POSITIONS[14] = new BlockPos(1, -1, 0);
            NEIGHBOR_POSITIONS[15] = new BlockPos(-1, -1, 0);
            NEIGHBOR_POSITIONS[16] = new BlockPos(0, -1, 1);
            NEIGHBOR_POSITIONS[17] = new BlockPos(0, -1, -1);

            NEIGHBOR_POSITIONS[18] = new BlockPos(1, 1, 1);
            NEIGHBOR_POSITIONS[19] = new BlockPos(1, 1, -1);
            NEIGHBOR_POSITIONS[20] = new BlockPos(-1, 1, 1);
            NEIGHBOR_POSITIONS[21] = new BlockPos(-1, 1, -1);

            NEIGHBOR_POSITIONS[22] = new BlockPos(1, -1, 1);
            NEIGHBOR_POSITIONS[23] = new BlockPos(1, -1, -1);
            NEIGHBOR_POSITIONS[24] = new BlockPos(-1, -1, 1);
            NEIGHBOR_POSITIONS[25] = new BlockPos(-1, -1, -1);
        }

        public static List<BlockPos> getBlocks(World world, BlockPos base) {
            HashSet<BlockPos> known = new HashSet<>();
            Predicate<BlockState> matcher = state -> state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.LEAVES);
            walk(world, matcher, base, known);
            return new ArrayList<>(known);
        }

        private static void walk(World world, Predicate<BlockState> matcher, BlockPos base, HashSet<BlockPos> known) {
            Set<BlockPos> traversed = new HashSet<>();
            Deque<BlockPos> openSet = new ArrayDeque<>();
            openSet.add(base);
            traversed.add(base);

            while (!openSet.isEmpty()) {
                BlockPos ptr = openSet.pop();
                BlockState toCheck = world.getBlockState(ptr);
                if (matcher.test(toCheck) && known.add(ptr)) {
                    for (BlockPos side : NEIGHBOR_POSITIONS) {
                        BlockPos offset = ptr.add(side);
                        if (traversed.add(offset)) {
                            openSet.add(offset);
                        }
                    }
                }
            }
        }
    }
}

