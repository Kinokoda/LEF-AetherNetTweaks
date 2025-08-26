package eu.lowendforum.LEFAetherNetTweaks.Listeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.event.block.BlockPreDispenseEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class TreeCutterActivationListener implements Listener {

    @EventHandler
    public void onActivate(BlockPreDispenseEvent triggerEvent) {
        Block triggerBlock = triggerEvent.getBlock();
        Inventory triggerBlockInventory = ((Dispenser) triggerBlock.getState()).getInventory();
        BlockData triggerBlockData = triggerBlock.getBlockData();
        if (!hasSawBlade(triggerBlockInventory)) {
            return;
        }
        triggerEvent.setCancelled(true);
        BlockFace triggerBlockFacing = null;
        if (triggerBlockData instanceof Directional directional) {
            triggerBlockFacing = directional.getFacing();
        }
        //System.out.println("Dispenser activated with saw blade: " + triggerBlock.getLocation().toString());
        Block targetBlock = null;
        switch (triggerBlockFacing) {
            case NORTH -> targetBlock = triggerBlock.getRelative(BlockFace.NORTH);
            case SOUTH -> targetBlock = triggerBlock.getRelative(BlockFace.SOUTH);
            case EAST -> targetBlock = triggerBlock.getRelative(BlockFace.EAST);
            case WEST -> targetBlock = triggerBlock.getRelative(BlockFace.WEST);
            default -> throw new IllegalStateException("Invalid block facing: " + triggerBlockFacing);
        }
        treeDestroy(triggerBlockInventory, targetBlock);
    }

    public void treeDestroy(Inventory dispenserInventory, Block bottomLog) {
        List<Block> blocksToDestroy = new ArrayList<>();
        Material logMaterial = bottomLog.getType();
        Material leafMaterial = getLeafMaterial(logMaterial);
        Location bottomLogLocation = bottomLog.getLocation();

        // Check if the bottom block is a log
        if (!bottomLog.getType().toString().endsWith("_LOG")) {
            return; // Not a log, exit the method
        }

        // Add the bottom log to the list
        blocksToDestroy.add(bottomLog);

        // Scan upwards for logs
        int y = 1;

        while (true) {
            Block currentBlock = bottomLogLocation.clone().add(0, y, 0).getBlock();
            if (currentBlock.getType() == logMaterial) {
                blocksToDestroy.add(currentBlock);
                y++;
            } else if (currentBlock.getType() == Material.AIR) {
                return;
            } else {
                break; // Stop if we reach a non-log block
            }
        }

        // Scan for leaves around each log block
        for (Block logBlock : new ArrayList<>(blocksToDestroy)) {
            Location logLocation = logBlock.getLocation();
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        Block nearbyBlock = logLocation.clone().add(dx, dy, dz).getBlock();
                        if (nearbyBlock.getType() == leafMaterial && !blocksToDestroy.contains(nearbyBlock)) {
                            blocksToDestroy.add(nearbyBlock);
                        }
                    }
                }
            }
        }

        // Destroy all collected blocks and add logs to dispenser inventory
        //Collect the blocks
        for (Block block : blocksToDestroy) {
            if (block.getType().toString().endsWith("_LOG")) {
                dispenserInventory.addItem(new ItemStack(block.getType(), 1));
            }
        }
        for (Block block : blocksToDestroy) {
            block.setType(Material.AIR);
        }
    }

    public boolean hasSawBlade(Inventory dispenserInventory){
        ItemStack checkItem = new ItemStack(Material.DIAMOND_AXE, 1);
        checkItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString("sawblade")
                .build()
        );
        ItemMeta checkItemMeta = checkItem.getItemMeta();
        checkItemMeta.displayName(Component.text("Saw Blade"));
        checkItem.setItemMeta(checkItemMeta);
        return dispenserInventory.contains(checkItem);
    }

    public Material getLeafMaterial(Material inputMaterial){
        return switch (inputMaterial) {
            case OAK_LOG -> Material.OAK_LEAVES;
            case SPRUCE_LOG -> Material.SPRUCE_LEAVES;
            case BIRCH_LOG -> Material.BIRCH_LEAVES;
            case JUNGLE_LOG -> Material.JUNGLE_LEAVES;
            case ACACIA_LOG -> Material.ACACIA_LEAVES;
            case DARK_OAK_LOG -> Material.DARK_OAK_LEAVES;
            case CHERRY_LOG -> Material.CHERRY_LEAVES;
            case PALE_OAK_LOG -> Material.PALE_OAK_LEAVES;
            case MANGROVE_LOG -> Material.MANGROVE_LEAVES;
            default -> throw new IllegalStateException("Unexpected value: " + inputMaterial);
        };
    }
}
