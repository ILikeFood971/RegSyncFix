package net.ilikefood971.mixin.client;

import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.ilikefood971.RegSyncFix;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnstableApiUsage")
@Mixin(RegistrySyncManager.class)
public class RegistrySyncManagerMixin {
	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;", ordinal = 0), method = "checkRemoteRemap", cancellable = true)
	private static void proceedAnyways(CallbackInfo info) {
		RegSyncFix.LOGGER.warn("Proceeding despite registry mismatch");
		info.cancel();
	}
}