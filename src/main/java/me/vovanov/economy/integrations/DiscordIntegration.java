package me.vovanov.economy.integrations;

import github.scarsz.discordsrv.DiscordSRV;
import me.vovanov.economy.Economy;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Represents a Class containing methods needed for Discord integration
 */
public class DiscordIntegration {
    private TextChannel channel;
    private JDA bot;
    private final ConcurrentHashMap<UUID, String> discordUsers = new ConcurrentHashMap<>();

    private static DiscordIntegration instance;

    /**
     * Pretty straightforward. Initializes the integration.
     * Creates a JDA instance, gets a logging channel, etc.
     */
    public void initialiseBot(@NotNull String token, long id) {

        createBot(token);
        awaitReady();
        setupLoggingChannel(id);

    }

    private void createBot(String token) {
        Economy plugin = Economy.getInstance();
        try {
            if (bot == null) {
                bot = JDABuilder.createDefault(token)
                        .enableIntents(GatewayIntent.DIRECT_MESSAGES)
                        .build();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create the bot: "+e.getMessage());
        }
    }

    private void awaitReady() {
        Economy plugin = Economy.getInstance();
        try {
            bot.awaitReady();
        } catch (InterruptedException e) {
            plugin.getLogger().severe( "Something interrupted the bot from initialising\nCause: "+e.getCause());
        }
    }

    private void setupLoggingChannel(long id) {
        Economy plugin = Economy.getInstance();
        channel = bot.getTextChannelById(id);
        if (channel == null) {
            plugin.getLogger().warning("No such channel with id: "+id);
            return;
        }
        plugin.getLogger().info("Economy logging channel: "+channel.getName()+" ("+channel.getId()+")");
    }

    /**
     * Returns the Discord integration instance
     * @return DiscordIntegration instance
     */
    public static DiscordIntegration getInstance() {
        if (instance == null) instance = new DiscordIntegration();
        return instance;
    }

    /**
     * Sends a message to log channel stated in the config
     * if the channel exists
     * @param message the message to log
     */
    public void sendToLogChannel(@NotNull String message) {
        if (channel != null) channel.sendMessage(message).queue();
    }

    /**
     * Sends a direct message to a Discord user associated with this player.
     * This method uses DiscordSRV API to get linked players
     * @param message the message to send
     * @param player the player to send the message to
     */
    public void sendDirect(@NotNull String message, @NotNull OfflinePlayer player) {
        if (bot == null) return;
        UUID playerUUID = player.getUniqueId();

        String cachedId = discordUsers.get(playerUUID);
        if (cachedId != null) {
            sendDirectToUser(cachedId, message);
            return;
        }

        cacheDiscordUser(playerUUID, userID -> {
            if (userID != null) {
                sendDirectToUser(userID, message);
            }
        });
    }

    private void sendDirectToUser(String userId, String message) {
        Economy plugin = Economy.getInstance();
        bot.retrieveUserById(userId).queue(user -> user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue(
                                success -> {},
                                error -> plugin.getLogger().warning("Couldn't send PM (User ID: "+userId+"): " + error.getMessage())),
                        error -> plugin.getLogger().warning("Couldn't open PM (User ID: "+userId+"): " + error.getMessage())),
                error -> plugin.getLogger().warning("Couldn't find the user (User ID: "+userId+"): " + error.getMessage()));
    }

    private void cacheDiscordUser(UUID playerUUID, Consumer<String> callback) {
        Economy plugin = Economy.getInstance();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.complete(DiscordSRV.getPlugin()
                    .getAccountLinkManager()
                    .getDiscordId(playerUUID));
            future.thenAccept(userID -> {
                if (userID != null) {
                    discordUsers.put(playerUUID, userID);
                    callback.accept(userID);
                }
            }).exceptionally(e -> {
                plugin.getLogger().warning("Couldn't get the Discord ID (Player UUID: "+playerUUID+"): " + e.getMessage());
                return null;
            });
        });
    }

    /**
     * Shuts down the JDA instance associated with this DiscordIntegration.
     */
    public void shutDownJDA() {
        if (bot == null) return;
        Economy plugin = Economy.getInstance();
        plugin.getLogger().info("Shutting down the bot");
        bot.shutdown();
        try {
            if (!bot.awaitShutdown(Duration.ofSeconds(10))) {
                bot.shutdownNow();
                bot.awaitShutdown();
            }
            plugin.getLogger().info("Bot successfully shut down");
        } catch (InterruptedException e) {
            plugin.getLogger().severe("Something interrupted the bot from shutting down!\nCause: "+e.getCause());
        }
        bot = null;
    }
}
