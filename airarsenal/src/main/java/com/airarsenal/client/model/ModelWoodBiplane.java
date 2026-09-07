package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for the Wood Biplane.
 * Features spinning propeller, detailed wings, and rolling rubber tires.
 */
public class ModelWoodBiplane extends ModelBase {

    public ModelRenderer fuselage;
    public ModelRenderer engineCowling;
    public ModelRenderer cockpitCutout;
    public ModelRenderer twinVickersMG;
    public ModelRenderer lowerWing;
    public ModelRenderer upperWing;
    public ModelRenderer leftStrut;
    public ModelRenderer rightStrut;
    public ModelRenderer rudder;
    public ModelRenderer horizontalStabilizer;
    public ModelRenderer propeller;
    public ModelRenderer gearStrut;
    public ModelRenderer leftWheel;
    public ModelRenderer rightWheel;
    public ModelRenderer tailSkid;

    public ModelWoodBiplane() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        // Fuselage
        this.fuselage = new ModelRenderer(this, 0, 0);
        this.fuselage.addBox(-5.0F, -5.0F, -18.0F, 10, 10, 36);
        this.fuselage.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Cockpit & Windscreen
        this.cockpitCutout = new ModelRenderer(this, 0, 24);
        this.cockpitCutout.addBox(-4.0F, -7.0F, -3.0F, 8, 2, 8);
        this.cockpitCutout.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Twin Vickers Machine Guns
        this.twinVickersMG = new ModelRenderer(this, 56, 30);
        this.twinVickersMG.addBox(-2.5F, -7.0F, 6.0F, 5, 2, 10);
        this.twinVickersMG.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Cowling
        this.engineCowling = new ModelRenderer(this, 56, 0);
        this.engineCowling.addBox(-5.5F, -5.5F, 18.0F, 11, 11, 8);
        this.engineCowling.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Lower Wing
        this.lowerWing = new ModelRenderer(this, 0, 46);
        this.lowerWing.addBox(-32.0F, 3.0F, -6.0F, 64, 2, 12);
        this.lowerWing.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Upper Wing
        this.upperWing = new ModelRenderer(this, 0, 46);
        this.upperWing.addBox(-32.0F, -11.0F, -6.0F, 64, 2, 12);
        this.upperWing.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Interplane Struts
        this.leftStrut = new ModelRenderer(this, 0, 0);
        this.leftStrut.addBox(-24.0F, -11.0F, -1.0F, 2, 14, 2);
        this.leftStrut.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.rightStrut = new ModelRenderer(this, 0, 0);
        this.rightStrut.addBox(22.0F, -11.0F, -1.0F, 2, 14, 2);
        this.rightStrut.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Tail Rudder
        this.rudder = new ModelRenderer(this, 94, 0);
        this.rudder.addBox(-1.0F, -14.0F, -24.0F, 2, 12, 10);
        this.rudder.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Horizontal Stabilizer
        this.horizontalStabilizer = new ModelRenderer(this, 56, 19);
        this.horizontalStabilizer.addBox(-12.0F, -2.0F, -24.0F, 24, 2, 8);
        this.horizontalStabilizer.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Propeller
        this.propeller = new ModelRenderer(this, 0, 20);
        this.propeller.addBox(-12.0F, -1.5F, 26.5F, 24, 3, 1);
        this.propeller.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Landing Gear & Rolling Wheels
        this.gearStrut = new ModelRenderer(this, 56, 42);
        this.gearStrut.addBox(-7.0F, 5.0F, 4.0F, 14, 4, 2);
        this.gearStrut.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.leftWheel = new ModelRenderer(this, 94, 22);
        this.leftWheel.addBox(-1.5F, -3.0F, -3.0F, 3, 6, 6);
        this.leftWheel.setRotationPoint(-7.0F, 9.0F, 5.0F);

        this.rightWheel = new ModelRenderer(this, 94, 22);
        this.rightWheel.addBox(-1.5F, -3.0F, -3.0F, 3, 6, 6);
        this.rightWheel.setRotationPoint(7.0F, 9.0F, 5.0F);

        this.tailSkid = new ModelRenderer(this, 94, 34);
        this.tailSkid.addBox(-1.0F, 4.0F, -18.0F, 2, 4, 2);
        this.tailSkid.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.fuselage.render(scale);
        this.cockpitCutout.render(scale);
        this.twinVickersMG.render(scale);
        this.engineCowling.render(scale);
        this.lowerWing.render(scale);
        this.upperWing.render(scale);
        this.leftStrut.render(scale);
        this.rightStrut.render(scale);
        this.rudder.render(scale);
        this.horizontalStabilizer.render(scale);
        this.gearStrut.render(scale);
        this.tailSkid.render(scale);

        // Spin propeller with age in ticks
        this.propeller.rotateAngleZ = ageInTicks * 0.8F;
        this.propeller.render(scale);

        // Animate rolling wheels during ground movement
        this.leftWheel.rotateAngleX = limbSwing * 0.65F;
        this.rightWheel.rotateAngleX = limbSwing * 0.65F;
        this.leftWheel.render(scale);
        this.rightWheel.render(scale);
    }
}
