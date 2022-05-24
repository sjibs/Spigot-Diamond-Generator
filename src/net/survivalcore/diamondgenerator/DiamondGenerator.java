package net.survivalcore.diamondgenerator;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import net.survivalcore.utilities.LoadingBarAscii;

public class DiamondGenerator implements Serializable {
    @Expose
	protected SerialisableBlockLocation sbLocation;
    @Expose
    protected int level;
    @Expose
    protected int inventory;
    @Expose
	protected int dgtimer = 0;
	
	private static final long serialVersionUID = 1L;
	private static ArrayList<DiamondGenerator> generators = new ArrayList<DiamondGenerator>();
	private static NamespacedKey nsk;
	protected Inventory uiInventory=null;

	private static Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .create();
	/**
	 * Sets the diamond generators class key
	 * @param plugin
	 */
	protected static void init(Plugin plugin){
		nsk = new NamespacedKey(plugin, "diamondgen");
	}
	/**
	 * default constructor for the diamond generator
	 */
	public DiamondGenerator(){}
	/**
	 * constructor for a diamond generator
	 * @param location the bukkit location of the generator
	 * @param level the level of the generator
	 * @param inventory the value of the maximum number of diamonds allowed within the generator
	 */
	public DiamondGenerator(Location location, int level, int inventory){
		this.sbLocation = new SerialisableBlockLocation(location.getBlockX(),location.getBlockY(),location.getBlockZ(), location.getWorld().getName());
		this.level = level;
		this.inventory = inventory;
	}
	/**
	 * method to place a diamond generator at a location
	 * @param location
	 */
	public static void placeDiamondGeneratorAt(Location location,int level){
		ArrayList<DiamondGenerator> gens = getDiamondGensInChunk(location.getChunk());
		DiamondGenerator dg = new DiamondGenerator(location, 1, 0);
		gens.add(dg);
		generators.add(dg);
		dg.level = level;
		saveDiamondGensForChunk(gens.toArray(new DiamondGenerator[gens.size()]),location.getChunk());
		ArmorStand as1 = (ArmorStand) location.getWorld().spawnEntity(location.add(0.5d,0d,0.5d), EntityType.ARMOR_STAND);
		as1.setBasePlate(false);
		as1.setVisible(false);
		as1.setSilent(true);
		as1.setGravity(false);
		as1.setCustomName(LoadingBarAscii.getLoadingBar(0,10));
		as1.getPersistentDataContainer().set(nsk, PersistentDataType.INTEGER, 0);
		as1.setCollidable(false);
		as1.setCustomNameVisible(true);
		as1.setSmall(true);
		as1.setAI(false);
		as1.setInvulnerable(true);
		as1.getLocation().getBlock().setBlockData(Material.BARRIER.createBlockData());
		as1.getEquipment().setHelmet(getItemStack(level));
		as1.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
		as1.addEquipmentLock(EquipmentSlot.CHEST, LockType.REMOVING_OR_CHANGING);
		as1.addEquipmentLock(EquipmentSlot.LEGS, LockType.REMOVING_OR_CHANGING);
		as1.addEquipmentLock(EquipmentSlot.FEET, LockType.REMOVING_OR_CHANGING);
		
		
		ArmorStand as2 = (ArmorStand) location.getWorld().spawnEntity(location.add(0,0.25d,0), EntityType.ARMOR_STAND);
		as2.setBasePlate(false);
		as2.setVisible(false);
		as2.setSilent(true);
		as2.setGravity(false);
		as2.setCustomName("§b§lDiamond Generator");
		as2.getPersistentDataContainer().set(nsk, PersistentDataType.INTEGER, 1);
		as2.setCustomNameVisible(true);
		as2.setSmall(true);
		as2.setCollidable(false);
		as2.setAI(false);
		as2.setInvulnerable(true);
		as2.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
		as2.addEquipmentLock(EquipmentSlot.CHEST, LockType.REMOVING_OR_CHANGING);
		as2.addEquipmentLock(EquipmentSlot.LEGS, LockType.REMOVING_OR_CHANGING);
		as2.addEquipmentLock(EquipmentSlot.FEET, LockType.REMOVING_OR_CHANGING);
		
	}
	/**
	 * method to destroy a diamond generator at a location
	 * @param location
	 */
	public static void destroyDiamondGeneratorAt(Location location){
		int level =1;
		int diamonds = 0;
		DiamondGenerator dg2remove = getDiamondGeneratorFromLocation(location);
		if(dg2remove==null)return;
		level = dg2remove.level;
		diamonds = dg2remove.inventory;
		generators.remove(dg2remove);
		ArrayList<DiamondGenerator> gens = getDiamondGensInChunk(location.getChunk());
		for(DiamondGenerator dg : gens){
			if(dg.sbLocation.isBukkitLocation(location)){
				dg2remove = dg;
			}
		}
		gens.remove(dg2remove);
		saveDiamondGensForChunk(gens.toArray(new DiamondGenerator[gens.size()]),location.getChunk());
		for(Entity en : location.getWorld().getNearbyEntities(location.getBlock().getBoundingBox())){
			if(en.getType().equals(EntityType.ARMOR_STAND)&&en.getPersistentDataContainer().has(nsk, PersistentDataType.INTEGER)){
				en.remove();
			}
		}
		
		location.getWorld().dropItem(location.add(0.5,0.5,0.5),getItemStack(level));
		if(diamonds>0){
			location.getWorld().dropItem(location.add(0.5,0.5,0.5), new ItemStack(Material.DIAMOND,diamonds));
		}
	}
	
	
	public static void loadDiamondGeneratorFromChunk(Chunk chunk){
		for(DiamondGenerator dg : getDiamondGensInChunk(chunk)){
			generators.add(dg);
		}
	}
	public static void unloadDiamondGeneratorFromChunk(Chunk chunk){
		if(!chunk.getPersistentDataContainer().has(nsk, PersistentDataType.STRING)) return;
		ArrayList<DiamondGenerator> chunkGenerators = getDiamondGensInChunk(chunk);
		for(DiamondGenerator dg :generators){
			for(DiamondGenerator dg2 : chunkGenerators){
				if(dg2.sbLocation!= null && dg.sbLocation.equals(dg2.sbLocation)){
					chunkGenerators.set(chunkGenerators.indexOf(dg2),dg);
				}
			}
		}
		for(DiamondGenerator dg : chunkGenerators){
			generators.remove(dg);
		}
		saveDiamondGensForChunk(chunkGenerators.toArray(new DiamondGenerator[chunkGenerators.size()]),chunk);
	}
	
