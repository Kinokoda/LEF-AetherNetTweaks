package eu.lowendforum.LEFAetherNetTweaks.Listeners;

import eu.lowendforum.LEFAetherNetTweaks.DataTypes.JukeboxInteractionType;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class JukeboxPASystemListener implements Listener {

    @EventHandler
    public void onDiscStart(PlayerInteractEvent triggerEvent){
        if(triggerEvent.getAction().isRightClick() && triggerEvent.getClickedBlock() != null && triggerEvent.getClickedBlock().getType() == Material.JUKEBOX){
            Jukebox triggerJukebox = (Jukebox) triggerEvent.getClickedBlock().getState();
            ItemStack playerItem = triggerEvent.getPlayer().getInventory().getItemInMainHand();
            JukeboxInteractionType interactionType = null;

            if(triggerJukebox.isPlaying()){
                interactionType = JukeboxInteractionType.STOPPING;
            }
            else if (!triggerJukebox.isPlaying() && playerItem.getType().toString().startsWith("MUSIC_DISC_")){
                interactionType = JukeboxInteractionType.STARTING;
            }

            System.out.println("Jukebox interaction detected: " + interactionType);
        }
    }
}
