package br.com.finalcraft.evernifecore.commands.misc;

import br.com.finalcraft.evernifecore.PermissionNodes;
import br.com.finalcraft.evernifecore.commands.finalcmd.annotations.Arg;
import br.com.finalcraft.evernifecore.commands.finalcmd.annotations.FinalCMD;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerData;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;

public class CMDGetUUID {

    @FinalCMD(
            aliases = {"getuuid", "uuidof"},
            permission = PermissionNodes.EVERNIFECORE_COMMAND_UUIDOF
    )
    public void onCommand(CommandSender sender, @Arg(name = "<PlayerUUID>") PlayerData playerData) {
        sender.sendMessage(Message.raw("§a [" + playerData.getPlayerName() + "] --> §e" + playerData.getUniqueId()));
    }

}
