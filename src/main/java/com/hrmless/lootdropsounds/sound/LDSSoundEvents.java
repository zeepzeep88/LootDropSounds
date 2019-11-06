package com.hrmless.lootdropsounds.sound;

import java.util.ArrayList;
import java.util.List;

import com.hrmless.lootdropsounds.util.Reference;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@ObjectHolder(Reference.MOD_ID)
public class LDSSoundEvents
{	
	public static final SoundEvent DROP;
	
	protected static final List<SoundEvent> SOUNDS = new ArrayList<SoundEvent>();
	
	static
	{
		DROP = registerSound("drop");
	}
	
	private static SoundEvent registerSound(ResourceLocation location)
	{
		SoundEvent sound = new SoundEvent(location).setRegistryName(location);
		SOUNDS.add(sound);
		return sound;
	}
	
	private static SoundEvent registerSound(String location)
	{
		return registerSound(new ResourceLocation(Reference.MOD_ID, location));
	}
	
	@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
	public static class SoundRegistry
	{
		@SubscribeEvent
		public static void register(final RegistryEvent.Register<SoundEvent> event)
		{
			final IForgeRegistry<SoundEvent> registry = event.getRegistry();
			
			registry.registerAll(SOUNDS.toArray( new SoundEvent[] {  } ));
		}
	}

}
