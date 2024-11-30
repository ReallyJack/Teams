package me.jack.teams.listener;

import me.jack.teams.team.Team;
import me.jack.teams.team.TeamHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    private TeamHandler teamHandler;

    public JoinListener(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        String teamName = teamHandler.getMappingMap().get(uuid);

        if (teamName == null) return;

        teamHandler.loadTeam(teamName);
    }
}
