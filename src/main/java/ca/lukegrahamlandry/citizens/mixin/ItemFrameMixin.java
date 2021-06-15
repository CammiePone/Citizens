package ca.lukegrahamlandry.citizens.mixin;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import ca.lukegrahamlandry.citizens.items.BuildingItem;
import ca.lukegrahamlandry.citizens.village.Village;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameMixin {
	@Inject(at = @At("HEAD"), method = "setHeldItemStack(Lnet/minecraft/item/ItemStack;Z)V")
	private void setHeldItemStack(ItemStack stack, boolean update, CallbackInfo ci) {
		ItemFrameEntity frame = (ItemFrameEntity)(Object)this;

		CitizensMain.log(stack.toString() + frame.getEntityWorld().isClient());
		if (stack.getItem() instanceof BuildingItem){
			BlockPos pos = frame.getBlockPos().offset(frame.getHorizontalFacing().getOpposite());
			BuildingBase building = ((BuildingItem) stack.getItem()).getBuilding(frame.getEntityWorld(), pos);
			boolean valid = building.validate();
			if (valid){
				building.displayFloorSpace();
				Village village = Village.findClosestVillage(pos);
				if (village == null) {
					CitizensMain.log("create village");
					village = new Village();
					Village.all.add(village);
				}
				village.buildings.add(building);
				CitizensMain.log("new buildings: " + village.buildings);
			}
		}
	}
}
