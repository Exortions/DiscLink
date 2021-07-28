package com.exortions.disclink.listeners;

import com.exortions.disclink.DiscLink;
import com.exortions.disclink.utilities.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.HashMap;

@SuppressWarnings("ConstantConditions")
public class CommandListener extends ListenerAdapter {
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getContentRaw();
        MessageChannel channel = event.getChannel();

        DiscLink link = DiscLink.getPlugin();
        JDA jda = DiscLink.getJda();

        Member member = jda.getGuildById(link.serverId).getMember(event.getAuthor());

        Role bypass = jda.getRoleById(link.ratelimitBypassRoleId);
        Role cmdRole = jda.getRoleById(link.commandSenderRoleId);

        if (channel.getIdLong() != link.logChannelId) return;
        if (content.equals(link.prefix + "playersonline")) {
            if (Config.getBoolean("online-players-command")) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.CYAN);
                builder.addField("Player Currently Online:", "" + Bukkit.getOnlinePlayers().size(), false);
                builder.setFooter("Tip: Use !listplayers to list all players online.");
                channel.sendMessage(builder.build()).queue();
            }
        } else if (content.equals(link.prefix + "listplayers")) {
            if (Config.getBoolean("list-players-command")) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.CYAN);
                builder.setDescription("Player Currently Online: (" + Bukkit.getOnlinePlayers().size() + ")");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    builder.addField("", " - " + player.getDisplayName(), true);
                }
                channel.sendMessage(builder.build()).queue();
            }
        } else if (content.equals(link.prefix + "help")) {
            if (Config.getBoolean("help-command")) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.YELLOW);
                builder.setDescription("Below are a list of all of the commands for this bot: ");
                builder.addField("Prefix", link.prefix, false);
                builder.addField("Players Online", "Command: " + link.prefix + "playersonline" + "\nDescription: Tells the sender how many players are online.\nRole: none\nEnabled: " + Config.getBoolean("online-players-command"), false);
                builder.addField("List Players", "Command: " + link.prefix + "listplayers" + "\nDescription: Lists the amount of players online.\nRole: none\nEnabled: " + Config.getBoolean("list-players-command"), false);
                builder.addField("Minecraft Command", "Command: /<minecraftcommand> <arguments>\nDescription: runs a command from the server console.\nRole: " + jda.getRoleById(link.commandSenderRoleId).getName(), false);
                channel.sendMessage(builder.build()).queue();
            }
        } else if (content.startsWith("/")) {
            if (Config.getBoolean("command-dispatching")) {
                if (member.getRoles().contains(cmdRole)) {
                    String cmd = content.substring(1);
                    Bukkit.getScheduler().callSyncMethod(DiscLink.getPlugin(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.GREEN);
                    builder.addField("Command Dispatched", "Command Successfully dispatched:\n" + cmd, false);
                    channel.sendMessage(builder.build()).queue();
                } else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.RED);
                    builder.addField("Command Failed", "You don't have permission to dispatch commands!", false);
                    channel.sendMessage(builder.build()).queue();
                }
            }
        } else {
            if (Config.getBoolean("discord-to-minecraft-chat")) {
                if (Config.getBoolean("ratelimit")) {
                    HashMap<User, Boolean> ratelimit = DiscLink.getRatelimit();
                    if (!ratelimit.containsKey(event.getAuthor())) {
                        ratelimit.put(event.getAuthor(), true);
                    }
                    if (!ratelimit.get(event.getAuthor()) && !member.getRoles().contains(bypass)) {
                        message.delete().queue();
                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(Color.RED);
                        builder.addField(Config.getString("ratelimit-embed-title"), Config.getString("ratelimit-embed-description"), false);
                        channel.sendMessage(builder.build()).queue();
                        return;
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String msg = Config.getString("discord-to-minecraft-chat-format").replaceAll("%discord_name%", event.getAuthor().getName()).replaceAll("%discord_tag%", event.getAuthor().getAsTag()).replaceAll("%content%", content);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                        ratelimit.put(event.getAuthor(), false);
                        DiscLink.setRatelimit(ratelimit);
                    }
                    ratelimit = DiscLink.getRatelimit();
                    HashMap<User, Boolean> finalRatelimit = ratelimit;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            finalRatelimit.put(event.getAuthor(), true);
                            DiscLink.setRatelimit(finalRatelimit);
                        }
                    }.runTaskLater(link, Config.getInt("ratelimit-limit") * 20L);
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String msg = Config.getString("discord-to-minecraft-chat-format").replaceAll("%discord_name%", event.getAuthor().getName()).replaceAll("%discord_tag%", event.getAuthor().getAsTag()).replaceAll("%content%", content);
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
                    }
                }
            }
        }
    }


}
