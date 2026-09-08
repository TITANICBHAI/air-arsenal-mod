package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Supersonic Fighter Jet.
 * Features rolling tricycle landing gear, twin canted rudders, wingtip missiles, and afterburners.
 */
public class ModelFighterJet extends ModelBase {

    public ModelRenderer fuselage;
    public ModelRenderer noseCone;
    public ModelRenderer pitotProbe;
    public ModelRenderer canopy;
    public ModelRenderer sweptWings;
    public ModelRenderer leftIntake;
    public ModelRenderer rightIntake;
    public ModelRenderer leftTailFin;
    public ModelRenderer rightTailFin;
    public ModelRenderer leftMissile;
    public ModelRenderer rightMissile;
    public ModelRenderer leftAfterburner;
    public ModelRenderer rightAfterburner;

    // Tricycle Rolling Landing Gear Wheels
    public ModelRenderer wheelNose;
    public ModelRenderer wheelMainL;
    public ModelRenderer wheelMainR;

    public ModelFighterJet() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.fuselage = new ModelRenderer(this, 0, 0);
        this.fuselage.addBox(-7.0F, -5.0F, -26.0F, 14, 10, 52);
        this.fuselage.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.noseCone = new ModelRenderer(this, 80, 0);
        this.noseCone.addBox(-4.0F, -3.0F, 26.0F, 8, 6, 16);
        this.noseCone.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.pitotProbe = new ModelRenderer(this, 80, 22);
        this.pitotProbe.addBox(-0.5F, -0.5F, 42.0F, 1, 1, 6);
        this.pitotProbe.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.canopy = new ModelRenderer(this, 0, 0);
        this.canopy.addBox(-4.5F, -9.0F, 4.0F, 9, 4, 18);
        this.canopy.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Side Air Intakes
        this.leftIntake = new ModelRenderer(this, 56, 32);
        this.leftIntake.addBox(-10.0F, -4.0F, 6.0F, 3, 7, 16);
        this.leftIntake.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.rightIntake = new ModelRenderer(this, 56, 32);
        this.rightIntake.addBox(7.0F, -4.0F, 6.0F, 3, 7, 16);
        this.rightIntake.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.sweptWings = new ModelRenderer(this, 0, 48);
        this.sweptWings.addBox(-36.0F, 0.0F, -14.0F, 72, 2, 22);
        this.sweptWings.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Wingtip Sidewinder Missiles
        this.leftMissile = new ModelRenderer(this, 96, 42);
        this.leftMissile.addBox(-38.0F, -1.0F, -12.0F, 2, 2, 18);
        this.leftMissile.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.rightMissile = new ModelRenderer(this, 96, 42);
        this.rightMissile.addBox(36.0F, -1.0F, -12.0F, 2, 2, 18);
        this.rightMissile.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Twin angled vertical stabilizers
        this.leftTailFin = new ModelRenderer(this, 96, 22);
        this.leftTailFin.addBox(-1.0F, -16.0F, -24.0F, 2, 14, 10);
        this.leftTailFin.setRotationPoint(-6.0F, 0.0F, 0.0F);
        this.leftTailFin.rotateAngleZ = -0.15F;

        this.rightTailFin = new ModelRenderer(this, 96, 22);
        this.rightTailFin.addBox(-1.0F, -16.0F, -24.0F, 2, 14, 10);
        this.rightTailFin.setRotationPoint(6.0F, 0.0F, 0.0F);
        this.rightTailFin.rotateAngleZ = 0.15F;

        // Twin Afterburning Nozzles
        this.leftAfterburner = new ModelRenderer(this, 0, 20);
        this.leftAfterburner.addBox(-5.0F, -4.0F, -30.0F, 4, 8, 4);
        this.leftAfterburner.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.rightAfterburner = new ModelRenderer(this, 0, 20);
        this.rightAfterburner.addBox(1.0F, -4.0F, -30.0F, 4, 8, 4);
        this.rightAfterburner.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Tricycle Landing Gear
        this.wheelNose = new ModelRenderer(this, 116, 0);
        this.wheelNose.addBox(-1.5F, -3.0F, -3.0F, 3, 6, 6);
        this.wheelNose.setRotationPoint(0.0F, 8.0F, 22.0F);

        this.wheelMainL = new ModelRenderer(this, 116, 0);
        this.wheelMainL.addBox(-1.5F, -3.5F, -3.5F, 3, 7, 7);
        this.wheelMainL.setRotationPoint(-10.0F, 8.5F, -4.0F);

        this.wheelMainR = new ModelRenderer(this, 116, 0);
        this.wheelMainR.addBox(-1.5F, -3.5F, -3.5F, 3, 7, 7);
        this.wheelMainR.setRotationPoint(10.0F, 8.5F, -4.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.fuselage.render(scale);
        this.noseCone.render(scale);
        this.pitotProbe.render(scale);
        this.canopy.render(scale);
        this.leftIntake.render(scale);
        this.rightIntake.render(scale);
        this.sweptWings.render(scale);
        this.leftMissile.render(scale);
        this.rightMissile.render(scale);
        this.leftTailFin.render(scale);
        this.rightTailFin.render(scale);
        this.leftAfterburner.render(scale);
        this.rightAfterburner.render(scale);

        // Rudder deflection on canted stabilizers
        float yawRad = netHeadYaw * 0.017453292F;
        this.leftTailFin.rotateAngleY = yawRad * 0.4F;
        this.rightTailFin.rotateAngleY = yawRad * 0.4F;

        // Rolling tires
        float roll = limbSwing * 0.8F;
        this.wheelNose.rotateAngleX = roll;
        this.wheelMainL.rotateAngleX = roll;
        this.wheelMainR.rotateAngleX = roll;

        this.wheelNose.render(scale);
        this.wheelMainL.render(scale);
        this.wheelMainR.render(scale);
    }
}
