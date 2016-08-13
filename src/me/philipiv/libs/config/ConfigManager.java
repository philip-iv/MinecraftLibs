package me.philipiv.libs.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
	private JavaPlugin plugin;
	private Map<String, FileConfiguration> configs = new HashMap<>();
	private Map<FileConfiguration, File> files = new HashMap<>();
	private Map<ConfigSection, Map<Field, Config>> configSections = new HashMap<>();
	
	/**
	 * Create a new ConfigManager
	 * @param plugin an instance of your plugin
	 */
	public ConfigManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Processes all annotations in the given class, allowing them to be saved and loaded.
	 * Any class with annotations that is not registered will not save or load their config values.
	 * @param clazz The class to register
	 */
	public void register(Class<?> clazz) {
		//get parent config section
		ConfigSection parent = clazz.getAnnotation(ConfigSection.class);
		
		//create FileConfiguration for the section if it doesn't already exist
		if (!configs.containsKey(parent.config()))
			createConfig(parent.config());
		
		//add the section to the hashmap
		if (!configSections.containsKey(parent))
			configSections.put(parent, new HashMap<Field, Config>());
		
		//get all configs in the class
		List<Field> fields = new ArrayList<>();
		for (Field f : clazz.getDeclaredFields()) {
			Config c = f.getAnnotation(Config.class);
			if (c != null)
				fields.add(f);			
		}
		
		//add all config elements to configSections
		for (Field f : fields) {
			Config c = f.getAnnotation(Config.class);
			
			//check if config has default parent
			ConfigSection cParent = null;
			try {
				if (!c.parent().equals(c.annotationType().getMethod("parent").getDefaultValue())) {
					cParent = c.parent();
					if (!configs.containsKey(cParent.config())) {
						createConfig(cParent.config());
					}
					if (!configSections.containsKey(cParent))
						configSections.put(cParent, new HashMap<Field, Config>());
				}
				else
					cParent = parent;
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			//save field to correct map
			Map<Field, Config> fieldConfigMap = configSections.get(cParent);
			fieldConfigMap.put(f, c);
			configSections.put(cParent, fieldConfigMap);
		}
	}
	
	/**
	 * For every {@link Config} with {@link Config#load load} set to true, load its value
	 */
	public void load() {
		for (ConfigSection cs : configSections.keySet()) {
			FileConfiguration config = configs.get(cs.config());
			String path = cs.path() + ".";
			Map<Field, Config> configs = configSections.get(cs);
			for (Field f : configs.keySet()) {
				Config c = configs.get(f);
				if (!c.load())
					continue;
				String cPath = path + ((c.name().equals("")) ? f.getName() : c.name());
				f.setAccessible(true);
				try {
					f.set(null, config.get(cPath));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					plugin.getLogger().info("WARNING: failed to load " + cPath + " from " + files.get(config).toString());
				}
			}
		}
	}
	
	/**
	 * For every {@link Config} with {@link Config#save save} set to true, save its value
	 */
	public void save() {
		for (ConfigSection cs : configSections.keySet()) {
			FileConfiguration config = configs.get(cs.config());
			String path = cs.path() + ".";
			Map<Field, Config> configs = configSections.get(cs);
			for (Field f : configs.keySet()) {
				Config c = configs.get(f);
				if (!c.save())
					continue;
				String cPath = path + ((c.name().equals("")) ? f.getName() : c.name());
				f.setAccessible(true);
				try {
					config.set(cPath, f.get(null));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			try {
				config.save(files.get(config));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void createConfig(String configName) {
		File configFile = new File(plugin.getDataFolder(), configName + ".yml");
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		configs.put(configName, config);
		files.put(config, configFile);
	}
	
	/**
	 * 
	 * @param config The name of the config, without the extension
	 * @return the {@link FileConfiguration} with that name, or null if it does not exist
	 */
	public FileConfiguration getConfig(String config) {
		return configs.get(config);
	}
}
