package ca.lukegrahamlandry.citizens.util;

import net.minecraft.item.ItemStack;

public interface IItemPredicate{
    boolean check(ItemStack stack);
}