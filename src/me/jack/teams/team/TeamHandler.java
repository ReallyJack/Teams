package me.jack.teams.team;

import me.jack.teams.Teams;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamHandler {

    private Teams instance;
    private Map<String, Team> teamMap;
    private Map<UUID, String> mappingMap;

    public TeamHandler(Teams instance) {
        this.instance = instance;
        this.teamMap = new HashMap<>();
        this.mappingMap = new HashMap<>();
    }

    public Map<String, Team> getTeamMap() {
        return this.teamMap;
    }

    public Map<UUID, String> getMappingMap() {
        return mappingMap;
    }

    public void createTeam(Team team) {
        teamMap.put(team.getName(), team);
    }

    public void removeTeam(Team team) {
        team.getMembers().clear();
        team.getInvites().clear();
        teamMap.remove(team.getName(), team);

        File dir = new File(instance.getDataFolder(), "teams");
        File file = new File(dir, team.getName() + ".yml");

        file.delete();
    }

    public Team getTeam(String name) {
        for (Team team : teamMap.values()) {
            if (team.getName().equals(name)) {
                return team;
            }
        }
        return null;
    }

    public Team getTeam(UUID uuid) {
        for (Team team : teamMap.values()) {
            for (Member member : team.getMembers()) {
                if (member.getUUID().equals(uuid)) {
                    return team;
                }
            }
        }
        return null;
    }

    public void writeMappings() {
        File file = new File(instance.getDataFolder(), "team_mapping.yml");

        if (!file.exists()) {
            instance.getDataFolder().mkdir();
            try {
                file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        if (teamMap == null) return;

        List<String> members = new ArrayList<>();

        for (Team team : teamMap.values()) {

            for (Member member : team.getMembers()) {

                members.add(member.getUUID().toString());

            }

            config.set(team.getName(), members);
        }

        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void loadMappings() {
        File file = new File(instance.getDataFolder(), "team_mapping.yml");

        if (!file.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String teamName : config.getKeys(false)) {

            for (String uuid : config.getStringList(teamName)) {

                mappingMap.put(UUID.fromString(uuid), teamName);
            }
        }

        System.out.println("mappings added to memory");
        file.delete();
    }


    public void loadTeam(String teamName) {
        if (teamMap.containsKey(teamName)) return;

        File dir = new File(instance.getDataFolder(), "teams");
        File yml = new File(dir, teamName + ".yml");

        if (!yml.exists()) return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(yml);

        UUID ownerUUID = UUID.fromString(config.getString("owner"));
        String password = config.getString("password");

        Team team = new Team(ownerUUID, teamName, password);

        ConfigurationSection memberSection = config.getConfigurationSection("members");

        for (String member : memberSection.getKeys(false)) {
            UUID uuid = UUID.fromString(member);
            Rank rank = Rank.valueOf(memberSection.getString(uuid + ".rank"));

            team.addMember(new Member(uuid, rank));
        }

        ConfigurationSection inviteSection = config.getConfigurationSection("invites");

        for (String invite : inviteSection.getKeys(false)) {
            if (invite == null) return;

            UUID uuid = UUID.fromString(invite);
            Team requestedTeam = getTeam(inviteSection.getString(uuid + ".team"));
            long expiry = Long.valueOf(inviteSection.getString(uuid + ".expiry"));

            team.createInvite(new Invite(requestedTeam, uuid, expiry));
        }

        teamMap.put(teamName, team);

    }

    public void writeTeams() {
        if (teamMap == null) return;

        for (Team team : teamMap.values()) {
            String teamName = team.getName();
            UUID ownerUUID = team.getOwnerUUID();
            Location hq = team.getHQ();
            String password = team.getPassword();

            File dir = new File(instance.getDataFolder(), "teams");
            File file = new File(dir, teamName + ".yml");

            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            config.set("owner", ownerUUID.toString());
            config.set("hq", hq != null ? hq.serialize() : "");
            config.set("password", password != null ? password : "");

            List<String> teamChatList = new ArrayList<>();

            for (UUID teamChatUUID : team.getTeamChat()) {
                teamChatList.add(teamChatUUID.toString());
            }

            config.set("teamchat", teamChatList);

            ConfigurationSection inviteSection = config.createSection("invites");

            for (Invite invite : team.getInvites()) {
                if (invite == null) return;

                UUID receiverUUID = invite.getReceiverUUID();
                String receiverUUIDasString = receiverUUID.toString();
                Team inviteTeam = invite.getTeam();
                long expiry = invite.getExpiryTime();

                inviteSection.set("invites." + receiverUUIDasString + ".team", inviteTeam.getName());
                inviteSection.set("invites." + receiverUUIDasString + ".expiry", Long.toString(expiry));
            }

            ConfigurationSection memberSection = config.createSection("members");

            for (Member member : team.getMembers()) {
                Rank rank = member.getRank();
                UUID memberUUID = member.getUUID();

                memberSection.set(memberUUID.toString() + ".rank", rank.name());
            }

            try {
                config.save(file);
            } catch (
                    IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}