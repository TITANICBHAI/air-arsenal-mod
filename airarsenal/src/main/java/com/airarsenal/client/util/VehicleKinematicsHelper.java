package com.airarsenal.client.util;

/**
 * Real-world kinematics and procedural animation helper methods for Air Arsenal vehicles.
 *
 * <p>Implements authentic mechanical and aeronautical formulas for Minecraft Forge 1.12.2:</p>
 * <ul>
 *   <li><b>Oleo-Pneumatic Shock Absorbers:</b> Dual-chamber gas-oil strut compression under static
 *       weight, high-sink touchdown impact damping, and speed-dependent taxi compliance.</li>
 *   <li><b>Procedural Retractable Landing Gear:</b> Multi-phase hydraulic sequencing for bay doors,
 *       main trunnion leg pivot, trailing bogie leveling tilt, and scissor link folding.</li>
 *   <li><b>Hydro-Pneumatic Artillery Recoil:</b> Dual-phase recoil brake and pneumatic nitrogen
 *       recuperator stroke profiles with soft-battery throttling.</li>
 *   <li><b>Chassis Suspension Squat & Rocking:</b> Dynamic pitch and roll moment transfer through
 *       torsion bar road arms when firing high-caliber tank and artillery ordnance.</li>
 *   <li><b>Synchronized Autocannon Recoil:</b> Phase-indexed reciprocating strokes for twin and quad
 *       anti-aircraft weapon emplacements.</li>
 * </ul>
 */
public final class VehicleKinematicsHelper {

    private VehicleKinematicsHelper() {}

    // ─────────────────────────────────────────────────────────────────────────
    //  Kinematic Data Containers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Holds the calculated angles and displacements for a procedural landing gear assembly.
     */
    public static class LandingGearKinematics {
        /** Oleo strut vertical compression displacement in blocks (0 = uncompressed, positive = compressed). */
        public float strutCompression;
        /** Main trunnion pivot rotation in degrees (0 = fully extended downward, ~90 = stowed horizontally). */
        public float strutRetractAngle;
        /** Gear bay door opening angle in degrees (0 = closed flush, ~90 = swung fully open). */
        public float bayDoorAngle;
        /** Trailing bogie truck pitch tilt in degrees (positive = rear wheels hanging lower in flight, 0 = level). */
        public float bogiePitchAngle;
        /** Scissor torque link folding angle in degrees. */
        public float scissorLinkAngle;
        /** Effective ground roll rotation in radians. */
        public float wheelRollRad;
    }

    /**
     * Holds the calculated recoil stroke displacement and associated chassis shock.
     */
    public static class RecoilKinematics {
        /** Linear barrel displacement along the bore axis in blocks (negative = kicked backward). */
        public float barrelDisplacement;
        /** Normalized recoil velocity (0 = stationary, 1 = maximum explosive kickback). */
        public float recoilVelocity;
        /** Chassis pitch squat angle in degrees (positive = nose/front dips down, negative = rear squats). */
        public float chassisPitchSquat;
        /** Chassis roll rocking angle in degrees from off-axis turret firing. */
        public float chassisRollRock;
        /** Muzzle blast gas expansion radius factor (0 to 1). */
        public float muzzleGasEnvelope;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  1. Aircraft Landing Gear Procedural Kinematics
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Calculates the oleo-pneumatic shock absorber compression based on real-world gas spring
     * physics and hydraulic orifice damping.
     *
     * @param onGround        whether the aircraft has Weight-on-Wheels (WoW)
     * @param verticalMotion  downward sink velocity (typically entity.motionY)
     * @param forwardSpeed    ground taxi speed
     * @param animTick        world animation tick
     * @param maxStrokeLength maximum physical travel distance of the oleo piston (e.g. 0.25f blocks)
     * @return displacement compressed in blocks (0.0 = full extension / hanging in air)
     */
    public static float calculateOleoCompression(boolean onGround, double verticalMotion,
                                                float forwardSpeed, float animTick, float maxStrokeLength) {
        if (!onGround) {
            // Strut hangs in full extension against the mechanical rebound stop
            return 0.0f;
        }

        // Static load compression under airframe mass (typically 45-60% of total oleo stroke)
        float staticSag = maxStrokeLength * 0.52f;

        // Dynamic touchdown impact damping (energy dissipated through hydraulic metering pin)
        float impactEnergy = 0.0f;
        if (verticalMotion < -0.05) {
            impactEnergy = (float) Math.min(maxStrokeLength * 0.45f, -verticalMotion * 0.8f);
        }

        // Taxi bump compliance: periodic ground terrain roughness damped by fluid throttling
        float taxiVibration = 0.0f;
        if (forwardSpeed > 0.05f) {
            float freq = animTick * (forwardSpeed * 4.5f);
            taxiVibration = (float) (Math.sin(freq) * 0.025f + Math.sin(freq * 2.3f) * 0.012f) * maxStrokeLength;
        }

        float totalCompression = staticSag + impactEnergy + taxiVibration;
        return Math.max(0.0f, Math.min(maxStrokeLength * 0.98f, totalCompression));
    }

