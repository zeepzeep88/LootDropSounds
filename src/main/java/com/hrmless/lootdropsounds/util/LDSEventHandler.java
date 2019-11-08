package com.hrmless.lootdropsounds.util;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;

public class LDSEventHandler {

	@SubscribeEvent
	public void onItemPick(EntityItemPickupEvent event)
	{
		//System.out.println("Item picked up!");
		// ModLoader.getMinecraftInstance().thePlayer.addChatMessage("");
		
	}
	
	// Play sound when gems drop from blocks harvested
	@SubscribeEvent
	public void onItemPick(BlockEvent.HarvestDropsEvent event)
	{	
		for (ItemStack drop : event.getDrops())
		{
			//Minecraft.getMinecraft().player.sendMessage(new TextComponentString(drop.getDisplayName()));
			if (Pattern.compile("diamond|emerald").matcher(drop.getDisplayName().toLowerCase()).find()) {
				playSound(event.getWorld(), event.getPos(), "gem_drop", 1.0F);
			}		
		}
	}
	
	// Disable placing of vanilla torches or redstone torches
	@SubscribeEvent
	public void onItemPick(PlayerInteractEvent event)
	{
		ItemStack item = event.getItemStack();
		String itemString = item.toString();
		//event.getEntityPlayer().sendMessage(new TextComponentString(itemString));
		
		if (Pattern.compile("torch|notGate").matcher(itemString).find()) {
			//event.getEntityPlayer().sendMessage(new TextComponentString("Cannot use torches..."));
			if (event.isCancelable()) {
				event.setCanceled(true);
			}
		} else {
			// event allowed
		}
		
	}
	
	HashMap<Integer, String> mobMap = new HashMap<Integer, String>();
	HashMap<Integer, String> tempMobMap = new HashMap<Integer, String>();
	Integer alertLevel = 0;
	Integer agroLevel = 0;
	Integer count = 0;
	Integer lastBeat = 0;
	HashMap<String, SoundEvent> soundEvents = new HashMap<String, SoundEvent>();
	
	@SubscribeEvent
	public void onItemPick(TickEvent.PlayerTickEvent event)
	{
		
		// Check for nearby mobs that are targeting the player. Updates alertLevel.
		if (count % 20 == 0) {
			//event.player.sendMessage(new TextComponentString("alertLevel: " + alertLevel.toString()));
			
			findNearbyMobs(
					event.player
					, Double.valueOf(event.player.getPosition().getX())
					, Double.valueOf(event.player.getPosition().getY())
					, Double.valueOf(event.player.getPosition().getZ()));
		}
		
		//event.player.sendMessage(new TextComponentString(lastBeat.toString() + ", " + count.toString() + ", alertLevel " + alertLevel.toString()));
		
		// Play alert sound or heart beat sound depending on current alert level
		
		if (alertLevel == 0) {
			// kay
		} else if (alertLevel > 2500 && count >= 75) {
			//event.player.sendMessage(new TextComponentString("BEAT6"));
			playSound(event.player.world, event.player.getPosition(), "deep_beat", 0.75F);
			count = 0;
		}  else if (alertLevel > 2000 && count >= 90) {
			//event.player.sendMessage(new TextComponentString("BEAT5"));
			playSound(event.player.world, event.player.getPosition(), "deep_beat", 0.65F);
			count = 0;
		}  else if (alertLevel > 1500 && count >= 105) {
			//event.player.sendMessage(new TextComponentString("BEAT4"));
			playSound(event.player.world, event.player.getPosition(), "deep_beat", 0.55F);
			count = 0;
		}  else if (alertLevel > 1000 && count >= 120) {
			//event.player.sendMessage(new TextComponentString("BEAT3"));
			playSound(event.player.world, event.player.getPosition(), "deep_beat", 0.45F);
			count = 0;
		}  else if (alertLevel > 500 && count >= 135) {
			//event.player.sendMessage(new TextComponentString("BEAT2"));
			playSound(event.player.world, event.player.getPosition(), "deep_beat", 0.35F);
			count = 0;
		}  else if (alertLevel > 0 && count >= 150) {
			//event.player.sendMessage(new TextComponentString("BEAT1"));
			playSound(event.player.world, event.player.getPosition(), "deep_beat", 0.25F);
			count = 0;
		} 
		// Decay alert level each tick
		alertLevel = alertLevel > 0 ? alertLevel - 2 : 0;
		
		count++;
	}

