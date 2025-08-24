package eu.lowendforum.LEFAetherNetTweaks.Listeners;

import eu.lowendforum.LEFAetherNetTweaks.MultiUseFunctions;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class JukeboxPASystemListener implements Listener {

    @EventHandler
    public void onDiscStart(PlayerInteractEvent triggerEvent){
        if(triggerEvent.getAction().isRightClick() && triggerEvent.getClickedBlock() != null && triggerEvent.getClickedBlock().getType() == Material.JUKEBOX){
            Jukebox jukebox = (Jukebox) triggerEvent.getClickedBlock().getState();


        }
    }
    public enum JukeboxInteractionType {
        BLANK,
        STOPPING,
        STARTING,
        SWITCHING
    }
}
