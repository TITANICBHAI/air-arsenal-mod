package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Predator Drone (UAV).
 */
public class ModelPredatorDrone extends ModelBase {

    public ModelRenderer fuselage;
    public ModelRenderer avionicsBulb;
    public ModelRenderer longGliderWings;
    public ModelRenderer leftInvertedVFin;
    public ModelRenderer rightInvertedVFin;
    public ModelRenderer rearPusherPropeller;
    public ModelRenderer sensorTurret;

    public ModelPredatorDrone() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.fuselage = new ModelRenderer(this, 0, 0);
        this.fuselage.addBox(-4.0F, -4.0F, -20.0F, 8, 8, 40);
        this.fuselage.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.avionicsBulb = new ModelRenderer(this, 56, 0);
        this.avionicsBulb.addBox(-4.5F, -6.5F, 6.0F, 9, 3, 14);
        this.avionicsBulb.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.longGliderWings = new ModelRenderer(this, 0, 48);
        this.longGliderWings.addBox(-44.0F, -1.0F, -3.0F, 88, 2, 6);
        this.longGliderWings.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Inverted V-Tail Fins
        this.leftInvertedVFin = new ModelRenderer(this, 96, 0);
        this.leftInvertedVFin.addBox(-1.0F, 0.0F, -20.0F, 2, 10, 6);
        this.leftInvertedVFin.setRotationPoint(-3.0F, 0.0F, 0.0F);
        this.leftInvertedVFin.rotateAngleZ = 0.45F;

        this.rightInvertedVFin = new ModelRenderer(this, 96, 0);
        this.rightInvertedVFin.addBox(-1.0F, 0.0F, -20.0F, 2, 10, 6);
        this.rightInvertedVFin.setRotationPoint(3.0F, 0.0F, 0.0F);
        this.rightInvertedVFin.rotateAngleZ = -0.45F;

        // Sensor ball under nose
        this.sensorTurret = new ModelRenderer(this, 0, 0);
        this.sensorTurret.addBox(-2.5F, 4.0F, 14.0F, 5, 5, 5);
        this.sensorTurret.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Rear pusher propeller
        this.rearPusherPropeller = new ModelRenderer(this, 0, 20);
        this.rearPusherPropeller.addBox(-8.0F, -1.0F, -21.0F, 16, 2, 1);
        this.rearPusherPropeller.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.fuselage.render(scale);
        this.avionicsBulb.render(scale);
        this.longGliderWings.render(scale);
        this.leftInvertedVFin.render(scale);
        this.rightInvertedVFin.render(scale);
        this.sensorTurret.render(scale);

        this.rearPusherPropeller.rotateAngleZ = ageInTicks * 0.95F;
        this.rearPusherPropeller.render(scale);
    }
}
