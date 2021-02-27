package com.gamesense.client.module.modules.misc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.ZipUtils;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Module;

public class BackupConfig extends Module {

	public BackupConfig() {
		super("BackupConfig",Category.Misc);
	}

	@Override
	public void onEnable() {
		String filename="gamesense-cofig-backup-"+GameSense.MODVER+"-"+new SimpleDateFormat("yyyyMMdd.HHmmss.SSS").format(new Date())+".zip";
		ZipUtils.zip(new File("GameSense/"),new File(filename));
		MessageBus.sendClientPrefixMessage("Config successfully saved in "+filename+"!");
		disable();
	}
}