    /**
     * Evaluates a complete multi-phase landing gear retraction/extension cycle.
     *
     * <p>Sequencing phases:
     * <ul>
     *   <li>Phase 1 (0.00 - 0.25): Hydraulic latches release, bay doors swing open to 90 degrees.</li>
     *   <li>Phase 2 (0.20 - 0.85): Main trunnion actuator swings the main leg 90 degrees into the well.</li>
     *   <li>Phase 3 (0.30 - 0.80): Multi-wheel bogies tilt and level to fit within the internal bay profile.</li>
     *   <li>Phase 4 (0.80 - 1.00): Bay doors close flush over the stowed gear.</li>
     * </ul></p>
     *
     * @param deployProgress 0.0 = fully stowed in bay, 1.0 = fully extended and locked down
     * @param onGround       Weight-on-Wheels state
     * @param groundSpeed    forward velocity for wheel roll
     * @param animTick       animation tick
     * @return populated {@link LandingGearKinematics} structure
     */
    public static LandingGearKinematics calculateProceduralLandingGear(float deployProgress, boolean onGround,
                                                                       float groundSpeed, float animTick) {
        LandingGearKinematics gear = new LandingGearKinematics();
        float p = clamp(deployProgress, 0.0f, 1.0f);

        // 1. Bay Door Sequencing
        // Doors open first as gear extends (p 0.0 -> 0.25), remain open, then may remain open or close depending on model
        if (p < 0.25f) {
            // Opening from stowed
            float t = p / 0.25f;
            gear.bayDoorAngle = smoothStep(0.0f, 1.0f, t) * 92.0f;
        } else if (p > 0.85f) {
            // Fully extended: doors remain clear of the downlock strut
            gear.bayDoorAngle = 92.0f;
        } else {
            gear.bayDoorAngle = 92.0f;
        }

        // 2. Main Leg Trunnion Pivot (folds between 90 deg stowed and 0 deg vertical extension)
        float legExtProgress = smoothStep(0.20f, 0.85f, p);
        gear.strutRetractAngle = (1.0f - legExtProgress) * 90.0f;

        // 3. Oleo Strut Dynamic Compression
        if (p >= 0.95f) {
            gear.strutCompression = calculateOleoCompression(onGround, 0.0, groundSpeed, animTick, 0.25f);
        } else {
            gear.strutCompression = 0.0f; // in flight during transit
        }

        // 4. Trailing Bogie Truck Tilt
        // In flight, trailing bogie hangs ~18 degrees rear-down; levels to 0 on ground contact
        if (onGround && p > 0.90f) {
            gear.bogiePitchAngle = 0.0f;
        } else {
            gear.bogiePitchAngle = 18.0f * (1.0f - gear.strutCompression / 0.25f);
        }

        // 5. Scissor Torque Links (fold together as oleo compresses)
        float compressionRatio = gear.strutCompression / 0.25f;
        gear.scissorLinkAngle = 35.0f + compressionRatio * 45.0f;

        // 6. Kinematic Wheel Roll
        gear.wheelRollRad = onGround ? (animTick * groundSpeed * 4.2f) : 0.0f;

        return gear;
    }

