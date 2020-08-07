package com.CaptainWolfie.HeadHunter.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import com.CaptainWolfie.HeadHunter.Main;
import com.CaptainWolfie.HeadHunter.Commands.SellHead;

public class HeadsPlaceEvent implements Listener
{

	@EventHandler
	public void blockPlaceEvent( BlockPlaceEvent e )
	{
		if( SellHead.isHead( e.getItemInHand() ) )
		{
			e.setCancelled( true );
			e.getPlayer().sendMessage( Main.plugin.getConfig().getString( "cant-place-msg" ).replaceAll( "&", "§" ) );
		}
	}

}
