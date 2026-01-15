package br.com.finalcraft.evernifecore.util;

import br.com.finalcraft.evernifecore.locale.FCLocale;
import br.com.finalcraft.evernifecore.locale.LocaleMessage;
import br.com.finalcraft.evernifecore.locale.LocaleType;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import br.com.finalcraft.evernifecore.api.common.commandsender.FCommandSender;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;

public class FCHytaleUtil {

    @FCLocale(lang = LocaleType.EN_US, text = "§4§l ▶ §cOnly players can use this command!.")
    @FCLocale(lang = LocaleType.PT_BR, text = "§4§l ▶ §cApenas jogadores podem usar esse comando!.")
    private static LocaleMessage ONLY_PLAYERS_CAN_USE_THIS_COMMAND;

    /**
     * If the sender is not a player, send the sender the message
     * "ONLY_PLAYERS_CAN_USE_THIS_COMMAND" and return true, otherwise
     * return false
     *
     * @param sender The CommandSender.
     * @return if the sender is a player.
     */
    public static boolean isNotPlayer(FCommandSender sender) {
        if (!(sender instanceof Player)) {
            ONLY_PLAYERS_CAN_USE_THIS_COMMAND
                    .send(sender);
            return true;
        }
        return false;
    }

    /**
     * If the player does not have the permission, send them a message and return false. Otherwise, return true
     *
     * @param player The player who is trying to execute the command.
     * @param permission The permission you want to check.
     * @return A boolean value.
     */
    public static boolean hasThePermission(FCommandSender player, String permission) {

        if (!player.hasPermission(permission)) {
            FCMessageUtil.needsThePermission(player, permission);
            return false;
        }
        return true;
    }

    /**
     * Força o console a executar um comando!
     */
    public static void makeConsoleExecuteCommand(String theCommand) {
        CommandManager.get().handleCommand(ConsoleSender.INSTANCE, theCommand);
    }

    /**
     * Força o console a executar um comando!
     */
    public static void makeConsoleExecuteCommand(String... theCommands) {
        for (String theCommand : theCommands) {
            CommandManager.get().handleCommand(ConsoleSender.INSTANCE, theCommand);
        }
    }

    /**
     * Força o jogador a executar um comando!
     */
    public static void makePlayerExecuteCommand(FCommandSender player, String theCommand) {
        com.hypixel.hytale.server.core.command.system.CommandSender delegate = player.getDelegate(com.hypixel.hytale.server.core.command.system.CommandSender.class);
        CommandManager.get().handleCommand(delegate, theCommand);
    }

    public static ItemStack getPlayersHeldItem(Player player) {
        return player.getInventory().getItemInHand();
    }
}
