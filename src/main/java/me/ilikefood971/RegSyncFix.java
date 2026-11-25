package me.ilikefood971;

//? if >=1.18 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?} else {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
*///?}

public final class RegSyncFix {
	//? if >=1.18
	public static final Logger LOGGER = LoggerFactory.getLogger("regsyncfix");
	//? else
	//public static final Logger LOGGER = LogManager.getLogger("regsyncfix");

	private RegSyncFix() {
	}
}