    /**
     * Calculates the nose-wheel steering castor angle with high-speed stability damping.
     *
     * @param rudderAngle     commanded rudder deflection in degrees
     * @param groundSpeed     taxi velocity
     * @param onGround        Weight-on-Wheels
     * @return nose wheel steering yaw in degrees
     */
    public static float calculateNoseWheelSteer(float rudderAngle, float groundSpeed, boolean onGround) {
        if (!onGround) return 0.0f;
        // High steering authority at slow taxi speeds (up to 45 deg), progressively restricted at high takeoff speeds
        float speedDamping = 1.0f / (1.0f + groundSpeed * 0.45f);
        return clamp(rudderAngle * 1.5f * speedDamping, -48.0f, 48.0f);
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  2. Artillery & Heavy Gun Recoil Kinematics
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Computes the exact hydro-pneumatic recoil displacement profile of an artillery or tank cannon.
     *
     * <p>Modeled after the M256 120mm smoothbore and M777 155mm howitzer mechanisms:</p>
     * <ul>
     *   <li><b>Recoil Stroke (0 to tRecoil):</b> Explosive chamber ignition accelerates the recoiling
     *       mass backward against hydraulic throttling resistance. Peak travel reached at tRecoil.</li>
     *   <li><b>Counter-Recoil / Recuperation (tRecoil to tRecoil + tRecup):</b> Nitrogen gas in the
     *       recuperator pushes the cradle forward, with hydraulic control rods throttling flow
     *       during the last 15% to seat gently into battery without forward shock.</li>
     * </ul>
     *
     * @param ticksSinceFire      elapsed ticks since trigger pull
     * @param recoilDuration      duration of explosive backward stroke in ticks (typically 1.5 - 3.0 ticks)
     * @param recuperatorDuration duration of forward counter-recoil return in ticks (typically 8 - 18 ticks)
     * @param maxStrokeBlocks     maximum linear stroke length in blocks (e.g. 0.35f blocks)
     * @return linear displacement along bore axis in blocks (negative = displaced backward)
     */
    public static float calculateHydroPneumaticRecoil(float ticksSinceFire, float recoilDuration,
                                                     float recuperatorDuration, float maxStrokeBlocks) {
        if (ticksSinceFire < 0.0f) return 0.0f;

        float totalDuration = recoilDuration + recuperatorDuration;
        if (ticksSinceFire >= totalDuration) {
            return 0.0f; // returned to battery
        }

        if (ticksSinceFire <= recoilDuration) {
            // Phase 1: High-velocity explosive kickback (quarter-sine impulse)
            float normTime = ticksSinceFire / recoilDuration;
            float strokeFactor = (float) Math.sin(normTime * (Math.PI / 2.0));
            return -maxStrokeBlocks * strokeFactor;
        } else {
            // Phase 2: Smooth hydraulic counter-recoil return with exponential buffer throttling
            float t = (ticksSinceFire - recoilDuration) / recuperatorDuration;
            // Critically damped return curve: (1 - t) * e^(-2.5 * t)
            float returnFactor = (1.0f - t) * (float) Math.exp(-1.8 * t);
            return -maxStrokeBlocks * returnFactor;
        }
    }

    /**
     * Calculates the chassis suspension shock (squatting and rocking) from firing heavy ordnance.
     *
     * <p>Considers elevation angle moment arms and off-axis traverse torque transferred to vehicle
     * torsion bar suspensions.</p>
     *
     * @param ticksSinceFire elapsed ticks since fire
     * @param elevationDeg   cannon elevation angle in degrees
     * @param relativeYawDeg turret traverse relative to chassis heading in degrees
     * @param caliberWeight  normalized ordnance impulse factor (1.0 = 120mm tank round, 1.8 = 155mm howitzer)
     * @return populated {@link RecoilKinematics} structure
     */
    public static RecoilKinematics calculateChassisRecoilShock(float ticksSinceFire, float elevationDeg,
                                                               float relativeYawDeg, float caliberWeight) {
        RecoilKinematics recoil = new RecoilKinematics();

        float recoilDuration = 2.0f;
        float recupDuration = 14.0f;
        float maxStroke = 0.30f * caliberWeight;

        recoil.barrelDisplacement = calculateHydroPneumaticRecoil(ticksSinceFire, recoilDuration, recupDuration, maxStroke);

        if (ticksSinceFire >= 0.0f && ticksSinceFire < (recoilDuration + recupDuration + 10.0f)) {
            // Torsion bar suspension response: damped harmonic oscillator
            float omega = 0.75f;   // natural frequency of heavy armored hull
            float zeta = 0.38f;    // damping ratio of hydraulic shock absorbers
            float t = ticksSinceFire;

            float impulseResponse = (float) (Math.exp(-zeta * omega * t) * Math.sin(omega * t));

            // Pitch squat: firing forward squats the rear; elevated firing drives force down through hull
            float elevRad = (float) Math.toRadians(elevationDeg);
            float relYawRad = (float) Math.toRadians(relativeYawDeg);

            float forwardImpulse = (float) Math.cos(elevRad) * (float) Math.cos(relYawRad);
            float lateralImpulse = (float) Math.cos(elevRad) * (float) Math.sin(relYawRad);

            // Pitch displacement in degrees
            recoil.chassisPitchSquat = forwardImpulse * impulseResponse * 3.8f * caliberWeight;

            // Lateral roll rocking in degrees when firing over the tracks
            recoil.chassisRollRock = lateralImpulse * impulseResponse * 4.6f * caliberWeight;

            // Muzzle gas envelope expansion
            if (ticksSinceFire < 3.5f) {
                recoil.muzzleGasEnvelope = 1.0f - (ticksSinceFire / 3.5f);
            } else {
                recoil.muzzleGasEnvelope = 0.0f;
            }
        }

        return recoil;
    }

    /**
     * Calculates synchronized, alternating reciprocating barrel strokes for multi-barrel
     * anti-aircraft cannons (e.g. Twin 35mm Oerlikon or Quad 23mm ZSU).
     *
     * @param animTick       world animation tick
     * @param cycleLength    total tick duration of full firing cycle
     * @param barrelIndex    index of this barrel (0 to totalBarrels - 1)
     * @param totalBarrels   number of barrels in the battery
     * @param strokeDistance linear recoil stroke distance in blocks
     * @return linear displacement for this barrel in blocks
     */
    public static float calculateAlternatingRecoil(float animTick, int cycleLength,
                                                    int barrelIndex, int totalBarrels, float strokeDistance) {
        if (totalBarrels <= 0) return 0.0f;
        float phaseOffset = ((float) barrelIndex / (float) totalBarrels) * cycleLength;
        float currentCycleTime = (animTick + phaseOffset) % cycleLength;

        float fireStrokeTicks = Math.max(1.0f, cycleLength * 0.25f);
        float returnTicks = cycleLength * 0.55f;

        if (currentCycleTime < fireStrokeTicks) {
            // Rapid kickback
            float t = currentCycleTime / fireStrokeTicks;
            return -strokeDistance * (float) Math.sin(t * (Math.PI / 2.0));
        } else if (currentCycleTime < (fireStrokeTicks + returnTicks)) {
            // Return to battery
            float t = (currentCycleTime - fireStrokeTicks) / returnTicks;
            return -strokeDistance * (1.0f - t);
        }

        return 0.0f;
    }

    // ─────────────────────────────────────────────────────────────────────────
    //  3. Kinematic Math Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Evaluates a smooth S-curve Hermite interpolation between 0 and 1.
     */
    public static float smoothStep(float edge0, float edge1, float x) {
        float t = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    /**
     * Clamps a value between a minimum and maximum limit.
     */
    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    /**
     * Normalizes an angle into the [-180, 180] degree range.
     */
    public static float normalizeAngle(float angle) {
        while (angle > 180.0f) angle -= 360.0f;
        while (angle < -180.0f) angle += 360.0f;
        return angle;
    }
}
