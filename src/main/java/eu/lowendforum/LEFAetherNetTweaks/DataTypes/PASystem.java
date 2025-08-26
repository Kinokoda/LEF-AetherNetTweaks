package eu.lowendforum.LEFAetherNetTweaks.DataTypes;

import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class PASystem {
    private Block Jukebox;
    private List<Block> Speakers = new ArrayList<>();

    public List<Block> getSpeakers() {
        return Speakers;
    }
    public void addSpeaker(Block speaker) {
        Speakers.add(speaker);
    }

    public PASystem(Block jukebox, Block firstSpeaker) {
        this.Jukebox = jukebox;
        this.Speakers.add(firstSpeaker);
    }
}
