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
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
			configure.setEnabled(false);
			killbutton.setEnabled(false);
			name.setEnabled(false);
			value.setEnabled(false);
			killet.setEnabled(false);
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
						// Look at me. I'm the captain now.
						Shell.SU.run("cp /data/data/com.google.android.gsf/databases/gservices.db /data/data/com.r3pwn.configurator/databases/gservices.db\n");
						SQLiteDatabase db=openOrCreateDatabase("gservices.db", Context.MODE_WORLD_READABLE, null);
						// All your overrides are belong to me.
						db.execSQL("INSERT INTO overrides (name, value) VALUES ('" + flagName + "', '" + flagValue + "');");
						db.execSQL("UPDATE overrides SET value='" + flagValue + "' WHERE name='" + flagName + "';");
						// Just kidding. You can have it back now.
						Shell.SU.run("cp /data/data/com.r3pwn.configurator/databases/gservices.db /data/data/com.google.android.gsf/databases/gservices.db\n");
						Shell.SU.run("rm -f /data/data/com.r3pwn.configurator/databases/gservices.db\n");
						Toast.makeText(getApplicationContext(), "Done. Please reboot.", Toast.LENGTH_LONG).show();
					}
					catch(android.database.SQLException sqle)
					{
						String flagName = name.getText().toString();
						String flagValue = value.getText().toString();
						Shell.SU.run("cp /data/data/com.google.android.gsf/databases/gservices.db /data/data/com.r3pwn.configurator/databases/gservices.db\n");
						SQLiteDatabase db=openOrCreateDatabase("gservices.db", Context.MODE_WORLD_READABLE, null);
						db.execSQL("UPDATE overrides SET value='" + flagValue + "' WHERE name='" + flagName + "';");
						Shell.SU.run("cp /data/data/com.r3pwn.configurator/databases/gservices.db /data/data/com.google.android.gsf/databases/gservices.db\n");
						Shell.SU.run("rm -f /data/data/com.r3pwn.configurator/databases/gservices.db\n");
						Toast.makeText(getApplicationContext(), "Done. Please reboot.", Toast.LENGTH_LONG).show();
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
						// User is not very smart or clicked the button by accident.
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
							// This spawns a system shell, which has access to kill processes. Neat, right?
							// Still not sure why the superuser can't kill processes on Lollipop. Not so super, now, are you, user?
							java.lang.Process su = Runtime.getRuntime().exec("su - system");
							DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
							// Kill all the things.
							outputStream.writeBytes("am force-stop com.google.android.gsf\n");
							outputStream.writeBytes("am force-stop com.google.android.gms\n");
							outputStream.writeBytes("am force-stop " + process + "\n");
							outputStream.writeBytes("exit\n");
							outputStream.flush();
							su.waitFor();
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
