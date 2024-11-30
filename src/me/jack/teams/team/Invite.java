package me.jack.teams.team;

import org.apache.logging.log4j.core.util.JsonUtils;

import java.util.UUID;

public class Invite {

    private UUID receiver;
    private long expiryTime;
    private Team team;

    public Invite(Team team, UUID receiver, long expiryTime) {
        this.team = team;
        this.receiver = receiver;
        this.expiryTime = getExpiryTime();
    }

    //testers
    public Team getTeam() {
        return this.team;
    }

    public UUID getReceiverUUID() {
        return this.receiver;
    }

    public long getExpiryTime() {
        return this.expiryTime;
    }
}
