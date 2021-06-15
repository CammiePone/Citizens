package ca.lukegrahamlandry.citizens.entity;

import ca.lukegrahamlandry.citizens.CitizensMain;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class NitWitEntity extends VillagerBase{
    public NitWitEntity(EntityType<NitWitEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return new Identifier(CitizensMain.MOD_ID, "textures/entity/nitwit");
    }

    // todo: different default schedual thats all wander no work

    @Override
    protected boolean tryFindWork() {
        // doesnt work anywhere;
        return false;
    }
}
