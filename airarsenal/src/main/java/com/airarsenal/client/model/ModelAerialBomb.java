package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for Aerial Dropped Bombs (Iron Bomb, Heavy Bomb, Napalm, etc.).
 */
public class ModelAerialBomb extends ModelBase {

    public ModelRenderer bombBody;
    public ModelRenderer noseFuze;
    public ModelRenderer tailFinH;
    public ModelRenderer tailFinV;
    public ModelRenderer tailRingShroud;

    public ModelAerialBomb() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.bombBody = new ModelRenderer(this, 0, 0);
        this.bombBody.addBox(-3.5F, -3.5F, -10.0F, 7, 7, 20);
        this.bombBody.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.noseFuze = new ModelRenderer(this, 34, 0);
        this.noseFuze.addBox(-1.5F, -1.5F, 10.0F, 3, 3, 4);
        this.noseFuze.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.tailFinH = new ModelRenderer(this, 0, 0);
        this.tailFinH.addBox(-7.0F, -0.5F, -16.0F, 14, 1, 8);
        this.tailFinH.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.tailFinV = new ModelRenderer(this, 0, 0);
        this.tailFinV.addBox(-0.5F, -7.0F, -16.0F, 1, 14, 8);
        this.tailFinV.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.tailRingShroud = new ModelRenderer(this, 44, 0);
        this.tailRingShroud.addBox(-5.0F, -5.0F, -17.0F, 10, 10, 3);
        this.tailRingShroud.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        // Spin the arming vane fuze rapidly in the oncoming slipstream
        this.noseFuze.rotateAngleZ = ageInTicks * 1.8F;

        this.bombBody.render(scale);
        this.noseFuze.render(scale);
        this.tailFinH.render(scale);
        this.tailFinV.render(scale);
        this.tailRingShroud.render(scale);
    }
}
