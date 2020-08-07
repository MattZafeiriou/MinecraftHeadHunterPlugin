package com.CaptainWolfie.HeadHunter.Listeners;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.CaptainWolfie.HeadHunter.Main;

public class MobsKillEvents implements Listener
{

	private ItemStack createItem( final String name, final String skullName, final String... lore )
	{
		Material material = Material.SKULL_ITEM;
		byte data = (byte) 3;
		final ItemStack item = new ItemStack( material, 1, data );
		final ItemMeta meta = item.getItemMeta();
		if( material.equals( Material.SKULL_ITEM ) )
		{

			SkullMeta skullMeta = (SkullMeta) item.getItemMeta();

			skullMeta.setOwner( skullName );
			skullMeta.setDisplayName( name );

			skullMeta.setLore( Arrays.asList( lore ) );
			item.setItemMeta( skullMeta );
		} else
		{
			// Set the name of the item
			meta.setDisplayName( name );

			// Set the lore of the item
			meta.setLore( Arrays.asList( lore ) );

			item.setItemMeta( meta );
		}

		return item;
	}

	@EventHandler
	public void onMobKill( EntityDeathEvent e )
	{
		Player p = e.getEntity().getKiller();
		if( p != null )
		{
			String eName = e.getEntityType().name();
			if( eName.equalsIgnoreCase( "SNOWMAN" ) )
			{
				eName = "Snowgolem";
			}
			String humanName = null;

			// replace all spaces and to lower case
			eName = eName.replaceAll( " ", "" ).replaceAll( "_", "" ).toLowerCase();

			// take first char and to upper
			char first = Character.toUpperCase( eName.charAt( 0 ) );
			// merge strings
			eName = first + eName.substring( 1 );

			if( e.getEntity() instanceof Player )
			{
				Player ded = (Player) e.getEntity();
				eName = "Human";
				humanName = ded.getName();
			}

			double cost = Main.plugin.getConfig().getDouble( "heads." + eName + ".price" );

			// name of mob
			String name = Main.plugin.getConfig().getString( "heads." + eName + ".name" );
			// name of the head
			String skullname = Main.plugin.getConfig().getString( "heads." + eName + ".headname" );

			if( humanName != null )
			{
				name = name.replaceAll( "%player%", humanName );
				skullname = skullname.replaceAll( "%player%", humanName );

			}
			// add the name of the skull
			String finalname = Main.plugin.getConfig().getString( "heads-name" ).replaceAll( "%name%", name )
					.replaceAll( "&", "§" );

			// lore
			String lore = Main.plugin.getConfig().getString( "heads-lore" ).replaceAll( "&", "§" ).replaceAll( "%cost%",
					cost + "" );

			ItemStack item = createItem( finalname, skullname, lore.split( "\n" ) );
			e.getEntity().getWorld().dropItemNaturally( e.getEntity().getLocation(), item );
		}
	}

}
