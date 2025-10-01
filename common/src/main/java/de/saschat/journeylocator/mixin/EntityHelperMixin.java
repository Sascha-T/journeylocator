package de.saschat.journeylocator.mixin;

import de.saschat.journeylocator.JourneyLocator;
import journeymap.client.InternalStateHandler;
import journeymap.client.model.entity.EntityHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityHelper.class, remap = false)
public class EntityHelperMixin {
    @Redirect(method = "getPlayersNearby", at = @At(value = "INVOKE", target = "Ljourneymap/client/InternalStateHandler;isExpandedRadarEnabled()Z"), remap = false)
    private static boolean gpn(InternalStateHandler instance) {
        return instance.isExpandedRadarEnabled() || JourneyLocator.getState();
    }
}
