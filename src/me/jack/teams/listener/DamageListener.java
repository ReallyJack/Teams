package me.jack.teams.listener;

import me.jack.teams.team.Member;
import me.jack.teams.team.Team;
import me.jack.teams.team.TeamHandler;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    private TeamHandler teamHandler;

    public DamageListener(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getEntity();
        Entity d = event.getDamager();

        if (!(e instanceof Player && d instanceof Player)) return;

        Player player = (Player) e;
        Player damager = (Player) d;

        Team team = teamHandler.getTeam(damager.getUniqueId());
        Member member = team.getMember(player.getUniqueId());

        if (member != null) {
            event.setCancelled(true);
            return;
        }
    }
}
