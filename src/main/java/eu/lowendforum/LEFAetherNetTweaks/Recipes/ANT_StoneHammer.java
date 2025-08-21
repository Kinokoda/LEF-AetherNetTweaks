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

public class ANT_StoneHammer {
    public static void makeRecipe(Plugin triggerPlugin){
        //Make item and key
        NamespacedKey key = new NamespacedKey(triggerPlugin, "ANT_StoneHammer");
        ItemStack outputItem = new ItemStack(Material.STONE_PICKAXE, 1);

        outputItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString("hammer")
                .build()
        );
        var outputItemMeta = outputItem.getItemMeta();
        outputItemMeta.displayName(Component.text("Stone Hammer"));
        outputItem.setItemMeta(outputItemMeta);

        ShapedRecipe recipe = new ShapedRecipe(key, outputItem);
        //Define the recipe shape and the Materials used
        recipe.shape
                (
                        "###",
                        " | ",
                        " | "
                );
        recipe.setIngredient('#', Material.STONE);
        recipe.setIngredient('|', Material.STICK);
        //Add the recipe to the server
        getServer().addRecipe(recipe);
    };

}
