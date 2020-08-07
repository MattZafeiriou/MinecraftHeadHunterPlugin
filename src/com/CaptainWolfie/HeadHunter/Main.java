package com.CaptainWolfie.HeadHunter;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.CaptainWolfie.HeadHunter.Commands.SellHead;
import com.CaptainWolfie.HeadHunter.FileManager.FileManager;
import com.CaptainWolfie.HeadHunter.Listeners.HeadsPlaceEvent;
import com.CaptainWolfie.HeadHunter.Listeners.MobsKillEvents;

import net.milkbowl.vault.economy.Economy;

/*
 * author: Matt Zafeiriou / CaptainWolfie
 * 
 * released on 7/8/2020
 */
public class Main extends JavaPlugin
{

	public static Economy econ = null;

	public static JavaPlugin plugin;

	@Override
	public void onEnable()
	{
		plugin = this;
		this.saveDefaultConfig();

		FileManager.init();

		if( ! setupEconomy() )
		{
			this.getLogger()
					.severe( "This plugin requires Vault and Essentials and couldn\'t be found. Disabling plugin.." );
			Bukkit.getPluginManager().disablePlugin( this );
			return;
		}

		getServer().getPluginManager().registerEvents( new MobsKillEvents(), this );

		if( ! getConfig().getBoolean( "place-heads" ) )
			getServer().getPluginManager().registerEvents( new HeadsPlaceEvent(), this );

		this.getCommand( "sellhead" ).setExecutor( new SellHead() );

		Logger log = this.getLogger();
		log.info( "HeadHunter Enabled!" );
	}

	public static void registerEvent( Listener l )
	{
		plugin.getServer().getPluginManager().registerEvents( l, plugin );
	}

	public static void unregisterEvent( Listener l )
	{
		HandlerList.unregisterAll( l );
	}

	private boolean setupEconomy()
	{
		if( Bukkit.getPluginManager().getPlugin( "Vault" ) == null )
		{
			return false;
		}

		RegisteredServiceProvider< Economy > rsp = getServer().getServicesManager().getRegistration( Economy.class );
		if( rsp == null )
		{
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

}
