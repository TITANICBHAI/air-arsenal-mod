package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Attack Helicopter.
 */
public class ModelAttackHelicopter extends ModelBase {

    public ModelRenderer cockpitHull;
    public ModelRenderer tailBoom;
    public ModelRenderer tailFin;
    public ModelRenderer stubWings;
    public ModelRenderer leftSkid;
    public ModelRenderer rightSkid;
    public ModelRenderer chinTurret;
    public ModelRenderer mainRotorMast;
    public ModelRenderer mainRotorBlade1;
    public ModelRenderer mainRotorBlade2;
    public ModelRenderer tailRotor;

    public ModelAttackHelicopter() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.cockpitHull = new ModelRenderer(this, 0, 0);
        this.cockpitHull.addBox(-6.0F, -6.0F, -10.0F, 12, 12, 28);
        this.cockpitHull.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.tailBoom = new ModelRenderer(this, 52, 0);
        this.tailBoom.addBox(-2.5F, -2.5F, -34.0F, 5, 5, 24);
        this.tailBoom.setRotationPoint(0.0F, 2.0F, 0.0F);

        this.tailFin = new ModelRenderer(this, 0, 0);
        this.tailFin.addBox(-1.0F, -14.0F, -36.0F, 2, 16, 6);
        this.tailFin.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.stubWings = new ModelRenderer(this, 0, 40);
        this.stubWings.addBox(-18.0F, 0.0F, 2.0F, 36, 2, 6);
        this.stubWings.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Skids
        this.leftSkid = new ModelRenderer(this, 84, 0);
        this.leftSkid.addBox(-9.0F, 9.0F, -10.0F, 2, 2, 28);
        this.leftSkid.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.rightSkid = new ModelRenderer(this, 84, 0);
        this.rightSkid.addBox(7.0F, 9.0F, -10.0F, 2, 2, 28);
        this.rightSkid.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Chin gun turret
        this.chinTurret = new ModelRenderer(this, 0, 0);
        this.chinTurret.addBox(-1.5F, 5.0F, 18.0F, 3, 3, 10);
        this.chinTurret.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Rotor Hub & Blades
        this.mainRotorMast = new ModelRenderer(this, 0, 0);
        this.mainRotorMast.addBox(-1.5F, -10.0F, 2.0F, 3, 4, 3);
        this.mainRotorMast.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.mainRotorBlade1 = new ModelRenderer(this, 0, 52);
        this.mainRotorBlade1.addBox(-38.0F, -10.5F, 2.5F, 76, 1, 3);
        this.mainRotorBlade1.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.mainRotorBlade2 = new ModelRenderer(this, 0, 52);
        this.mainRotorBlade2.addBox(-1.5F, -10.5F, -34.0F, 3, 1, 76);
        this.mainRotorBlade2.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Tail Rotor
        this.tailRotor = new ModelRenderer(this, 0, 20);
        this.tailRotor.addBox(1.5F, -10.0F, -34.5F, 1, 10, 2);
        this.tailRotor.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.cockpitHull.render(scale);
        this.tailBoom.render(scale);
        this.tailFin.render(scale);
        this.stubWings.render(scale);
        this.leftSkid.render(scale);
        this.rightSkid.render(scale);
        // Animate chin turret aiming
        float yawRad = netHeadYaw * 0.017453292F;
        float pitchRad = headPitch * 0.017453292F;
        this.chinTurret.rotateAngleY = yawRad;
        this.chinTurret.rotateAngleX = pitchRad;
        this.chinTurret.render(scale);
        this.mainRotorMast.render(scale);

        // Cyclic tilt (rotor disc tilts slightly forward with forward flight)
        float cyclicForward = -0.08F;
        this.mainRotorBlade1.rotateAngleX = cyclicForward;
        this.mainRotorBlade2.rotateAngleX = cyclicForward;

        // Rotate main rotor
        this.mainRotorBlade1.rotateAngleY = ageInTicks * 1.2F;
        this.mainRotorBlade2.rotateAngleY = ageInTicks * 1.2F;
        this.mainRotorBlade1.render(scale);
        this.mainRotorBlade2.render(scale);

        // Rotate tail rotor
        this.tailRotor.rotateAngleX = ageInTicks * 1.5F;
        this.tailRotor.render(scale);
    }
}
