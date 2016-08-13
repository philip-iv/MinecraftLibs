package me.philipiv.libs;

import org.bukkit.plugin.java.JavaPlugin;

import me.philipiv.libs.config.Config;
import me.philipiv.libs.config.ConfigSection;
import me.philipiv.libs.config.ConfigManager;

@ConfigSection(path="test")
public class LibsMain extends JavaPlugin {
	
	@Config(parent=@ConfigSection(path=""))
	public static String test1 = "test1";
	@Config(parent=@ConfigSection(config="config2"))
	private static String test2 = "test2";
	@Config(save=false)
	private static String wontsave = "hello";
	@Config(load=true)
	private static int testLoad = 3;
	
	@Override
	public void onEnable() {
		ConfigManager cm = new ConfigManager(this);
		cm.register(this.getClass());
		cm.load();
		getLogger().info(testLoad + "");
		cm.save();
	}
	
	@Override
	public void onDisable() {
		
	}
}
