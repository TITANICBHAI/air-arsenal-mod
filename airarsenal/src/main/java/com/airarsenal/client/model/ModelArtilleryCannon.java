package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for Artillery & Emplacements (AA Cannon, Flak, Howitzer).
 */
public class ModelArtilleryCannon extends ModelBase {

    public ModelRenderer pedestalBase;
    public ModelRenderer swivelRing;
    public ModelRenderer trunnionLeft;
    public ModelRenderer trunnionRight;
    public ModelRenderer gunBreech;
    public ModelRenderer leftBarrel;
    public ModelRenderer rightBarrel;

    public ModelArtilleryCannon() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.pedestalBase = new ModelRenderer(this, 0, 0);
        this.pedestalBase.addBox(-14.0F, 0.0F, -14.0F, 28, 4, 28);
        this.pedestalBase.setRotationPoint(0.0F, 20.0F, 0.0F);

        this.swivelRing = new ModelRenderer(this, 0, 32);
        this.swivelRing.addBox(-10.0F, -4.0F, -10.0F, 20, 4, 20);
        this.swivelRing.setRotationPoint(0.0F, 20.0F, 0.0F);

        this.trunnionLeft = new ModelRenderer(this, 84, 0);
        this.trunnionLeft.addBox(-8.0F, -14.0F, -4.0F, 3, 10, 8);
        this.trunnionLeft.setRotationPoint(0.0F, 20.0F, 0.0F);

        this.trunnionRight = new ModelRenderer(this, 84, 0);
        this.trunnionRight.addBox(5.0F, -14.0F, -4.0F, 3, 10, 8);
        this.trunnionRight.setRotationPoint(0.0F, 20.0F, 0.0F);

        // Elevating gun assembly
        this.gunBreech = new ModelRenderer(this, 0, 0);
        this.gunBreech.addBox(-4.0F, -14.0F, -8.0F, 8, 8, 12);
        this.gunBreech.setRotationPoint(0.0F, 20.0F, 0.0F);

        this.leftBarrel = new ModelRenderer(this, 106, 0);
        this.leftBarrel.addBox(-3.0F, -12.0F, 4.0F, 2, 2, 28);
        this.leftBarrel.setRotationPoint(0.0F, 20.0F, 0.0F);

        this.rightBarrel = new ModelRenderer(this, 106, 0);
        this.rightBarrel.addBox(1.0F, -12.0F, 4.0F, 2, 2, 28);
        this.rightBarrel.setRotationPoint(0.0F, 20.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.pedestalBase.render(scale);

        // Swivel
        float yawRad = netHeadYaw * 0.017453292F;
        float pitchRad = headPitch * 0.017453292F;

        this.swivelRing.rotateAngleY = yawRad;
        this.trunnionLeft.rotateAngleY = yawRad;
        this.trunnionRight.rotateAngleY = yawRad;
        this.gunBreech.rotateAngleY = yawRad;
        this.gunBreech.rotateAngleX = -pitchRad;

        this.leftBarrel.rotateAngleY = yawRad;
        this.leftBarrel.rotateAngleX = -pitchRad;
        this.rightBarrel.rotateAngleY = yawRad;
        this.rightBarrel.rotateAngleX = -pitchRad;

        // Alternating twin barrel recoil kickback
        int cycle = (int) (ageInTicks % 20);
        float lRecoil = (cycle < 4) ? -3.0F * (1.0F - cycle / 4.0F) : 0.0F;
        float rRecoil = (cycle >= 10 && cycle < 14) ? -3.0F * (1.0F - (cycle - 10) / 4.0F) : 0.0F;
        this.leftBarrel.setRotationPoint(0.0F, 20.0F, lRecoil);
        this.rightBarrel.setRotationPoint(0.0F, 20.0F, rRecoil);

        this.swivelRing.render(scale);
        this.trunnionLeft.render(scale);
        this.trunnionRight.render(scale);
        this.gunBreech.render(scale);
        this.leftBarrel.render(scale);
        this.rightBarrel.render(scale);
    }
}
