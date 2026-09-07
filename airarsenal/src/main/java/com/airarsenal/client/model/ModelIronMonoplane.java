package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Iron Monoplane.
 * Features cantilever wings, underwing bomb pylons, spinning metal propeller, and rolling rubber tires.
 */
public class ModelIronMonoplane extends ModelBase {

    public ModelRenderer fuselage;
    public ModelRenderer engineNose;
    public ModelRenderer cockpitGlass;
    public ModelRenderer wings;
    public ModelRenderer pylonL;
    public ModelRenderer pylonR;
    public ModelRenderer bombL;
    public ModelRenderer bombR;
    public ModelRenderer rudder;
    public ModelRenderer horizontalStabilizer;
    public ModelRenderer propeller;

    // Rolling landing gear
    public ModelRenderer gearStrutL;
    public ModelRenderer gearStrutR;
    public ModelRenderer wheelL;
    public ModelRenderer wheelR;
    public ModelRenderer tailWheel;

    public ModelIronMonoplane() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.fuselage = new ModelRenderer(this, 0, 0);
        this.fuselage.addBox(-6.0F, -6.0F, -20.0F, 12, 12, 40);
        this.fuselage.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.engineNose = new ModelRenderer(this, 64, 0);
        this.engineNose.addBox(-5.5F, -5.5F, 20.0F, 11, 11, 8);
        this.engineNose.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.cockpitGlass = new ModelRenderer(this, 64, 19);
        this.cockpitGlass.addBox(-4.0F, -10.0F, 0.0F, 8, 4, 12);
        this.cockpitGlass.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.wings = new ModelRenderer(this, 0, 52);
        this.wings.addBox(-38.0F, 0.0F, -5.0F, 76, 2, 10);
        this.wings.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Underwing Ordnance Pylons & Bombs
        this.pylonL = new ModelRenderer(this, 64, 35);
        this.pylonL.addBox(-16.0F, 2.0F, -2.0F, 2, 3, 4);
        this.pylonL.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.pylonR = new ModelRenderer(this, 64, 35);
        this.pylonR.addBox(14.0F, 2.0F, -2.0F, 2, 3, 4);
        this.pylonR.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.bombL = new ModelRenderer(this, 80, 35);
        this.bombL.addBox(-17.0F, 5.0F, -4.0F, 4, 4, 8);
        this.bombL.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.bombR = new ModelRenderer(this, 80, 35);
        this.bombR.addBox(13.0F, 5.0F, -4.0F, 4, 4, 8);
        this.bombR.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.rudder = new ModelRenderer(this, 96, 0);
        this.rudder.addBox(-1.0F, -16.0F, -26.0F, 2, 12, 8);
        this.rudder.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.horizontalStabilizer = new ModelRenderer(this, 0, 0);
        this.horizontalStabilizer.addBox(-14.0F, -2.0F, -26.0F, 28, 2, 8);
        this.horizontalStabilizer.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.propeller = new ModelRenderer(this, 0, 20);
        this.propeller.addBox(-14.0F, -2.0F, 28.5F, 28, 4, 1);
        this.propeller.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Landing Gear & Tires
        this.gearStrutL = new ModelRenderer(this, 96, 20);
        this.gearStrutL.addBox(-10.0F, 6.0F, 4.0F, 2, 6, 2);
        this.gearStrutL.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.gearStrutR = new ModelRenderer(this, 96, 20);
        this.gearStrutR.addBox(8.0F, 6.0F, 4.0F, 2, 6, 2);
        this.gearStrutR.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.wheelL = new ModelRenderer(this, 104, 20);
        this.wheelL.addBox(-1.5F, -3.5F, -3.5F, 3, 7, 7);
        this.wheelL.setRotationPoint(-9.0F, 12.0F, 5.0F);

        this.wheelR = new ModelRenderer(this, 104, 20);
        this.wheelR.addBox(-1.5F, -3.5F, -3.5F, 3, 7, 7);
        this.wheelR.setRotationPoint(9.0F, 12.0F, 5.0F);

        this.tailWheel = new ModelRenderer(this, 104, 34);
        this.tailWheel.addBox(-1.0F, -2.0F, -2.0F, 2, 4, 4);
        this.tailWheel.setRotationPoint(0.0F, 6.0F, -20.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.fuselage.render(scale);
        this.engineNose.render(scale);
        this.cockpitGlass.render(scale);
        this.wings.render(scale);
        this.pylonL.render(scale);
        this.pylonR.render(scale);
        this.bombL.render(scale);
        this.bombR.render(scale);
        this.rudder.render(scale);
        this.horizontalStabilizer.render(scale);
        this.gearStrutL.render(scale);
        this.gearStrutR.render(scale);

        // Spin propeller
        this.propeller.rotateAngleZ = ageInTicks * 0.9F;
        this.propeller.render(scale);

        // Animate rolling tires
        float roll = limbSwing * 0.7F;
        this.wheelL.rotateAngleX = roll;
        this.wheelR.rotateAngleX = roll;
        this.tailWheel.rotateAngleX = roll;

        this.wheelL.render(scale);
        this.wheelR.render(scale);
        this.tailWheel.render(scale);
    }
}
