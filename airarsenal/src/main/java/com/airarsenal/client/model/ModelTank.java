package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Main Battle Tank.
 * Features 360-degree rotating turret, detailed 120mm cannon, and 10 rolling road wheels.
 */
public class ModelTank extends ModelBase {

    public ModelRenderer lowerHull;
    public ModelRenderer upperGlacis;
    public ModelRenderer leftSkirt;
    public ModelRenderer rightSkirt;
    public ModelRenderer turret;
    public ModelRenderer cupola;
    public ModelRenderer smokeGrenadesL;
    public ModelRenderer smokeGrenadesR;
    public ModelRenderer gunMantlet;
    public ModelRenderer mainGunBarrel;
    public ModelRenderer muzzleBrake;

    // 10 Rolling Road Wheels
    public ModelRenderer[] leftWheels = new ModelRenderer[5];
    public ModelRenderer[] rightWheels = new ModelRenderer[5];

    public ModelTank() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.lowerHull = new ModelRenderer(this, 0, 0);
        this.lowerHull.addBox(-12.0F, -4.0F, -20.0F, 24, 8, 40);
        this.lowerHull.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.upperGlacis = new ModelRenderer(this, 0, 48);
        this.upperGlacis.addBox(-10.0F, -6.0F, -18.0F, 20, 2, 36);
        this.upperGlacis.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Armored skirts
        this.leftSkirt = new ModelRenderer(this, 88, 0);
        this.leftSkirt.addBox(-15.0F, -5.0F, -22.0F, 2, 8, 44);
        this.leftSkirt.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.rightSkirt = new ModelRenderer(this, 88, 0);
        this.rightSkirt.addBox(13.0F, -5.0F, -22.0F, 2, 8, 44);
        this.rightSkirt.setRotationPoint(0.0F, 16.0F, 0.0F);

        // 10 Road wheels
        for (int i = 0; i < 5; i++) {
            float zOffset = -16.0F + (i * 8.0F);

            this.leftWheels[i] = new ModelRenderer(this, 0, 24);
            this.leftWheels[i].addBox(-2.0F, -3.0F, -3.0F, 4, 6, 6);
            this.leftWheels[i].setRotationPoint(-13.0F, 18.0F, zOffset);

            this.rightWheels[i] = new ModelRenderer(this, 0, 24);
            this.rightWheels[i].addBox(-2.0F, -3.0F, -3.0F, 4, 6, 6);
            this.rightWheels[i].setRotationPoint(13.0F, 18.0F, zOffset);
        }

        // Rotating Turret
        this.turret = new ModelRenderer(this, 0, 16);
        this.turret.addBox(-9.0F, -12.0F, -10.0F, 18, 6, 22);
        this.turret.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.cupola = new ModelRenderer(this, 56, 16);
        this.cupola.addBox(-6.0F, -14.0F, -4.0F, 5, 2, 5);
        this.cupola.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.smokeGrenadesL = new ModelRenderer(this, 80, 20);
        this.smokeGrenadesL.addBox(-11.0F, -11.0F, 0.0F, 2, 3, 5);
        this.smokeGrenadesL.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.smokeGrenadesR = new ModelRenderer(this, 80, 20);
        this.smokeGrenadesR.addBox(9.0F, -11.0F, 0.0F, 2, 3, 5);
        this.smokeGrenadesR.setRotationPoint(0.0F, 16.0F, 0.0F);

        // 120mm Smoothbore Gun & Mantlet
        this.gunMantlet = new ModelRenderer(this, 56, 24);
        this.gunMantlet.addBox(-4.0F, -11.5F, 12.0F, 8, 5, 4);
        this.gunMantlet.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.mainGunBarrel = new ModelRenderer(this, 0, 0);
        this.mainGunBarrel.addBox(-1.5F, -10.5F, 16.0F, 3, 3, 28);
        this.mainGunBarrel.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.muzzleBrake = new ModelRenderer(this, 0, 0);
        this.muzzleBrake.addBox(-2.5F, -11.5F, 44.0F, 5, 5, 4);
        this.muzzleBrake.setRotationPoint(0.0F, 16.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.lowerHull.render(scale);
        this.upperGlacis.render(scale);
        this.leftSkirt.render(scale);
        this.rightSkirt.render(scale);

        // Animate rolling road wheels
        float wheelRot = limbSwing * 0.75F;
        for (int i = 0; i < 5; i++) {
            this.leftWheels[i].rotateAngleX = wheelRot;
            this.rightWheels[i].rotateAngleX = wheelRot;
            this.leftWheels[i].render(scale);
            this.rightWheels[i].render(scale);
        }

        // Turret yaw & gun pitch
        float yawRad = netHeadYaw * 0.017453292F;
        float pitchRad = headPitch * 0.017453292F;

        // Real-world hydro-pneumatic 120mm cannon recoil stroke (buffer + recuperator)
        float cycleTick = (ageInTicks % 60.0F);
        float recoilOffset = com.airarsenal.client.util.VehicleKinematicsHelper.calculateHydroPneumaticRecoil(
            cycleTick, 2.0F, 12.0F, 2.8F
        );

        this.turret.rotateAngleY = yawRad;
        this.cupola.rotateAngleY = yawRad;
        this.smokeGrenadesL.rotateAngleY = yawRad;
        this.smokeGrenadesR.rotateAngleY = yawRad;
        this.gunMantlet.rotateAngleY = yawRad;
        this.mainGunBarrel.rotateAngleY = yawRad;
        this.muzzleBrake.rotateAngleY = yawRad;

        this.gunMantlet.rotateAngleX = pitchRad;
        this.mainGunBarrel.rotateAngleX = pitchRad;
        this.muzzleBrake.rotateAngleX = pitchRad;

        this.mainGunBarrel.setRotationPoint(0.0F, 16.0F, recoilOffset);
        this.muzzleBrake.setRotationPoint(0.0F, 16.0F, recoilOffset);

        this.turret.render(scale);
        this.cupola.render(scale);
        this.smokeGrenadesL.render(scale);
        this.smokeGrenadesR.render(scale);
        this.gunMantlet.render(scale);
        this.mainGunBarrel.render(scale);
        this.muzzleBrake.render(scale);
    }
}
