package me.ilikefood971;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

/**
 * Server, Fabric API for 1.14 to early 1.17. Registry sync comes from {@code MixinPlayerManager},
 * which does nothing else. Cancelling that mixin cancels all syncs.
 */
public class LegacySyncSenderCanceller implements MixinCanceller {
	private static final String SYNC_SENDER = "net.fabricmc.fabric.mixin.registry.sync.MixinPlayerManager";

	@Override
	public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
		if (SYNC_SENDER.equals(mixinClassName.replace('/', '.'))) {
			RegSyncFix.LOGGER.info("Disabled Fabric API's {}", mixinClassName);
			return true;
		}
		return false;
	}
}
