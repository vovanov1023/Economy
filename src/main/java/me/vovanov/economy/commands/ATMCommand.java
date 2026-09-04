package me.vovanov.economy.commands;

import me.vovanov.economy.api.EconomyAPI;
import me.vovanov.economy.model.ATM;
import me.vovanov.economy.services.ConfigService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Stream;

public class ATMCommand implements CommandExecutor, TabCompleter {
    private final EconomyAPI api;

    public ATMCommand(EconomyAPI api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create" -> onCreate(sender, args);
            case "remove" -> onRemove(sender, args);
            case "list" -> onList(sender, args);
            default -> sendHelp(sender);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Stream.of("create", "remove", "list", "help").filter(arg -> arg.startsWith(args[0].toLowerCase())).toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return api.getATMList().stream().map(atm -> String.valueOf(atm.id())).toList();
        }
        return List.of();
    }

    private void onCreate(CommandSender sender, String[] args) {
        int x;
        int y;
        int z;
        String worldName;
        if (args.length == 1) {
            if (sender instanceof Player player) {
                Block lookingBlock = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
                if (lookingBlock == null || lookingBlock.getType() != ConfigService.getInstance().getAtmBlock()) {
                    player.sendMessage("You're not looking at an ATM block! (" + ConfigService.getInstance().getAtmBlock() + ")");
                    return;
                }
                Location lookingLocation = lookingBlock.getLocation();
                x = lookingLocation.getBlockX();
                y = lookingLocation.getBlockY();
                z = lookingLocation.getBlockZ();
                worldName = lookingLocation.getWorld().getName();
            } else {
                sender.sendMessage("Not enough arguments");
                return;
            }
        } else if (args.length >= 5) {
            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
                worldName = args[4];
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid value");
                return;
            }
        } else {
            sender.sendMessage("Not enough arguments");
            return;
        }

        if (api.doesATMExist(x, y, z, worldName)) {
            sender.sendMessage("There's already an ATM here!");
            return;
        }

        ATM newATM = api.createATM(x, y, z, worldName);
        if (newATM == null) {
            sender.sendMessage("Couldn't create an ATM! (ask admins for help)");
            return;
        }
        sender.sendMessage("Successfully created an ATM! (ID: " + newATM.id() + ")");
    }

    private void onRemove(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Not enough arguments ");
                return;
            }

            Block block = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
            if (block == null) {
                player.sendMessage("You are not looking at a block!");
                return;
            }

            ATM targetAtm = api.getATMList().stream()
                    .filter(atm -> atm.x() == block.getX() &&
                            atm.y() == block.getY() &&
                            atm.z() == block.getZ() &&
                            atm.worldName().equals(block.getWorld().getName()))
                    .findFirst()
                    .orElse(null);

            if (targetAtm == null) {
                player.sendMessage("There is no ATM at the block you are looking at.");
                return;
            }

            api.removeATM(targetAtm.id());
            player.sendMessage("Successfully removed ATM (ID: " + targetAtm.id() + ")");

        } else if (args.length == 2) {
            try {
                int id = Integer.parseInt(args[1]);
                if (!api.doesATMExist(id)) {
                    sender.sendMessage("ATM with ID " + id + " does not exist.");
                    return;
                }
                api.removeATM(id);
                sender.sendMessage("Successfully removed ATM (ID: " + id + ")");
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid ID");
            }
        } else {
            sender.sendMessage("Nut inaf orgomeents");
        }
    }

    private void onList(CommandSender sender, String[] args) {

        List<ATM> list = api.getATMList();
        int page;
        try {
            page = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid value");
            return;
        }

        if (list.isEmpty()) {
            sender.sendMessage("Список банкоматов пуст.");
            return;
        }
        int pageSize = 5;
        int maxPages = (int) Math.ceil((double) list.size() / pageSize);
        if (page < 1 || page > maxPages) {
            sender.sendMessage(Component.text("No such page (Max: " + maxPages + ")"));
            return;
        }

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, list.size());
        List<ATM> listToIterate = list.subList(start, end);

        sender.sendMessage("- ATM List -");
        for (ATM atm : listToIterate) {
            sender.sendMessage(
                    Component.text("["+atm.id()+"] x: "+atm.x()+" y: "+atm.y()+" z: "+atm.z()+" ("+atm.worldName()+") ")
                    .append(Component.text("[X]")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/atm remove "+atm.id()))
                            .hoverEvent(HoverEvent.showText(Component.text("Remove this ATM")))
                    )
            );
        }

        int nextPage = page + 1;
        int prevPage = page - 1;

        Component prevButton = Component.text("[<-] ")
                .hoverEvent(HoverEvent.showText(Component.text("Previous page")))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/atm list "+(page-1)));
        Component nextButton = Component.text(" [->]")
                .hoverEvent(HoverEvent.showText(Component.text("Next page")))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/atm list "+(page+1)));

        TextComponent.Builder footer = Component.text();
        if (prevPage > 0) footer.append(prevButton);
        footer.append(Component.text("Page "+page+"/"+maxPages));
        if (nextPage < maxPages) footer.append(nextButton);

        sender.sendMessage(footer);

    }

    private void sendHelp(CommandSender sender) {
        // TODO
    }
}
