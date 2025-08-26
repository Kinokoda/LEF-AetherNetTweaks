package eu.lowendforum.LEFAetherNetTweaks;

import eu.lowendforum.LEFAetherNetTweaks.Listeners.*;
import eu.lowendforum.LEFAetherNetTweaks.Recipes.*;
import eu.lowendforum.LEFAetherNetTweaks.Recipes.Smithing.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class LEFAetherNetTweaks extends JavaPlugin {



    @Override
    public void onEnable() {

        // Activate all the listeners
        getServer().getPluginManager().registerEvents(new HammerBlockDestructionListener(), this);
        getServer().getPluginManager().registerEvents(new TreeCutterActivationListener(), this);
        getServer().getPluginManager().registerEvents(new JukeboxPASystemListener(), this);

        //Add custom recipes
        ANT_IronHammer.makeRecipe(this);
        ANT_StoneHammer.makeRecipe(this);
        ANT_DiamondHammer.makeRecipe(this);
        ANT_SawBlade.makeRecipe(this);
        var netheriteHammerRecipe = ANT_NetheriteHammer.makeRecipe(this);



        System.out.println("[INFO]: LEFAetherNetTweaks has been enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[INFO]: Goodbye from LEFAetherNetTweaks");
    }
}
