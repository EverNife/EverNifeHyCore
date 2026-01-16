package br.com.finalcraft.evernifecore.api.common.commandsender;

import br.com.finalcraft.evernifecore.api.common.IFHasDelegate;
import br.com.finalcraft.evernifecore.fancytext.FancyText;
import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface FCommandSender extends IFHasDelegate {

    String getName();

    UUID getUniqueId();

    default boolean isPlayer() {
        return getUniqueId() != null;
    }

    default boolean isConsole() {
        return !isPlayer();
    }

    boolean hasPermission(@Nonnull String permission);

    void sendMessage(@Nonnull String message);

    void sendMessage(@Nonnull FancyText message);

    void sendMessage(@Nonnull Component component);

}
