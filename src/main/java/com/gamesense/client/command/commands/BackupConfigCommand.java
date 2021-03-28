package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.ZipUtils;
import com.gamesense.client.GameSense;
import com.gamesense.client.command.Command;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Command.Declaration(name = "BackupConfig", syntax = "backupconfig", alias = {"backupconfig"})
public class BackupConfigCommand extends Command {

    @Override
    public void onCommand(String command, String[] message) {
        String filename = "gamesense-cofig-backup-" + GameSense.MODVER + "-" + new SimpleDateFormat("yyyyMMdd.HHmmss.SSS").format(new Date()) + ".zip";
        ZipUtils.zip(new File("GameSense/"), new File(filename));
        MessageBus.sendCommandMessage("Config successfully saved in " + filename + "!", true);
    }
}