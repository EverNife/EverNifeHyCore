package br.com.finalcraft.evernifecore.commands.misc;

import br.com.finalcraft.evernifecore.EverNifeCore;
import br.com.finalcraft.evernifecore.commands.finalcmd.annotations.FinalCMD;
import br.com.finalcraft.evernifecore.version.FCJavaVersion;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;

public class CMDSvInfo {

    @FinalCMD(
            aliases = {"serverinfo","svinfo"}
    )
    public void onCommand(CommandSender sender) {
        sender.sendMessage(Message.raw("§a-------- SV_INFO --------"));
        sender.sendMessage(Message.raw(""));
        sender.sendMessage(Message.raw(" §a - JavaVersion: §e" + FCJavaVersion.getCurrent().getName()));
        sender.sendMessage(Message.raw(" §a - EverNifeCore: " + EverNifeCore.instance.getManifest().getVersion()));
        sender.sendMessage(Message.raw(""));
        sender.sendMessage(Message.raw("§a-------- SV_INFO --------"));
    }
}
