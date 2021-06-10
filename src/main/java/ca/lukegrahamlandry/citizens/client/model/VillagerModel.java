package ca.lukegrahamlandry.citizens.client.model;

import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class VillagerModel extends PlayerEntityModel<VillagerBase> {
    public VillagerModel(ModelPart root) {
        super(root, false);
    }
}
