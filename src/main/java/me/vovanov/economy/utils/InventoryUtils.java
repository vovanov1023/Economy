package me.vovanov.economy.utils;

import me.vovanov.economy.services.ConfigService;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static int removeCashFromInventory(Player player, int amount) {
        ConfigService config = ConfigService.getInstance();
        Material currencyMaterial = config.getCurrencyMaterial();
        Inventory inventory = player.getInventory();

        int cash = getCashAmount(player);
        if (cash < amount) return cash;

        int remainingToRemove = amount;

        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() != currencyMaterial) continue;
            if (item.getAmount() <= remainingToRemove) {
                remainingToRemove -= item.getAmount();
                inventory.setItem(i, null);
            } else {
                item.setAmount(item.getAmount() - remainingToRemove);
                break;
            }
        }

        return -1;
    }

    public static int getCashAmount(Player player) {
        ConfigService config = ConfigService.getInstance();
        Material currencyMaterial = config.getCurrencyMaterial();
        Inventory inventory = player.getInventory();

        int inventoryAmount = 0;
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() != currencyMaterial) continue;
            inventoryAmount += item.getAmount();
        }
        return inventoryAmount;
    }
}
