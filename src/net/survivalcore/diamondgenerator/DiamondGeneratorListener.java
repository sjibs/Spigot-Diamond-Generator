package net.survivalcore.diamondgenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.survivalcore.utilities.LoadingBarAscii;

public class DiamondGeneratorListener implements Listener {
	Plugin plugin;
	DiamondGeneratorListener(Plugin p){
		this.plugin = p;
	}
	//needed because of some ridiculous spigot bug where interact event is called twice for the main
	//hand... YES, I do check if its the off-hand.
	HashSet<UUID> placeCooldown = new HashSet<UUID>();
	@EventHandler
	public void onDiamondGeneratorPlace(PlayerInteractEvent e){
		//guards
		if(e.useItemInHand().equals(Result.ALLOWED))return;
		if(e.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(e.getHand().equals(EquipmentSlot.OFF_HAND)) return;
		if(e.getItem()==null) return;
		if(e.getItem().getType().equals(Material.STICK));
		if(e.getClickedBlock().getType().isInteractable()&&!e.getPlayer().isSneaking()) return;
		if(e.getClickedBlock().getType().equals(Material.BARRIER)&&!e.getPlayer().isSneaking()) return;
		if(!e.getItem().hasItemMeta()) return;
		if(!e.getItem().getItemMeta().hasCustomModelData()) return;
		if(!e.getItem().getItemMeta().getPersistentDataContainer().has(DiamondGenerator.getNSK(), PersistentDataType.BYTE)) return;
		if(placeCooldown.contains(e.getPlayer().getUniqueId())) return;
		Location placeLocation = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();
		if(!placeLocation.getBlock().isEmpty()) return;
		if(DiamondGenerator.getDiamondGensInChunk(e.getClickedBlock().getChunk()).size()>=2){
			DGMessage.sendError(e.getPlayer(),"You can place a maximum of 2 generators per chunk!");
			return;
		}
		//checks that the player can place a block here
		BlockPlaceEvent bpe = new BlockPlaceEvent(placeLocation.getBlock(), placeLocation.getBlock().getState(), e.getClickedBlock(), e.getItem(), e.getPlayer(), true, e.getHand());
		Bukkit.getPluginManager().callEvent(bpe);
		if(bpe.isCancelled()) return;
		placeCooldown.add(e.getPlayer().getUniqueId());
		DiamondGenerator.placeDiamondGeneratorAt(placeLocation,e.getItem().getItemMeta().getCustomModelData());
		e.getItem().setAmount(e.getItem().getAmount()-1);
		e.getPlayer().updateInventory();
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable(){
			@Override
			public void run() {
				placeCooldown.remove(e.getPlayer().getUniqueId());
			}
		},5);
	}
	
	@EventHandler
	public void onDiamondGeneratorOpen(PlayerInteractEvent e){
		if(e.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(e.useItemInHand().equals(Result.ALLOWED))return;
		if(e.getAction()!=Action.RIGHT_CLICK_BLOCK) return;
		if(e.getHand().equals(EquipmentSlot.OFF_HAND)) return;
		if(placeCooldown.contains(e.getPlayer().getUniqueId())) return;
		if(e.getPlayer().isSneaking()) return;
		if(!e.getClickedBlock().getType().equals(Material.BARRIER))return;
		DiamondGenerator dg = DiamondGenerator.getDiamondGeneratorFromLocation(e.getClickedBlock().getLocation());
		if(dg==null)return;
		if(e.getItem()!=null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().getPersistentDataContainer().has(UpgradeShard.nsk, PersistentDataType.INTEGER)){
			//upgrade the generator
			e.getClickedBlock().getWorld().playSound(e.getClickedBlock().getLocation(), Sound.BLOCK_AMETHYST_BLOCK_FALL, 2f, 0.3f);
			int level = e.getItem().getItemMeta().getPersistentDataContainer().get(UpgradeShard.nsk, PersistentDataType.INTEGER)+1;
			if(dg.level>=level){
				DGMessage.sendError(e.getPlayer(),"This Generator has already been upgraded to Level §a"+dg.level);
			}else{
				DGMessage.sendMessage(e.getPlayer(), "Generator Upgraded to Level §a"+level);
				dg.updateLevel(level);
				e.getItem().setAmount(e.getItem().getAmount()-1);
				e.getPlayer().updateInventory();
			}
		}else{
			dg.openInventory(e.getPlayer());
			e.setCancelled(true);
		}

	}
	
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		if(!e.getBlock().getType().equals(Material.BARRIER))return;
		DiamondGenerator dg = DiamondGenerator.getDiamondGeneratorFromLocation(e.getBlock().getLocation());
		if(dg==null)return;
		DiamondGenerator.destroyDiamondGeneratorAt(e.getBlock().getLocation());
	}
	@EventHandler 
	void onChunkLoad(ChunkLoadEvent e){
		DiamondGenerator.loadDiamondGeneratorFromChunk(e.getChunk());
	}
	@EventHandler 
	void onChunkUnload(ChunkUnloadEvent e){
		DiamondGenerator.unloadDiamondGeneratorFromChunk(e.getChunk());
	}
	@EventHandler
	void onInventoryClick(InventoryClickEvent e){
		if(e.getInventory().getHolder() instanceof DiamondGeneratorUI){
			((DiamondGeneratorUI) e.getInventory().getHolder()).onClick(e);
		}
	}
	@EventHandler 
	void onInventoryClose(InventoryCloseEvent e){
		if(e.getInventory().getHolder() instanceof DiamondGeneratorUI){
			((DiamondGeneratorUI) e.getInventory().getHolder()).close();
		}
	}
	
}
