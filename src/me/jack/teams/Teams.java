package me.jack.teams;

import me.jack.teams.command.TeamCommand;
import me.jack.teams.listener.TabListener;
import me.jack.teams.listener.DamageListener;
import me.jack.teams.listener.JoinListener;
import me.jack.teams.team.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Teams extends JavaPlugin {

    private TeamHandler teamHandler;

    @Override
    public void onEnable() {
        teamHandler = new TeamHandler(this);

        registerListeners();
        registerCommands();

        teamHandler.loadMappings();

        for (Player pl : Bukkit.getOnlinePlayers()) {
            String teamName = teamHandler.getMappingMap().get(pl.getUniqueId());

            teamHandler.loadTeam(teamName);
        }
    }

    @Override
    public void onDisable() {

        teamHandler.writeMappings();
        teamHandler.writeTeams();
    }

    private void registerCommands() {
        getCommand("team").setExecutor(new TeamCommand(teamHandler));
        getCommand("team").setTabCompleter(new TabListener(teamHandler));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new DamageListener(teamHandler), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(teamHandler), this);
    }

}
