// Air Arsenal - Dedicated Munitions, Ordnance & Artillery 3D Geometry Builders
window.MunitionsBuilders = {
  build: function(id, H) {
    const { addPart, addBox, addCylZ, addConeZ, addSphere, addRingZ, makeMat, modelGroup, modelParts } = H;

    // ── GUIDED MISSILES ─────────────────────────────────────────────────────────
    if (id === 'hellfire') {
      // AGM-114 Hellfire
      addSphere('Laser Optical Seeker Lens', 'Avionics', 0.14, 0xf59e0b, { x: 0, y: 0, z: 1.35 }, { x: 0, y: 0, z: 0.6 }, 0.2, 0.1, 0.75);
      addCylZ('Guidance Actuator Collar', 'Avionics', 0.15, 0.15, 0.25, 0x1f2937, { x: 0, y: 0, z: 1.15 }, { x: 0, y: 0, z: 0.4 });
      // 4 Steering Canards
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const cx = Math.cos(ang) * 0.22;
        const cy = Math.sin(ang) * 0.22;
        addBox(`Guidance Canard #${i + 1}`, 'Airframe', 0.22, 0.02, 0.18, 0xdc2626, { x: cx, y: cy, z: 1.15 }, { x: cx * 1.8, y: cy * 1.8, z: 0.4 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('HEAT Warhead (Yellow Stencil Band)', 'Armament', 0.152, 0.152, 0.18, 0xeab308, { x: 0, y: 0, z: 0.8 }, { x: 0, y: 0, z: 0.2 });
      addCylZ('Olive Drab Solid Rocket Fuselage', 'Propulsion', 0.15, 0.15, 1.4, 0x365314, { x: 0, y: 0, z: 0.05 }, { x: 0, y: 0, z: 0 });
      addBox('External Wire Conduit Raceway', 'Avionics', 0.03, 0.03, 1.3, 0x18181b, { x: 0.15, y: 0.05, z: 0.05 }, { x: 0.3, y: 0.1, z: 0 });
      // 4 Tail Stabilizer Fins
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2 + Math.PI / 4;
        const tx = Math.cos(ang) * 0.28;
        const ty = Math.sin(ang) * 0.28;
        addBox(`Tail Stabilizer Fin #${i + 1}`, 'Airframe', 0.32, 0.02, 0.45, 0x1e293b, { x: tx, y: ty, z: -0.7 }, { x: tx * 1.8, y: ty * 1.8, z: -0.3 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('Rocket Exhaust Expansion Nozzle', 'Propulsion', 0.11, 0.14, 0.2, 0x0f172a, { x: 0, y: 0, z: -0.75 }, { x: 0, y: 0, z: -0.5 }, 16, 0.8, 0.2);
      return true;
    }

    if (id === 'brahmos') {
      // PJ-10 BrahMos Hypersonic Ramjet
      addConeZ('Supersonic Shock Cone Spike', 'Avionics', 0.07, 0.55, 0x0284c7, { x: 0, y: 0, z: 2.15 }, { x: 0, y: 0, z: 0.8 });
      addCylZ('Annular Ramjet Intake Cowl', 'Propulsion', 0.24, 0.25, 0.35, 0x94a3b8, { x: 0, y: 0, z: 1.75 }, { x: 0, y: 0, z: 0.5 }, 20, 0.7, 0.3);
      addCylZ('White Ramjet Fuselage (Forward)', 'Airframe', 0.25, 0.25, 1.4, 0xf8fafc, { x: 0, y: 0, z: 0.9 }, { x: 0, y: 0, z: 0.2 });
      addCylZ('Red Stencil Telemetry Section', 'Avionics', 0.252, 0.252, 0.25, 0xdc2626, { x: 0, y: 0, z: 0.1 }, { x: 0, y: 0, z: 0 });
      // 4 Diamond-Wedge Mid-Wings
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const wx = Math.cos(ang) * 0.45;
        const wy = Math.sin(ang) * 0.45;
        addBox(`Supersonic Delta Wing #${i + 1}`, 'Airframe', 0.55, 0.03, 0.6, 0xdc2626, { x: wx, y: wy, z: 0.1 }, { x: wx * 1.8, y: wy * 1.8, z: 0 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('Aft Booster Rocket Stage', 'Propulsion', 0.25, 0.25, 1.2, 0x334155, { x: 0, y: 0, z: -0.7 }, { x: 0, y: 0, z: -0.2 }, 20, 0.6, 0.4);
      // 4 Rear Steering Grid Fins
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2 + Math.PI / 4;
        const fx = Math.cos(ang) * 0.4;
        const fy = Math.sin(ang) * 0.4;
        addBox(`Steering Actuator Fin #${i + 1}`, 'Airframe', 0.38, 0.03, 0.35, 0x0f172a, { x: fx, y: fy, z: -1.3 }, { x: fx * 1.8, y: fy * 1.8, z: -0.4 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('De Laval Hypersonic Nozzle', 'Propulsion', 0.16, 0.24, 0.35, 0xf97316, { x: 0, y: 0, z: -1.45 }, { x: 0, y: 0, z: -0.7 }, 18, 0.8, 0.2);
      return true;
    }

    if (id === 'predator_missile') {
      // Optical TV Guided Strike Missile
      addBox('Optical TV Camera Window', 'Avionics', 0.2, 0.2, 0.3, 0x0284c7, { x: 0, y: 0, z: 1.35 }, { x: 0, y: 0, z: 0.6 }, { x: 0, y: 0, z: 0 }, 0.3, 0.1, 0.7);
      addCylZ('Avionics Processing Bay', 'Avionics', 0.16, 0.16, 0.5, 0x334155, { x: 0, y: 0, z: 0.95 }, { x: 0, y: 0, z: 0.3 });
      addCylZ('Rocket Motor Body Tube', 'Propulsion', 0.16, 0.16, 1.4, 0x475569, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 });
      // Folding Glider Wings
      addBox('Left Folding Wing', 'Airframe', 0.75, 0.03, 0.25, 0x1e293b, { x: -0.45, y: 0.05, z: 0.1 }, { x: -0.9, y: 0.1, z: 0 });
      addBox('Right Folding Wing', 'Airframe', 0.75, 0.03, 0.25, 0x1e293b, { x: 0.45, y: 0.05, z: 0.1 }, { x: 0.9, y: 0.1, z: 0 });
      // 4 Lattice Grid Fins
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const gx = Math.cos(ang) * 0.28;
        const gy = Math.sin(ang) * 0.28;
        addBox(`Titanium Grid Fin #${i + 1}`, 'Airframe', 0.26, 0.04, 0.24, 0xdc2626, { x: gx, y: gy, z: -0.75 }, { x: gx * 1.8, y: gy * 1.8, z: -0.3 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('Rocket Exhaust Port', 'Propulsion', 0.1, 0.13, 0.2, 0x0f172a, { x: 0, y: 0, z: -0.8 }, { x: 0, y: 0, z: -0.5 });
      return true;
    }

    if (id === 'manpads_missile') {
      // FIM-92 Stinger IR MANPADS
      addSphere('Sapphire Dual-Band IR Seeker', 'Avionics', 0.09, 0x38bdf8, { x: 0, y: 0, z: 1.3 }, { x: 0, y: 0, z: 0.6 }, 0.2, 0.1, 0.85);
      addCylZ('Forward Guidance Electronics', 'Avionics', 0.09, 0.09, 0.4, 0x4d5d43, { x: 0, y: 0, z: 1.05 }, { x: 0, y: 0, z: 0.3 });
      // 4 Folding Canards
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const cx = Math.cos(ang) * 0.15;
        const cy = Math.sin(ang) * 0.15;
        addBox(`Steering Canard #${i + 1}`, 'Airframe', 0.14, 0.015, 0.14, 0x94a3b8, { x: cx, y: cy, z: 1.05 }, { x: cx * 2, y: cy * 2, z: 0.3 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('Slender Anodized Aluminum Fuselage', 'Propulsion', 0.088, 0.088, 1.7, 0x4d5d43, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 16, 0.6, 0.3);
      // 4 Spring Curved Tail Fins
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2 + Math.PI / 4;
        const fx = Math.cos(ang) * 0.16;
        const fy = Math.sin(ang) * 0.16;
        addBox(`Curved Wrap Fin #${i + 1}`, 'Airframe', 0.18, 0.015, 0.25, 0x334155, { x: fx, y: fy, z: -0.9 }, { x: fx * 2, y: fy * 2, z: -0.3 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('Dual-Thrust Nozzle', 'Propulsion', 0.06, 0.08, 0.18, 0x0f172a, { x: 0, y: 0, z: -0.95 }, { x: 0, y: 0, z: -0.5 });
      return true;
    }

    if (id === 'truck_guided_missile') {
      // Heavy Tactical Surface-to-Surface / SAM Missile
      addConeZ('Biconic Ceramic Radome', 'Avionics', 0.32, 0.9, 0xf59e0b, { x: 0, y: 0, z: 1.8 }, { x: 0, y: 0, z: 0.8 }, 20, 0.4, 0.4);
      addCylZ('DACS Lateral Reaction Thruster Ring', 'Avionics', 0.32, 0.32, 0.35, 0x1e293b, { x: 0, y: 0, z: 1.2 }, { x: 0, y: 0, z: 0.4 });
      addCylZ('Desert Camo Solid Rocket Fuselage', 'Propulsion', 0.32, 0.32, 2.8, 0xd97706, { x: 0, y: 0, z: -0.2 }, { x: 0, y: 0, z: 0 }, 20, 0.4, 0.5);
      addBox('External Harness Duct Raceway', 'Avionics', 0.05, 0.06, 2.6, 0x78350f, { x: 0.33, y: 0, z: -0.2 }, { x: 0.6, y: 0, z: 0 });
      // 4 Heavy Clipped-Delta Tail Actuators
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const tx = Math.cos(ang) * 0.58;
        const ty = Math.sin(ang) * 0.58;
        addBox(`Titanium Tail Fin #${i + 1}`, 'Airframe', 0.65, 0.04, 0.6, 0x1e293b, { x: tx, y: ty, z: -1.35 }, { x: tx * 1.8, y: ty * 1.8, z: -0.4 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('High-Expansion Rocket Exhaust Bell', 'Propulsion', 0.22, 0.3, 0.45, 0x0f172a, { x: 0, y: 0, z: -1.75 }, { x: 0, y: 0, z: -0.7 }, 18, 0.8, 0.2);
      return true;
    }

    if (id === 'mlrs_rocket') {
      // 227mm Artillery Saturation Rocket
      addConeZ('Spin-Stabilized Nose Fuze', 'Avionics', 0.15, 0.45, 0xd97706, { x: 0, y: 0, z: 1.6 }, { x: 0, y: 0, z: 0.7 });
      addCylZ('227mm Submunition Warhead Casing', 'Armament', 0.16, 0.16, 0.9, 0x273318, { x: 0, y: 0, z: 0.95 }, { x: 0, y: 0, z: 0.3 });
      addCylZ('Solid Propellant Motor Tube', 'Propulsion', 0.16, 0.16, 2.0, 0x3f4f2c, { x: 0, y: 0, z: -0.4 }, { x: 0, y: 0, z: 0 });
      // 4 Curved Wrap-Around Fins
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2 + Math.PI / 4;
        const fx = Math.cos(ang) * 0.25;
        const fy = Math.sin(ang) * 0.25;
        addBox(`Spring-Ejected Tail Fin #${i + 1}`, 'Airframe', 0.22, 0.02, 0.3, 0x18181b, { x: fx, y: fy, z: -1.25 }, { x: fx * 1.9, y: fy * 1.9, z: -0.3 }, { x: 0, y: 0, z: ang });
      }
      addCylZ('Multi-Orifice Exhaust Plate', 'Propulsion', 0.12, 0.15, 0.2, 0x0f172a, { x: 0, y: 0, z: -1.45 }, { x: 0, y: 0, z: -0.5 });
      return true;
    }

    // ── AERIAL BOMBS ────────────────────────────────────────────────────────────
    if (id === 'iron_bomb') {
      // General Purpose 250lb HE Bomb
      addConeZ('Brass Nose Fuze & Spinning Impeller', 'Armament', 0.1, 0.3, 0xf59e0b, { x: 0, y: 0, z: 1.0 }, { x: 0, y: 0, z: 0.6 });
      addCylZ('Cast Iron Teardrop Bomb Body', 'Armament', 0.24, 0.24, 1.4, 0x365314, { x: 0, y: 0, z: 0.15 }, { x: 0, y: 0, z: 0 }, 18);
      addBox('Dual Suspension Bail Lugs', 'Armament', 0.05, 0.1, 0.6, 0x18181b, { x: 0, y: 0.27, z: 0.15 }, { x: 0, y: 0.4, z: 0 });
      // Cruciform Tail Fins + Shroud Ring
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const fx = Math.cos(ang) * 0.3;
        const fy = Math.sin(ang) * 0.3;
        addBox(`Stabilizer Fin #${i + 1}`, 'Airframe', 0.32, 0.02, 0.5, 0x18181b, { x: fx, y: fy, z: -0.7 }, { x: fx * 1.8, y: fy * 1.8, z: -0.3 }, { x: 0, y: 0, z: ang });
      }
      addRingZ('Tubular Stabilizing Shroud Ring', 'Airframe', 0.28, 0.02, 0x18181b, { x: 0, y: 0, z: -0.9 }, { x: 0, y: 0, z: -0.5 });
      return true;
    }

    if (id === 'heavy_bomb') {
      // 1,000kg Demolition Blockbuster
      addCylZ('Yellow Explosive Hazard Ring', 'Armament', 0.385, 0.385, 0.15, 0xeab308, { x: 0, y: 0, z: 1.2 }, { x: 0, y: 0, z: 0.5 });
      addConeZ('Hardened Steel Penetrator Nose', 'Armament', 0.38, 0.6, 0x1e293b, { x: 0, y: 0, z: 1.55 }, { x: 0, y: 0, z: 0.8 }, 18, 0.7, 0.3);
      addCylZ('Massive Demolition Bomb Casing', 'Armament', 0.38, 0.38, 2.0, 0x273318, { x: 0, y: 0, z: 0.1 }, { x: 0, y: 0, z: 0 }, 20);
      addBox('Heavy Forged Suspension Lugs', 'Armament', 0.08, 0.14, 0.9, 0x0f172a, { x: 0, y: 0.43, z: 0.1 }, { x: 0, y: 0.5, z: 0 });
      // 4 Tail Fins + Heavy Shroud
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const fx = Math.cos(ang) * 0.45;
        const fy = Math.sin(ang) * 0.45;
        addBox(`Heavy Tail Fin #${i + 1}`, 'Airframe', 0.45, 0.03, 0.65, 0x1e293b, { x: fx, y: fy, z: -1.05 }, { x: fx * 1.8, y: fy * 1.8, z: -0.4 }, { x: 0, y: 0, z: ang });
      }
      addRingZ('Demolition Fin Shroud Ring', 'Airframe', 0.44, 0.025, 0x1e293b, { x: 0, y: 0, z: -1.35 }, { x: 0, y: 0, z: -0.7 });
      return true;
    }

    if (id === 'napalm_bomb') {
      // BLU-27 Jellied Fuel Firebomb
      addSphere('Rounded All-Ways Nose Igniter', 'Armament', 0.26, 0xdc2626, { x: 0, y: 0, z: 1.05 }, { x: 0, y: 0, z: 0.6 }, 0.5, 0.4);
      addCylZ('Orange Incendiary Warning Band (Nose)', 'Armament', 0.265, 0.265, 0.18, 0xf97316, { x: 0, y: 0, z: 0.8 }, { x: 0, y: 0, z: 0.3 });
      addCylZ('Aluminum Incendiary Fuel Drum', 'Armament', 0.26, 0.26, 1.8, 0x94a3b8, { x: 0, y: 0, z: -0.1 }, { x: 0, y: 0, z: 0 }, 18, 0.75, 0.25);
      addCylZ('Orange Incendiary Warning Band (Tail)', 'Armament', 0.265, 0.265, 0.18, 0xf97316, { x: 0, y: 0, z: -0.7 }, { x: 0, y: 0, z: -0.2 });
      addSphere('Rounded Tail Closure Cap', 'Armament', 0.26, 0xdc2626, { x: 0, y: 0, z: -1.0 }, { x: 0, y: 0, z: -0.4 }, 0.5, 0.4);
      // 4 Low-Profile Sheet Metal Fins
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2 + Math.PI / 4;
        const fx = Math.cos(ang) * 0.36;
        const fy = Math.sin(ang) * 0.36;
        addBox(`Canted Fin #${i + 1}`, 'Airframe', 0.28, 0.02, 0.45, 0x1e293b, { x: fx, y: fy, z: -0.9 }, { x: fx * 1.8, y: fy * 1.8, z: -0.3 }, { x: 0, y: 0, z: ang });
      }
      return true;
    }

    if (id === 'cluster_bomb') {
      // Clamshell Dispenser Canister
      addConeZ('Radar Proximity Altimeter Nose', 'Avionics', 0.28, 0.5, 0xfacc15, { x: 0, y: 0, z: 1.35 }, { x: 0, y: 0, z: 0.7 });
      addCylZ('Left Clamshell Dispenser Half', 'Armament', 0.28, 0.28, 1.8, 0x365314, { x: -0.05, y: 0, z: 0.2 }, { x: -0.4, y: 0, z: 0.1 }, 18);
      addCylZ('Right Clamshell Dispenser Half', 'Armament', 0.28, 0.28, 1.8, 0x365314, { x: 0.05, y: 0, z: 0.2 }, { x: 0.4, y: 0, z: 0.1 }, 18);
      addBox('Visible Internal Submunitions Stack', 'Armament', 0.2, 0.2, 1.2, 0xf59e0b, { x: 0, y: 0, z: 0.2 }, { x: 0, y: 0.3, z: 0 });
      // 4 Folding Tail Fins
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const fx = Math.cos(ang) * 0.38;
        const fy = Math.sin(ang) * 0.38;
        addBox(`Dispenser Tail Fin #${i + 1}`, 'Airframe', 0.36, 0.02, 0.45, 0x18181b, { x: fx, y: fy, z: -0.85 }, { x: fx * 1.8, y: fy * 1.8, z: -0.4 }, { x: 0, y: 0, z: ang });
      }
      return true;
    }

    if (id === 'emp_bomb') {
      // High-Tech Electromagnetic Pulse Weapon
      addConeZ('Microwave Horn Dielectric Radome', 'Avionics', 0.28, 0.55, 0x38bdf8, { x: 0, y: 0, z: 1.35 }, { x: 0, y: 0, z: 0.7 }, 18, 0.3, 0.1, 0.85);
      addCylZ('Carbon-Composite Pulse Casing', 'Armament', 0.28, 0.28, 1.8, 0x111827, { x: 0, y: 0, z: 0.1 }, { x: 0, y: 0, z: 0 }, 18, 0.2, 0.7);
      addRingZ('Wound Copper Flux Armature (Front)', 'Armament', 0.29, 0.02, 0x38bdf8, { x: 0, y: 0, z: 0.5 }, { x: 0, y: 0.2, z: 0 });
      addRingZ('Wound Copper Flux Armature (Mid)', 'Armament', 0.29, 0.02, 0x38bdf8, { x: 0, y: 0, z: 0.1 }, { x: 0, y: 0.2, z: 0 });
      addRingZ('Wound Copper Flux Armature (Aft)', 'Armament', 0.29, 0.02, 0x38bdf8, { x: 0, y: 0, z: -0.3 }, { x: 0, y: 0.2, z: 0 });
      // 4 Composite Grid Fins
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const fx = Math.cos(ang) * 0.38;
        const fy = Math.sin(ang) * 0.38;
        addBox(`Composite Grid Fin #${i + 1}`, 'Airframe', 0.35, 0.04, 0.3, 0x0284c7, { x: fx, y: fy, z: -0.9 }, { x: fx * 1.8, y: fy * 1.8, z: -0.4 }, { x: 0, y: 0, z: ang });
      }
      return true;
    }

    if (id === 'fuel_air_bomb') {
      // Thermobaric Aerosol Blast Weapon
      addBox('Standoff Cloud Ignition Probe', 'Avionics', 0.04, 0.04, 0.6, 0x0f172a, { x: 0, y: 0, z: 1.6 }, { x: 0, y: 0, z: 0.8 });
      addSphere('Domed Tank Forward Head', 'Armament', 0.34, 0xd97706, { x: 0, y: 0, z: 1.0 }, { x: 0, y: 0, z: 0.5 });
      addCylZ('Pressurized Aerosol Fuel Tank', 'Armament', 0.34, 0.34, 1.8, 0xb45309, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 20);
      addSphere('Domed Tank Aft Head', 'Armament', 0.34, 0xd97706, { x: 0, y: 0, z: -0.9 }, { x: 0, y: 0, z: -0.3 });
      for (let i = 0; i < 4; i++) {
        const ang = (i * Math.PI) / 2;
        const fx = Math.cos(ang) * 0.42;
        const fy = Math.sin(ang) * 0.42;
        addBox(`Aero Brake Fin #${i + 1}`, 'Airframe', 0.4, 0.03, 0.45, 0x1e293b, { x: fx, y: fy, z: -1.15 }, { x: fx * 1.8, y: fy * 1.8, z: -0.4 }, { x: 0, y: 0, z: ang });
      }
      return true;
    }

    if (id === 'depth_charge') {
      // Hydrostatic Naval Depth Charge
      addCylZ('Hydrostatic Steel Drum Canister', 'Armament', 0.34, 0.34, 1.1, 0x334155, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 20, 0.5, 0.4);
      addCylZ('Pressure Bellows Depth Fuze Dial', 'Avionics', 0.16, 0.16, 0.06, 0xfacc15, { x: 0, y: 0, z: 0.58 }, { x: 0, y: 0, z: 0.4 });
      addRingZ('Forward Steel Rolling Hoop', 'Armor', 0.35, 0.02, 0x0f172a, { x: 0, y: 0, z: 0.3 }, { x: 0, y: 0, z: 0.1 });
      addRingZ('Aft Steel Rolling Hoop', 'Armor', 0.35, 0.02, 0x0f172a, { x: 0, y: 0, z: -0.3 }, { x: 0, y: 0, z: -0.1 });
      addCylZ('Heavy Lead Ballast Base', 'Armor', 0.32, 0.32, 0.08, 0x0f172a, { x: 0, y: 0, z: -0.58 }, { x: 0, y: 0, z: -0.3 });
      return true;
    }

    if (id === 'smoke_bomb') {
      // Tactical Smoke Screen Dispenser
      addCylZ('Smoke Emission Canister', 'Armament', 0.24, 0.24, 1.4, 0x64748b, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 18);
      addCylZ('High-Visibility Orange Cap', 'Armament', 0.245, 0.245, 0.25, 0xf97316, { x: 0, y: 0, z: 0.7 }, { x: 0, y: 0, z: 0.4 });
      addBox('Safety Pull-Ring Lever Mechanism', 'Armament', 0.06, 0.12, 0.2, 0xdc2626, { x: 0, y: 0.2, z: 0.75 }, { x: 0, y: 0.4, z: 0.5 });
      addCylZ('Perforated Vent Smoke Ring', 'Armament', 0.23, 0.23, 0.2, 0x0f172a, { x: 0, y: 0, z: -0.7 }, { x: 0, y: 0, z: -0.3 });
      return true;
    }

    // ── ARTILLERY & EMPLACEMENTS ───────────────────────────────────────────────
    if (id === 'aa_cannon') {
      addBox('Fortified Octagonal Swivel Turntable', 'Armor', 2.2, 0.35, 2.2, 0x334155, { x: 0, y: -0.6, z: 0 }, { x: 0, y: -0.4, z: 0 });
      addBox('Armored Gunner Shield & Trunnion', 'Armor', 1.4, 0.85, 1.3, 0x1e293b, { x: 0, y: 0, z: 0 }, { x: 0, y: 0.3, z: 0 });
      addCylZ('Left 20mm Autocannon Barrel', 'Armament', 0.06, 0.06, 2.6, 0x0f172a, { x: -0.26, y: 0.15, z: 1.2 }, { x: -0.4, y: 0.2, z: 0.6 });
      addCylZ('Right 20mm Autocannon Barrel', 'Armament', 0.06, 0.06, 2.6, 0x0f172a, { x: 0.26, y: 0.15, z: 1.2 }, { x: 0.4, y: 0.2, z: 0.6 });
      addBox('Twin Rapid-Feed Ammo Drum Cans', 'Armament', 0.45, 0.5, 0.6, 0x15803d, { x: 0, y: -0.05, z: -0.45 }, { x: 0, y: 0.1, z: -0.3 });
      return true;
    }

    if (id === 'flak_battery') {
      addBox('Cruciform Heavy Outrigger Base', 'Armor', 3.0, 0.25, 3.0, 0x334155, { x: 0, y: -0.6, z: 0 }, { x: 0, y: -0.4, z: 0 });
      addBox('Center Elevation Pivot Pedestal', 'Armor', 1.2, 0.9, 1.2, 0x1e293b, { x: 0, y: -0.05, z: 0 }, { x: 0, y: 0.3, z: 0 });
      addCylZ('88mm Rifled Flak Cannon Barrel', 'Armament', 0.12, 0.16, 3.4, 0x0f172a, { x: 0, y: 0.35, z: 1.5 }, { x: 0, y: 0.5, z: 0.8 });
      addBox('Recoil Counterweight Cylinders', 'Armament', 0.35, 0.35, 1.2, 0x475569, { x: 0, y: 0.6, z: 0.3 }, { x: 0, y: 0.8, z: 0.2 });
      addBox('Interrupted-Screw Breech & Loading Tray', 'Armament', 0.45, 0.45, 0.7, 0x0f172a, { x: 0, y: 0.35, z: -0.55 }, { x: 0, y: 0.4, z: -0.4 });
      return true;
    }

    if (id === 'howitzer') {
      addBox('Split-Trail Artillery Carriage Legs', 'Armor', 2.0, 0.4, 2.6, 0x3f4f2c, { x: 0, y: -0.45, z: -0.4 }, { x: 0, y: -0.3, z: -0.3 });
      addBox('Hydraulic Recoil Damper Buffers', 'Armament', 0.4, 0.3, 1.4, 0x1e293b, { x: 0, y: 0.25, z: 0.4 }, { x: 0, y: 0.4, z: 0.2 });
      addCylZ('155mm Heavy Howitzer Barrel', 'Armament', 0.16, 0.2, 3.6, 0x0f172a, { x: 0, y: 0.05, z: 1.6 }, { x: 0, y: 0.3, z: 0.8 });
      addBox('Double-Baffle Slotted Muzzle Brake', 'Armament', 0.42, 0.35, 0.55, 0x1e293b, { x: 0, y: 0.05, z: 3.5 }, { x: 0, y: 0.3, z: 1.1 });
      return true;
    }

    if (id === 'mlrs') {
      addBox('Concrete Ballistic Turntable Ring', 'Armor', 2.4, 0.35, 2.4, 0x334155, { x: 0, y: -0.6, z: 0 }, { x: 0, y: -0.4, z: 0 });
      addBox('Elevating Armored 12-Cell Rocket Pod', 'Armament', 1.6, 0.8, 2.4, 0x1e293b, { x: 0, y: 0.2, z: 0 }, { x: 0, y: 0.5, z: 0 });
      addCylZ('Hydraulic Elevation Ram', 'Armament', 0.1, 0.1, 1.0, 0x38bdf8, { x: 0, y: -0.2, z: -0.3 }, { x: 0, y: 0.1, z: -0.2 }, 12, 0.8, 0.2);
      // Visible rocket tips in 12 cells (2 rows of 6)
      for (let r = 0; r < 2; r++) {
        for (let c = 0; c < 6; c++) {
          const rx = (c - 2.5) * 0.24;
          const ry = (r - 0.5) * 0.32 + 0.2;
          addConeZ(`Rocket Warhead Tip [${r + 1},${c + 1}]`, 'Armament', 0.07, 0.18, 0xd97706, { x: rx, y: ry, z: 1.25 }, { x: rx, y: ry + 0.3, z: 0.8 });
        }
      }
      return true;
    }

    if (id === 'static_missile_battery') {
      addBox('Hardened Concrete Silo Blast Collar', 'Armor', 2.8, 0.4, 2.8, 0x0f172a, { x: 0, y: -0.6, z: 0 }, { x: 0, y: -0.3, z: 0 });
      addBox('Left Hydraulic Silo Blast Door', 'Armor', 1.2, 0.15, 2.4, 0x334155, { x: -0.85, y: -0.35, z: 0 }, { x: -1.6, y: -0.2, z: 0 });
      addBox('Right Hydraulic Silo Blast Door', 'Armor', 1.2, 0.15, 2.4, 0x334155, { x: 0.85, y: -0.35, z: 0 }, { x: 1.6, y: -0.2, z: 0 });
      // Vertical Launch Interceptor Missile in silo
      const missileGeom = new THREE.CylinderGeometry(0.2, 0.2, 2.4, 16);
      addPart('Vertical Launch SAM Interceptor', 'Armament', missileGeom, makeMat(0xf8fafc, 0.4, 0.4), { x: 0, y: 0.4, z: 0 }, { x: 0, y: 1.6, z: 0 });
      const noseGeom = new THREE.ConeGeometry(0.2, 0.5, 16);
      addPart('SAM Radar Proximity Seeker Cone', 'Avionics', noseGeom, makeMat(0xdc2626, 0.4, 0.4), { x: 0, y: 1.85, z: 0 }, { x: 0, y: 2.2, z: 0 });
      return true;
    }

    if (id === 'orbital_cannon_core') {
      addBox('3x3 Reinforced Antimatter Foundation Pad', 'Armor', 3.2, 0.6, 3.2, 0x0f172a, { x: 0, y: -0.7, z: 0 }, { x: 0, y: -0.4, z: 0 });
      // 3 Concentric Superconducting Rings
      addRingZ('Outer Superconducting Magnetic Coil Ring', 'Propulsion', 1.35, 0.08, 0x0284c7, { x: 0, y: -0.1, z: 0 }, { x: 0, y: 0.3, z: 0 }, 0.8, 0.2);
      addRingZ('Mid Plasma Acceleration Ring', 'Propulsion', 0.95, 0.08, 0x38bdf8, { x: 0, y: 0.3, z: 0 }, { x: 0, y: 0.6, z: 0 }, 0.8, 0.2);
      addRingZ('Inner Hypervelocity Accelerator Ring', 'Propulsion', 0.55, 0.08, 0x38bdf8, { x: 0, y: 0.7, z: 0 }, { x: 0, y: 0.9, z: 0 }, 0.8, 0.2);
      addCylZ('Tungsten Rod Hypervelocity Vacuum Bore', 'Armament', 0.35, 0.35, 2.6, 0x020617, { x: 0, y: 0.4, z: 0 }, { x: 0, y: 1.2, z: 0 });
      // 4 Capacitor Pillars
      addBox('Capacitor Tower (NW)', 'Avionics', 0.4, 1.6, 0.4, 0x1e293b, { x: -1.1, y: 0.2, z: -1.1 }, { x: -1.6, y: 0.2, z: -1.6 });
      addBox('Capacitor Tower (NE)', 'Avionics', 0.4, 1.6, 0.4, 0x1e293b, { x: 1.1, y: 0.2, z: -1.1 }, { x: 1.6, y: 0.2, z: -1.6 });
      addBox('Capacitor Tower (SW)', 'Avionics', 0.4, 1.6, 0.4, 0x1e293b, { x: -1.1, y: 0.2, z: 1.1 }, { x: -1.6, y: 0.2, z: 1.6 });
      addBox('Capacitor Tower (SE)', 'Avionics', 0.4, 1.6, 0.4, 0x1e293b, { x: 1.1, y: 0.2, z: 1.1 }, { x: 1.6, y: 0.2, z: 1.6 });
      return true;
    }

    // ── PROJECTILES & SHELLS ───────────────────────────────────────────────────
    if (id === 'bullet' || id === 'aa_shell') {
      addCylZ('Drawn Brass Cartridge Case', 'Armament', 0.18, 0.2, 1.4, 0xd97706, { x: 0, y: 0, z: -0.3 }, { x: 0, y: 0, z: -0.5 }, 18, 0.9, 0.2);
      addConeZ(id === 'bullet' ? 'Copper Penetrator Bullet' : 'High-Explosive Tracer Bullet', 'Armament', 0.17, 0.7, id === 'bullet' ? 0xb45309 : 0xdc2626, { x: 0, y: 0, z: 0.75 }, { x: 0, y: 0, z: 0.5 });
      return true;
    }

    if (id === 'flak_shell' || id === 'howitzer_shell') {
      addConeZ('Clockwork Time Fuze Nose Cone', 'Avionics', 0.22, 0.55, 0xfacc15, { x: 0, y: 0, z: 1.25 }, { x: 0, y: 0, z: 0.7 });
      addCylZ('Forged Steel Shell Casing Body', 'Armament', 0.25, 0.25, 1.6, 0x334155, { x: 0, y: 0, z: 0.2 }, { x: 0, y: 0, z: 0 }, 20);
      addRingZ('Copper Rifling Driving Band', 'Armament', 0.26, 0.025, 0xd97706, { x: 0, y: 0, z: -0.4 }, { x: 0, y: 0, z: -0.3 });
      return true;
    }

    if (id === 'tank_shell') {
      // 120mm APFSDS Kinetic Dart
      addCylZ('Tungsten APFSDS Long-Rod Dart', 'Armament', 0.05, 0.05, 2.6, 0x0f172a, { x: 0, y: 0, z: 0.2 }, { x: 0, y: 0, z: 0 }, 14, 0.9, 0.15);
      addConeZ('Aerodynamic Needle Point Tip', 'Armament', 0.05, 0.45, 0x38bdf8, { x: 0, y: 0, z: 1.7 }, { x: 0, y: 0, z: 0.6 });
      addCylZ('3-Petal Aluminum Discarding Sabot', 'Armament', 0.24, 0.24, 0.7, 0x94a3b8, { x: 0, y: 0, z: 0.3 }, { x: 0, y: 0.5, z: 0 }, 16, 0.8, 0.2);
      for (let i = 0; i < 6; i++) {
        const ang = (i * Math.PI) / 3;
        const fx = Math.cos(ang) * 0.12;
        const fy = Math.sin(ang) * 0.12;
        addBox(`Dart Stabilizer Fin #${i + 1}`, 'Airframe', 0.12, 0.015, 0.35, 0x0f172a, { x: fx, y: fy, z: -0.9 }, { x: fx * 2, y: fy * 2, z: -0.3 }, { x: 0, y: 0, z: ang });
      }
      return true;
    }

    if (id === 'mortar_shell') {
      addConeZ('Impact Point Fuze', 'Avionics', 0.1, 0.3, 0xf59e0b, { x: 0, y: 0, z: 1.0 }, { x: 0, y: 0, z: 0.6 });
      addCylZ('Teardrop High-Explosive Body', 'Armament', 0.24, 0.24, 1.0, 0x3f4f2c, { x: 0, y: 0, z: 0.35 }, { x: 0, y: 0, z: 0 }, 18);
      addCylZ('Perforated Tail Boom Tube', 'Propulsion', 0.08, 0.08, 0.8, 0x1e293b, { x: 0, y: 0, z: -0.45 }, { x: 0, y: 0, z: -0.3 });
      for (let i = 0; i < 8; i++) {
        const ang = (i * Math.PI) / 4;
        const fx = Math.cos(ang) * 0.18;
        const fy = Math.sin(ang) * 0.18;
        addBox(`Mortar Fin #${i + 1}`, 'Airframe', 0.16, 0.015, 0.3, 0x0f172a, { x: fx, y: fy, z: -0.7 }, { x: fx * 1.8, y: fy * 1.8, z: -0.4 }, { x: 0, y: 0, z: ang });
      }
      return true;
    }

    return false;
  }
};
