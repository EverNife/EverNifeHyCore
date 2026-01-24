package br.com.finalcraft.evernifecore.inventory.player;

import br.com.finalcraft.evernifecore.EverNifeCore;
import br.com.finalcraft.evernifecore.api.common.player.FPlayer;
import br.com.finalcraft.evernifecore.api.hytale.HytaleFPlayer;
import br.com.finalcraft.evernifecore.config.yaml.anntation.Loadable;
import br.com.finalcraft.evernifecore.config.yaml.anntation.Salvable;
import br.com.finalcraft.evernifecore.config.yaml.section.ConfigSection;
import br.com.finalcraft.evernifecore.inventory.GenericInventory;
import br.com.finalcraft.evernifecore.inventory.extrainvs.ExtraInv;
import br.com.finalcraft.evernifecore.inventory.extrainvs.ExtraInvManager;
import br.com.finalcraft.evernifecore.inventory.extrainvs.factory.IExtraInvFactory;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class FCPlayerInventory implements Salvable {

    protected GenericInventory storage = new GenericInventory();
    protected GenericInventory armor = new GenericInventory();
    protected GenericInventory hotbar = new GenericInventory();
    protected GenericInventory utility = new GenericInventory();
    protected GenericInventory tools = new GenericInventory();
    protected GenericInventory backpack = new GenericInventory();

    protected List<ExtraInv> extraInvs = new ArrayList<>();

    public FCPlayerInventory(FPlayer player) {
        this(player, ExtraInvManager.getAllFactories());
    }

    public FCPlayerInventory(GenericInventory storage, GenericInventory armor, GenericInventory hotbar, GenericInventory utility, GenericInventory tools, GenericInventory backpack, List<ExtraInv> extraInvs) {
        this.storage = storage;
        this.armor = armor;
        this.hotbar = hotbar;
        this.utility = utility;
        this.tools = tools;
        this.backpack = backpack;
        this.extraInvs = extraInvs;
    }

    public FCPlayerInventory(FPlayer player, @Nullable Collection<IExtraInvFactory<?>> inventoryFactories) {
        Inventory playerInventory = ((HytaleFPlayer)player).getPlayer().getInventory();

        this.storage = new GenericInventory(playerInventory.getStorage());
        this.armor = new GenericInventory(playerInventory.getArmor());
        this.hotbar = new GenericInventory(playerInventory.getHotbar());
        this.utility = new GenericInventory(playerInventory.getUtility());
        this.tools = new GenericInventory(playerInventory.getTools());
        this.backpack = new GenericInventory(playerInventory.getBackpack());

        if (inventoryFactories != null){
            for (IExtraInvFactory<?> factory : inventoryFactories) {
                try {
                    ExtraInv extraInv = factory.extractFromPlayer(player);
                    extraInvs.add(extraInv);
                }catch (Exception e){
                    EverNifeCore.getLog().info("Failed to extract ExtraInv(" + factory.getId() + ") from " + player.getName());
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onConfigSave(ConfigSection section) {
        section.setValue(null);

        section.setValue("storage", storage);
        section.setValue("armor", armor);
        section.setValue("hotbar", hotbar);
        section.setValue("utility", utility);
        section.setValue("tools", tools);
        section.setValue("backpack", backpack);
        section.setValue("extra", null); //Clear content before saving it

        for (ExtraInv extraInv : extraInvs) {
            String extraInvID = extraInv.getFactory().getId();
            section.setValue("extra." + extraInvID, extraInv);
        }
    }

    @Loadable
    public static FCPlayerInventory onConfigLoad(ConfigSection section){

        GenericInventory storage  = section.getLoadable("storage", GenericInventory.class);
        GenericInventory armor    = section.getLoadable("armor", GenericInventory.class);
        GenericInventory hotbar   = section.getLoadable("hotbar", GenericInventory.class);
        GenericInventory utility  = section.getLoadable("utility", GenericInventory.class);
        GenericInventory tools    = section.getLoadable("tools", GenericInventory.class);
        GenericInventory backpack = section.getLoadable("backpack", GenericInventory.class);

        List<ExtraInv> extraInvList = new ArrayList<>();
        for (String extraInvKey : section.getKeys("extra")) {
            ConfigSection extraInvSection = section.getConfigSection("extra." + extraInvKey);
            try {
                IExtraInvFactory factory = ExtraInvManager.getFactory(extraInvKey);
                if (factory == null){
                    continue;
                }

                ExtraInv extraInv = factory.onConfigLoad(extraInvSection);
                extraInvList.add(extraInv);
            }catch (Throwable e){
                EverNifeCore.getLog().info("Failed to load ExtraInv(" + extraInvKey + ") at " + extraInvSection.toString());
                e.printStackTrace();
            }
        }

        return new FCPlayerInventory(storage, armor, hotbar, utility, tools, backpack, extraInvList);
    }

    public ItemStack getHelmet() {
        return armor.getItem(0);
    }

    public ItemStack getChestplate() {
        return armor.getItem(1);
    }

    public ItemStack getLeggings() {
        return armor.getItem(2);
    }

    public ItemStack getBoots() {
        return armor.getItem(3);
    }

    public ExtraInv getExtraInv(String extraInvId){
        return extraInvs.stream()
                .filter(extraInv -> extraInv.getFactory().getId().equals(extraInvId))
                .findFirst()
                .orElse(null);
    }

    public void restoreTo(FPlayer player){
        restoreTo(player, ExtraInvManager.getAllFactories());
    }

    public void restoreTo(FPlayer player, @Nullable Collection<IExtraInvFactory<?>> inventoryFactories) {
        Inventory playerInventory = ((HytaleFPlayer)player).getPlayer().getInventory();

        if (this.storage != null) storage.restoreTo(playerInventory.getStorage());
        if (this.armor != null) armor.restoreTo(playerInventory.getStorage());
        if (this.hotbar != null) hotbar.restoreTo(playerInventory.getStorage());
        if (this.utility != null) utility.restoreTo(playerInventory.getStorage());
        if (this.tools != null) tools.restoreTo(playerInventory.getStorage());
        if (this.backpack != null) backpack.restoreTo(playerInventory.getStorage());

        for (IExtraInvFactory factory : inventoryFactories) {
            // We need to ge all factories, rather than use 'this.getExtraInvs()'
            // because if there is a factory that is not present on 'this.extraInvs()',
            // it means that we need to erase that extraInv on the player
            try {
                ExtraInv extraInv = this.getExtraInv(factory.getId());
                if (extraInv == null){
                    extraInv = factory.createEmptyExtraInv();
                }
                factory.applyToPlayer(player, extraInv);
            }catch (Throwable e){
                EverNifeCore.getLog().info("Failed to restore ExtraInv(" + factory.getId() + ") into " + player.getName());
                e.printStackTrace();
            }
        }
    }
}