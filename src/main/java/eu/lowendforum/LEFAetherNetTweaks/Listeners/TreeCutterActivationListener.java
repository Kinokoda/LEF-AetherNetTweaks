package eu.lowendforum.LEFAetherNetTweaks.Listeners;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class TreeCutterActivationListener implements Listener {

    @EventHandler
    public void onActivate(BlockDispenseEvent triggerEvent) {
        var triggerBlock = triggerEvent.getBlock();
        var dispenserData = triggerBlock.getBlockData();
        var triggerBlockInventory = ((org.bukkit.block.Dispenser) triggerBlock.getState()).getInventory();
        var triggerItem = triggerEvent.getItem();
        boolean missfired = false;

        ItemStack checkItem = new ItemStack(Material.DIAMOND_AXE, 1);
        checkItem.setData(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelData.customModelData()
                .addString("tree_cutter")
                .build()
        );
        var checkItemMeta = checkItem.getItemMeta();
        checkItemMeta.displayName(Component.text("Saw Blade"));
        checkItem.setItemMeta(checkItemMeta);

        if(triggerBlockInventory.contains(checkItem)) {
            triggerEvent.setCancelled(true);
            System.out.println("[INFO]: Dispenser contains a Saw Blade but tried firing something else: " + triggerItem.getType());
            triggerItem = checkItem;
            System.out.println("[INFO] Replacing with: " + triggerItem);
        }
        var triggerItemMetadata = triggerItem.getItemMeta();

        BlockFace triggerBlockFacing = null;
        if(dispenserData instanceof org.bukkit.block.data.type.Dispenser){
            triggerBlockFacing = ((org.bukkit.block.data.type.Dispenser) dispenserData).getFacing();
        }



        //Exit if the item doesn't have the required metadata
        if(!(triggerItemMetadata.getCustomModelDataComponent().getStrings().contains("tree_cutter") && triggerItem.getType().equals(Material.DIAMOND_AXE))) { return; }
        else{
            triggerEvent.setCancelled(true);
            Block targetBlock = null;
            System.out.println("[INFO]: TreeCutterActivationListener has been called. Dispenser Facing: " + triggerBlockFacing);
            switch(triggerBlockFacing) {
                case NORTH -> targetBlock = triggerBlock.getRelative(BlockFace.NORTH);
                case SOUTH -> targetBlock = triggerBlock.getRelative(BlockFace.SOUTH);
                case EAST -> targetBlock = triggerBlock.getRelative(BlockFace.EAST);
                case WEST -> targetBlock = triggerBlock.getRelative(BlockFace.WEST);
                default -> throw new IllegalStateException("Invalid block facing: " + triggerBlockFacing);
            }
            System.out.println("[INFO]: Target Block: " + targetBlock.getType() + " at " + targetBlock.getLocation().toString());
            treeDestroy(missfired, triggerBlock, targetBlock);
        }
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

    public void treeDestroy(boolean misfired, Block dispenserBlock, Block bottomLog) {
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
        /*
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
        */
        // Destroy all collected blocks and add logs to dispenser inventory
        var dispenserState = (org.bukkit.block.Dispenser) dispenserBlock.getState();
        var dispenserInventory = dispenserState.getInventory();
        //Collect the blocks
        for (Block block : blocksToDestroy) {
            if (block.getType().toString().endsWith("_LOG")) {
                if (misfired) {
                    placeItem(dispenserInventory, new ItemStack(bottomLog.getType(), 2));
                    misfired = false;
                }
                placeItem(dispenserInventory, new ItemStack(bottomLog.getType(), 1));
            }
        }
        for (Block block : blocksToDestroy) {
            block.setType(Material.AIR);
        }
    }

    /**
     * Since addItem() doesn't feel like functioning, I am writing this function to replace it for this specific application.
     * @param dispenserInventory The inventory of the dispenser that items will be added to.
     * @param itemToAdd The item to be added to the dispenser inventory.
     * @return None.
     */
    void placeItem(Inventory dispenserInventory, ItemStack itemToAdd) {
        for(var slot: dispenserInventory.getStorageContents()) {
            int slotIndex = 0;
            if(slot == null) {
                dispenserInventory.setItem(slotIndex, itemToAdd);
                return;
            }
            else if(slot.getType() == itemToAdd.getType() && slot.getAmount() < slot.getMaxStackSize()) {
                slot.setAmount(slot.getAmount() + itemToAdd.getAmount());
                return;
            }
        }

    }
}
