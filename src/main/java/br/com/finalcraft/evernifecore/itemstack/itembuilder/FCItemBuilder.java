package br.com.finalcraft.evernifecore.itemstack.itembuilder;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.List;

public class FCItemBuilder {

    private ItemStack itemStack;

    public FCItemBuilder(@Nonnull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Read the ItemStack to a DataPart String List
     *
     * @return A list of strings.
     */
    @Nonnull
    public List<String> toDataPart(){
        return Arrays.asList("");
    }

    public ItemStack build(){
        return itemStack.withQuantity(itemStack.getQuantity() == 1 ? 2 : 1)
                .withQuantity(itemStack.getQuantity());
    }


}
