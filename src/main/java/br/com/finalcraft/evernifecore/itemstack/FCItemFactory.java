package br.com.finalcraft.evernifecore.itemstack;

import br.com.finalcraft.evernifecore.itemstack.itembuilder.FCItemBuilder;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import jakarta.annotation.Nonnull;

public class FCItemFactory {

    @Nonnull
    public static FCItemBuilder from(@Nonnull final ItemStack itemStack) {
        return new FCItemBuilder(itemStack);
    }

}
