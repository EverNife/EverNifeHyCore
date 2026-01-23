package br.com.finalcraft.evernifecore.api.hytale;

import br.com.finalcraft.evernifecore.EverNifeCore;
import br.com.finalcraft.evernifecore.api.common.player.BaseFPlayer;
import br.com.finalcraft.evernifecore.fancytext.FancyText;
import br.com.finalcraft.evernifecore.logger.ECDebugModule;
import br.com.finalcraft.evernifecore.scheduler.FCScheduler;
import br.com.finalcraft.evernifecore.util.FCAdventureUtil;
import com.hypixel.hytale.builtin.teleport.components.TeleportHistory;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import jakarta.annotation.Nullable;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
        Ref<EntityStore> ref = getPlayerRef().getReference();

        if (ref == null || !ref.isValid()) {
            return null;
        }

        Store<EntityStore> store = ref.getStore();
        if (store == null) {
            return null;
        }

        World world = store.getExternalData().getWorld();

        return FCScheduler.SynchronizedAction.runAndGet(world, () -> {
            TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
            if (transformComponent == null) {
                return null;
            }

            HeadRotation headRotation = store.getComponent(ref, HeadRotation.getComponentType());

            Vector3f rotation = headRotation != null ? headRotation.getRotation() : new Vector3f(0.0F, 0.0F, 0.0F);
            Vector3d position = transformComponent.getPosition();

            return new Location(world.getName(), position, rotation);
        });
    }

    public boolean teleportTo(Location targetLocation){
        Ref<EntityStore> ref = getPlayerRef().getReference();

        if (ref == null || !ref.isValid()) {
            return false;
        }

        Store<EntityStore> store = ref.getStore();
        if (store == null) {
            return false;
        }

        World sourceWorld = store.getExternalData().getWorld();

        World targetWorld = targetLocation.getWorld().equals(sourceWorld.getName())
                ? sourceWorld
                : Universe.get().getWorld(targetLocation.getWorld());

        if (targetWorld == null){
            return false;
        }

        AtomicReference<TransformComponent> transformComponent = new AtomicReference<>();
        AtomicReference<HeadRotation> headRotationComponent = new AtomicReference<>();

        FCScheduler.SynchronizedAction.run(sourceWorld, () -> {
            //Get these components only inside the sourceWorld
            transformComponent.set(store.getComponent(ref, TransformComponent.getComponentType()));
            headRotationComponent.set(store.getComponent(ref, HeadRotation.getComponentType()));
        });

        if (transformComponent.get() == null) {
            return false;
        }

        Vector3d previousPos = transformComponent.get().getPosition().clone();
        Vector3f previousRotation = headRotationComponent.get() == null
                ? headRotationComponent.get().getRotation().clone()
                : new Vector3f(Float.NaN, Float.NaN, Float.NaN);

        //Load the chunk if already not loaded, this will prevent the player from be teleported OUTSIDE THE FRICKING WORLD
        WorldChunk worldChunk = targetWorld.isInThread()
                ? targetWorld.getChunk(targetLocation.getPosition().hashCode())
                : targetWorld.getChunkAsync(targetLocation.getPosition().hashCode()).join();

        FCScheduler.SynchronizedAction.run(targetWorld, () -> {
            Teleport teleport = new Teleport(targetWorld, targetLocation.getPosition(), new Vector3f());
            store.addComponent(ref, Teleport.getComponentType(), teleport);

            TeleportHistory teleportHistoryComponent = store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
            teleportHistoryComponent.append(targetWorld, previousPos, previousRotation, "[EC] teleport " + getPlayerRef().getUsername() +   " to " + targetLocation);
        });

        ECDebugModule.HYTALE_WRAPPER_FPLAYER.debugModule(() -> {
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
