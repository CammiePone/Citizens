package ca.lukegrahamlandry.citizens.client.render;

import ca.lukegrahamlandry.citizens.client.model.VillagerModel;
import ca.lukegrahamlandry.citizens.entity.VillagerBase;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class VillagerRenderer extends LivingEntityRenderer<VillagerBase, VillagerModel> {
    public VillagerRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new VillagerModel(ctx.getPart(EntityModelLayers.PLAYER)), 0.5F);
        this.addFeature(new ArmorFeatureRenderer(this, new BipedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR))));
        this.addFeature(new PlayerHeldItemFeatureRenderer(this));
        this.addFeature(new StuckArrowsFeatureRenderer(ctx, this));
        // this.addFeature(new Deadmau5FeatureRenderer(this));
        // this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new HeadFeatureRenderer(this, ctx.getModelLoader()));
        this.addFeature(new ElytraFeatureRenderer(this, ctx.getModelLoader()));
        // this.addFeature(new ShoulderParrotFeatureRenderer(this, ctx.getModelLoader()));
        this.addFeature(new TridentRiptideFeatureRenderer(this, ctx.getModelLoader()));
        this.addFeature(new StuckStingersFeatureRenderer(this));
    }

    public void render(VillagerBase villager, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.setModelPose(villager);
        super.render(villager, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public Vec3d getPositionOffset(VillagerBase villager, float f) {
        return villager.isInSneakingPose() ? new Vec3d(0.0D, -0.125D, 0.0D) : super.getPositionOffset(villager, f);
    }

    private void setModelPose(VillagerBase player) {
        PlayerEntityModel<VillagerBase> playerEntityModel = (PlayerEntityModel)this.getModel();
        playerEntityModel.setVisible(true);
        playerEntityModel.hat.visible = true; // player.isPartVisible(PlayerModelPart.HAT);
        playerEntityModel.jacket.visible = true; // player.isPartVisible(PlayerModelPart.JACKET);
        playerEntityModel.leftPants.visible = true; // player.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
        playerEntityModel.rightPants.visible = true; // player.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
        playerEntityModel.leftSleeve.visible = true; // player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
        playerEntityModel.rightSleeve.visible = true; // player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
        playerEntityModel.sneaking = player.isInSneakingPose();
        BipedEntityModel.ArmPose armPose = getArmPose(player, Hand.MAIN_HAND);
        BipedEntityModel.ArmPose armPose2 = getArmPose(player, Hand.OFF_HAND);
        if (armPose.isTwoHanded()) {
            armPose2 = player.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
        }

        if (player.getMainArm() == Arm.RIGHT) {
            playerEntityModel.rightArmPose = armPose;
            playerEntityModel.leftArmPose = armPose2;
        } else {
            playerEntityModel.rightArmPose = armPose2;
            playerEntityModel.leftArmPose = armPose;
        }
    }

    private static BipedEntityModel.ArmPose getArmPose(VillagerBase player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        } else {
            if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack.getUseAction();
                if (useAction == UseAction.BLOCK) {
                    return BipedEntityModel.ArmPose.BLOCK;
                }

                if (useAction == UseAction.BOW) {
                    return BipedEntityModel.ArmPose.BOW_AND_ARROW;
                }

                if (useAction == UseAction.SPEAR) {
                    return BipedEntityModel.ArmPose.THROW_SPEAR;
                }

                if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
                    return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useAction == UseAction.SPYGLASS) {
                    return BipedEntityModel.ArmPose.SPYGLASS;
                }
            } else if (!player.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
            }

            return BipedEntityModel.ArmPose.ITEM;
        }
    }

    public Identifier getTexture(VillagerBase villager) {
        return villager.getTexture();
    }

    protected void scale(VillagerBase villager, MatrixStack matrixStack, float f) {
        float g = 0.9375F;
        matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    /* todo: random names? maybe some special name plate idk
    protected void renderLabelIfPresent(VillagerBase villager, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        double d = this.dispatcher.getSquaredDistanceToCamera(villager);
        matrixStack.push();
        if (d < 100.0D) {
            Scoreboard scoreboard = villager.getScoreboard();
            ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
            if (scoreboardObjective != null) {
                ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(villager.getEntityName(), scoreboardObjective);
                super.renderLabelIfPresent(villager, (new LiteralText(Integer.toString(scoreboardPlayerScore.getScore()))).append(" ").append(scoreboardObjective.getDisplayName()), matrixStack, vertexConsumerProvider, i);
                Objects.requireNonNull(this.getFontRenderer());
                matrixStack.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
            }
        }

        super.renderLabelIfPresent(villager, text, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
     */

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, VillagerBase player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).rightArm, ((PlayerEntityModel)this.model).rightSleeve);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, VillagerBase player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).leftArm, ((PlayerEntityModel)this.model).leftSleeve);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, VillagerBase villager, ModelPart arm, ModelPart sleeve) {
        PlayerEntityModel<VillagerBase> playerEntityModel = (PlayerEntityModel)this.getModel();
        this.setModelPose(villager);
        playerEntityModel.handSwingProgress = 0.0F;
        playerEntityModel.sneaking = false;
        playerEntityModel.leaningPitch = 0.0F;
        playerEntityModel.setAngles(villager, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        arm.pitch = 0.0F;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(villager.getTexture())), light, OverlayTexture.DEFAULT_UV);
        sleeve.pitch = 0.0F;
        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(villager.getTexture())), light, OverlayTexture.DEFAULT_UV);
    }

    protected void setupTransforms(VillagerBase villager, MatrixStack matrixStack, float f, float g, float h) {
        float i = villager.getLeaningPitch(h);
        float n;
        float k;
        if (villager.isFallFlying()) {
            super.setupTransforms(villager, matrixStack, f, g, h);
            n = (float)villager.getRoll() + h;
            k = MathHelper.clamp(n * n / 100.0F, 0.0F, 1.0F);
            if (!villager.isUsingRiptide()) {
                matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(k * (-90.0F - villager.getPitch())));
            }

            Vec3d vec3d = villager.getRotationVec(h);
            Vec3d vec3d2 = villager.getVelocity();
            double d = vec3d2.horizontalLengthSquared();
            double e = vec3d.horizontalLengthSquared();
            if (d > 0.0D && e > 0.0D) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((float)(Math.signum(m) * Math.acos(l))));
            }
        } else if (i > 0.0F) {
            super.setupTransforms(villager, matrixStack, f, g, h);
            n = villager.isTouchingWater() ? -90.0F - villager.getPitch() : -90.0F;
            k = MathHelper.lerp(i, 0.0F, n);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(k));
            if (villager.isInSwimmingPose()) {
                matrixStack.translate(0.0D, -1.0D, 0.30000001192092896D);
            }
        } else {
            super.setupTransforms(villager, matrixStack, f, g, h);
        }
    }
}
