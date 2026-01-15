package br.com.finalcraft.evernifecore.integration.placeholders;

import br.com.finalcraft.evernifecore.config.playerdata.IPlayerData;
import br.com.finalcraft.evernifecore.exeptions.HytaleDoesNotHaveTheMinecraftEquivalentYet;
import br.com.finalcraft.evernifecore.placeholder.replacer.RegexReplacer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class PAPIIntegration {

    private static Boolean enabled = null;

    public static <P extends IPlayerData> RegexReplacer<P> createPlaceholderIntegration(@Nonnull JavaPlugin plugin, @Nonnull String pluginBaseID, @Nonnull Class<P> playerDataType){
        throw new HytaleDoesNotHaveTheMinecraftEquivalentYet();
    }

    public static String parse(@Nullable Player player, @Nonnull String text){
        throw new HytaleDoesNotHaveTheMinecraftEquivalentYet();
    }

}
