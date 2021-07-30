package ca.lukegrahamlandry.citizens.entity;

import ca.lukegrahamlandry.citizens.CitizensMain;
import ca.lukegrahamlandry.citizens.village.buildings.BuildingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class NitWitEntity extends VillagerBase{
    public NitWitEntity(EntityType<NitWitEntity> entityType, World world) {
        super(entityType, world);
    }

    // todo: different default schedual thats all wander no work

    public boolean isValidWork(BuildingBase building){
        return false;
    }
}
