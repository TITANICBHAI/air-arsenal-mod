package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Stealth Flying Wing Bomber.
 */
public class ModelStealthBomber extends ModelBase {

    public ModelRenderer centerBody;
    public ModelRenderer cockpitRidge;
    public ModelRenderer leftWing;
    public ModelRenderer rightWing;
    public ModelRenderer leftExhaust;
    public ModelRenderer rightExhaust;

    public ModelStealthBomber() {
        this.textureWidth = 256;
        this.textureHeight = 128;

        this.centerBody = new ModelRenderer(this, 0, 0);
        this.centerBody.addBox(-12.0F, -4.0F, -18.0F, 24, 8, 36);
        this.centerBody.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.cockpitRidge = new ModelRenderer(this, 84, 0);
        this.cockpitRidge.addBox(-6.0F, -6.5F, 0.0F, 12, 3, 14);
        this.cockpitRidge.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Huge swept delta wing panels
        this.leftWing = new ModelRenderer(this, 0, 44);
        this.leftWing.addBox(-48.0F, -2.0F, -14.0F, 48, 4, 28);
        this.leftWing.setRotationPoint(-12.0F, 0.0F, 0.0F);

        this.rightWing = new ModelRenderer(this, 0, 44);
        this.rightWing.addBox(0.0F, -2.0F, -14.0F, 48, 4, 28);
        this.rightWing.setRotationPoint(12.0F, 0.0F, 0.0F);

        this.leftExhaust = new ModelRenderer(this, 0, 0);
        this.leftExhaust.addBox(-8.0F, -2.0F, -22.0F, 6, 3, 4);
        this.leftExhaust.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.rightExhaust = new ModelRenderer(this, 0, 0);
        this.rightExhaust.addBox(2.0F, -2.0F, -22.0F, 6, 3, 4);
        this.rightExhaust.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.centerBody.render(scale);
        this.cockpitRidge.render(scale);
        this.leftWing.render(scale);
        this.rightWing.render(scale);
        this.leftExhaust.render(scale);
        this.rightExhaust.render(scale);
    }
}
