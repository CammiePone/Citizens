package ca.lukegrahamlandry.citizens.util;

import ca.lukegrahamlandry.citizens.CitizensConfig;
import com.mojang.datafixers.types.Func;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.function.Function;


// instead of just getting the first valid stack of seeds you should be able too use a gui to select which seeds to plant

public enum FetchType implements IItemPredicate {
    SEEDS((stack) -> CitizensConfig.isSeed(stack.getItem()), new ItemStack(Items.WHEAT_SEEDS)),
    AXE((stack) -> stack.getItem() instanceof AxeItem, new ItemStack(Items.IRON_AXE));

    private final Function<ItemStack, Boolean> itemPredicate;
    private final ItemStack displayStack;  // if we ever want to do thought bubbles like tektopia

    FetchType(Function<ItemStack, Boolean> itemPredicate, ItemStack displayStack) {
        this.itemPredicate = itemPredicate;
        this.displayStack = displayStack;
    }

    @Override
    public boolean check(ItemStack stack) {
        return this.itemPredicate.apply(stack);
    }
}

