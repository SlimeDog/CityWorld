package me.daddychurchill.CityWorld;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.daddychurchill.CityWorld.CityWorldGenerator.WorldStyle;

class CommandCityWorld implements CommandExecutor {
	private final CityWorld plugin;

	public CommandCityWorld(CityWorld plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if(split.length == 1) {
			if (split[0].equalsIgnoreCase("reload")) {
				if(sender instanceof Player player) {
					if (!player.hasPermission("cityworld.command.reload")) {
						player.sendMessage("§cYou do not have permission to use this command");
						return true;
					}
				}

				this.plugin.reload();

				sender.sendMessage("§aPlugin Reloaded");
				return true;
			}

			sender.sendMessage("/cityworld reload");
			sender.sendMessage("/cityworld [leave] [normal|destroyed|maze|astral|sanddunes|snowdunes|flooded|floating|nature|metro] [normal|nether|the_end]");
			return true;
		}

		if (sender instanceof Player player) {
			if (player.hasPermission("cityworld.command")) {

				boolean leaving = false;
				WorldStyle style = WorldStyle.NORMAL;
				Environment environment = Environment.NORMAL;
				boolean error = false;

				// arguments?
				for (String s : split) {
					if (s.compareToIgnoreCase("LEAVE") == 0) {
						leaving = true;
						break;
					} else if (s.compareToIgnoreCase("NETHER") == 0) {
						environment = Environment.NETHER;
					} else if (s.compareToIgnoreCase("THE_END") == 0) {
						environment = Environment.THE_END;
					} else
						try {
							style = WorldStyle.valueOf(s.trim().toUpperCase());
						} catch (IllegalArgumentException e) {
							CityWorld.log.info("[CityWorld] Unknown world style " + s);

							WorldStyle[] styles = WorldStyle.values();
							StringBuilder worldStyles = new StringBuilder();
							for (WorldStyle worldStyle : styles) {
								if (worldStyles.length() != 0)
									worldStyles.append("|");
								worldStyles.append(worldStyle.toString().toLowerCase());
							}
							CityWorld.log.info(
									"[CityWorld] this version knows about these styles: " + worldStyles);
							style = WorldStyle.NORMAL;
							error = true;
							break;
						}
				}

				// that isn't an option we support
				if (error) {
					sender.sendMessage("Syntax error");
					sender.sendMessage("/cityworld reload");
					sender.sendMessage("/cityworld [leave] [normal|destroyed|maze|astral|sanddunes|snowdunes|flooded|floating|nature|metro] [normal|nether|the_end]");
					return true;

					// let's try to leave the city
				} else if (leaving) {
					World world = Bukkit.getServer().getWorld("world");
					if (world == null) {
						sender.sendMessage("Cannot find the default world");
						sender.sendMessage("/cityworld reload");
						sender.sendMessage("/cityworld [leave] [normal|destroyed|maze|astral|sanddunes|snowdunes|flooded|floating|nature|metro] [normal|nether|the_end]");
						return true;
					} else if (player.getLocation().getWorld() == world) {
						sender.sendMessage("You are already there");
						return true;
					} else {
						player.sendMessage("Leaving CityWorld... Come back soon!");
						player.teleport(world.getHighestBlockAt(world.getSpawnLocation()).getLocation());
						return true;
					}

					// okay, let's enter the city
				} else {
					String worldName = getDefaultWorldName(style, environment);
					World world = Bukkit.getServer().getWorld(worldName);

					// if the world doesn't exist but the player has permission to create it
					if (world == null && player.hasPermission("cityworld.create")) {
						sender.sendMessage("Creating " + worldName + "... This will take a moment...");
						world = getDefaultCityWorld(style, environment);
					}

					// test to see if it exists
					if (world == null) {
						sender.sendMessage("Cannot find or create " + worldName);
						return true;
					} else {

						// are we actually going to the right place
						if (!(world.getGenerator() instanceof CityWorldGenerator))
							sender.sendMessage(
									"WARNING: The world called " + worldName + " does NOT use the CityWorld generator");

						// actually go there then
						if (player.getLocation().getWorld() == world) {
							sender.sendMessage("You are already here");
						} else {
							player.sendMessage("Entering " + worldName + "...");
							player.teleport(world.getSpawnLocation());
						}
						return true;
					}
				}
			} else {
				sender.sendMessage("You do not have permission to use this command");
				return true;
			}
		} else {
			sender.sendMessage("This command is only usable by a player");
			sender.sendMessage("/cityworld reload");
			sender.sendMessage("/cityworld [leave] [normal|destroyed|maze|astral|sanddunes|snowdunes|flooded|floating|nature|metro] [normal|nether|the_end]");
			return true;
		}
	}

	// prime world support (loosely based on ExpansiveTerrain)
	private final static String DEFAULT_WORLD_NAME = "CityWorld";

	private static String getDefaultWorldName(WorldStyle style, Environment environment) {
		String worldName = DEFAULT_WORLD_NAME;
		style = CityWorldGenerator.validateStyle(style);
		if (style != WorldStyle.NORMAL)
			worldName = worldName + "_" + style.toString().toLowerCase();
		if (environment != Environment.NORMAL)
			worldName = worldName + "_" + environment.toString().toLowerCase();
		return worldName;
	}

	private World getDefaultCityWorld(WorldStyle style, Environment environment) {

		// built yet?
		String worldName = getDefaultWorldName(style, environment);
		World cityWorldPrime = Bukkit.getServer().getWorld(worldName);
		if (cityWorldPrime == null) {

			// if neither then create/build it!
			WorldCreator worldcreator = new WorldCreator(worldName);
			// worldcreator.seed(-7457540200860308014L); // Beta seed
			// worldcreator.seed(5509442565638151977L); // 82,-35
			worldcreator.environment(environment);
			worldcreator.generator(new CityWorldGenerator(plugin, worldName, style.toString()));
			cityWorldPrime = Bukkit.getServer().createWorld(worldcreator);
		}
		return cityWorldPrime;
	}
}
