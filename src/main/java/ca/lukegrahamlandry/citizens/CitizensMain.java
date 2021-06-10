package ca.lukegrahamlandry.citizens;

import ca.lukegrahamlandry.citizens.registry.EntityInit;
import net.fabricmc.api.ModInitializer;

public class CitizensMain implements ModInitializer {
	public static final String MOD_ID = "citizens";

	@Override
	public void onInitialize() {
		EntityInit.registerAll();
	}
}
