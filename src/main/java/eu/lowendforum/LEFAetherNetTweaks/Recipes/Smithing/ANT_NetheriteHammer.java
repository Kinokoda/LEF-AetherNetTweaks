package eu.lowendforum.LEFAetherNetTweaks.Recipes.Smithing;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.plugin.Plugin;

public class ANT_NetheriteHammer {
    public static SmithingTransformRecipe makeRecipe(Plugin triggerPlugin) {

        ItemStack outputItem = new ItemStack(Material.NETHERITE_PICKAXE, 1);
        outputItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData().addString("hammer").build());
        outputItem.setData(DataComponentTypes.CUSTOM_NAME, Component.text("Iron Hammer"));

        // Create the SmithingTransformRecipe
        return new SmithingTransformRecipe(
            new NamespacedKey(triggerPlugin, "ANT_NetheriteHammer"),
            outputItem,
            new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
            new RecipeChoice.MaterialChoice(Material.DIAMOND_PICKAXE),
            new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT)
        );
    }
}
