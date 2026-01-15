package br.com.finalcraft.evernifecore.fancytext;

import br.com.finalcraft.evernifecore.config.playerdata.PlayerController;
import br.com.finalcraft.evernifecore.config.playerdata.PlayerData;
import br.com.finalcraft.evernifecore.util.FCAdventureUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import net.kyori.adventure.text.Component;

import java.util.Map;
import java.util.function.Function;

public class FancyTextManager {

    public static void send(FancyText fancyText, IMessageReceiver... commandSenders) {
        if (fancyText.fancyFormatter != null) {
            send(fancyText.fancyFormatter, commandSenders);
            return;
        }

        Component component = fancyText.toComponent();
        
        for (IMessageReceiver sender : commandSenders) {
            if (sender instanceof Player player) {
                Message message = FCAdventureUtil.toHytaleMessage(component);
                player.sendMessage(message);
            } else {
                sender.sendMessage(Message.raw(fancyText.text));
            }
        }
    }

    public static void send(FancyFormatter fancyFormatter, IMessageReceiver... commandSenders) {
        if (!fancyFormatter.hasPlaceholders()) {
            Component component = fancyFormatter.toComponent();
            
            for (IMessageReceiver sender : commandSenders) {
                if (sender instanceof Player player) {
                    Message message = FCAdventureUtil.toHytaleMessage(component);
                    player.sendMessage(message);
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (FancyText aFancyText : fancyFormatter.getFancyTextList()) {
                        stringBuilder.append(aFancyText.text);
                    }
                    sender.sendMessage(Message.raw(stringBuilder.toString()));
                }
            }
            return;
        }

        if (fancyFormatter.complexPlaceholder) {
            for (IMessageReceiver sender : commandSenders) {
                FancyFormatter formatterClone = fancyFormatter.clone();
                final boolean isPlayer = sender instanceof Player;
                final PlayerData playerData = isPlayer ? PlayerController.getPlayerData(((Player) sender).getPlayerRef().getUuid()) : null;
                
                for (Map.Entry<String, Object> entry : formatterClone.mapOfPlaceholders.entrySet()) {
                    String placeholder = entry.getKey();
                    String value;
                    if (isPlayer && entry.getValue() instanceof Function) {
                        value = String.valueOf(((Function<PlayerData, Object>) entry.getValue()).apply(playerData));
                    } else {
                        value = String.valueOf(entry.getValue());
                    }
                    formatterClone.replace(placeholder, value);
                }

                if (isPlayer) {
                    Player player = (Player) sender;
                    Component component = formatterClone.toComponent();
                    Message message = FCAdventureUtil.toHytaleMessage(component);
                    player.sendMessage(message);
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (FancyText aFancyText : formatterClone.getFancyTextList()) {
                        stringBuilder.append(aFancyText.text);
                    }
                    sender.sendMessage(Message.raw(stringBuilder.toString()));
                }
            }
            return;
        }

        FancyFormatter formatterClone = fancyFormatter.clone();
        for (Map.Entry<String, Object> entry : fancyFormatter.mapOfPlaceholders.entrySet()) {
            String placeholder = entry.getKey();
            String value = String.valueOf(entry.getValue());
            formatterClone.replace(placeholder, value);
        }

        Component component = formatterClone.toComponent();
        
        for (IMessageReceiver sender : commandSenders) {
            if (sender instanceof Player player) {
                Message message = FCAdventureUtil.toHytaleMessage(component);
                player.sendMessage(message);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (FancyText aFancyText : formatterClone.getFancyTextList()) {
                    stringBuilder.append(aFancyText.text);
                }
                sender.sendMessage(Message.raw(stringBuilder.toString()));
            }
        }
    }
}
