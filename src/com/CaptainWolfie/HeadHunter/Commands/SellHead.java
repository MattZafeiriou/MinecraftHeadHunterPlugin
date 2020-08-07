package com.CaptainWolfie.HeadHunter.Commands;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.CaptainWolfie.HeadHunter.Main;
import com.CaptainWolfie.HeadHunter.Inventory.InventoryLoader;

public class SellHead implements CommandExecutor
{

	@Override
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args )
	{
		if( sender instanceof Player )
		{

			Player p = (Player) sender;
			new InventoryLoader( p ).openInventory();

		} else
		{
			System.out.println( "Only players can execute this command!" );
		}

		return true;
	}

	public static String getMob( ItemStack item )
	{
		if( item.getType() == Material.SKULL_ITEM )
		{
			// item name
			String dName = item.getItemMeta().getDisplayName().replaceAll( "§", "&" );
			String removeStrings[] = Main.plugin.getConfig().getString( "heads-name" ).split( "%name%" );

			for( String rmvString : removeStrings )
			{
				if( ! dName.contains( rmvString ) )
					return null;

				dName = dName.replaceFirst( rmvString, "" );
			}

			String[] mobNames =
			{ "Chicken", "Pig", "Cow", "Sheep", "Skeleton", "Zombie", "Creeper", "Spider", "Slime", "Ghast",
					"Zombiepigman", "Enderman", "Cavespider", "Silverfish", "Blaze", "Magmacube", "Bat", "Witch",
					"Endermite", "Guardian", "Squid", "Wolf", "Mooshroom", "Ocelot", "Horse", "Donkey", "Rabbit",
					"Villager", "Wither", "Enderdragon", "Snowgolem", "Irongolem" };

			if( dName != null )
			{
				String mob = "";

				// check name
				for( String name : mobNames )
				{
					String mobName = Main.plugin.getConfig().getString( "heads." + name + ".name" );
					if( mobName.equals( dName ) )
					{
						mob = name;
						return mob;
					}
				}

				if( mob.equals( "" ) )
				{
					// found nothing, check for human
					String human = Main.plugin.getConfig().getString( "heads.Human.name" );
					String[] removedName = human.split( "%player%" );

					for( String rmvString : removedName )
					{
						if( ! dName.contains( rmvString ) )
							return null;

						dName.replaceFirst( rmvString, "" );
					}
					mob = "Human";
					return mob;
				}
			}
		}
		return null;
	}

	public static double getCost( String mob )
	{
		return Main.plugin.getConfig().getDouble( "heads." + mob + ".price" );
	}

	public static double getCost( ItemStack item )
	{
		return Main.plugin.getConfig().getDouble( "heads." + getMob( item ) + ".price" );
	}

	public static boolean isHead( ItemStack item )
	{
		if( item.getType() == Material.SKULL_ITEM )
		{

			String mob = getMob( item );

			if( mob == null )
				return false;

			double cost = getCost( mob );

			// check lore
			String lore = Main.plugin.getConfig().getString( "heads-lore" ).replaceAll( "&", "§" );

			lore = lore.replaceAll( "%cost%", cost + "" );

			List< String > dLore = item.getItemMeta().getLore(); // item lore
			String fLore = ""; // final lore
			for( String s : dLore )
			{
				fLore += s;
			}

			if( fLore.equals( lore.replaceAll( "\n", "" ) ) )
				return true;
		}
		return false;
	}

}