	//Returns a list of nearby mobs in a 80*80*60 (x*z*y) rectangular prism around (x, y, z) coordinates
    public void findNearbyMobs(EntityPlayer player, double x, double y, double z)
    {
        double d0 = 40.0D;
        double d1 = 30.0D;
        List<EntityMob> mobList = player.world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(x - d0, y - d1, z - d0, x + d0, y + d1, z + d0));
        
        //player.sendMessage(new TextComponentString("mobList.size() -> " + mobList.size() + ", mobMap size -> " + mobMap.keySet().size()));

        agroLevel = Integer.valueOf(0);
        for (EntityMob mob : mobList) 
        {
        	try {
        		//player.sendMessage(new TextComponentString("mob.getName(): " + mob.getName()));
        		EntityLivingBase target = mob.getAttackTarget();
        		String targetName = target.getName();
        		//player.sendMessage(new TextComponentString("mob target name: " + targetName));
        		
        		if (targetName == player.getName()) {
        			agroLevel++;
        		}
        	} catch(Exception e) {
        		//player.sendMessage(new TextComponentString("fail1: " + e.toString()));
        	} 		
        	
        }
        
        // Only update alert level UP. Otherwise let it naturally decay as the play "calms down after a fight"
        if (alertLevel == 0 && agroLevel > 0) {
        	playSound(player.world, player.getPosition(), "stress", 0.5F);
        }
        if (alertLevel < agroLevel * 500) {
        	alertLevel = agroLevel * 500;
        	alertLevel = alertLevel > 3000 ? 3000 : alertLevel;
        } 
        
        //mobMap.clear();
        //mobMap.putAll(tempMobMap);
        
      // player.sendMessage(new TextComponentString("mobList.size() -> " + mobList.size() + ", alertLevel -> " + alertLevel.toString()));
        
    }
	
	
	public void playSound(World world, BlockPos pos, String sound, Float volScale)
	{
		if (!soundEvents.containsKey(sound)) {
			
			//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Initializing sound: " + sound));
			
			ResourceLocation location = new ResourceLocation("lootdropsounds", sound);
			SoundEvent sEvent = new SoundEvent(location);
			soundEvents.put(sound, sEvent);
		} else {
			//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("sound exists: " + sound));
		}
		
		world.playSound(
			Minecraft.getMinecraft().player
			, pos
			, soundEvents.get(sound)
			, SoundCategory.PLAYERS
			, 1.0F * volScale
			, 1.2F / (world.rand.nextFloat() * 0.2f + 0.9f));
	}
	
	// Play sound when gems drop from mob deaths
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemPick(LivingDropsEvent event)
	{
		
		Boolean isPlayerKill = false;
		
		try {
			String source = event.getSource().getTrueSource().toString();
			//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("LivingDropsEvent source: " + source));
			isPlayerKill = Pattern.compile("EntityPlayer").matcher(source).find();
			
		} catch(Exception e) {
			// source was probably not a player
			return;
		}
		
		if (!isPlayerKill) {
			return;
		}

		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString(drops.size() + " drops"));
		
		for (EntityItem drop : event.getDrops())
		{
			
			//Minecraft.getMinecraft().player.sendMessage(new TextComponentString(drop.getName()));
			
			if (Pattern.compile("diamond|emerald").matcher(drop.getName()).find()) {
				playSound(event.getEntity().world, drop.getPosition(), "gem_drop", 1.0F);
			}	
			if (Pattern.compile("shard_common").matcher(drop.getName()).find()) {
				playSound(event.getEntity().world, drop.getPosition(), "common_drop", 1.0F);
			}
			if (Pattern.compile("shard_rare").matcher(drop.getName()).find()) {
				playSound(event.getEntity().world, drop.getPosition(), "rare_drop", 1.0F);
			}
			if (Pattern.compile("shard_epic").matcher(drop.getName()).find()) {
				playSound(event.getEntity().world, drop.getPosition(), "epic_drop", 1.0F);
			}
		}
		
	}
	
}
