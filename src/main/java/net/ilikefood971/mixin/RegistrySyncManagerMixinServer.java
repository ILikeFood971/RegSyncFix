package net.ilikefood971.mixin;

import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(RegistrySyncManager.class)
public class RegistrySyncManagerMixinServer {
	@Inject(at = @At("HEAD"), method = "configureClient", cancellable = true)
	private static void init(CallbackInfo info) {
		info.cancel();
	}
}