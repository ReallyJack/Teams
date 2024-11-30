package me.jack.teams.team;

import java.util.UUID;

public class Member {

    private UUID uuid;
    private Rank rank;

    public Member(UUID uuid, Rank rank) {
        this.uuid = uuid;
        this.rank = rank;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Rank getRank() {
        return this.rank;
    }

    public Rank setRank(Rank rank) {
        return this.rank = rank;
    }

}
