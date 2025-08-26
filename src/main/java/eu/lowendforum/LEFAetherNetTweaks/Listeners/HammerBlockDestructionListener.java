package eu.lowendforum.LEFAetherNetTweaks.Listeners;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HammerBlockDestructionListener implements Listener {

    @EventHandler
    public void onBlockDestruction(BlockBreakEvent blockTriggerEvent){

        //Get player tool
        var userTool = blockTriggerEvent.getPlayer().getInventory().getItemInMainHand();
        var userToolMeta = userTool.getItemMeta();
        //Get the block that triggered the event
        var triggerBlock = blockTriggerEvent.getBlock();


        //Hammer Code Block
        if (userToolMeta == null) return;
        if (!userToolMeta.getCustomModelDataComponent().getStrings().contains("hammer")) return; //Exit if the tool is not a hammer
        //Get the qualities of the tool we are using
        List<Tag<Material>> serviceableBlocks = getCompatibleQualities(userTool);

        //If the tool is not compatible with the block, cancel the event and exit
        if ((getQualities().stream().anyMatch(qualityTag -> qualityTag.isTagged(triggerBlock.getType())) && serviceableBlocks.stream().noneMatch(tag -> tag.isTagged(triggerBlock.getType()))) || triggerBlock.getType().getHardness() == -1) {
            blockTriggerEvent.setCancelled(true);
            return;
        }
        //Get the player that triggered the event
        var triggerPlayer = blockTriggerEvent.getPlayer();
        //Get the player's eye location
        var triggerPlayerEyeLocation = triggerPlayer.getEyeLocation();

        //Perform a ray trace to find the block face the player is looking at and save it
        RayTraceResult faceFindingResult = triggerPlayer.getLocation().getWorld().rayTraceBlocks(triggerPlayerEyeLocation,triggerPlayerEyeLocation.getDirection(),10, FluidCollisionMode.NEVER);

        //If the ray trace did not hit a block, exit NOTE: Throw an exception as well, this should never happen.
        if (faceFindingResult == null) return;
        //Get the face the block was broken on
        BlockFace triggerBlockHitFace = faceFindingResult.getHitBlockFace();
        //If the face is null, exit NOTE: Throw an exception as well, this should never happen.
        if (triggerBlockHitFace == null) return;


        //ToDo: Understand what the hell this does
        /*for (BlockFace blockFace : getBlockFaces()) {
            if (blockFace == triggerBlockHitFace || blockFace == triggerBlockHitFace.getOppositeFace()) {
                continue;
            }
            var targetBlock = triggerBlock.getRelative(blockFace);
            if ((getQualities().stream().anyMatch(qualityTag -> qualityTag.isTagged(targetBlock.getType())) && serviceableBlocks.stream().noneMatch(tag -> tag.isTagged(targetBlock.getType()))) || targetBlock.getType().getHardness() == -1) {
                continue;
            }
            targetBlock.breakNaturally(userTool);
        }*/
        List<Block> area = new ArrayList<>();
        switch(triggerBlockHitFace) {
            case UP:
            case DOWN:
                for (int x = -1; x <= 1; x++)
                    for (int z = -1; z <= 1; z++)
                        area.add(triggerBlock.getRelative(x,0,z));
                break;
            case NORTH:
            case SOUTH:
                for (int x = -1; x <= 1; x++)
                    for (int y = -1; y <= 1; y++)
                        area.add(triggerBlock.getRelative(x,y,0));
                break;
            case EAST:
            case WEST:
                for (int y = -1; y <= 1; y++)
                    for (int z = -1; z <= 1; z++)
                        area.add(triggerBlock.getRelative(0,y,z));
                break;
        }
        //Loop through the area and break all blocks that are compatible with the tool
        for (Block targetBlock : area) {
            if ((getQualities().stream().anyMatch(qualityTag -> qualityTag.isTagged(targetBlock.getType())) && serviceableBlocks.stream().noneMatch(tag -> tag.isTagged(targetBlock.getType()))) || targetBlock.getType().getHardness() == -1) {
                continue;
            }
            if (targetBlock.getType() == Material.AIR) continue; //Skip air blocks
            targetBlock.breakNaturally(userTool);

            if(userToolMeta instanceof Damageable damageable){
                damageable.setDamage(damageable.getDamage() + 1);
                userTool.setItemMeta(damageable);
            }

        }
        if(userToolMeta instanceof Damageable damageable){
            if (damageable.getDamage() >= userTool.getType().getMaxDurability()) {
                triggerPlayer.getInventory().setItemInMainHand(null);
            }
        }

    }
    /**
     * Returns a string representation of the block face direction.
     *
     * @param itemStack The tool held by the player.
     * @return The block types the tool can break.
     */
    private static List<Tag<Material>> getCompatibleQualities(ItemStack itemStack) {
        ArrayList<Tag<Material>> compatibleQualities = new ArrayList<>();
        List<Material> diamondTools = Arrays.asList(
                Material.DIAMOND_AXE,
                Material.DIAMOND_HOE,
                Material.DIAMOND_PICKAXE,
                Material.DIAMOND_SHOVEL,
                Material.NETHERITE_AXE,
                Material.NETHERITE_HOE,
                Material.NETHERITE_PICKAXE,
                Material.NETHERITE_SHOVEL
        );
        List<Material> ironTools = Arrays.asList(
                Material.IRON_AXE,
                Material.IRON_HOE,
                Material.IRON_PICKAXE,
                Material.IRON_SHOVEL
        );
        List<Material> stoneTools = Arrays.asList(
                Material.STONE_AXE,
                Material.STONE_HOE,
                Material.STONE_PICKAXE,
                Material.STONE_SHOVEL
        );
        if (stoneTools.contains(itemStack.getType())) {
            compatibleQualities.add(Tag.NEEDS_STONE_TOOL);
        }
        if (ironTools.contains(itemStack.getType())) {
            compatibleQualities.add(Tag.NEEDS_IRON_TOOL);
            compatibleQualities.add(Tag.NEEDS_STONE_TOOL);
        }
        if (diamondTools.contains(itemStack.getType())) {
            compatibleQualities.add(Tag.NEEDS_DIAMOND_TOOL);
            compatibleQualities.add(Tag.NEEDS_IRON_TOOL);
            compatibleQualities.add(Tag.NEEDS_STONE_TOOL);
        }
        return compatibleQualities;
    }

    /**
     * Returns a list of qualities that can be used to break blocks.
     *
     * @return A list of qualities.
     */
    private static List<Tag<Material>> getQualities() {
        return Arrays.asList(
                Tag.NEEDS_STONE_TOOL,
                Tag.NEEDS_IRON_TOOL,
                Tag.NEEDS_DIAMOND_TOOL
        );
    }

    /**
     * Returns a list of block faces that can be used to break blocks.
     *
     * @return A list of block faces.
     */
    private static List<BlockFace> getBlockFaces() {
        return Arrays.asList(
                BlockFace.NORTH,
                BlockFace.EAST,
                BlockFace.SOUTH,
                BlockFace.WEST,
                BlockFace.UP,
                BlockFace.DOWN,
                BlockFace.NORTH_EAST,
                BlockFace.NORTH_WEST,
                BlockFace.SOUTH_EAST,
                BlockFace.SOUTH_WEST
        );
    }
}
