package br.com.finalcraft.evernifecore.integration.placeholders;

import at.helpch.placeholderapi.PlaceholderAPI;
import at.helpch.placeholderapi.PlaceholderAPIPlugin;
import at.helpch.placeholderapi.expansion.PlaceholderExpansion;
import br.com.finalcraft.evernifecore.EverNifeCore;
import br.com.finalcraft.evernifecore.config.playerdata.IPlayerData;
import br.com.finalcraft.evernifecore.integration.placeholders.papi.PAPIRegexReplacer;
import br.com.finalcraft.evernifecore.integration.placeholders.papi.SimplePAPIHook;
import br.com.finalcraft.evernifecore.placeholder.replacer.RegexReplacer;
import br.com.finalcraft.evernifecore.util.FCColorUtil;
import br.com.finalcraft.evernifecore.util.FCReflectionUtil;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class PAPIIntegration {

    private static Boolean isPresent = null;

    public static boolean isPresent(){
        if (isPresent == null){
            isPresent = FCReflectionUtil.isClassLoaded("at.helpch.placeholderapi.PlaceholderAPIPlugin");
        }
        return isPresent;
    }

    public static <P extends IPlayerData> RegexReplacer<P> createPlaceholderIntegration(@Nonnull JavaPlugin plugin, @Nonnull String pluginBaseID, @Nonnull Class<P> playerDataType){

        PAPIRegexReplacer papiRegexReplacer = new PAPIRegexReplacer(playerDataType);

        SimplePAPIHook simplePAPIHook = new SimplePAPIHook(plugin, papiRegexReplacer);

        EverNifeCore.getLog().info("Registering PAPI Hook for the plugin " + plugin.getManifest().getName() + " with prefix '"  + pluginBaseID + "' using Modern PAPI method.");
        PlaceholderAPIPlugin.instance().localExpansionManager().register(new PlaceholderExpansion() {

            @Override
            public @Nonnull String getName() {
                return plugin.getManifest().getName();
            }

            @Override
            public @Nonnull String getIdentifier() {
                return pluginBaseID;
            }

            @Override
            public @Nonnull String getAuthor() {
                return plugin.getManifest().getAuthors().getFirst().getName();
            }

            @Override
            public @Nonnull String getVersion() {
                return plugin.getManifest().getVersion().toString();
            }

            @Override
            public String onPlaceholderRequest(PlayerRef player, @NotNull String params) {
                return simplePAPIHook.onPlaceholderRequest(player, params);
            }

            @Override
            public boolean persist() {
                return true;
            }
        });

        return papiRegexReplacer.getRegexReplacer();
    }

    public static String parse(@Nullable PlayerRef playerRef, @Nonnull String text){
        if (isPresent()){
            text = PlaceholderAPI.setPlaceholders(playerRef, text);
        }

        return FCColorUtil.colorfy(text);
    }

}
