package me.jack.teams.team;

import org.bukkit.Location;

import java.util.*;

public class Team {

    private UUID ownerUUID;
    private String name;
    private String password;
    private Location hq;
    private Set<Member> members;
    private Set<Invite> invites;
    private Set<UUID> teamChat;

    public Team(UUID ownerUUID, String name) {
        this(ownerUUID, name, null);
    }

    public Team(UUID ownerUUID, String name, String password) {
        this.ownerUUID = ownerUUID;
        this.name = name;
        this.password = password;
        this.members = new HashSet<>();
        this.invites = new HashSet<>();
        this.teamChat = new HashSet<>();

        addMember(new Member(ownerUUID, Rank.OWNER));
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public UUID setOwnerUUID(UUID uuid) {
        return this.ownerUUID = uuid;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public String setPassword(String password) {
        return this.password = password;
    }

    public Location getHQ() {
        return this.hq;
    }

    public Location setHQ(Location location) {
        return this.hq = location;
    }

    public Set<Member> getMembers() {
        return this.members;
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public void removeMember(Member member) {
        this.members.remove(member);
    }

    public Member getMember(UUID uuid) {
        for (Member member : members) {
            if (member.getUUID().equals(uuid)) {
                return member;
            }
        }
        return null;
    }

    public Set<Invite> getInvites() {
        return this.invites;
    }

    public void createInvite(Invite invite) {
        invites.add(invite);
    }

    public void removeInvite(Invite invite) {
        invites.remove(invite);
    }

    public Invite getInvite(UUID uuid) {
        for (Invite invite : invites) {
            if (invite.getReceiverUUID().equals(uuid)) {
                return invite;
            }
        }
        return null;
    }

    public Set<UUID> getTeamChat() {
        return this.teamChat;
    }

}
