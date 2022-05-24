package net.survivalcore.diamondgenerator;

import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class DiamondGeneratorCommands implements CommandExecutor {
	private Plugin plugin;
	NamespacedKey nsk;
	public DiamondGeneratorCommands(Main plugin){
		plugin.getCommand("dg").setExecutor(this);
		this.plugin = plugin;
		nsk = new NamespacedKey(plugin, "diamondgen");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		if(!sender.hasPermission("dg"))return false;
		if(args.length<1) return false;
		if(args[0].equals("give")&&args.length>=3){
			Player p = Bukkit.getPlayer(args[1]);
			switch(args[2]){
			case "dg":
				((Player) sender).getInventory().addItem(DiamondGenerator.getItemStack(1));
				break;
			case "shard1":
				((Player) sender).getInventory().addItem(UpgradeShard.commonUpgradeShard);
				break;
			case "shard2":
				((Player) sender).getInventory().addItem(UpgradeShard.rareUpgradeShard);
				break;
			case "shard3":
				((Player) sender).getInventory().addItem(UpgradeShard.legendaryUpgradeShard);
				break;
			}
		}
		return true;
	}

}
