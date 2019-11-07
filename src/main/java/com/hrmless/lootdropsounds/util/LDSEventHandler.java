package com.hrmless.lootdropsounds.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LDSEventHandler {

	@SubscribeEvent
	public void onItemPick(EntityItemPickupEvent event)
	{
		//System.out.println("Item picked up!");
		// ModLoader.getMinecraftInstance().thePlayer.addChatMessage("");
		
	}
	
	@SubscribeEvent
	public void onItemPick(PlayerInteractEvent event)
	{
		ItemStack item = event.getItemStack();
		String itemString = item.toString();
		//event.getEntityPlayer().sendMessage(new TextComponentString(itemString));
		
		if (Pattern.compile("torch|notGate").matcher(itemString).find()) {
			event.getEntityPlayer().sendMessage(new TextComponentString("Cannot use torches..."));
			if (event.isCancelable()) {
				event.setCanceled(true);
			}
		} else {
			// event allowed
		}
		
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onItemPick(LivingDropsEvent event)
	{
		
		Boolean isPlayerKill = false;
		
		try {
			String source = event.getSource().getTrueSource().toString();
			//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("LivingDropsEvent source: " + source));
		
			String sourcePattern = "EntityPlayer";
			Pattern sourceCheck = Pattern.compile(sourcePattern);
			Matcher matchSource = sourceCheck.matcher(source);
			isPlayerKill = matchSource.find();
			
		} catch(Exception e) {
			return;
			// source was probably not a player
		}
		
		if (!isPlayerKill) {
			return;
		}
		
		//ScalingHealthAPI.getEntityDifficulty(e.entityLiving)
		
		//Minecraft.getMinecraft().player.sendChatMessage("LivingDropsEvent");
		
		//Minecraft.getMinecraft().player.sendChatMessage(event.getSource().toString());
		
		//Minecraft.getMinecraft().player.sendChatMessage(event.getDrops().toString());
		
		//System.out.println("Drops!");
		//System.out.println(event.getSource());
		//System.out.println(event.getDrops());
		
		// if (drop == Items.DIAMOND) world.playSound(...);
		
		String gemPattern = "diamond|emerald|gem";
		Pattern gem = Pattern.compile(gemPattern);
		BlockPos pos = null;
		
		List<EntityItem> drops = event.getDrops();
		
		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString(drops.size() + " drops"));
		
		for (EntityItem drop : drops)
		{
			String dropName = drop.getName();
			//System.out.println(dropName);
			
			//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("A drop! --> " + dropName));
			
			//Minecraft.getMinecraft().player.sendChatMessage(dropName);
			Matcher m = gem.matcher(dropName);
			if (m.find()) {
				pos = drop.getPosition();
				//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Beef!"));
				
				ResourceLocation location = new ResourceLocation("lootdropsounds", "gem_drop");
				SoundEvent sEvent = new SoundEvent(location);
				
				event.getEntity().world.playSound(
						null
						, pos
						, sEvent
						, SoundCategory.BLOCKS
						, 1.0F
						, 1.2F / (event.getEntity().world.rand.nextFloat() * 0.2f + 0.9f));
				
			} else {
				//Minecraft.getMinecraft().player.sendChatMessage("no match, no sound");
				//System.out.println("no match, no sound");
			}
		}
		
	}
	
}
