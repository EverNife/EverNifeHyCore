package br.com.finalcraft.evernifecore.api.hytale;

import br.com.finalcraft.evernifecore.EverNifeCore;
import br.com.finalcraft.evernifecore.api.common.player.BaseFPlayer;
import br.com.finalcraft.evernifecore.fancytext.FancyText;
import br.com.finalcraft.evernifecore.scheduler.FCScheduler;
import br.com.finalcraft.evernifecore.util.FCAdventureUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import jakarta.annotation.Nullable;
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

    public abstract PlayerRef getPlayerRef();

    @Override
    public String getName() {
        return getPlayerRef().getUsername();
    }

    @Override
    public UUID getUniqueId() {
        return getPlayerRef().getUuid();
    }

    @Override
    public void sendMessage(@NonNull String message) {
        getPlayerRef().sendMessage(Message.raw(message));
    }

    @Override
    public void sendMessage(@NonNull Component component) {
        Message message = FCAdventureUtil.toHytaleMessage(component);
        getPlayerRef().sendMessage(message);
    }

    @Override
    public boolean hasPermission(@NonNull String permission) {
        return PermissionsModule.get().hasPermission(getPlayerRef().getUuid(), permission);
    }

    @Override
    public boolean isOnline() {
        return getPlayerRef() != null && getPlayerRef().isValid();
    }

    public @Nullable Location getLocation() {
        Ref<EntityStore> playerRef = getPlayerRef().getReference();

        if (playerRef == null || !playerRef.isValid()) {
            return null;
        }

        Store<EntityStore> playerRefStore = playerRef.getStore();
        if (playerRefStore == null) {
            return null;
        }

        World world = playerRefStore.getExternalData().getWorld();

        return FCScheduler.SynchronizedAction.runAndGet(world, () -> {
            TransformComponent transformComponent = playerRefStore.getComponent(playerRef, TransformComponent.getComponentType());
            if (transformComponent == null) {
                return null;
            }

            EntityStore entityStore = playerRefStore.getExternalData();

            if (entityStore.getWorld() == null){
                return null;
            }

            Vector3d position = transformComponent.getPosition();
            Vector3f rotation = transformComponent.getRotation();

            return new Location(world.getName(), position, rotation);
        });
    }

    public boolean teleportTo(Location targetLocation){
        Ref<EntityStore> playerRef = getPlayerRef().getReference();

        if (playerRef == null || !playerRef.isValid()) {
            return false;
        }

        Store<EntityStore> playerRefStore = playerRef.getStore();
        if (playerRefStore == null) {
            return false;
        }

        World targetWorld = Universe.get().getWorld(targetLocation.getWorld());

        if (targetWorld == null){
            return false;
        }

        TransformComponent transformComponent = playerRefStore.getComponent(playerRef, TransformComponent.getComponentType());
        if (transformComponent == null) {
            return false;
        }

        Teleport teleport = new Teleport(targetWorld, targetLocation.getPosition(), targetLocation.getRotation());
        playerRefStore.putComponent(playerRef, Teleport.getComponentType(), teleport);

        EverNifeCore.getLog().debug(() -> {
            Location origin = getLocation();
            return String.format("[TP] Teleport player %s from %s to %s ", getName(), origin, targetLocation);
        });

        return false;
    }

    public static class PlayerRefFPlayer extends HytaleFPlayer<PlayerRef> {

        public PlayerRefFPlayer(PlayerRef playerRef) {
            super(playerRef);
        }

        @Override
        public PlayerRef getPlayerRef() {
            return getDelegate();
        }

    }

    public static class PlayerFPlayer extends HytaleFPlayer<Player> {

        public PlayerFPlayer(Player player) {
            super(player);
        }

        @Override
        public PlayerRef getPlayerRef() {
            return getDelegate().getPlayerRef();
        }

    }

}
