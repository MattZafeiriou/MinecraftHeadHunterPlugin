package com.CaptainWolfie.HeadHunter.FileManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.CaptainWolfie.HeadHunter.Main;

public class FileManager
{

	public static FileConfiguration multiplierConfig;
	public static File multiplierFile;

	public static void init()
	{
		createConfigs();
	}

	private static void createConfigs()
	{
		multiplierFile = createFile( "Multipliers", "OP:\n    Permission: \"hecate.op\"\n    Multiplier: 1.5" );
		multiplierConfig = createConfig( multiplierFile );
	}

	private static FileConfiguration createConfig( File file )
	{
		return YamlConfiguration.loadConfiguration( file );
	}

	private static File createFile( String name, String data )
	{
		File newFile = new File( Main.plugin.getDataFolder(), name + ".yml" );
		if( ! newFile.exists() )
		{
			newFile.getParentFile().mkdirs();
			try
			{
				newFile.createNewFile();
				writeToFile( newFile, data );
			} catch( IOException e )
			{
				e.printStackTrace();
			}
		}
		return newFile;
	}

	private static void writeToFile( File file, String data )
	{
		try
		{
			FileWriter myWriter = new FileWriter( file );
			myWriter.write( data );
			myWriter.close();
		} catch( IOException e )
		{
			System.out.println( "An error occurred." );
			e.printStackTrace();
		}
	}

}
