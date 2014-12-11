package com.r3pwn.configurator;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import android.content.*;
import android.preference.*;
import android.net.*;
import android.animation.*;
import android.database.sqlite.*;
import eu.chainfire.libsuperuser.*;
import java.sql.*;


public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		/**
		Flags:
		Google Play Music
		---------------------------
		 music_download_passthrough_cookies
		 music_supported_audio
		 music_chunk_size_bites
		 music_throttle_download
		 music_throttle_download_kbps
		 music_throttle_download_kb
		 music_enable_storage_migrations
		 music_short_term_cache_min_free_space
		 music_long_term_cache_min_free_space
		 music_download_max_blacklist_size
		 music_download_blacklist_ttl_sec
		 music_local_http_stuffing
		 music_shop_url
		 music_enable_tracks_upsync_deletion
		 music_action_bar_height
		 music_notification_bar_height
		 music_device_enable_active_check
		 music_device_type
		 music_smartphone_min_screen_size_in_tenth_of_inch
		 music_smartphone_max_screen_size_in_tenth_of_inch
		 music_use_star_ratings
		 music_overdraw_listview
		 music_using_new_download_ui
		 music_temp_nautilus_status_exp_time_in_seconds
		 music_enable_sync_notifications
		 music_enable_autocache
		 music_autocache_opt_in_default_nautilus
		 music_autocache_opt_in_default
		 music_enable_find_video
		 music_enable_cast_remote_playback
		 music_nautilus_offline_status_ttl_days
		 music_show_radio_animation
		 music_min_version_upgrade_sync
		 music_downstream_page_size
		 music_upstream_page_size
		 music_sync_radio
		 music_sync_config
		 music_merge_block_size
		 music_enable_track_stats_upsync
		 music_periodic_sync_frequency_in_seconds
		 music_max_ephemeral_top_size
		 music_sync_invalid_account
		 music_use_downstream_posts
		 music_sync_server_options
		 music_enable_playlist_radio
		 music_enable_keepon_radio
		 music_enable_secondary_sdcards
		 music_debug_logs_enabled
		 music_enable_shared_playlists
		 music_gapless_enabled
		 music_gapless_enabled_pinned
		 music_enable_chromecast_wake_lock
		 music_cast_app_name
		 musoc_version_key
		 music_keepon_radio_always_use_downloaded_songs
		 
		Google Play Movies
		---------------------------
		 videos:dogfood_enabled
		*/
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		final boolean nevertellme = preferences.getBoolean("nevertellme", false);
        final SharedPreferences.Editor prefs_edit = preferences.edit();
		final Button configure = (Button)findViewById(R.id.mainButton1);
		final EditText name = (EditText)findViewById(R.id.flagNameET);
		final EditText value = (EditText)findViewById(R.id.flagValueET);
		final Button killbutton = (Button)findViewById(R.id.mainButton2);
		final EditText killet = (EditText)findViewById(R.id.processKillET);
	
		if (!Shell.SU.available())
		{
			final AlertDialog sualertDialog = new AlertDialog.Builder(MainActivity.this).create();
			sualertDialog.setTitle("You aren't rooted");
			sualertDialog.setMessage("It looks like you aren't rooted. Most features will not work. There is nothing I can do to help you.");
			sualertDialog.setButton("Alright, thanks anyways.", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					sualertDialog.dismiss();
				}
			});
			sualertDialog.show();
		}
		configure.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v) 
			{
				if (!value.getText().toString().matches("") || name.getText().toString().matches(""))
				{
					try 
					{
						String flagName = name.getText().toString();
						String flagValue = value.getText().toString();
						Shell.SU.run("cp /data/data/com.google.android.gsf/databases/gservices.db /data/data/com.r3pwn.configurator/databases/gservices.db\n");
						SQLiteDatabase db=openOrCreateDatabase("gservices.db", Context.MODE_WORLD_READABLE, null);
						db.execSQL("INSERT INTO overrides (name, value) VALUES ('" + flagName + "', '" + flagValue + "');");
						db.execSQL("UPDATE overrides SET value='" + flagValue + "' WHERE name='" + flagName + "';");
						Shell.SU.run("cp /data/data/com.r3pwn.configurator/databases/gservices.db /data/data/com.google.android.gsf/databases/gservices.db\n");
					}
					catch(android.database.SQLException sqle)
					{
						String flagName = name.getText().toString();
						String flagValue = value.getText().toString();
						Shell.SU.run("cp /data/data/com.google.android.gsf/databases/gservices.db /data/data/com.r3pwn.configurator/databases/gservices.db\n");
						SQLiteDatabase db=openOrCreateDatabase("gservices.db", Context.MODE_WORLD_READABLE, null);
						db.execSQL("UPDATE overrides SET value='" + flagValue + "' WHERE name='" + flagName + "';");
						Shell.SU.run("cp /data/data/com.r3pwn.configurator/databases/gservices.db /data/data/com.google.android.gsf/databases/gservices.db\n");
					}
				}
				if (name.getText().toString().matches(""))
				{
					final AlertDialog sualertDialog = new AlertDialog.Builder(MainActivity.this).create();
					sualertDialog.setTitle("You need to enter a flag name");
					sualertDialog.setMessage("You forgot to enter a flag name, go ahead and to that before proceeding.");
					sualertDialog.setButton("Oh, oops.", new DialogInterface.OnClickListener() 
					{
							public void onClick(DialogInterface dialog, int which) 
							{
								sualertDialog.dismiss();
							}
						});
						sualertDialog.show();
					}
					if (value.getText().toString().matches(""))
					{
						final AlertDialog sualertDialog = new AlertDialog.Builder(MainActivity.this).create();
						sualertDialog.setTitle("You need to enter a flag value");
						sualertDialog.setMessage("You forgot to enter a flag value, go ahead and to that before proceeding.");
						sualertDialog.setButton("Oh, oops.", new DialogInterface.OnClickListener()
						{
								public void onClick(DialogInterface dialog, int which) 
								{
									sualertDialog.dismiss();
								}
							});
						sualertDialog.show();
					}
				}
			});
		killbutton.setOnClickListener(new View.OnClickListener() 
			{
				public void onClick(View v) 
				{
					String process;
					if (killet.getText().toString().matches(""))
					{
						final AlertDialog sualertDialog = new AlertDialog.Builder(MainActivity.this).create();
						sualertDialog.setTitle("You need to enter a process name");
						sualertDialog.setMessage("You forgot to enter a process name. It's okay, this step is optional.");
						sualertDialog.setButton("Oh, okay.", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) 
								{
									sualertDialog.dismiss();
								}
							});
						sualertDialog.show();
					}
					if (killet.getText().toString().matches("") == false)
					{
						try {
							process = killet.getText().toString();
							java.lang.Process su = Runtime.getRuntime().exec("su");
							DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
							outputStream.writeBytes("am force-stop com.google.android.gsf\n");
							outputStream.writeBytes("am force-stop com.google.android.gms\n");
							outputStream.writeBytes("am force-stop " + process + "\n");
							outputStream.writeBytes("exit\n");
							outputStream.flush();
							su.waitFor();
							if (Build.VERSION.SDK_INT >= 21)
							{
								final AlertDialog sualertDialog = new AlertDialog.Builder(MainActivity.this).create();
								sualertDialog.setTitle("You are running Lollipop or higher");
								sualertDialog.setMessage("This feature may not work. Go check. If it didn't work, then a reboot may be required before changes take effect.");
								sualertDialog.setButton("Oh, okay.", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) 
										{
											sualertDialog.dismiss();
										}
									});
								if (nevertellme == false)
								{
									sualertDialog.setButton2("Never tell me this again.", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) 
										{
											prefs_edit.putBoolean("nevertelleme", true);
											prefs_edit.commit();
										}
									});
									sualertDialog.show();
								}
							}
						}
						catch(IOException e)
						{
							// We do this to keep the compiler happy.
						}
						catch (InterruptedException e)
						{
							// We do this to keep the compiler happy
						}
					}
				}
			});
    }
}
