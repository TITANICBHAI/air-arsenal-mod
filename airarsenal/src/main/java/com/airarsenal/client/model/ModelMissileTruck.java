package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Missile Command Truck.
 * Features steerable front tires, rolling 6x6 wheels, hydraulic pistons, and elevated launch tubes.
 */
public class ModelMissileTruck extends ModelBase {

    public ModelRenderer chassis;
    public ModelRenderer bullbar;
    public ModelRenderer cab;
    public ModelRenderer windshieldArmor;
    public ModelRenderer launchPistonL;
    public ModelRenderer launchPistonR;
    public ModelRenderer leftMissileTube;
    public ModelRenderer rightMissileTube;
    public ModelRenderer missileNoseL;
    public ModelRenderer missileNoseR;

    // 6 Heavy Off-Road Rolling Wheels
    public ModelRenderer wheelFL;
    public ModelRenderer wheelFR;
    public ModelRenderer wheelML;
    public ModelRenderer wheelMR;
    public ModelRenderer wheelRL;
    public ModelRenderer wheelRR;

    public ModelMissileTruck() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.chassis = new ModelRenderer(this, 0, 0);
        this.chassis.addBox(-10.0F, -4.0F, -24.0F, 20, 6, 48);
        this.chassis.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Front bullbar & recovery winch
        this.bullbar = new ModelRenderer(this, 88, 32);
        this.bullbar.addBox(-10.5F, -2.0F, 24.0F, 21, 4, 3);
        this.bullbar.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Armored Cab
        this.cab = new ModelRenderer(this, 0, 0);
        this.cab.addBox(-9.5F, -12.0F, 8.0F, 19, 8, 14);
        this.cab.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.windshieldArmor = new ModelRenderer(this, 56, 48);
        this.windshieldArmor.addBox(-8.5F, -11.0F, 22.0F, 17, 6, 1);
        this.windshieldArmor.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Hydraulic lift pistons
        this.launchPistonL = new ModelRenderer(this, 0, 32);
        this.launchPistonL.addBox(-6.0F, -6.0F, -6.0F, 2, 8, 2);
        this.launchPistonL.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.launchPistonR = new ModelRenderer(this, 0, 32);
        this.launchPistonR.addBox(4.0F, -6.0F, -6.0F, 2, 8, 2);
        this.launchPistonR.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Elevated twin missile launch tubes
        this.leftMissileTube = new ModelRenderer(this, 88, 0);
        this.leftMissileTube.addBox(-7.0F, -12.0F, -20.0F, 5, 5, 26);
        this.leftMissileTube.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.leftMissileTube.rotateAngleX = -0.35F;

        this.rightMissileTube = new ModelRenderer(this, 88, 0);
        this.rightMissileTube.addBox(2.0F, -12.0F, -20.0F, 5, 5, 26);
        this.rightMissileTube.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.rightMissileTube.rotateAngleX = -0.35F;

        // Visible missile noses inside canisters
        this.missileNoseL = new ModelRenderer(this, 56, 32);
        this.missileNoseL.addBox(-6.0F, -11.0F, 6.0F, 3, 3, 4);
        this.missileNoseL.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.missileNoseL.rotateAngleX = -0.35F;

        this.missileNoseR = new ModelRenderer(this, 56, 32);
        this.missileNoseR.addBox(3.0F, -11.0F, 6.0F, 3, 3, 4);
        this.missileNoseR.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.missileNoseR.rotateAngleX = -0.35F;

        // 6 Heavy Off-Road Road Wheels with center rotation points
        this.wheelFL = new ModelRenderer(this, 0, 54);
        this.wheelFL.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelFL.setRotationPoint(-11.5F, 20.0F, 14.0F);

        this.wheelFR = new ModelRenderer(this, 0, 54);
        this.wheelFR.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelFR.setRotationPoint(11.5F, 20.0F, 14.0F);

        this.wheelML = new ModelRenderer(this, 0, 54);
        this.wheelML.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelML.setRotationPoint(-11.5F, 20.0F, -4.0F);

        this.wheelMR = new ModelRenderer(this, 0, 54);
        this.wheelMR.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelMR.setRotationPoint(11.5F, 20.0F, -4.0F);

        this.wheelRL = new ModelRenderer(this, 0, 54);
        this.wheelRL.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelRL.setRotationPoint(-11.5F, 20.0F, -17.0F);

        this.wheelRR = new ModelRenderer(this, 0, 54);
        this.wheelRR.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelRR.setRotationPoint(11.5F, 20.0F, -17.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.chassis.render(scale);
        this.bullbar.render(scale);
        this.cab.render(scale);
        this.windshieldArmor.render(scale);
        this.launchPistonL.render(scale);
        this.launchPistonR.render(scale);
        this.leftMissileTube.render(scale);
        this.rightMissileTube.render(scale);
        this.missileNoseL.render(scale);
        this.missileNoseR.render(scale);

        // Animate rolling tires during vehicle movement
        float wheelRoll = limbSwing * 0.7F;
        float steerAngle = netHeadYaw * 0.017453292F * 0.4F;

        // Front steerable rolling wheels
        this.wheelFL.rotateAngleX = wheelRoll;
        this.wheelFL.rotateAngleY = steerAngle;
        this.wheelFR.rotateAngleX = wheelRoll;
        this.wheelFR.rotateAngleY = steerAngle;

        // Rear rolling wheels
        this.wheelML.rotateAngleX = wheelRoll;
        this.wheelMR.rotateAngleX = wheelRoll;
        this.wheelRL.rotateAngleX = wheelRoll;
        this.wheelRR.rotateAngleX = wheelRoll;

        this.wheelFL.render(scale);
        this.wheelFR.render(scale);
        this.wheelML.render(scale);
        this.wheelMR.render(scale);
        this.wheelRL.render(scale);
        this.wheelRR.render(scale);
    }
}
