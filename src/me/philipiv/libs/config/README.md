##About
An annotation system for saving and loading fields from config. Supports use of any config file, not just the default, and automatically handles their creation if they're not found.

##How to Use
1. Add the contents of this folder to your project
2. In your onEnable, create a new instance of ConfigManager, passing your plugin instance to the constructor, as so:
```Java
ConfigManager cm = new ConfigManager(this);
```
3. Use `cm.register(class)` to register all annotations in a given class, so that they can be correctly loaded/saved. Annotations in a non-registered class won't do anything!
4. Call `cm.load()` to load all values with `load=true` from the config.
5. Call `cm.save()` to save all values with `save=true` to the config.

That's it!