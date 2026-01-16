package br.com.finalcraft.evernifecore.api.hytale;

import br.com.finalcraft.evernifecore.api.common.player.BaseFPlayer;
import br.com.finalcraft.evernifecore.fancytext.FancyText;
import br.com.finalcraft.evernifecore.util.FCAdventureUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public abstract class HytaleFPlayer<DELEGATE> extends BaseFPlayer<DELEGATE> {

    public HytaleFPlayer(DELEGATE delegate) {
        super(delegate);
    }

    public static HytaleFPlayer of(PlayerRef playerRef) {
        return new PlayerRefFPlayer(playerRef);
    }

    public static HytaleFPlayer of(Player player) {
        return new PlayerFPlayer(player);
    }

    @Override
    public void sendMessage(FancyText fancyText) {
        fancyText.send(this);
    }

    public static class PlayerRefFPlayer extends HytaleFPlayer<PlayerRef> {

        public PlayerRefFPlayer(PlayerRef playerRef) {
            super(playerRef);
        }

        @Override
        public String getName() {
            return getDelegate().getUsername();
        }

        @Override
        public UUID getUniqueId() {
            return getDelegate().getUuid();
        }

        @Override
        public void sendMessage(@NonNull String message) {
            getDelegate().sendMessage(Message.raw(message));
        }

        @Override
        public void sendMessage(@NonNull Component component) {
            Message message = FCAdventureUtil.toHytaleMessage(component);
            getDelegate().sendMessage(message);
        }

        @Override
        public boolean hasPermission(@NonNull String permission) {
            return PermissionsModule.get().hasPermission(getDelegate().getUuid(), permission);
        }

        @Override
        public boolean isOnline() {
            return getDelegate().isValid();
        }
    }

    public static class PlayerFPlayer extends HytaleFPlayer<Player> {

        public PlayerFPlayer(Player player) {
            super(player);
        }

        @Override
        public String getName() {
            return getDelegate().getPlayerRef().getUsername();
        }

        @Override
        public UUID getUniqueId() {
            return getDelegate().getPlayerRef().getUuid();
        }

        @Override
        public void sendMessage(@NonNull String message) {
            getDelegate().getPlayerRef().sendMessage(Message.raw(message));
        }

        @Override
        public void sendMessage(@NonNull Component component) {
            Message message = FCAdventureUtil.toHytaleMessage(component);
            getDelegate().getPlayerRef().sendMessage(message);
        }

        @Override
        public boolean hasPermission(@NonNull String permission) {
            return PermissionsModule.get().hasPermission(getDelegate().getPlayerRef().getUuid(), permission);
        }

        @Override
        public boolean isOnline() {
            return getDelegate().getPlayerRef() != null && getDelegate().getPlayerRef().isValid();
        }
    }

}
