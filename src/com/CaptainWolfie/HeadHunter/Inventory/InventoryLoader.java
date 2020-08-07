package com.CaptainWolfie.HeadHunter.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.CaptainWolfie.HeadHunter.Main;
import com.CaptainWolfie.HeadHunter.Commands.SellHead;
import com.CaptainWolfie.HeadHunter.FileManager.FileManager;

public class InventoryLoader implements Listener
{
	private Inventory inv = null;

	private double multiplier = 1;

	private Player p;

	private boolean sold = false;

	public InventoryLoader( Player p )
	{
		this.p = p;

		Main.registerEvent( this );

		findMultiplier( p );

		inv = Bukkit.createInventory( null, 5 * 9,
				Main.plugin.getConfig().getString( "inventory-name" ).replaceAll( "&", "§" ) );

		initializeItems();
	}

	private void findMultiplier( Player p )
	{
		Set< String > fkeys = FileManager.multiplierConfig.getConfigurationSection( "" ).getKeys( false );

		List< String > keys = new ArrayList<>( fkeys );

		double max = 1;
		for( String key : keys )
		{
			String permission = FileManager.multiplierConfig.getString( key + ".Permission" );
			double current = FileManager.multiplierConfig.getDouble( key + ".Multiplier" );

			if( p.hasPermission( permission ) )
				if( current > max )
					max = current;
		}
		multiplier = max;
	}

	// You can call this whenever you want to put the items in
	public void initializeItems()
	{

		String cancel = Main.plugin.getConfig().getString( "cancel-button" ).replaceAll( "&", "§" );

		for( int i = 0; i < 4; i++ )
		{
			inv.setItem( 36 + i, createGuiItem( Material.STAINED_GLASS_PANE, cancel, (byte) 14, "CaptainWolfieGR" ) );

		}

		String info = Main.plugin.getConfig().getString( "info-button" ).replaceAll( "&", "§" );

		inv.setItem( 40, createGuiItem( Material.STAINED_GLASS_PANE, info, (byte) 7, "CaptainWolfieGR" ) );

		createAcceptButton();
	}

	private void createAcceptButton()
	{
		double cost = 0;
		int amount = 0;
		for( int i = 0; i < inv.getSize() - 9; i++ )
		{
			if( inv.getItem( i ) != null )
			{
				ItemStack item = inv.getItem( i );
				if( SellHead.isHead( item ) )
				{
					amount += item.getAmount();
					cost += SellHead.getCost( item ) * item.getAmount() * multiplier;
				}
			}
		}

		String accept = Main.plugin.getConfig().getString( "accept-button" ).replaceAll( "&", "§" )
				.replaceAll( "%amount%", amount + "" ).replaceAll( "%cost%", cost + "" );

		for( int i = 5; i < 9; i++ )
		{
			inv.setItem( 36 + i, createGuiItem( Material.STAINED_GLASS_PANE, accept, (byte) 5, "CaptainWolfieGR" ) );
		}
	}

	private void cancel( boolean close )
	{
		List< ItemStack > items = new ArrayList< ItemStack >();

		// add player heads to list
		for( int i = 0; i < inv.getSize() - 9; i++ )
		{
			if( inv.getItem( i ) != null )
			{
				items.add( inv.getItem( i ) );
				inv.setItem( i, null );
			}
		}

		// add list heads to player's inventory
		for( int i = 0; i < 36; i++ )
		{
			if( items.size() != 0 )
			{
				if( p.getInventory().getItem( i ) == null )
				{
					p.getInventory().setItem( i, items.get( 0 ) );
					items.remove( 0 );
				}
			} else
				break;
		}

		// if everything is not in players inventory drop em
		if( items.size() != 0 )
		{
			for( ItemStack item : items )
			{
				p.getWorld().dropItemNaturally( p.getLocation(), item );
			}
			items = null;
		}

		if( close )
			p.closeInventory();

	}

	@SuppressWarnings( "deprecation" )
	private void accept()
	{
		List< ItemStack > items = new ArrayList< ItemStack >();

		// measure the cost and the amount
		double cost = 0;
		int amount = 0;
		// check if everything is heads
		for( int i = 0; i < inv.getSize() - 9; i++ )
		{
			if( inv.getItem( i ) != null )
			{
				ItemStack item = inv.getItem( i );
				if( ! SellHead.isHead( item ) )
				{
					items.add( item );
				} else
				{
					amount += item.getAmount();
					cost += SellHead.getCost( item ) * item.getAmount();
				}
			}
		}

		cost *= multiplier;

		if( amount == 0 )
			return;

		// add list heads to player's inventory
		for( int i = 0; i < 36; i++ )
		{
			if( items.size() != 0 )
			{
				if( p.getInventory().getItem( i ) == null )
				{
					p.getInventory().setItem( i, items.get( 0 ) );
					items.remove( 0 );
				}
			} else
				break;
		}

		// if everything is not in players inventory drop em
		if( items.size() != 0 )
		{
			for( ItemStack item : items )
			{
				p.getWorld().dropItemNaturally( p.getLocation(), item );
			}
			items = null;
		}

		Main.econ.depositPlayer( p, cost );
		p.sendMessage( Main.plugin.getConfig().getString( "heads-sold" ).replaceAll( "&", "§" )
				.replaceAll( "%cost%", cost + "" ).replaceAll( "%amount%", amount + "" ) );

		sold = true;
		// sound
		p.playSound( p.getLocation(), Sound.valueOf( Main.plugin.getConfig().getString( "sound-on-sell" ) ), 1, 1 );

		// screen title
		p.sendTitle( Main.plugin.getConfig().getString( "heads-sold" ).replaceAll( "&", "§" )
				.replaceAll( "%cost%", cost + "" ).replaceAll( "%amount%", amount + "" ), "" );

		p.closeInventory();
	}

	// Nice little method to create a gui item with a custom name, and description
	protected ItemStack createGuiItem( final Material material, final String name, byte data, final String skullName,
			final String... lore )
	{
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

	// You can open the inventory with this
	public void openInventory()
	{
		p.openInventory( inv );
	}

	@EventHandler
	public void onInventoryClick( final InventoryClickEvent e )
	{
		if( ! e.getInventory().equals( inv ) )
			return;

		final ItemStack clickedItem = e.getCurrentItem();

		// verify current item is not null
		if( clickedItem == null || clickedItem.getType() == Material.AIR )
			return;

		if( e.getRawSlot() >= 36 && e.getRawSlot() < inv.getSize() )
		{
			e.setCancelled( true );

			if( e.getRawSlot() < 40 )
				cancel( true );

			if( e.getRawSlot() > 41 )
				accept();
		} else
			// update things after awhile
			new BukkitRunnable()
			{
				public void run()
				{
					createAcceptButton();
				}
			}.runTaskLater( Main.plugin, 1 );
	}

	@EventHandler
	public void onInventoryClick( final InventoryDragEvent e )
	{
		if( e.getInventory().equals( inv ) )
		{
			e.setCancelled( true );
		}
	}

	@EventHandler
	public void onClose( final InventoryCloseEvent e )
	{
		if( e.getInventory().equals( inv ) )
		{
			if( ! sold )
				cancel( false );
			// optimize the server
			Main.unregisterEvent( this );
			inv = null;
			p = null;
			Runtime.getRuntime().gc();
		}
	}
}