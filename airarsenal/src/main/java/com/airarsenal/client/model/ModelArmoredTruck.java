package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Armored Patrol Truck.
 * Features rolling 6x6 tires, steerable front wheels, and 360-degree roof machine gun cupola.
 */
public class ModelArmoredTruck extends ModelBase {

    public ModelRenderer chassis;
    public ModelRenderer armoredCabin;
    public ModelRenderer roofCupola;
    public ModelRenderer machineGun;
    public ModelRenderer ammoBox;
    public ModelRenderer bullbar;

    // 6 Heavy Off-Road Rolling Wheels
    public ModelRenderer wheelFL;
    public ModelRenderer wheelFR;
    public ModelRenderer wheelML;
    public ModelRenderer wheelMR;
    public ModelRenderer wheelRL;
    public ModelRenderer wheelRR;

    public ModelArmoredTruck() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        this.chassis = new ModelRenderer(this, 0, 0);
        this.chassis.addBox(-10.0F, -4.0F, -22.0F, 20, 6, 44);
        this.chassis.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.armoredCabin = new ModelRenderer(this, 0, 0);
        this.armoredCabin.addBox(-9.0F, -14.0F, -18.0F, 18, 10, 36);
        this.armoredCabin.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Roof Gun Cupola & .50 Cal Machine Gun
        this.roofCupola = new ModelRenderer(this, 84, 0);
        this.roofCupola.addBox(-4.0F, -18.0F, -2.0F, 8, 4, 8);
        this.roofCupola.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.machineGun = new ModelRenderer(this, 104, 0);
        this.machineGun.addBox(-1.0F, -17.0F, 6.0F, 2, 2, 16);
        this.machineGun.setRotationPoint(0.0F, 16.0F, 0.0F);

        this.ammoBox = new ModelRenderer(this, 84, 16);
        this.ammoBox.addBox(-3.5F, -17.0F, 2.0F, 2, 3, 4);
        this.ammoBox.setRotationPoint(0.0F, 16.0F, 0.0F);

        // Front bullbar
        this.bullbar = new ModelRenderer(this, 0, 50);
        this.bullbar.addBox(-10.0F, -2.0F, 22.0F, 20, 6, 4);
        this.bullbar.setRotationPoint(0.0F, 16.0F, 0.0F);

        // 6 Wheels with center rotation pivot
        this.wheelFL = new ModelRenderer(this, 0, 54);
        this.wheelFL.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelFL.setRotationPoint(-11.5F, 20.0F, 13.0F);

        this.wheelFR = new ModelRenderer(this, 0, 54);
        this.wheelFR.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelFR.setRotationPoint(11.5F, 20.0F, 13.0F);

        this.wheelML = new ModelRenderer(this, 0, 54);
        this.wheelML.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelML.setRotationPoint(-11.5F, 20.0F, -3.0F);

        this.wheelMR = new ModelRenderer(this, 0, 54);
        this.wheelMR.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelMR.setRotationPoint(11.5F, 20.0F, -3.0F);

        this.wheelRL = new ModelRenderer(this, 0, 54);
        this.wheelRL.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelRL.setRotationPoint(-11.5F, 20.0F, -16.0F);

        this.wheelRR = new ModelRenderer(this, 0, 54);
        this.wheelRR.addBox(-2.0F, -3.5F, -3.5F, 4, 7, 7);
        this.wheelRR.setRotationPoint(11.5F, 20.0F, -16.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.chassis.render(scale);
        this.armoredCabin.render(scale);
        this.bullbar.render(scale);

        // Cupola & gun swivel
        float yawRad = netHeadYaw * 0.017453292F;
        this.roofCupola.rotateAngleY = yawRad;
        this.machineGun.rotateAngleY = yawRad;
        this.ammoBox.rotateAngleY = yawRad;

        this.roofCupola.render(scale);
        this.machineGun.render(scale);
        this.ammoBox.render(scale);

        // Animate rolling tires during vehicle movement
        float roll = limbSwing * 0.72F;
        float steer = yawRad * 0.4F;

        this.wheelFL.rotateAngleX = roll;
        this.wheelFL.rotateAngleY = steer;
        this.wheelFR.rotateAngleX = roll;
        this.wheelFR.rotateAngleY = steer;

        this.wheelML.rotateAngleX = roll;
        this.wheelMR.rotateAngleX = roll;
        this.wheelRL.rotateAngleX = roll;
        this.wheelRR.rotateAngleX = roll;

        this.wheelFL.render(scale);
        this.wheelFR.render(scale);
        this.wheelML.render(scale);
        this.wheelMR.render(scale);
        this.wheelRL.render(scale);
        this.wheelRR.render(scale);
    }
}
