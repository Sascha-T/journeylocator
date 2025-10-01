package de.saschat.journeylocator.mixin;

import de.saschat.journeylocator.JourneyLocator;
import journeymap.client.InternalStateHandler;
import journeymap.client.model.entity.EntityHelper;
import journeymap.client.render.ingame.WaypointDecorationRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InternalStateHandler.class, remap = false)
public class InternalStateHandlerMixin {

    @Inject(at = @At("HEAD"), method = "setJourneyMapServerConnection", remap = false)
    public void setJourneyMapServerConnectionMixin(boolean journeyMapServerConnection, CallbackInfo ci)  {
        JourneyLocator.setState(!journeyMapServerConnection);
    }
}
