package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for Guided Missiles (Hellfire, BrahMos, MANPADS, etc.).
 */
public class ModelGuidedMissile extends ModelBase {

    public ModelRenderer missileBody;
    public ModelRenderer seekerNose;
    public ModelRenderer finH1;
    public ModelRenderer finH2;
    public ModelRenderer finV1;
    public ModelRenderer finV2;
    public ModelRenderer exhaustNozzle;

    public ModelGuidedMissile() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.missileBody = new ModelRenderer(this, 0, 0);
        this.missileBody.addBox(-2.0F, -2.0F, -14.0F, 4, 4, 28);
        this.missileBody.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.seekerNose = new ModelRenderer(this, 36, 0);
        this.seekerNose.addBox(-1.5F, -1.5F, 14.0F, 3, 3, 6);
        this.seekerNose.setRotationPoint(0.0F, 0.0F, 0.0F);

        // 4 Cruciform steering & stabilizing fins
        this.finH1 = new ModelRenderer(this, 0, 0);
        this.finH1.addBox(-6.0F, -0.5F, -12.0F, 4, 1, 6);
        this.finH1.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.finH2 = new ModelRenderer(this, 0, 0);
        this.finH2.addBox(2.0F, -0.5F, -12.0F, 4, 1, 6);
        this.finH2.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.finV1 = new ModelRenderer(this, 0, 0);
        this.finV1.addBox(-0.5F, -6.0F, -12.0F, 1, 4, 6);
        this.finV1.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.finV2 = new ModelRenderer(this, 0, 0);
        this.finV2.addBox(-0.5F, 2.0F, -12.0F, 1, 4, 6);
        this.finV2.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.exhaustNozzle = new ModelRenderer(this, 0, 20);
        this.exhaustNozzle.addBox(-1.5F, -1.5F, -16.0F, 3, 3, 2);
        this.exhaustNozzle.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.missileBody.render(scale);
        this.seekerNose.render(scale);
        this.finH1.render(scale);
        this.finH2.render(scale);
        this.finV1.render(scale);
        this.finV2.render(scale);
        this.exhaustNozzle.render(scale);
    }
}
