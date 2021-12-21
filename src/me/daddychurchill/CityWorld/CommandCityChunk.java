package me.daddychurchill.CityWorld;

import java.util.List;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import me.daddychurchill.CityWorld.Support.SupportBlocks;

class CommandCityChunk implements CommandExecutor {
	private final CityWorld plugin;

	public CommandCityChunk(CityWorld plugin) {
		this.plugin = plugin;
	}

	public CityWorld getWorld() {
		return plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		Player player = null;
		if (sender instanceof Player)
			player = (Player) sender;
//		else {
//			player = sender.getServer().getPlayer("echurchill");
//			if (player != null)
//				sender.sendMessage("Found " + player);
//		}

		if (player != null) {
			if (player.hasPermission("citychunk.command")) {
				boolean cleaning = false;
				boolean regening = false;
				boolean error = false;
				int radius = 0;

				// arguments?
				for (String s : split) {
					if (s.compareToIgnoreCase("CLEAN") == 0 && !cleaning)
						cleaning = true;
					else if (s.compareToIgnoreCase("REGEN") == 0 && !regening)
						regening = true;
					else if (cleaning || regening) {
						try {
							radius = Integer.parseInt(s);
						} catch (NumberFormatException e) {
							error = true;
							break;
						}
						radius = Math.max(0, Math.min(15, radius));
					} else {
						error = true;
						break;
					}
				}

				// that isn't an option we support or no option was given
				if (error || !(cleaning || regening)) {
					sender.sendMessage("Syntax error");
					return false;

					// let's do our stuff
				} else {

					// find ourselves
					World world = player.getWorld();
					Location location = player.getLocation();
					Chunk chunk = world.getChunkAt(location);
					int chunkX = chunk.getX();
					int chunkZ = chunk.getZ();

					// iterate through the chunks
					if (regening) {
						for (int x = chunkX - radius; x <= chunkX + radius; x++) {
							for (int z = chunkZ - radius; z <= chunkZ + radius; z++) {
								player.sendMessage("Regenerating chunk[" + x + ", " + z + "]");
								regenerateChunk(world.getChunkAt(x, z));
							}
						}
					}

					// cleaning up chunks of stray items
					if (cleaning) {
						player.sendMessage("Cleaning up orphan items");
						int x1 = (chunkX - radius) * SupportBlocks.sectionBlockWidth;
						int x2 = (chunkX + radius + 1) * SupportBlocks.sectionBlockWidth;
						int z1 = (chunkZ - radius) * SupportBlocks.sectionBlockWidth;
						int z2 = (chunkZ + radius + 1) * SupportBlocks.sectionBlockWidth;
						List<Entity> entities = world.getEntities();
						for (Entity entity : entities) {

							// something we care about?
							if (entity instanceof Item) {

								// is it in the right place?
								Location entityAt = entity.getLocation();
								if (entityAt.getBlockX() >= x1 && entityAt.getBlockX() < x2
										&& entityAt.getBlockZ() >= z1 && entityAt.getBlockZ() < z2)
									entity.remove();
							}
						}
					}

					// all done
					player.sendMessage("Finished chunk operation");
					return true;
				}

			} else {
				sender.sendMessage("You do not have permission to use this command");
				return false;
			}
		} else {
			sender.sendMessage("This command is only usable by a player");
			return false;
		}
	}

	public boolean regenerateChunk(Chunk chunk) {
		int bx = chunk.getX() << 4;
		int bz = chunk.getZ() << 4;
		try {
			BlockVector3 pt1 = BlockVector3.at(bx, 0, bz);
			BlockVector3 pt2 = BlockVector3.at(bx + 15, 256, bz + 15);
			BukkitWorld world = new BukkitWorld(chunk.getWorld());
			CuboidRegion region = new CuboidRegion(world, pt1, pt2);
			EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, 65536);
			boolean result = world.regenerate(region, session);
			session.flushSession();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
