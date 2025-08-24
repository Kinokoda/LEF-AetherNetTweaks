package eu.lowendforum.LEFAetherNetTweaks;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MultiUseFunctions {

    /**
     * Send a message to a player.
     * @param player The player to whom the message will be sent.
     * @param message The message to be sent to the player.
     */
    public static void printToPlayer(@NotNull Player player, @NotNull String message){
        player.sendMessage("[LEFAetherNetTweaks] " + message);
    }
}
