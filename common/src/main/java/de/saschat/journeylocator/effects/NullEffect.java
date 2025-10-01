package de.saschat.journeylocator.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class NullEffect extends MobEffect {
    public NullEffect() {
        super(MobEffectCategory.NEUTRAL, 1);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}
