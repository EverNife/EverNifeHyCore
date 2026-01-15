package br.com.finalcraft.evernifecore.commands.misc;

import br.com.finalcraft.evernifecore.PermissionNodes;
import br.com.finalcraft.evernifecore.commands.finalcmd.annotations.Arg;
import br.com.finalcraft.evernifecore.commands.finalcmd.annotations.FinalCMD;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerController;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerData;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;

import java.util.UUID;

public class CMDGetName {

    @FinalCMD(
            aliases = {"getname", "nameof"},
            permission = PermissionNodes.EVERNIFECORE_COMMAND_NAMEOF
    )
    public void onCommand(CommandSender sender, @Arg(name = "<PlayerUUID>") UUID playerUUID) {
        PlayerData playerData = PlayerController.getPlayerData(playerUUID);
        sender.sendMessage(Message.raw("§a [" + playerData.getPlayerName() + "] --> §e" + playerData.getUniqueId()));
    }

}
