package br.com.finalcraft.evernifecore.api.common.commandsender;

import br.com.finalcraft.evernifecore.fancytext.FancyText;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface FCommandSender {

    public Object getDelegate();

    public default <DELEGATE> DELEGATE getDelegate(Class<DELEGATE> delegateClass) {
        Validate.isTrue(delegateClass.isAssignableFrom(this.getDelegate().getClass()));
        return (DELEGATE) getDelegate();
    }

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
