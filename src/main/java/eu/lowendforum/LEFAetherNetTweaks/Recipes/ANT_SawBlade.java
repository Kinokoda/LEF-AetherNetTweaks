package eu.lowendforum.LEFAetherNetTweaks.Recipes;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.getServer;

public class ANT_SawBlade {
    public static void makeRecipe(Plugin triggerPlugin) {
        // Recipe implementation goes here
        //Make item and key
        NamespacedKey key = new NamespacedKey(triggerPlugin, "ANT_SawBlade");
        ItemStack outputItem = new ItemStack(Material.DIAMOND_AXE, 1);

        outputItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString("sawblade")
                .build()
        );
        var outputItemMeta = outputItem.getItemMeta();
        outputItemMeta.displayName(Component.text("Saw Blade"));
        outputItem.setItemMeta(outputItemMeta);

        ShapedRecipe recipe = new ShapedRecipe(key, outputItem);
        //Define the recipe shape and the Materials used
        recipe.shape
                (
                        " # ",
                        "#|#",
                        " # "
                );
        recipe.setIngredient('#', Material.IRON_INGOT);
        recipe.setIngredient('|', Material.DIAMOND_AXE);
        //Add the recipe to the server
        getServer().addRecipe(recipe);
    }
}
