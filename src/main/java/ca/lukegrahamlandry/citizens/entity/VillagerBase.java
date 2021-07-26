package ca.lukegrahamlandry.citizens.entity;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.goals.CommuteGoal;
import ca.lukegrahamlandry.citizens.goals.VillagerSchedule;
import ca.lukegrahamlandry.citizens.village.Village;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import ca.lukegrahamlandry.citizens.village.buildings.HouseBuilding;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

// should eventually not be a PathAwareEntity and just use automatone for all pathfinding
public abstract class VillagerBase extends PathAwareEntity {
    protected BuildingBase home;
    public BuildingBase work;
    protected Village village;
    public MainInventory inventory = new MainInventory();

    protected VillagerBase(EntityType<? extends VillagerBase> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder attributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0D).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2D);
    }

    public abstract Identifier getTexture();

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new CommuteGoal(this));

        // TODO: Activity.PANIC
        // have to remake these to use automatone pathfinding
        // have fear (same as normal villagers)

        this.goalSelector.add(1, new FleeEntityGoal(this, ZombieEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, EvokerEntity.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, VindicatorEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, VexEntity.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, PillagerEntity.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, IllusionerEntity.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new FleeEntityGoal(this, ZoglinEntity.class, 10.0F, 0.5D, 0.5D));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 0.5D));

    }

    public enum Activity {
        WORK,
        SLEEP,
        COMMUTE,
        RESTOCK,
        PANIC,
        WANDER,
        HOME;
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isClient()){
            if (this.home == null) {
                this.tryFindAHome();
            } else if (this.work == null) {
                this.tryFindWork();
            }

            // todo: only check this every x ticks? idk if you want to be able to push them out of their home temporarily and have them wait a bit before running back

            Activity goal = this.getTargetActivity();
            boolean isCommuting = this.currentActivity == Activity.COMMUTE;
            // System.out.println("should " + goal + " is " + this.currentActivity + " commute " + isCommuting);

            if (!isCommuting && this.home != null && !this.atBuilding(this.home) && (goal == Activity.HOME || goal == Activity.SLEEP)){
                this.startCommuteTo(this.home);
                System.out.println("go home");
            }

            // todo: allow restocking without immediately running back to work
            if (!isCommuting && this.work != null && !this.atBuilding(this.work) && goal == Activity.WORK){
                this.startCommuteTo(this.work);
                System.out.println("go work");
            } else if (this.atBuilding(this.work) && goal == Activity.WORK && this.currentActivity != Activity.WORK){
                this.currentActivity = Activity.WORK;
                System.out.println("start work");
            }

            if (!isCommuting && goal == Activity.WANDER && this.getRandom().nextInt(200) == 0){
                int index = this.getRandom().nextInt(this.village.buildings.size());
                BuildingBase building = this.village.buildings.get(index);
                this.startCommuteTo(building);
            }

            if (this.isAwake() && goal == Activity.SLEEP && this.atBuilding(this.home)){
                // todo: start sleep animation
            }
        }
    }

    protected boolean isAwake(){
        // todo
        return true;
    }

    // do i have to do something clever with converting the center of hitbox to a blockpos
    public boolean atBuilding(BuildingBase building) {
        if (building == null) return false;
        for (BlockPos check : building.getFloorSpace()){
            if (check.equals(this.getBlockPos())) return true;
        }

        return false;


        // this seemed to not work
        // return building.getFloorSpace().contains(this.getBlockPos());
    }

    protected boolean tryFindAHome(){
        Village checkVillage = Village.findClosestVillage(this.getBlockPos());
        if (checkVillage == null) return false;
        for (BuildingBase building : checkVillage.buildings){
            if (building instanceof HouseBuilding){
                if (building.hasOpenSpace()) {
                    this.village = checkVillage;
                    building.villagers.add(this);
                    this.home = building;
                    checkVillage.villagers.add(this);
                    CitizensMain.log("found home at: " + building.getFirstInsidePos());
                    return true;
                }
            }
        }
        return false;
    }

    protected abstract boolean tryFindWork();

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (this.home != null){
            this.village.villagers.remove(this);
            this.home.villagers.remove(this);
        }
        if (this.work != null){
            this.work.villagers.remove(this);
        }
    }

    public Activity currentActivity;
    public BuildingBase commuteLocation;

    VillagerSchedule schedule = new VillagerSchedule();
    public Activity getTargetActivity(){
        return schedule.getCurrent(this.world.getTimeOfDay());
    }

    public void startCommuteTo(BuildingBase building){
        this.commuteLocation = building;
        this.currentActivity = Activity.COMMUTE;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(new NamedScreenHandler());
            return ActionResult.SUCCESS;
        }
    }

    class NamedScreenHandler implements NamedScreenHandlerFactory {
        @Override
        public Text getDisplayName() {
            return new LiteralText("Villager Inventory");
        }

        @Override
        public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, inv, VillagerBase.this.inventory, 4);
        }
    }

    // just for simple display for now
    // TODO: eventually make real ui on the villager that includes armor & happiness, etc
    // will have to do main hand & off hand properly
    // TODO: save in nbt

    class MainInventory implements Inventory {
        private final DefaultedList<ItemStack> main;

        public MainInventory(){
            this.main = DefaultedList.ofSize(36, ItemStack.EMPTY);
        }

        @Override
        public int size() {
            return this.main.size();
        }

        @Override
        public boolean isEmpty() {
            return this.main.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return this.main.get(slot);
        }


        // login taken from PlayerInventory
        @Override
        public ItemStack removeStack(int slot, int amount) {
            List<ItemStack> list = null;

            DefaultedList defaultedList;
            for(Iterator var4 = this.main.iterator(); var4.hasNext(); slot -= defaultedList.size()) {
                defaultedList = (DefaultedList)var4.next();
                if (slot < defaultedList.size()) {
                    list = defaultedList;
                    break;
                }
            }

            return list != null && !((ItemStack)list.get(slot)).isEmpty() ? Inventories.splitStack(list, slot, amount) : ItemStack.EMPTY;
        }

        // login taken from PlayerInventory
        @Override
        public ItemStack removeStack(int slot) {
            DefaultedList<ItemStack> defaultedList = null;

            DefaultedList defaultedList2;
            for(Iterator var3 = this.main.iterator(); var3.hasNext(); slot -= defaultedList2.size()) {
                defaultedList2 = (DefaultedList)var3.next();
                if (slot < defaultedList2.size()) {
                    defaultedList = defaultedList2;
                    break;
                }
            }

            if (defaultedList != null && !((ItemStack)defaultedList.get(slot)).isEmpty()) {
                ItemStack itemStack = (ItemStack)defaultedList.get(slot);
                defaultedList.set(slot, ItemStack.EMPTY);
                return itemStack;
            } else {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            this.main.set(slot, stack);
        }

        @Override
        public void markDirty() {
            ;
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            this.main.clear();
        }
    }
}
