package ca.lukegrahamlandry.citizens;

import ca.lukegrahamlandry.citizens.registry.EntityInit;
import ca.lukegrahamlandry.citizens.registry.ItemInit;
import net.fabricmc.api.ModInitializer;

public class CitizensMain implements ModInitializer {
	public static final String MOD_ID = "citizens";

    public static void log(String done) {
    	System.out.println(done);
    }

    @Override
	public void onInitialize() {
		EntityInit.registerAll();
		ItemInit.registerAll();
	}
}
