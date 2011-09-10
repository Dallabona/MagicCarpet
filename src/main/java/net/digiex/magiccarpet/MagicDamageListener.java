package net.digiex.magiccarpet;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.EventExecutor;

/*
* Magic Carpet 2.0
* Copyright (C) 2011 Celtic Minstrel
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

public class MagicDamageListener implements Listener {
	public EventExecutor executor = new EventExecutor() {
		@Override@SuppressWarnings("incomplete-switch")
		public void execute(Listener listener, Event event) {
			switch(event.getType()) {
			case BLOCK_BREAK:
				((MagicDamageListener)listener).onBlockBreak((BlockBreakEvent)event);
				break;
			case ENTITY_DAMAGE:
				((MagicDamageListener)listener).onEntityDamage((EntityDamageEvent)event);
				break;
			case BLOCK_PHYSICS:
				((MagicDamageListener)listener).onBlockPhysics((BlockPhysicsEvent)event);
				break;
			}
		}
	};
	private MagicCarpet plugin;
	
	public MagicDamageListener(MagicCarpet plug) {
		plugin = plug;
	}
	
	//When a player joins the game, if they had a carpet when the logged out it puts it back.
	public void onBlockBreak(BlockBreakEvent event) {
		for(Carpet carpet : plugin.carpets.all()){
			if(carpet == null || !carpet.isVisible()) continue;
			if(carpet.isCovering(event.getBlock())) event.setCancelled(true);
		}
	}
	
	// Prevent carpets from suffocating players (and mobs too!)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getCause() == DamageCause.SUFFOCATION) {
			if(!(event.getEntity() instanceof LivingEntity)) return;
			Block eyes = ((LivingEntity)event.getEntity()).getEyeLocation().getBlock();
			Block block = event.getEntity().getLocation().getBlock();
			for(Carpet carpet : plugin.carpets.all()){
				if(carpet == null || !carpet.isVisible()) continue;
				if(carpet.touches(eyes)) {
					event.setCancelled(true);
					return;
				} else if(carpet.touches(block)) {
					event.setCancelled(true);
					return;
				}
			}
		} else if(event.getCause() == DamageCause.FALL) {
			if(!(event.getEntity() instanceof Player)) return;
			if(plugin.carpets.has((Player)event.getEntity())) event.setCancelled(true);
			System.out.println("Cancelled fall damage? " + event.isCancelled());
		}
	}
	
	// Prevent carpets from affecting things they pass such as floating sand
	public void onBlockPhysics(BlockPhysicsEvent event) {
		//System.out.println("Block physics: " + event.getBlock().getType() + "; changed " + event.getChangedType());
		for(Carpet carpet : plugin.carpets.all()){
			if(carpet == null || !carpet.isVisible()) continue;
			if(carpet.touches(event.getBlock())) {
				event.setCancelled(true);
				return;
			}
//			if(!carpet.touches(event.getBlock())) continue;
//			for(BlockFace face : BlockFace.values()) {
//				if(face.getModX() > 1 || face.getModX() < -1 || face.getModY() > 1 || face.getModY() < -1) continue;
//				Block block = event.getBlock().getRelative(face);
//				if(carpet.isCovering(block)) {
//					event.setCancelled(true);
//					return;
//				}
//			}
		}
	}
}