	public void updateLoadingBarArmorStand(int i){
		Location location = sbLocation.getBukkitLocation();
		for(Entity en : location.getWorld().getNearbyEntities(location.getBlock().getBoundingBox())){
			if(en.getType().equals(EntityType.ARMOR_STAND)&&en.getPersistentDataContainer().has(nsk, PersistentDataType.INTEGER)&&en.getPersistentDataContainer().get(nsk, PersistentDataType.INTEGER)==0){
				en.setCustomName(LoadingBarAscii.getLoadingBar(i, 10));
			}
		}
	}
	
	public void updateLevel(int i){
		this.level = i;
		Location location = sbLocation.getBukkitLocation();
		for(Entity en : location.getWorld().getNearbyEntities(location.getBlock().getBoundingBox())){
			if(en.getType().equals(EntityType.ARMOR_STAND)&&en.getPersistentDataContainer().has(nsk, PersistentDataType.INTEGER)&&en.getPersistentDataContainer().get(nsk, PersistentDataType.INTEGER)==0){
				((ArmorStand) en).getEquipment().setHelmet(getItemStack(i)); 
			}
		}
	}
	
	public static ArrayList<DiamondGenerator> getDiamondGensInChunk( Chunk chunk){
		ArrayList<DiamondGenerator> gens = new ArrayList<DiamondGenerator>();
		String serializedGenerators = "";
		if(chunk.getPersistentDataContainer().has(nsk, PersistentDataType.STRING)){
			serializedGenerators = chunk.getPersistentDataContainer().get(nsk, PersistentDataType.STRING);
			gens = new ArrayList<DiamondGenerator>(Arrays.asList(gson.fromJson(serializedGenerators, DiamondGenerator[].class)));
		}
		return gens;
	}
	
	private static void saveDiamondGensForChunk(DiamondGenerator[] gens, Chunk chunk){
		chunk.getPersistentDataContainer().set(nsk, PersistentDataType.STRING, gson.toJson(gens));
	}
	
	public static void getInformation(Player p){
		p.sendMessage("Generators loaded: "+generators.size(),"\nin this chunk: "+getDiamondGensInChunk(p.getLocation().getChunk()).size());
	}
	/**
	 * Adds the diamonds to all of the generators.
	 */
	public static void addDiamonds(){
		for(DiamondGenerator dg:generators){
			if(dg.level>dg.inventory){
				dg.dgtimer++;
				if(dg.dgtimer%100==0){
					dg.inventory++;
				}
				int progress =(int) ((dg.inventory/(double) dg.level)*100)+((dg.dgtimer%100)/dg.level);
				dg.updateLoadingBarArmorStand(progress);
				if(dg.uiInventory!=null) {
					((DiamondGeneratorUI) dg.uiInventory.getHolder()).setLoadingBar(progress);
				}
			}
		}
		
	}
	public void openInventory(Player player){
		if(uiInventory==null){
			uiInventory = new DiamondGeneratorUI(this).getInventory();
		}
		player.openInventory(uiInventory);
	}
	public static DiamondGenerator getDiamondGeneratorFromLocation(Location location){
		for(DiamondGenerator dg:generators){
			if(dg.sbLocation.isBukkitLocation(location)){
				return dg;
			}
		}
		return null;
	}
	public static NamespacedKey getNSK() {
		return nsk;
	}
	static Random r = new Random();
	public static ItemStack getItemStack(int i) {
		ItemStack is = new ItemStack(Material.STICK,1);
		ItemMeta im = is.getItemMeta();
		im.setCustomModelData(i);
		switch(i){
		case 1:
			im.setDisplayName("§b§lDiamond Generator");
			break;
		case 2:
			im.setDisplayName("§e§lDiamond Generator");
			break;
		case 3:
			im.setDisplayName("§d§lDiamond Generator");
			break;
		default:
			im.setDisplayName("§c§lDiamond Generator");
			break;
		}
		im.setLore(Arrays.asList("§7Level §a"+i+"§7 Diamond Generator","§7Right click on the ground","§7to place a diamond generator"));
		//makes the items unstackable
		im.getPersistentDataContainer().set(nsk, PersistentDataType.BYTE,(byte)r.nextInt(Byte.MAX_VALUE));
		is.setItemMeta(im);
		return is;
	}
}
