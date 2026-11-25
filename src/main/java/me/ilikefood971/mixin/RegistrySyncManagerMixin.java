package me.ilikefood971.mixin;

import me.ilikefood971.RegSyncFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Turns off Fabric API's registry sync.
 * <p>
 * Each injector uses {@code require = 0} because each one exists only in some Fabric API versions.
 */
@Mixin(targets = "net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager", remap = false)
public abstract class RegistrySyncManagerMixin {
	/**
	 * On the server don't send a registry sync to joining players.
	 * {@code sendPacket} is used by Fabric API for 1.17 to 1.20.1, {@code configureClient} for 1.20.2+.
	 */
	@SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = {"sendPacket", "configureClient"}, remap = false, at = @At("HEAD"), cancellable = true, require = 0)
	private static void regsyncfix$skipSync(CallbackInfo ci) {
		RegSyncFix.LOGGER.info("Skipped sending registry sync to client");
		ci.cancel();
	}

	/**
	 * Client, Fabric API for 1.14 to 1.21.3 Skips the remap but still finishes the handshake.
	 */
	@SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @ModifyVariable(method = "receivePacket", remap = false, at = @At("HEAD"), argsOnly = true, require = 0)
	private static boolean regsyncfix$ignoreSync(boolean accept) {
		if (accept) {
			RegSyncFix.LOGGER.warn("Ignoring registry sync from server");
		}
		return false;
	}
}
