package br.com.finalcraft.evernifecore.api.hytale;

import br.com.finalcraft.evernifecore.EverNifeCore;
import br.com.finalcraft.evernifecore.api.common.player.BaseFPlayer;
import br.com.finalcraft.evernifecore.fancytext.FancyText;
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

            HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());

            if (headRotationComponent == null) {
                return null;
            }

            EntityStore entityStore = store.getExternalData();

            if (entityStore.getWorld() == null){
                return null;
            }

            Vector3d position = transformComponent.getPosition();
            Vector3f rotation = headRotationComponent.getRotation();

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

        World world = Universe.get().getWorld(targetLocation.getWorld());

        if (world == null){
            return false;
        }

        TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());
        if (transformComponent == null) {
            return false;
        }

        HeadRotation headRotationComponent = store.getComponent(ref, HeadRotation.getComponentType());

//        Vector3f previousBodyRotation = transformComponent.getRotation().clone();
        Vector3d previousPos = transformComponent.getPosition().clone();
        Vector3f previousRotation = headRotationComponent.getRotation().clone();

        TeleportHistory teleportHistoryComponent = store.ensureAndGetComponent(ref, TeleportHistory.getComponentType());
        teleportHistoryComponent.append(world, previousPos, previousRotation, "EverNifeCore teleport to " + world.getName());
        
        Transform transform = new Transform(targetLocation.getPosition(), targetLocation.getRotation());
//        Vector3f preRotation = transform.getRotation().clone();
//        transform.setRotation(new Vector3f(previousBodyRotation.getPitch(), preRotation.getYaw(), previousBodyRotation.getRoll()));

        Teleport teleport = new Teleport(world, transform);
        store.addComponent(ref, Teleport.getComponentType(), teleport);

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
