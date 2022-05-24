package net.survivalcore.diamondgenerator;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class UpgradeShard {
	public static ItemStack commonUpgradeShard;
	public static ItemStack rareUpgradeShard;
	public static ItemStack legendaryUpgradeShard;
	protected static NamespacedKey nsk;
	public static void init(Plugin plugin){
		nsk = new NamespacedKey(plugin, "upgradeshard");
		ItemStack is = new ItemStack(Material.AMETHYST_SHARD);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§e§lCommon Upgrade Shard");
		im.setLore(Arrays.asList("§7Used to upgrade spawners and","§7diamond generators to Level 2"));
		im.getPersistentDataContainer().set(nsk, PersistentDataType.INTEGER, 1);
		im.setCustomModelData(1);
		is.setItemMeta(im);
		commonUpgradeShard= is;
		is = new ItemStack(Material.AMETHYST_SHARD);
		im = is.getItemMeta();
		im.setDisplayName("§d§lRare Upgrade Shard");
		im.getPersistentDataContainer().set(nsk, PersistentDataType.INTEGER, 2);
		im.setCustomModelData(2);
		im.setLore(Arrays.asList("§7Used to upgrade spawners and","§7diamond generators to Level 3"));
		is.setItemMeta(im);
		rareUpgradeShard=is;
		is = new ItemStack(Material.AMETHYST_SHARD);
		im = is.getItemMeta();
		im.setDisplayName("§c§lLegendary Upgrade Shard");
		im.setLore(Arrays.asList("§7Used to upgrade spawners and","§7diamond generators to Level 4"));
		im.getPersistentDataContainer().set(nsk, PersistentDataType.INTEGER, 3);
		im.setCustomModelData(3);
		is.setItemMeta(im);
		legendaryUpgradeShard=is;
	}
}
