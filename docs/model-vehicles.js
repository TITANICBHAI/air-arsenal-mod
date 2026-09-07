// Air Arsenal - Dedicated Aircraft & Combat Vehicle 3D Geometry Builders
window.VehicleBuilders = {
  build: function(id, H) {
    const { addPart, addBox, addCylZ, addConeZ, addSphere, addRingZ, addWheel, makeMat, modelGroup, modelParts, animRotors, animAfterburners } = H;

    // ── AIRCRAFT ──────────────────────────────────────────────────────────────
    if (id === 'wood_biplane') {
      // Fuselage & Nose
      addBox('Spruce Timber Fuselage', 'Airframe', 0.65, 0.65, 2.4, 0x8b5a2b, { x: 0, y: 0.05, z: -0.1 }, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 0.1, 0.85);
      addBox('Recessed Cockpit Well', 'Avionics', 0.48, 0.12, 0.55, 0x3e2723, { x: 0, y: 0.42, z: -0.15 }, { x: 0, y: 0.3, z: 0 });
      addBox('Curved Acrylic Windscreen', 'Avionics', 0.44, 0.2, 0.03, 0xf59e0b, { x: 0, y: 0.52, z: 0.14 }, { x: 0, y: 0.4, z: 0.1 }, { x: -0.2, y: 0, z: 0 }, 0.2, 0.1, 0.7);
      addBox('Metal Engine Firewall & Cowl', 'Propulsion', 0.68, 0.68, 0.5, 0x475569, { x: 0, y: 0.05, z: 1.35 }, { x: 0, y: 0, z: 0.5 }, { x: 0, y: 0, z: 0 }, 0.75, 0.25);
      addCylZ('Clerget 9B Rotary Engine Cylinders', 'Propulsion', 0.28, 0.28, 0.35, 0x1e293b, { x: 0, y: 0.05, z: 1.35 }, { x: 0, y: 0, z: 0.5 });
      // Twin Vickers Guns
      addBox('Synchronized Vickers MG (Left)', 'Armament', 0.06, 0.06, 0.8, 0x0f172a, { x: -0.14, y: 0.46, z: 0.75 }, { x: -0.3, y: 0.3, z: 0.4 });
      addBox('Synchronized Vickers MG (Right)', 'Armament', 0.06, 0.06, 0.8, 0x0f172a, { x: 0.14, y: 0.46, z: 0.75 }, { x: 0.3, y: 0.3, z: 0.4 });
      // Lower Wings
      addBox('Left Lower Wing', 'Airframe', 1.9, 0.06, 0.8, 0xa16207, { x: -1.25, y: -0.15, z: 0.25 }, { x: -0.8, y: -0.2, z: 0 });
      addBox('Right Lower Wing', 'Airframe', 1.9, 0.06, 0.8, 0xa16207, { x: 1.25, y: -0.15, z: 0.25 }, { x: 0.8, y: -0.2, z: 0 });
      // Upper Wing
      addBox('Upper Wing Span', 'Airframe', 4.4, 0.06, 0.85, 0xb45309, { x: 0, y: 0.75, z: 0.3 }, { x: 0, y: 0.8, z: 0 });
      // Cabane Struts
      addBox('Cabane Struts (Center)', 'Airframe', 0.6, 0.55, 0.04, 0x18181b, { x: 0, y: 0.42, z: 0.28 }, { x: 0, y: 0.4, z: 0 });
      addBox('Left Interplane Strut', 'Airframe', 0.05, 0.84, 0.05, 0x18181b, { x: -1.75, y: 0.3, z: 0.28 }, { x: -0.6, y: 0.3, z: 0 });
      addBox('Right Interplane Strut', 'Airframe', 0.05, 0.84, 0.05, 0x18181b, { x: 1.75, y: 0.3, z: 0.28 }, { x: 0.6, y: 0.3, z: 0 });
      // Tail Empennage
      addBox('Horizontal Stabilizer & Elevator', 'Airframe', 1.8, 0.05, 0.45, 0x8b5a2b, { x: 0, y: 0.2, z: -1.3 }, { x: 0, y: 0.3, z: -0.4 });
      addBox('Vertical Fin & Hinged Rudder', 'Airframe', 0.05, 0.65, 0.5, 0xb45309, { x: 0, y: 0.55, z: -1.35 }, { x: 0, y: 0.5, z: -0.5 });
      // Landing Gear
      addBox('Landing Gear V-Struts', 'Running Gear', 0.9, 0.45, 0.06, 0x18181b, { x: 0, y: -0.35, z: 0.5 }, { x: 0, y: -0.3, z: 0 });
      addWheel('Left Wire-Spoke Rolling Wheel', 'Running Gear', { x: -0.62, y: -0.55, z: 0.5 }, { x: -0.5, y: -0.3, z: 0 }, 0.28, 0.14);
      addWheel('Right Wire-Spoke Rolling Wheel', 'Running Gear', { x: 0.62, y: -0.55, z: 0.5 }, { x: 0.5, y: -0.3, z: 0 }, 0.28, 0.14);
      addBox('Wooden Tail Skid Shoe', 'Running Gear', 0.06, 0.25, 0.3, 0x3e2723, { x: 0, y: -0.22, z: -1.2 }, { x: 0, y: -0.3, z: -0.3 });
      // Propeller
      const propGroup = new THREE.Group();
      propGroup.position.set(0, 0.05, 1.62);
      propGroup.userData = { partName: 'Spruce Propeller & Hub Spinner', subsystem: 'Propulsion', basePos: new THREE.Vector3(0, 0.05, 1.62), explodedPos: new THREE.Vector3(0, 0.05, 2.3) };
      const spinner = new THREE.Mesh(new THREE.ConeGeometry(0.12, 0.22, 16), makeMat(0x334155, 0.8, 0.2));
      spinner.rotation.x = Math.PI / 2;
      const blade1 = new THREE.Mesh(new THREE.BoxGeometry(0.12, 1.6, 0.03), makeMat(0xa16207, 0.1, 0.9));
      propGroup.add(spinner, blade1);
      modelGroup.add(propGroup);
      modelParts.push(propGroup);
      H.setAnimPropeller(propGroup);
      return true;
    }

    if (id === 'iron_monoplane') {
      // Duralumin Monoplane
      addBox('Riveted Duralumin Fuselage', 'Airframe', 0.78, 0.75, 2.8, 0x64748b, { x: 0, y: 0.1, z: -0.1 }, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 0.7, 0.3);
      addCylZ('NACA Radial Engine Cowl', 'Propulsion', 0.42, 0.42, 0.55, 0x475569, { x: 0, y: 0.1, z: 1.55 }, { x: 0, y: 0, z: 0.6 }, 20, 0.75, 0.25);
      addBox('Framed Greenhouse Canopy', 'Avionics', 0.55, 0.36, 0.9, 0x38bdf8, { x: 0, y: 0.55, z: 0.2 }, { x: 0, y: 0.5, z: 0 }, { x: 0, y: 0, z: 0 }, 0.2, 0.1, 0.65);
      // Wings
      addBox('Left Cantilever Wing', 'Airframe', 2.2, 0.08, 0.95, 0x475569, { x: -1.45, y: 0.05, z: 0.25 }, { x: -0.9, y: 0, z: 0 });
      addBox('Right Cantilever Wing', 'Airframe', 2.2, 0.08, 0.95, 0x475569, { x: 1.45, y: 0.05, z: 0.25 }, { x: 0.9, y: 0, z: 0 });
      // Underwing Bombs
      addBox('Left Bomb Pylon Rack', 'Armament', 0.06, 0.12, 0.4, 0x1e293b, { x: -1.25, y: -0.06, z: 0.25 }, { x: -0.5, y: -0.1, z: 0 });
      addCylZ('Left 250lb Iron Bomb', 'Armament', 0.12, 0.12, 0.65, 0x273318, { x: -1.25, y: -0.22, z: 0.25 }, { x: -0.5, y: -0.4, z: 0 });
      addBox('Right Bomb Pylon Rack', 'Armament', 0.06, 0.12, 0.4, 0x1e293b, { x: 1.25, y: -0.06, z: 0.25 }, { x: 0.5, y: -0.1, z: 0 });
      addCylZ('Right 250lb Iron Bomb', 'Armament', 0.12, 0.12, 0.65, 0x273318, { x: 1.25, y: -0.22, z: 0.25 }, { x: 0.5, y: -0.4, z: 0 });
      // Tail
      addBox('Horizontal Tailplane & Elevators', 'Airframe', 1.9, 0.06, 0.45, 0x475569, { x: 0, y: 0.25, z: -1.45 }, { x: 0, y: 0.3, z: -0.4 });
      addBox('Vertical Tail Fin & Rudder', 'Airframe', 0.06, 0.75, 0.6, 0x334155, { x: 0, y: 0.65, z: -1.45 }, { x: 0, y: 0.5, z: -0.5 });
      // Oleo Landing Gear
      addBox('Left Oleo Strut Housing', 'Running Gear', 0.08, 0.45, 0.08, 0x1e293b, { x: -0.75, y: -0.22, z: 0.4 }, { x: -0.4, y: -0.2, z: 0 });
      addBox('Right Oleo Strut Housing', 'Running Gear', 0.08, 0.45, 0.08, 0x1e293b, { x: 0.75, y: -0.22, z: 0.4 }, { x: 0.4, y: -0.2, z: 0 });
      addWheel('Left Rolling Combat Tire', 'Running Gear', { x: -0.75, y: -0.52, z: 0.4 }, { x: -0.4, y: -0.4, z: 0 }, 0.3, 0.18);
      addWheel('Right Rolling Combat Tire', 'Running Gear', { x: 0.75, y: -0.52, z: 0.4 }, { x: 0.4, y: -0.4, z: 0 }, 0.3, 0.18);
      addWheel('Castoring Tailwheel', 'Running Gear', { x: 0, y: -0.22, z: -1.45 }, { x: 0, y: -0.3, z: -0.4 }, 0.14, 0.08);
      // Propeller
      const propGroup = new THREE.Group();
      propGroup.position.set(0, 0.1, 1.88);
      propGroup.userData = { partName: '3-Blade Variable-Pitch Propeller', subsystem: 'Propulsion', basePos: new THREE.Vector3(0, 0.1, 1.88), explodedPos: new THREE.Vector3(0, 0.1, 2.5) };
      const spinner = new THREE.Mesh(new THREE.ConeGeometry(0.14, 0.28, 16), makeMat(0xdc2626, 0.8, 0.2));
      spinner.rotation.x = Math.PI / 2;
      const blades = new THREE.Mesh(new THREE.BoxGeometry(0.14, 1.8, 0.03), makeMat(0x1e293b, 0.7, 0.3));
      propGroup.add(spinner, blades);
      modelGroup.add(propGroup);
      modelParts.push(propGroup);
      H.setAnimPropeller(propGroup);
      return true;
    }

    if (id === 'fighter_jet') {
      // Supersonic Air Superiority Fighter
      addBox('Chiseled Supersonic Fuselage', 'Airframe', 0.85, 0.52, 3.8, 0x334155, { x: 0, y: 0.0, z: 0.0 }, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 0.6, 0.4);
      addBox('Dorsal Avionics Spine', 'Avionics', 0.35, 0.15, 2.2, 0x1e293b, { x: 0, y: 0.32, z: -0.2 }, { x: 0, y: 0.3, z: 0 });
      addConeZ('Radome Nose with Pitot Probe', 'Avionics', 0.38, 1.0, 0x0f172a, { x: 0, y: 0.0, z: 2.35 }, { x: 0, y: 0, z: 0.8 }, 18, 0.5, 0.5);
      addBox('Bubble Canopy & ACES II Seat', 'Avionics', 0.52, 0.35, 1.25, 0x38bdf8, { x: 0, y: 0.42, z: 0.85 }, { x: 0, y: 0.5, z: 0.2 }, { x: 0, y: 0, z: 0 }, 0.1, 0.1, 0.6);
      // Intakes with Splitter Plate Gaps
      addBox('Left Caret Supersonic Intake', 'Propulsion', 0.35, 0.45, 1.3, 0x1e293b, { x: -0.62, y: -0.05, z: 0.8 }, { x: -0.4, y: 0, z: 0.2 });
      addBox('Right Caret Supersonic Intake', 'Propulsion', 0.35, 0.45, 1.3, 0x1e293b, { x: 0.62, y: -0.05, z: 0.8 }, { x: 0.4, y: 0, z: 0.2 });
      // Swept Delta Wings
      addBox('Left Swept Delta Wing', 'Airframe', 1.9, 0.07, 1.6, 0x334155, { x: -1.35, y: -0.02, z: -0.3 }, { x: -0.9, y: 0, z: 0 });
      addBox('Right Swept Delta Wing', 'Airframe', 1.9, 0.07, 1.6, 0x334155, { x: 1.35, y: -0.02, z: -0.3 }, { x: 0.9, y: 0, z: 0 });
      // Wingtip Missiles (Placed Below Rails with No Volume Collision!)
      addBox('Left LAU-128 Missile Rail', 'Armament', 0.06, 0.08, 1.1, 0x0f172a, { x: -2.35, y: -0.02, z: -0.3 }, { x: -1.2, y: 0, z: 0 });
      addCylZ('Left AIM-9X Sidewinder Missile', 'Armament', 0.06, 0.06, 1.0, 0xf8fafc, { x: -2.35, y: -0.1, z: -0.25 }, { x: -1.2, y: -0.3, z: 0 });
      addBox('Right LAU-128 Missile Rail', 'Armament', 0.06, 0.08, 1.1, 0x0f172a, { x: 2.35, y: -0.02, z: -0.3 }, { x: 1.2, y: 0, z: 0 });
      addCylZ('Right AIM-9X Sidewinder Missile', 'Armament', 0.06, 0.06, 1.0, 0xf8fafc, { x: 2.35, y: -0.1, z: -0.25 }, { x: 1.2, y: -0.3, z: 0 });
      // Canted Vertical Fins
      addBox('Left Canted Vertical Fin', 'Airframe', 0.06, 0.95, 0.85, 0x1e293b, { x: -0.55, y: 0.65, z: -1.5 }, { x: -0.5, y: 0.5, z: -0.3 }, { x: 0, y: 0, z: 0.25 });
      addBox('Right Canted Vertical Fin', 'Airframe', 0.06, 0.95, 0.85, 0x1e293b, { x: 0.55, y: 0.65, z: -1.5 }, { x: 0.5, y: 0.5, z: -0.3 }, { x: 0, y: 0, z: -0.25 });
      // Twin Turbofans & Afterburner Flames
      addCylZ('Left Turbofan Titanium Nozzle', 'Propulsion', 0.2, 0.24, 0.45, 0x0f172a, { x: -0.28, y: -0.02, z: -2.05 }, { x: -0.2, y: 0, z: -0.6 }, 18, 0.85, 0.2);
      addCylZ('Right Turbofan Titanium Nozzle', 'Propulsion', 0.2, 0.24, 0.45, 0x0f172a, { x: 0.28, y: -0.02, z: -2.05 }, { x: 0.2, y: 0, z: -0.6 }, 18, 0.85, 0.2);
      // Animated Pulsing Flames
      const flameL = addConeZ('Left Afterburner Flame Core', 'Propulsion', 0.16, 0.7, 0x38bdf8, { x: -0.28, y: -0.02, z: -2.55 }, { x: -0.2, y: 0, z: -1.0 }, { x: 0, y: 0, z: 0 }, 16, 0.1, 0.1, 0.85);
      const flameR = addConeZ('Right Afterburner Flame Core', 'Propulsion', 0.16, 0.7, 0x38bdf8, { x: 0.28, y: -0.02, z: -2.55 }, { x: 0.2, y: 0, z: -1.0 }, { x: 0, y: 0, z: 0 }, 16, 0.1, 0.1, 0.85);
      animAfterburners.push(flameL, flameR);
      // Tricycle Gear
      addWheel('Nose Gear Steerable Wheel', 'Running Gear', { x: 0, y: -0.48, z: 1.6 }, { x: 0, y: -0.4, z: 0.5 }, 0.24, 0.14, true);
      addWheel('Main Gear Wheel (Left)', 'Running Gear', { x: -0.75, y: -0.52, z: -0.3 }, { x: -0.5, y: -0.4, z: 0 }, 0.28, 0.18);
      addWheel('Main Gear Wheel (Right)', 'Running Gear', { x: 0.75, y: -0.52, z: -0.3 }, { x: 0.5, y: -0.4, z: 0 }, 0.28, 0.18);
      return true;
    }

    if (id === 'stealth_bomber') {
      // B-2 Spirit Stealth Bomber - Radar-Absorbent Flying Wing & Serrated W-Trailing Edge
      addBox('Faceted Blended-Wing Centerbody', 'Armor', 1.8, 0.38, 2.4, 0x111827, { x: 0, y: 0.08, z: 0.1 }, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 0.2, 0.85);
      addBox('Stealth Forward Chine Wedge', 'Armor', 1.2, 0.22, 1.2, 0x18181b, { x: 0, y: 0.06, z: 1.5 }, { x: 0, y: 0, z: 0.4 });
      addBox('Serrated W-Chevron Beaver Tail', 'Airframe', 0.8, 0.16, 0.9, 0x111827, { x: 0, y: 0.08, z: -1.45 }, { x: 0, y: 0, z: -0.4 });

      // Cockpit & Covert Radar
      addBox('Gold-Film Anti-Reflective Cockpit Glazing', 'Avionics', 0.82, 0.16, 0.75, 0x0284c7, { x: 0, y: 0.26, z: 0.9 }, { x: 0, y: 0.4, z: 0.2 }, { x: 0, y: 0, z: 0 }, 0.2, 0.1, 0.65);
      addBox('APQ-181 Covert Radar Sensor Bay', 'Avionics', 0.6, 0.14, 0.5, 0x0f172a, { x: 0, y: 0.02, z: 2.05 }, { x: 0, y: 0, z: 0.6 });
      addBox('Dorsal Refueling Receptacle & Chevron', 'Airframe', 0.3, 0.04, 0.5, 0x38bdf8, { x: 0, y: 0.28, z: 0.2 }, { x: 0, y: 0.3, z: 0 });

      // Quad F118 Turbofans & S-Duct Intakes
      addBox('Left Flush S-Duct Intake & Diverter Ramp', 'Propulsion', 0.55, 0.22, 1.2, 0x0f172a, { x: -0.65, y: 0.22, z: 0.4 }, { x: -0.4, y: 0.2, z: 0.2 });
      addBox('Right Flush S-Duct Intake & Diverter Ramp', 'Propulsion', 0.55, 0.22, 1.2, 0x0f172a, { x: 0.65, y: 0.22, z: 0.4 }, { x: 0.4, y: 0.2, z: 0.2 });
      addBox('Left Thermal-Suppression Exhaust Trough', 'Propulsion', 0.5, 0.12, 1.0, 0x27272a, { x: -0.65, y: 0.18, z: -1.1 }, { x: -0.4, y: 0.2, z: -0.4 });
      addBox('Right Thermal-Suppression Exhaust Trough', 'Propulsion', 0.5, 0.12, 1.0, 0x27272a, { x: 0.65, y: 0.18, z: -1.1 }, { x: 0.4, y: 0.2, z: -0.4 });

      // Swept 33° Continuous Flying Wings & Elevons
      addBox('Left Swept Inner Flying Wing', 'Airframe', 1.8, 0.18, 2.0, 0x18181b, { x: -1.6, y: 0.06, z: -0.15 }, { x: -0.8, y: 0, z: 0 });
      addBox('Right Swept Inner Flying Wing', 'Airframe', 1.8, 0.18, 2.0, 0x18181b, { x: 1.6, y: 0.06, z: -0.15 }, { x: 0.8, y: 0, z: 0 });
      addBox('Left Outer Stealth Wing Extension', 'Airframe', 2.4, 0.1, 1.5, 0x18181b, { x: -3.5, y: 0.04, z: -0.65 }, { x: -1.5, y: 0, z: 0 });
      addBox('Right Outer Stealth Wing Extension', 'Airframe', 2.4, 0.1, 1.5, 0x18181b, { x: 3.5, y: 0.04, z: -0.65 }, { x: 1.5, y: 0, z: 0 });
      addBox('Left Split Drag-Rudder & Elevon', 'Airframe', 1.2, 0.05, 0.45, 0x27272a, { x: -3.4, y: 0.04, z: -1.35 }, { x: -1.2, y: 0, z: -0.4 });
      addBox('Right Split Drag-Rudder & Elevon', 'Airframe', 1.2, 0.05, 0.45, 0x27272a, { x: 3.4, y: 0.04, z: -1.35 }, { x: 1.2, y: 0, z: -0.4 });

      // Internal Weapons Bays & 4x GBU-31 Precision JDAM Bombs
      addBox('Left Internal Rotary Weapon Bay Doors', 'Armament', 0.65, 0.12, 1.6, 0x09090b, { x: -0.5, y: -0.14, z: 0.1 }, { x: -0.4, y: -0.4, z: 0 });
      addBox('Right Internal Rotary Weapon Bay Doors', 'Armament', 0.65, 0.12, 1.6, 0x09090b, { x: 0.5, y: -0.14, z: 0.1 }, { x: 0.4, y: -0.4, z: 0 });
      addCylZ('Left Bay JDAM Precision Bomb (Front)', 'Armament', 0.11, 0.11, 0.75, 0x3f4f2c, { x: -0.5, y: -0.06, z: 0.5 }, { x: -0.4, y: -0.6, z: 0.2 });
      addCylZ('Left Bay JDAM Precision Bomb (Rear)', 'Armament', 0.11, 0.11, 0.75, 0x3f4f2c, { x: -0.5, y: -0.06, z: -0.3 }, { x: -0.4, y: -0.6, z: -0.2 });
      addCylZ('Right Bay JDAM Precision Bomb (Front)', 'Armament', 0.11, 0.11, 0.75, 0x3f4f2c, { x: 0.5, y: -0.06, z: 0.5 }, { x: 0.4, y: -0.6, z: 0.2 });
      addCylZ('Right Bay JDAM Precision Bomb (Rear)', 'Armament', 0.11, 0.11, 0.75, 0x3f4f2c, { x: 0.5, y: -0.06, z: -0.3 }, { x: 0.4, y: -0.6, z: -0.2 });

      // Heavy Retractable Landing Gear Bogies
      addWheel('Nose Steerable Twin Gear Assembly', 'Running Gear', { x: 0, y: -0.42, z: 1.35 }, { x: 0, y: -0.3, z: 0.4 }, 0.22, 0.16, true);
      addWheel('Left Main 4-Wheel Bogie (Front)', 'Running Gear', { x: -1.25, y: -0.45, z: -0.1 }, { x: -0.6, y: -0.3, z: 0 }, 0.26, 0.18);
      addWheel('Left Main 4-Wheel Bogie (Rear)', 'Running Gear', { x: -1.25, y: -0.45, z: -0.55 }, { x: -0.6, y: -0.3, z: -0.2 }, 0.26, 0.18);
      addWheel('Right Main 4-Wheel Bogie (Front)', 'Running Gear', { x: 1.25, y: -0.45, z: -0.1 }, { x: 0.6, y: -0.3, z: 0 }, 0.26, 0.18);
      addWheel('Right Main 4-Wheel Bogie (Rear)', 'Running Gear', { x: 1.25, y: -0.45, z: -0.55 }, { x: 0.6, y: -0.3, z: -0.2 }, 0.26, 0.18);
      return true;
    }

    if (id === 'predator_drone') {
      // Slender Aerodynamic UAV Composite Airframe
      addBox('Carbon Composite Slender Fuselage', 'Airframe', 0.48, 0.46, 2.8, 0x64748b, { x: 0, y: 0.08, z: 0.0 }, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 0.4, 0.6);
      addBox('Bulbous SATCOM Satellite Uplink Dome', 'Avionics', 0.5, 0.32, 1.1, 0xf8fafc, { x: 0, y: 0.34, z: 0.75 }, { x: 0, y: 0.4, z: 0.2 });
      addRingZ('Internal SATCOM Parabolic Antenna Dish', 'Avionics', 0.18, 0.03, 0x0284c7, { x: 0, y: 0.38, z: 0.8 }, { x: 0, y: 0.7, z: 0.2 });
      addBox('Dorsal Turbocharger Engine Air Scoop', 'Propulsion', 0.28, 0.18, 0.7, 0x334155, { x: 0, y: 0.34, z: -0.65 }, { x: 0, y: 0.3, z: 0 });
      addCylZ('Nose Airspeed & Angle-of-Attack Pitot Probe', 'Avionics', 0.02, 0.02, 0.35, 0x0f172a, { x: 0, y: 0.08, z: 1.55 }, { x: 0, y: 0, z: 0.5 });

      // AN/AAS-52 Multi-spectral Targeting System (MTS) Gimbal
      addBox('MTS Gyro Gimbal Base Mount', 'Avionics', 0.32, 0.16, 0.32, 0x1e293b, { x: 0, y: -0.18, z: 1.15 }, { x: 0, y: -0.2, z: 0.3 });
      addSphere('EO/IR Multi-Spectral Sensor Turret Ball', 'Avionics', 0.2, 0x0284c7, { x: 0, y: -0.32, z: 1.15 }, { x: 0, y: -0.5, z: 0.4 });

      // High-Aspect Glider Wings & Winglets
      addBox('Left High-Aspect Glider Wing', 'Airframe', 3.1, 0.06, 0.52, 0x475569, { x: -1.75, y: 0.14, z: 0.2 }, { x: -1.1, y: 0, z: 0 });
      addBox('Right High-Aspect Glider Wing', 'Airframe', 3.1, 0.06, 0.52, 0x475569, { x: 1.75, y: 0.14, z: 0.2 }, { x: 1.1, y: 0, z: 0 });
      addBox('Left Winglet Aerodynamic Tip', 'Airframe', 0.05, 0.25, 0.35, 0x334155, { x: -3.3, y: 0.24, z: 0.2 }, { x: -1.4, y: 0.1, z: 0 });
      addBox('Right Winglet Aerodynamic Tip', 'Airframe', 0.05, 0.25, 0.35, 0x334155, { x: 3.3, y: 0.24, z: 0.2 }, { x: 1.4, y: 0.1, z: 0 });

      // Underwing M296 Pylons & AGM-114 Laser-Guided Hellfire Missiles
      addBox('Left Underwing Weapon Pylon Rack', 'Armament', 0.08, 0.16, 0.7, 0x1e293b, { x: -1.15, y: -0.02, z: 0.2 }, { x: -0.5, y: -0.2, z: 0 });
      addBox('Right Underwing Weapon Pylon Rack', 'Armament', 0.08, 0.16, 0.7, 0x1e293b, { x: 1.15, y: -0.02, z: 0.2 }, { x: 0.5, y: -0.2, z: 0 });
      addCylZ('Left AGM-114 Hellfire Missile Body', 'Armament', 0.08, 0.08, 1.0, 0x365314, { x: -1.15, y: -0.16, z: 0.2 }, { x: -0.5, y: -0.5, z: 0 });
      addSphere('Left Hellfire Semi-Active Laser Seeker', 'Armament', 0.075, 0xf59e0b, { x: -1.15, y: -0.16, z: 0.72 }, { x: -0.5, y: -0.5, z: 0.3 });
      addCylZ('Right AGM-114 Hellfire Missile Body', 'Armament', 0.08, 0.08, 1.0, 0x365314, { x: 1.15, y: -0.16, z: 0.2 }, { x: 0.5, y: -0.5, z: 0 });
      addSphere('Right Hellfire Semi-Active Laser Seeker', 'Armament', 0.075, 0xf59e0b, { x: 1.15, y: -0.16, z: 0.72 }, { x: 0.5, y: -0.5, z: 0.3 });

      // Inverted V-Tail & Ventral Skid Fin
      addBox('Inverted V-Ruddervator (Left)', 'Airframe', 0.06, 0.7, 0.42, 0x334155, { x: -0.36, y: -0.26, z: -1.45 }, { x: -0.5, y: -0.3, z: -0.4 }, { x: 0, y: 0, z: 0.78 });
      addBox('Inverted V-Ruddervator (Right)', 'Airframe', 0.06, 0.7, 0.42, 0x334155, { x: 0.36, y: -0.26, z: -1.45 }, { x: 0.5, y: -0.3, z: -0.4 }, { x: 0, y: 0, z: -0.78 });
      addBox('Ventral Keel Fin & Ground Propeller Guard', 'Airframe', 0.05, 0.45, 0.38, 0x1e293b, { x: 0, y: -0.35, z: -1.35 }, { x: 0, y: -0.4, z: -0.3 });

      // Pusher Propeller Engine Unit
      addBox('Rotax 914F Turbocharged Engine Nacelle', 'Propulsion', 0.4, 0.36, 0.65, 0x1e293b, { x: 0, y: 0.08, z: -1.3 }, { x: 0, y: 0, z: -0.4 });
      const propGroup = new THREE.Group();
      propGroup.position.set(0, 0.08, -1.65);
      propGroup.userData = { partName: 'Rear Rotax Pusher Propeller & Spinner', subsystem: 'Propulsion', basePos: new THREE.Vector3(0, 0.08, -1.65), explodedPos: new THREE.Vector3(0, 0.08, -2.3) };
      const spinner = new THREE.Mesh(new THREE.ConeGeometry(0.1, 0.18, 14), makeMat(0x0f172a, 0.8, 0.2));
      spinner.rotation.x = -Math.PI / 2;
      const blade = new THREE.Mesh(new THREE.BoxGeometry(0.08, 1.25, 0.02), makeMat(0x18181b, 0.6, 0.3));
      propGroup.add(spinner, blade);
      modelGroup.add(propGroup);
      modelParts.push(propGroup);
      H.setAnimPropeller(propGroup);

      // Tricycle Landing Gear
      addWheel('Nose Steerable Rolling Combat Wheel', 'Running Gear', { x: 0, y: -0.48, z: 0.85 }, { x: 0, y: -0.35, z: 0.4 }, 0.18, 0.1, true);
      addWheel('Left Main Spring-Strut Rolling Wheel', 'Running Gear', { x: -0.62, y: -0.52, z: -0.15 }, { x: -0.4, y: -0.3, z: 0 }, 0.2, 0.12);
      addWheel('Right Main Spring-Strut Rolling Wheel', 'Running Gear', { x: 0.62, y: -0.52, z: -0.15 }, { x: 0.4, y: -0.3, z: 0 }, 0.2, 0.12);
      return true;
    }

    if (id === 'attack_helicopter') {
      // Narrow Tandem Gunship
      addBox('Armored Tandem Gunship Fuselage', 'Airframe', 0.72, 0.92, 2.4, 0x1f2937, { x: 0, y: 0.1, z: 0.1 }, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 0.5, 0.5);
      addBox('Front Gunner Armored Canopy', 'Avionics', 0.54, 0.36, 0.7, 0x38bdf8, { x: 0, y: 0.42, z: 0.8 }, { x: 0, y: 0.5, z: 0.3 }, { x: 0, y: 0, z: 0 }, 0.2, 0.1, 0.65);
      addBox('Raised Pilot Armored Canopy', 'Avionics', 0.54, 0.42, 0.7, 0x38bdf8, { x: 0, y: 0.62, z: 0.15 }, { x: 0, y: 0.6, z: 0.1 }, { x: 0, y: 0, z: 0 }, 0.2, 0.1, 0.65);
      // TADS Nose & Chin 20mm Rotary Cannon
      addSphere('TADS/PNVS Sensor Turret', 'Avionics', 0.18, 0x0284c7, { x: 0, y: -0.05, z: 1.35 }, { x: 0, y: -0.2, z: 0.5 });
      addCylZ('Chin 20mm 3-Barrel Rotary Cannon', 'Armament', 0.08, 0.08, 1.2, 0x0f172a, { x: 0, y: -0.42, z: 0.9 }, { x: 0, y: -0.4, z: 0.4 });
      // Stub Wings & Weapons
      addBox('Left Weapon Stub Wing', 'Airframe', 0.95, 0.1, 0.4, 0x334155, { x: -0.82, y: 0.15, z: 0.2 }, { x: -0.6, y: 0, z: 0 });
      addBox('Right Weapon Stub Wing', 'Airframe', 0.95, 0.1, 0.4, 0x334155, { x: 0.82, y: 0.15, z: 0.2 }, { x: 0.6, y: 0, z: 0 });
      addCylZ('Left Hydra 70 Rocket Pod', 'Armament', 0.16, 0.16, 0.9, 0x273318, { x: -0.75, y: 0.02, z: 0.2 }, { x: -0.6, y: -0.3, z: 0 });
      addCylZ('Right Hydra 70 Rocket Pod', 'Armament', 0.16, 0.16, 0.9, 0x273318, { x: 0.75, y: 0.02, z: 0.2 }, { x: 0.6, y: -0.3, z: 0 });
      // Turboshaft Nacelles
      addCylZ('Left Turboshaft Nacelle', 'Propulsion', 0.22, 0.22, 1.3, 0x334155, { x: -0.42, y: 0.45, z: -0.2 }, { x: -0.3, y: 0.3, z: 0 });
      addCylZ('Right Turboshaft Nacelle', 'Propulsion', 0.22, 0.22, 1.3, 0x334155, { x: 0.42, y: 0.45, z: -0.2 }, { x: 0.3, y: 0.3, z: 0 });
      // Main Rotor
      const rotorGroup = new THREE.Group();
      rotorGroup.position.set(0, 0.85, 0.1);
      rotorGroup.userData = { partName: '4-Blade Composite Main Rotor', subsystem: 'Propulsion', basePos: new THREE.Vector3(0, 0.85, 0.1), explodedPos: new THREE.Vector3(0, 1.6, 0.1) };
      const mast = new THREE.Mesh(new THREE.CylinderGeometry(0.08, 0.08, 0.5, 12), makeMat(0x0f172a, 0.8, 0.2));
      const bladeA = new THREE.Mesh(new THREE.BoxGeometry(5.2, 0.04, 0.22), makeMat(0x18181b, 0.4, 0.6));
      const bladeB = new THREE.Mesh(new THREE.BoxGeometry(0.22, 0.04, 5.2), makeMat(0x18181b, 0.4, 0.6));
      rotorGroup.add(mast, bladeA, bladeB);
      modelGroup.add(rotorGroup);
      modelParts.push(rotorGroup);
      animRotors.push(rotorGroup);
      // Tail Boom & Rotor
      addBox('Tail Boom Structure', 'Airframe', 0.26, 0.32, 2.4, 0x1f2937, { x: 0, y: 0.3, z: -1.8 }, { x: 0, y: 0.2, z: -0.5 });
      addBox('Vertical Tail Fin', 'Airframe', 0.06, 0.75, 0.6, 0x334155, { x: 0, y: 0.65, z: -2.95 }, { x: 0, y: 0.4, z: -0.6 });
      const tailRotor = new THREE.Group();
      tailRotor.position.set(0.18, 0.65, -2.95);
      tailRotor.userData = { partName: 'Counter-Torque Tail Rotor', subsystem: 'Propulsion', isTailRotor: true, basePos: new THREE.Vector3(0.18, 0.65, -2.95), explodedPos: new THREE.Vector3(0.6, 0.65, -3.2) };
      tailRotor.add(new THREE.Mesh(new THREE.BoxGeometry(0.04, 1.2, 0.12), makeMat(0x0f172a, 0.6, 0.3)));
      modelGroup.add(tailRotor);
      modelParts.push(tailRotor);
      animRotors.push(tailRotor);
      // Tubular Skids
      addBox('Landing Skid (Left)', 'Running Gear', 0.08, 0.08, 2.2, 0x0f172a, { x: -0.65, y: -0.65, z: 0.1 }, { x: -0.4, y: -0.3, z: 0 });
      addBox('Landing Skid (Right)', 'Running Gear', 0.08, 0.08, 2.2, 0x0f172a, { x: 0.65, y: -0.65, z: 0.1 }, { x: 0.4, y: -0.3, z: 0 });
      return true;
    }

    // ── COMBAT VEHICLES ───────────────────────────────────────────────────────
    if (id === 'tank') {
      // Chobham Welded Lower Hull & Mine-Deflecting Belly
      addBox('Chobham Welded Lower Hull Tub', 'Armor', 1.7, 0.56, 3.6, 0x3f4f2c, { x: 0, y: 0.12, z: 0 }, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 }, 0.4, 0.6);
      addBox('Sloped Upper Glacis & Composite Tiles', 'Armor', 1.65, 0.24, 1.25, 0x364325, { x: 0, y: 0.46, z: 1.3 }, { x: 0, y: 0.3, z: 0.4 });
      addBox('Driver Hatch & 3-Prism Periscopes', 'Avionics', 0.45, 0.12, 0.45, 0x1e293b, { x: -0.35, y: 0.56, z: 1.05 }, { x: -0.2, y: 0.4, z: 0.2 });
      addBox('Front Mud Flaps & Heavy Tow Clevises', 'Running Gear', 1.85, 0.35, 0.25, 0x111827, { x: 0, y: 0.1, z: 1.88 }, { x: 0, y: 0, z: 0.5 });

      // Rear Engine Louver Deck & Dual Auxiliary Fuel Drums
      addBox('Turbine Engine Louver Exhaust Deck', 'Propulsion', 1.65, 0.14, 1.25, 0x273318, { x: 0, y: 0.46, z: -1.15 }, { x: 0, y: 0.3, z: -0.4 });
      addCylZ('Auxiliary External Fuel Drum (Left)', 'Propulsion', 0.22, 0.22, 0.75, 0x1f3317, { x: -0.55, y: 0.48, z: -1.85 }, { x: -0.3, y: 0.2, z: -0.5 });
      addCylZ('Auxiliary External Fuel Drum (Right)', 'Propulsion', 0.22, 0.22, 0.75, 0x1f3317, { x: 0.55, y: 0.48, z: -1.85 }, { x: 0.3, y: 0.2, z: -0.5 });

      // 12 Independent Road Wheels & Continuous Caterpillar Tracks
      for (let i = -2; i <= 3; i++) {
        const zPos = (i - 0.5) * 0.56;
        addWheel(`Road Wheel L#${i + 3}`, 'Running Gear', { x: -1.15, y: -0.22, z: zPos }, { x: -0.6, y: -0.2, z: (i - 0.5) * 0.05 }, 0.28, 0.18);
        addWheel(`Road Wheel R#${i + 3}`, 'Running Gear', { x: 1.15, y: -0.22, z: zPos }, { x: 0.6, y: -0.2, z: (i - 0.5) * 0.05 }, 0.28, 0.18);
      }
      addBox('Upper Caterpillar Track Band (Left)', 'Running Gear', 0.24, 0.06, 3.6, 0x18181b, { x: -1.15, y: 0.12, z: 0 }, { x: -0.4, y: 0.1, z: 0 });
      addBox('Upper Caterpillar Track Band (Right)', 'Running Gear', 0.24, 0.06, 3.6, 0x18181b, { x: 1.15, y: 0.12, z: 0 }, { x: 0.4, y: 0.1, z: 0 });

      // Modular Composite Side Skirts with ERA Blocks
      addBox('Heavy ERA Modular Skirts (Left)', 'Armor', 0.08, 0.48, 3.5, 0x273318, { x: -1.36, y: 0.16, z: 0 }, { x: -0.7, y: 0.1, z: 0 });
      addBox('Heavy ERA Modular Skirts (Right)', 'Armor', 0.08, 0.48, 3.5, 0x273318, { x: 1.36, y: 0.16, z: 0 }, { x: 0.7, y: 0.1, z: 0 });

      // 360° Rotating Combat Turret Assembly
      const turretGroup = new THREE.Group();
      turretGroup.position.set(0, 0.88, -0.05);
      turretGroup.userData = { partName: '360° Rotating Combat Turret', subsystem: 'Armament', basePos: new THREE.Vector3(0, 0.88, -0.05), explodedPos: new THREE.Vector3(0, 1.7, -0.05) };

      // Chobham Wedge Armored Turret Shell
      const turretBody = new THREE.Mesh(new THREE.BoxGeometry(1.65, 0.6, 2.1), makeMat(0x364325, 0.4, 0.6));
      turretGroup.add(turretBody);

      // Commander Cupola & Panoramic CITV
      const cupola = new THREE.Mesh(new THREE.CylinderGeometry(0.28, 0.28, 0.22, 16), makeMat(0x1e293b, 0.6, 0.3));
      cupola.position.set(-0.45, 0.4, -0.25);
      const citv = new THREE.Mesh(new THREE.CylinderGeometry(0.12, 0.14, 0.35, 14), makeMat(0x38bdf8, 0.6, 0.3));
      citv.position.set(-0.48, 0.58, 0.22);
      const gunnerSight = new THREE.Mesh(new THREE.BoxGeometry(0.26, 0.25, 0.35), makeMat(0x0284c7, 0.5, 0.3));
      gunnerSight.position.set(0.48, 0.42, 0.35);
      turretGroup.add(cupola, citv, gunnerSight);

      // Roof .50 Cal M2HB Machine Gun & Ammo Box
      const mgReceiver = new THREE.Mesh(new THREE.BoxGeometry(0.08, 0.12, 0.55), makeMat(0x0f172a, 0.8, 0.2));
      mgReceiver.position.set(-0.45, 0.62, -0.2);
      const mgBarrel = new THREE.Mesh(new THREE.CylinderGeometry(0.02, 0.02, 0.6, 8), makeMat(0x0f172a, 0.8, 0.2));
      mgBarrel.rotation.x = Math.PI / 2;
      mgBarrel.position.set(-0.45, 0.62, 0.35);
      const mgAmmo = new THREE.Mesh(new THREE.BoxGeometry(0.14, 0.18, 0.22), makeMat(0x15803d, 0.3, 0.7));
      mgAmmo.position.set(-0.58, 0.62, -0.15);
      turretGroup.add(mgReceiver, mgBarrel, mgAmmo);

      // Smoke Grenade Discharger Clusters
      const smokeL = new THREE.Mesh(new THREE.BoxGeometry(0.18, 0.22, 0.38), makeMat(0x18181b, 0.4, 0.6));
      smokeL.position.set(-0.92, 0.22, 0.2);
      const smokeR = new THREE.Mesh(new THREE.BoxGeometry(0.18, 0.22, 0.38), makeMat(0x18181b, 0.4, 0.6));
      smokeR.position.set(0.92, 0.22, 0.2);
      turretGroup.add(smokeL, smokeR);

      // Rear Turret Bustle Storage Basket & Comms Antennas
      const bustle = new THREE.Mesh(new THREE.BoxGeometry(1.5, 0.32, 0.65), makeMat(0x1e293b, 0.6, 0.4));
      bustle.position.set(0, 0.15, -1.35);
      const antennaL = new THREE.Mesh(new THREE.CylinderGeometry(0.015, 0.015, 1.2, 8), makeMat(0x0f172a, 0.8, 0.2));
      antennaL.position.set(-0.65, 0.75, -1.55);
      const antennaR = new THREE.Mesh(new THREE.CylinderGeometry(0.015, 0.015, 1.2, 8), makeMat(0x0f172a, 0.8, 0.2));
      antennaR.position.set(0.65, 0.75, -1.55);
      turretGroup.add(bustle, antennaL, antennaR);

      // 120mm Smoothbore Main Cannon & Mantlet
      const mantlet = new THREE.Mesh(new THREE.BoxGeometry(0.55, 0.38, 0.5), makeMat(0x1f2937, 0.6, 0.4));
      mantlet.position.set(0, 0, 1.15);
      const coaxMg = new THREE.Mesh(new THREE.CylinderGeometry(0.02, 0.02, 0.3, 8), makeMat(0x0f172a, 0.9, 0.1));
      coaxMg.rotation.x = Math.PI / 2;
      coaxMg.position.set(0.2, 0, 1.4);
      const barrelGeom = new THREE.CylinderGeometry(0.085, 0.095, 2.8, 16);
      barrelGeom.rotateX(Math.PI / 2);
      const barrel = new THREE.Mesh(barrelGeom, makeMat(0x111827, 0.75, 0.25));
      barrel.position.set(0, 0, 2.65);
      const evacuatorGeom = new THREE.CylinderGeometry(0.14, 0.14, 0.5, 14);
      evacuatorGeom.rotateX(Math.PI / 2);
      const evacuator = new THREE.Mesh(evacuatorGeom, makeMat(0x364325, 0.4, 0.6));
      evacuator.position.set(0, 0, 2.45);
      const muzzleGeom = new THREE.CylinderGeometry(0.11, 0.11, 0.2, 14);
      muzzleGeom.rotateX(Math.PI / 2);
      const muzzle = new THREE.Mesh(muzzleGeom, makeMat(0x1f2937, 0.8, 0.2));
      muzzle.position.set(0, 0, 4.05);

      turretGroup.add(mantlet, coaxMg, barrel, evacuator, muzzle);
      modelGroup.add(turretGroup);
      modelParts.push(turretGroup);
      H.setAnimTurret(turretGroup);
      return true;
    }

    if (id === 'missile_truck') {
      // 6x6 Heavy Boxed Tactical Truck Chassis
      addBox('Boxed C-Channel Chassis Rails', 'Airframe', 1.15, 0.3, 4.4, 0x334155, { x: 0, y: 0.18, z: 0 }, { x: 0, y: 0, z: 0 });
      addBox('Front Heavy Bullbar & Recovery Winch', 'Running Gear', 1.6, 0.45, 0.38, 0x0f172a, { x: 0, y: 0.22, z: 2.25 }, { x: 0, y: 0, z: 0.6 });

      // 6 Heavy Off-Road Combat Tires (Front Axle Steerable)
      addWheel('Front-Left Steerable Combat Tire', 'Running Gear', { x: -0.94, y: -0.22, z: 1.55 }, { x: -0.6, y: -0.2, z: 0.4 }, 0.38, 0.24, true);
      addWheel('Front-Right Steerable Combat Tire', 'Running Gear', { x: 0.94, y: -0.22, z: 1.55 }, { x: 0.6, y: -0.2, z: 0.4 }, 0.38, 0.24, true);
      addWheel('Mid-Left Heavy Planetary Tire', 'Running Gear', { x: -0.94, y: -0.22, z: -0.3 }, { x: -0.6, y: -0.2, z: 0 }, 0.38, 0.24);
      addWheel('Mid-Right Heavy Planetary Tire', 'Running Gear', { x: 0.94, y: -0.22, z: -0.3 }, { x: 0.6, y: -0.2, z: 0 }, 0.38, 0.24);
      addWheel('Rear-Left Heavy Planetary Tire', 'Running Gear', { x: -0.94, y: -0.22, z: -1.5 }, { x: -0.6, y: -0.2, z: -0.4 }, 0.38, 0.24);
      addWheel('Rear-Right Heavy Planetary Tire', 'Running Gear', { x: 0.94, y: -0.22, z: -1.5 }, { x: 0.6, y: -0.2, z: -0.4 }, 0.38, 0.24);

      // Deployable Hydraulic Stabilizer Outriggers
      addBox('Front-Left Hydraulic Stabilizer Outrigger', 'Running Gear', 0.28, 0.42, 0.2, 0x111827, { x: -0.92, y: -0.22, z: 0.6 }, { x: -0.5, y: -0.2, z: 0.2 });
      addBox('Front-Right Hydraulic Stabilizer Outrigger', 'Running Gear', 0.28, 0.42, 0.2, 0x111827, { x: 0.92, y: -0.22, z: 0.6 }, { x: 0.5, y: -0.2, z: 0.2 });
      addBox('Rear-Left Hydraulic Stabilizer Outrigger', 'Running Gear', 0.28, 0.42, 0.2, 0x111827, { x: -0.92, y: -0.22, z: -2.1 }, { x: -0.5, y: -0.2, z: -0.4 });
      addBox('Rear-Right Hydraulic Stabilizer Outrigger', 'Running Gear', 0.28, 0.42, 0.2, 0x111827, { x: 0.92, y: -0.22, z: -2.1 }, { x: 0.5, y: -0.2, z: -0.4 });

      // Armored Forward Crew Cab & Equipment Bay
      addBox('Armored Crew Cab & Sun Visor', 'Avionics', 1.5, 0.95, 1.5, 0x1e293b, { x: 0, y: 0.85, z: 1.35 }, { x: 0, y: 0.5, z: 0.4 });
      addBox('Armored Ballistic Glass Louvers', 'Armor', 1.35, 0.35, 0.1, 0x38bdf8, { x: 0, y: 0.95, z: 2.12 }, { x: 0, y: 0.6, z: 0.5 }, { x: 0, y: 0, z: 0 }, 0.2, 0.1, 0.7);
      addCylZ('Engine Intake Snorkel & Pre-Cleaner', 'Propulsion', 0.06, 0.06, 1.2, 0x0f172a, { x: 0.82, y: 0.95, z: 1.35 }, { x: 0.4, y: 0.3, z: 0.2 });
      addBox('Diamond-Plate Flatbed Deck & Lockers', 'Armor', 1.55, 0.12, 2.5, 0x475569, { x: 0, y: 0.38, z: -0.95 }, { x: 0, y: 0.2, z: -0.3 });

      // Spare Off-Road Tire Mounted Behind Cab
      addWheel('Spare Combat Tire Carrier', 'Running Gear', { x: 0, y: 0.75, z: 0.45 }, { x: 0, y: 0.4, z: 0.2 }, 0.36, 0.22);

      // Elevated Guided Missile Launcher Carriage
      const launchGroup = new THREE.Group();
      launchGroup.position.set(0, 0.78, -0.7);
      launchGroup.rotation.x = -0.4;
      launchGroup.userData = { partName: 'Elevated Guided Cruise Missile Launch Cells', subsystem: 'Armament', basePos: new THREE.Vector3(0, 0.78, -0.7), explodedPos: new THREE.Vector3(0, 1.7, -0.7) };

      // Chrome Hydraulic Lifting Rams
      const ramL = new THREE.Mesh(new THREE.CylinderGeometry(0.06, 0.06, 1.2, 10), makeMat(0x94a3b8, 0.9, 0.1));
      ramL.position.set(-0.45, 0.2, -0.45);
      const ramR = new THREE.Mesh(new THREE.CylinderGeometry(0.06, 0.06, 1.2, 10), makeMat(0x94a3b8, 0.9, 0.1));
      ramR.position.set(0.45, 0.2, -0.45);
      launchGroup.add(ramL, ramR);

      // Dual Armored Launch Canisters
      const tubeL = new THREE.Mesh(new THREE.BoxGeometry(0.62, 0.62, 2.6), makeMat(0x334155, 0.5, 0.4));
      tubeL.position.set(-0.38, 0, 0);
      const tubeR = new THREE.Mesh(new THREE.BoxGeometry(0.62, 0.62, 2.6), makeMat(0x334155, 0.5, 0.4));
      tubeR.position.set(0.38, 0, 0);

      // Visible Guided Cruise Missiles inside canisters
      const missileBodyL = new THREE.Mesh(new THREE.CylinderGeometry(0.18, 0.18, 2.2, 14), makeMat(0xf8fafc, 0.4, 0.5));
      missileBodyL.rotation.x = Math.PI / 2;
      missileBodyL.position.set(-0.38, 0, 0.1);
      const coneL = new THREE.Mesh(new THREE.ConeGeometry(0.2, 0.45, 14), makeMat(0xdc2626, 0.6, 0.3));
      coneL.rotation.x = Math.PI / 2;
      coneL.position.set(-0.38, 0, 1.4);

      const missileBodyR = new THREE.Mesh(new THREE.CylinderGeometry(0.18, 0.18, 2.2, 14), makeMat(0xf8fafc, 0.4, 0.5));
      missileBodyR.rotation.x = Math.PI / 2;
      missileBodyR.position.set(0.38, 0, 0.1);
      const coneR = new THREE.Mesh(new THREE.ConeGeometry(0.2, 0.45, 14), makeMat(0xdc2626, 0.6, 0.3));
      coneR.rotation.x = Math.PI / 2;
      coneR.position.set(0.38, 0, 1.4);

      // Mast-Mounted Phased Array Radar Dome on Launcher
      const radarMast = new THREE.Mesh(new THREE.CylinderGeometry(0.04, 0.04, 0.8, 8), makeMat(0x1e293b, 0.7, 0.3));
      radarMast.position.set(0, 0.65, -0.6);
      const radarDish = new THREE.Mesh(new THREE.CylinderGeometry(0.24, 0.24, 0.08, 16), makeMat(0x0284c7, 0.6, 0.35));
      radarDish.rotation.x = Math.PI / 4;
      radarDish.position.set(0, 1.05, -0.6);

      launchGroup.add(tubeL, tubeR, missileBodyL, coneL, missileBodyR, coneR, radarMast, radarDish);
      modelGroup.add(launchGroup);
      modelParts.push(launchGroup);
      return true;
    }

    if (id === 'armored_truck') {
      // MRAP V-Shaped Blast Deflecting Lower Monocoque Hull
      addBox('MRAP V-Shaped Blast Deflecting Hull', 'Armor', 1.4, 0.48, 4.0, 0x334155, { x: 0, y: 0.16, z: 0 }, { x: 0, y: 0, z: 0 });
      addBox('Reinforced Heavy Bumper & Ram Guard', 'Armor', 1.65, 0.42, 0.32, 0x0f172a, { x: 0, y: 0.22, z: 2.15 }, { x: 0, y: 0, z: 0.6 });
      addBox('Armored Hood & Engine Cooling Louvers', 'Armor', 1.35, 0.45, 1.3, 0x1e293b, { x: 0, y: 0.65, z: 1.35 }, { x: 0, y: 0.3, z: 0.3 });

      // 6 Run-Flat Heavy Combat Wheels (Front Steerable)
      addWheel('Front-Left Run-Flat Combat Wheel', 'Running Gear', { x: -0.96, y: -0.22, z: 1.35 }, { x: -0.6, y: -0.2, z: 0.4 }, 0.38, 0.24, true);
      addWheel('Front-Right Run-Flat Combat Wheel', 'Running Gear', { x: 0.96, y: -0.22, z: 1.35 }, { x: 0.6, y: -0.2, z: 0.4 }, 0.38, 0.24, true);
      addWheel('Mid-Left Run-Flat Combat Wheel', 'Running Gear', { x: -0.96, y: -0.22, z: -0.15 }, { x: -0.6, y: -0.2, z: 0 }, 0.38, 0.24);
      addWheel('Mid-Right Run-Flat Combat Wheel', 'Running Gear', { x: 0.96, y: -0.22, z: -0.15 }, { x: 0.6, y: -0.2, z: 0 }, 0.38, 0.24);
      addWheel('Rear-Left Run-Flat Combat Wheel', 'Running Gear', { x: -0.96, y: -0.22, z: -1.45 }, { x: -0.6, y: -0.2, z: -0.4 }, 0.38, 0.24);
      addWheel('Rear-Right Run-Flat Combat Wheel', 'Running Gear', { x: 0.96, y: -0.22, z: -1.45 }, { x: 0.6, y: -0.2, z: -0.4 }, 0.38, 0.24);

      // Full-Length Steel Running Boards & Crew Steps
      addBox('Left Anti-Skid Crew Running Board', 'Running Gear', 0.22, 0.06, 3.4, 0x0f172a, { x: -1.02, y: -0.05, z: 0 }, { x: -0.5, y: 0, z: 0 });
      addBox('Right Anti-Skid Crew Running Board', 'Running Gear', 0.22, 0.06, 3.4, 0x0f172a, { x: 1.02, y: -0.05, z: 0 }, { x: 0.5, y: 0, z: 0 });

      // Armored 4-Door Crew Capsule & Ballistic Windows
      addBox('Armored Welded 4-Door Crew Capsule', 'Armor', 1.5, 0.9, 2.9, 0x1e293b, { x: 0, y: 0.85, z: 0.15 }, { x: 0, y: 0.5, z: 0 });
      addBox('Multi-Pane Ballistic Windscreen', 'Avionics', 1.35, 0.36, 0.1, 0x38bdf8, { x: 0, y: 1.05, z: 0.95 }, { x: 0, y: 0.6, z: 0.3 }, { x: 0, y: 0, z: 0 }, 0.2, 0.1, 0.7);

      // Vertical Side Exhaust Stack with Heat Shield & Rain Cap
      addCylZ('Vertical Side Exhaust Stack & Shield', 'Propulsion', 0.08, 0.08, 1.4, 0x334155, { x: 0.86, y: 0.95, z: 0.6 }, { x: 0.4, y: 0.2, z: 0.2 });

      // Counter-RCIED Jammer Antenna & Rear Whip Antennas
      addCylZ('Duke Counter-RCIED Electronic Jammer', 'Avionics', 0.08, 0.08, 0.6, 0x0284c7, { x: -0.5, y: 1.6, z: 0.8 }, { x: -0.3, y: 0.6, z: 0.3 });
      addCylZ('Rear Comms Whip Antenna (Left)', 'Avionics', 0.015, 0.015, 1.1, 0x0f172a, { x: -0.68, y: 1.5, z: -1.25 }, { x: -0.3, y: 0.5, z: -0.3 });
      addCylZ('Rear Comms Whip Antenna (Right)', 'Avionics', 0.015, 0.015, 1.1, 0x0f172a, { x: 0.68, y: 1.5, z: -1.25 }, { x: 0.3, y: 0.5, z: -0.3 });

      // Rear Door, Spare Tire Carrier & Dual Jerry Cans
      addWheel('Rear Tailgate Spare Tire Carrier', 'Running Gear', { x: 0.35, y: 0.75, z: -1.45 }, { x: 0.2, y: 0.3, z: -0.4 }, 0.36, 0.22);
      addBox('Dual Fuel & Water Jerry Can Rack', 'Armor', 0.35, 0.38, 0.22, 0x15803d, { x: -0.45, y: 0.75, z: -1.42 }, { x: -0.3, y: 0.3, z: -0.4 });

      // Roof O-GPK Armored Gunner Cupola with .50 Cal M2HB
      const cupolaGroup = new THREE.Group();
      cupolaGroup.position.set(0, 1.5, 0.35);
      cupolaGroup.userData = { partName: 'Roof O-GPK Protected Cupola & .50 Cal M2HB', subsystem: 'Armament', basePos: new THREE.Vector3(0, 1.5, 0.35), explodedPos: new THREE.Vector3(0, 2.3, 0.35) };

      // Armored Shield Ring & Ballistic Glass Vision Blocks
      const shield = new THREE.Mesh(new THREE.CylinderGeometry(0.45, 0.45, 0.38, 16), makeMat(0x475569, 0.5, 0.4));
      const visionGlass = new THREE.Mesh(new THREE.BoxGeometry(0.4, 0.12, 0.08), makeMat(0x38bdf8, 0.4, 0.3, 0.7));
      visionGlass.position.set(0, 0.08, 0.45);
      cupolaGroup.add(shield, visionGlass);

      // Heavy .50 Cal M2HB Gun, Spade Grips & Flash Hider
      const gunBody = new THREE.Mesh(new THREE.BoxGeometry(0.12, 0.14, 0.75), makeMat(0x0f172a, 0.8, 0.2));
      gunBody.position.set(0, 0.14, 0.15);
      const gunBarrel = new THREE.Mesh(new THREE.CylinderGeometry(0.025, 0.03, 0.9, 10), makeMat(0x0f172a, 0.8, 0.2));
      gunBarrel.rotation.x = Math.PI / 2;
      gunBarrel.position.set(0, 0.14, 0.85);
      const ammoBox = new THREE.Mesh(new THREE.BoxGeometry(0.2, 0.24, 0.3), makeMat(0x15803d, 0.3, 0.7));
      ammoBox.position.set(-0.24, 0.14, 0.2);

      cupolaGroup.add(gunBody, gunBarrel, ammoBox);
      modelGroup.add(cupolaGroup);
      modelParts.push(cupolaGroup);
      return true;
    }

    return false;
  }
};
