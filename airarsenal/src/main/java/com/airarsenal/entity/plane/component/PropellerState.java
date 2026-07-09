package com.airarsenal.entity.plane.component;

/**
 * The five functional damage states of a plane's propeller.
 *
 * Transitions are driven by {@link PropellerComponent#takeDamage(float)}.
 *
 * | State       | HP %    | Speed mult | Yaw drift       |
 * |-------------|---------|------------|-----------------|
 * | INTACT      | >75 %   | 1.00       | none            |
 * | DAMAGED     | >50 %   | 0.90       | none            |
 * | HEAVY_DAMAGE| >25 %   | 0.65       | ±3°/tick        |
 * | CRITICAL    | >0  %   | 0.35       | ±7°/tick        |
 * | DESTROYED   | 0 %     | 0.00       | none (glide)    |
 */
public enum PropellerState {
    INTACT,
    DAMAGED,
    HEAVY_DAMAGE,
    CRITICAL,
    DESTROYED
}
