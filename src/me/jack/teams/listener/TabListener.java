package me.jack.teams.listener;

import me.jack.teams.team.Rank;
import me.jack.teams.team.Team;
import me.jack.teams.team.TeamHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabListener implements TabCompleter {

    private TeamHandler teamHandler;

    public TabListener(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> commands = new ArrayList<>();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Team team = teamHandler.getTeam(player.getUniqueId());
            Rank rank = team.getMember(player.getUniqueId()).getRank();

            if (team == null) {
                commands.add("help");
                commands.add("create");
                commands.add("join");
                commands.add("info");

            } else {

                commands.add("leave");
                commands.add("hq");
                commands.add("teamchat");

                if (rank == Rank.ADMIN) {
                    commands.add("sethq");
                    commands.add("delhq");
                    commands.add("setpass");
                    commands.add("delpass");
                    commands.add("promote");
                    commands.add("kick");

                    if (rank == Rank.OWNER) {
                        commands.add("demote");
                        commands.add("setowner");
                    }
                }
            }
        }

        return commands;
    }
}
