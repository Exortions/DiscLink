package com.exortions.disclink;

import com.exortions.disclink.listeners.CommandListener;
import com.exortions.disclink.listeners.EventListener;
import com.exortions.disclink.utilities.Config;
import com.exortions.pluginutils.plugin.MinecraftVersion;
import com.exortions.pluginutils.plugin.SpigotPlugin;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.JavaVersion;
import org.bukkit.scheduler.BukkitRunnable;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public final class DiscLink extends SpigotPlugin {

    @Getter
    private static DiscLink plugin;

    @Getter
    private static JDA jda;
    @Getter @Setter
    private static HashMap<User, Boolean> ratelimit;

    public long serverId;
    public long logChannelId;
    public long commandSenderRoleId;
    public long ratelimitBypassRoleId;

    public String prefix;

    public static MessageChannel channel;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        if (Config.getString("bot-token") == null || Config.getString("bot-token").equals("YOUR_BOT_TOKEN")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[DiscLink] Fatal Error: bot-token is null in config.yml, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        ratelimit = new HashMap<>();


        JDABuilder builder = JDABuilder.createDefault(Config.getString("bot-token"));

        builder.setBulkDeleteSplittingEnabled(false);
        switch (Config.getString("bot-status-type")) {
            case "watching":
                builder.setActivity(Activity.watching(Config.getString("bot-status")));
                break;
            case "listening":
                builder.setActivity(Activity.listening(Config.getString("bot-status")));
                break;
            default:
                builder.setActivity(Activity.playing(Config.getString("bot-status")));
        }

        try {
            jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[DiscLink] " + ChatColor.AQUA + "logged in with token " + jda.getToken());

        new BukkitRunnable() {
            @Override
            public void run() {
                checkConfig();
            }
        }.runTaskLater(this, 60L);

        registerListeners();
        registerCommands();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[DiscLink] " + ChatColor.AQUA + "plugin enabled!");
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[DiscLink] " + ChatColor.AQUA + "plugin disabled!");
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        jda.addEventListener(new CommandListener());
    }

    @Override
    public void registerCommands() {

    }

    @Override
    public String getPluginName() {
        return "DiscLink";
    }

    @Override
    public String getPluginVersion() {
        return "1.0.0-ALPHA";
    }

    @Override
    public MinecraftVersion getPluginMinecraftVersion() {
        return MinecraftVersion.MINECRAFT_1_16_5;
    }

    @Override
    public JavaVersion getJavaVersion() {
        return JavaVersion.JAVA_1_8;
    }

    private void checkConfig() {
        if (jda.getGuildById(Config.getLong("server-id")) == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[DiscLink] Fatal Error: server-id is null in config.yml, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            serverId = Config.getLong("server-id");
        }
        if (jda.getGuildChannelById(Config.getLong("log-channel-id")) == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[DiscLink] Fatal Error: log-channel-id is null in config.yml, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            logChannelId = Config.getLong("log-channel-id");
            channel = (MessageChannel) jda.getGuildChannelById(Config.getLong("log-channel-id"));
        }
        if (jda.getRoleById(Config.getLong("command-sender-role-id")) == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[DiscLink] Fatal Error: command-sender-role-id is null in config.yml, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            commandSenderRoleId = Config.getLong("command-sender-role-id");
        }
        if (jda.getRoleById(Config.getLong("ratelimit-bypass-role-id")) == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[DiscLink] Fatal Error: ratelimit-bypass-role-id is null in config.yml, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            ratelimitBypassRoleId = Config.getLong("ratelimit-bypass-role-id");
        }
        if (Config.getString("bot-prefix") == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[DiscLink] Fatal Error: bot-prefix is null in config.yml, disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            prefix = Config.getString("bot-prefix");
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[DiscLink] " + ChatColor.AQUA + "config loaded!");
    }

}
