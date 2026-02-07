package br.com.finalcraft.evernifecore.listeners.base;

import br.com.finalcraft.evernifecore.argumento.Argumento;
import br.com.finalcraft.evernifecore.locale.FCLocaleManager;
import br.com.finalcraft.evernifecore.util.FCArrayUtil;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import jakarta.annotation.Nonnull;

public interface ECListener {

    public default String[] requiredPlugins(){
        return FCArrayUtil.toArray();
    }

    public default boolean canRegister(){
        return true;
    }

    public default boolean silentRegistration(){
        return false;
    }

    public void onRegister();

    public default void unregisterAll() {

    }

    public static boolean register(@Nonnull JavaPlugin pluginInstance, ECListener listener){
        try {
            String[] requiredPlugins = listener.requiredPlugins();

            if (requiredPlugins != null && requiredPlugins.length > 0){
                for (String requiredPlugin : listener.requiredPlugins()) {//Check if all required plugins are present
                    if (new Argumento(requiredPlugin).getPlugin() == null){
                        return false;
                    }
                }
            }

            Boolean canRegister = null;
            try {
                canRegister = listener.canRegister();
            }catch (Throwable e){
                pluginInstance.getLogger().atWarning().log("[ECListener] Failed to call [canRegister()] method of the ECListener: " + listener.getClass().getName());
                e.printStackTrace();
            }

            if (canRegister == null || canRegister == false){
                return false;
            }

            if (!listener.silentRegistration()){
                pluginInstance.getLogger().atInfo().log("[ECListener] Registering Listener [" + listener.getClass().getName() + "]");
            }

            //Check for locales
            FCLocaleManager.loadLocale(pluginInstance, true, listener.getClass());
            try {
                listener.onRegister();
            }catch (Throwable e){
                pluginInstance.getLogger().atWarning().log("[ECListener] Failed to call [onRegister()] method of the ECListener: " + listener.getClass().getName());
                e.printStackTrace();
                return false;
            }

            return true;
        }catch (Throwable t){
            pluginInstance.getLogger().atWarning().log("[ECListener] Failed to register Listener: " + listener.getClass().getName());
            t.printStackTrace();
        }
        return false;
    }

    public static boolean register(@Nonnull JavaPlugin pluginInstance, Class<? extends ECListener> clazz) {
        try {
            ECListener listener = clazz.getDeclaredConstructor().newInstance();
            return register(pluginInstance, listener);
        } catch (Throwable t) {
            pluginInstance.getLogger().atWarning().log("[ECListener] Failed to register Listener: [" + clazz.getName() + "] " + t.getClass().getSimpleName() + " [" + t.getMessage() + "]");
        }
        return false;
    }

}
