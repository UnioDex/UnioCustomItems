package me.uniodex.uniocustomitems.listeners;

import me.uniodex.uniocustomitems.CustomItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProtectionListeners implements Listener {

    private CustomItems plugin;

    public ProtectionListeners(CustomItems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() != 64537) {
            if (event.getInventory().getType() == InventoryType.ANVIL) {
                Player p = (Player) event.getWhoClicked();
                ItemStack item = event.getCurrentItem();
                if (item != null) {
                    for (ItemStack specialItem : plugin.getItemManager().getItems().values()) {
                        if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null || specialItem.getItemMeta() == null || specialItem.getItemMeta().getDisplayName() == null) {
                            continue;
                        }

                        if (item.getType().equals(specialItem.getType())) {
                            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(specialItem.getItemMeta().getDisplayName())) {
                                event.setCancelled(true);
                                p.sendMessage(plugin.getMessage("protections.cantPutToAnvil"));
                            }
                        }
                    }

                    if (item.getType().equals(Material.HOPPER) || item.getType().equals(Material.CHEST) || item.getType().equals(Material.TRAPPED_CHEST)) {
                        event.setCancelled(true);
                        p.sendMessage(plugin.getMessage("protections.cantPutToAnvil"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(plugin.getMessage("protections.droppedSpecialItem"));
            return;
        }

        if (item != null) {
            for (ItemStack specialItem : plugin.getItemManager().getItems().values()) {
                if (item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null || specialItem.getItemMeta() == null || specialItem.getItemMeta().getDisplayName() == null) {
                    continue;
                }

                if (item.getType().equals(specialItem.getType())) {
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(specialItem.getItemMeta().getDisplayName())) {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.sendMessage(plugin.getMessage("protections.droppedSpecialItem"));
                            return;
                        }
                        event.setCancelled(true);
                        player.sendMessage(plugin.getMessage("protections.youCantDropSpecialItems"));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        String bossOwnerName = plugin.getUnioProtections().getCheckManager().getBossOwnerManager().getBossOwner(entity.getUniqueId());
        if (bossOwnerName == null) return;

        Player bossOwner = Bukkit.getPlayerExact(bossOwnerName);
        if (bossOwner == null) return;

        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        for (ItemStack drop : drops) {
            Map<Integer, ItemStack> leftOvers = bossOwner.getInventory().addItem(drop);
            if (leftOvers.size() > 0) {
                event.getDrops().remove(drop);
                for (Integer index : leftOvers.keySet()) {
                    event.getDrops().add(leftOvers.get(index));
                }
                return;
            } else {
                event.getDrops().remove(drop);
            }
        }
    }
}
