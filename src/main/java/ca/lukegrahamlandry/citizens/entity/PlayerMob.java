package ca.lukegrahamlandry.citizens.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

// the goal with this is better mod compatibility (like item right click methods working)
// may have to do client and server separately at some point if some mod does weird rendering things

// TODO: they are not saved when you reload the world. absolutly no idea why

public abstract class PlayerMob extends PlayerEntity {
    protected final GoalSelector goalSelector;
    EntityType<? extends PlayerMob> realType;
    public PlayerMob(EntityType<? extends PlayerMob> type, World world) {
        super(world, BlockPos.ORIGIN, 0, new GameProfile(UUID.randomUUID(), "Not A Player"));
        this.realType = type;
        this.goalSelector = new GoalSelector(world.getProfilerSupplier());
        if (world != null && !world.isClient) {
            this.initGoals();
        }
    }

    @Override
    public EntityType getType(){
        return this.realType;
    }

    protected void initGoals() {

    }

    @Override
    public void tick() {
        super.tick();

        this.world.getProfiler().push("goalSelector");
        this.goalSelector.tick();
        this.world.getProfiler().pop();
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
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
            return new LiteralText("Inventory");
        }

        @Override
        public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, inv, new MainInventory(), 4);
        }
    }

    // just for simple display for now
    // eventually make real ui on the villager that includes armor & happiness, etc

    class MainInventory implements Inventory {
        public MainInventory(){

        }

        @Override
        public int size() {
            return PlayerMob.this.getInventory().main.size();
        }

        @Override
        public boolean isEmpty() {
            return PlayerMob.this.getInventory().main.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return PlayerMob.this.getInventory().main.get(slot);
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return PlayerMob.this.getInventory().removeStack(slot, amount);
        }

        @Override
        public ItemStack removeStack(int slot) {
            return PlayerMob.this.getInventory().removeStack(slot);
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
            PlayerMob.this.getInventory().setStack(slot, stack);
        }

        @Override
        public void markDirty() {
            PlayerMob.this.getInventory().markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return true;
        }

        @Override
        public void clear() {
            PlayerMob.this.getInventory().clear();
        }
    }
}
