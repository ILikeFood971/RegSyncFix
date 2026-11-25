package me.ilikefood971.mixin;

import me.ilikefood971.RegSyncFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client, Fabric API for 1.21.4+: {@code apply} handles registry data sent by the server.
 * Skipping keeps the client's own registry IDs while still finishing the handshake.
 * <p>
 * {@link Pseudo} makes this work with older Fabric API versions missing the target.
 */
@Pseudo
@Mixin(targets = "net.fabricmc.fabric.impl.client.registry.sync.ClientRegistrySyncHandler", remap = false)
public abstract class ClientRegistrySyncHandlerMixin {
	@Inject(method = "apply", remap = false, at = @At("HEAD"), cancellable = true, require = 0)
	private static void regsyncfix$ignoreSync(CallbackInfo ci) {
		RegSyncFix.LOGGER.warn("Ignoring registry sync from server");
		ci.cancel();
	}
}
