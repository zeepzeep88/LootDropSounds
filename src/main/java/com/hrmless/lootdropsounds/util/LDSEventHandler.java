package com.hrmless.lootdropsounds.util;

import java.util.regex.Pattern;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
				playSound(event.getWorld(), event.getPos(), "gem_drop");
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
	
	public void playSound(World world, BlockPos pos, String sound)
	{
		//Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Playing sound: " + sound));
		ResourceLocation location = new ResourceLocation("lootdropsounds", sound);
		SoundEvent sEvent = new SoundEvent(location);
		
		world.playSound(
				null
				, pos
				, sEvent
				, SoundCategory.BLOCKS
				, 1.0F
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
				playSound(event.getEntity().world, drop.getPosition(), "gem_drop");
			}	
			if (Pattern.compile("shard_common").matcher(drop.getName()).find()) {
				playSound(event.getEntity().world, drop.getPosition(), "common_drop");
			}
			if (Pattern.compile("shard_rare").matcher(drop.getName()).find()) {
				playSound(event.getEntity().world, drop.getPosition(), "rare_drop");
			}
			if (Pattern.compile("shard_epic").matcher(drop.getName()).find()) {
				playSound(event.getEntity().world, drop.getPosition(), "epic_drop");
			}
		}
		
	}
	
}
