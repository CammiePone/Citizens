package ca.lukegrahamlandry.citizens;

import com.google.common.collect.ImmutableList;
import io.netty.channel.group.DefaultChannelGroup;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class CitizensConfig {
    // todo: actually do this. autoconfig dependency + tags



    // todo: use tag instead
    private static List<Item> seeds = ImmutableList.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.POTATO, Items.CARROT);
    public static boolean isSeed(Item item){
        return seeds.contains(item);
    }
}
