package com.exortions.disclink.listeners;

import com.exortions.disclink.DiscLink;
import com.exortions.disclink.utilities.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Config.getBoolean("log-join-leave")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.GREEN);
            builder.addField(Config.getString("join-embed-title").replaceAll("%player%", e.getPlayer().getDisplayName()),
                    Config.getString("join-embed-description").replaceAll("%player%", e.getPlayer().getDisplayName()), false);
            DiscLink.channel.sendMessage(builder.build()).queue();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (Config.getBoolean("log-join-leave")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.addField(Config.getString("leave-embed-title").replaceAll("%player%", e.getPlayer().getDisplayName()),
                    Config.getString("leave-embed-description").replaceAll("%player%", e.getPlayer().getDisplayName()), false);
            DiscLink.channel.sendMessage(builder.build()).queue();
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (Config.getBoolean("log-chat-messages")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.ORANGE);
            builder.addField(Config.getString("chat-embed-title").replaceAll("%player%", e.getPlayer().getDisplayName()),
                Config.getString("chat-embed-description").replaceAll("%player%", e.getPlayer().getDisplayName()).
                replaceAll("%message%", e.getMessage()), false);
            DiscLink.channel.sendMessage(builder.build()).queue();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (Config.getBoolean("log-player-death")) {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.RED);
            builder.addField(Config.getString("death-embed-title").replaceAll("%player%", e.getEntity().getDisplayName()), Config.getString("death-embed-description").replaceAll("%message%", Objects.requireNonNull(e.getDeathMessage())), false);
            DiscLink.channel.sendMessage(builder.build()).queue();
        }
    }

}
