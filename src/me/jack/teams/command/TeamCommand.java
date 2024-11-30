package me.jack.teams.command;

import me.jack.teams.team.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private TeamHandler teamHandler;

    public TeamCommand(TeamHandler teamHandler) {
        this.teamHandler = teamHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Incorrect usage! /team help");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {

            if (args.length < 2) {
                player.sendMessage("specify a name!");
                return true;
            }

            String name = args[1];

            if (teamHandler.getTeam(player.getUniqueId()) != null) {
                player.sendMessage("you are in a team already!");
                return true;
            }

            if (teamHandler.getTeam(name) != null) {
                player.sendMessage("team exists!");
                return true;
            }

            if (args.length < 3) {//no password
                player.sendMessage("team " + name + " created");

                Team team = new Team(player.getUniqueId(), name);
                teamHandler.createTeam(team);

                return true;
            }

            if (args.length > 3) {
                player.sendMessage("passwords can not have spaces!");
                return true;
            }

            String password = args[2];

            player.sendMessage("team " + name + " created");

            Team team = new Team(player.getUniqueId(), name, password);
            teamHandler.createTeam(team);

        } else if (args[0].equalsIgnoreCase("join")) {

            if (args.length < 2) {
                player.sendMessage("specify a name!");
                return true;
            }

            String name = args[1];

            if (teamHandler.getTeam(player.getUniqueId()) != null) {
                player.sendMessage("you are in a team already!");
                return true;
            }

            if (teamHandler.getTeam(name) == null) {
                player.sendMessage("team does not exist!");
                return true;
            }

            Team team = teamHandler.getTeam(name);

            if (args.length < 3) {//no password entered

                Invite invite = team.getInvite(player.getUniqueId());

                if (team.getPassword() != null) {//team requires password unless invite check invites

                    if (invite == null) {//player does not have an invite

                        player.sendMessage("enter the team password!");

                    } else {//player has invite

                        player.sendMessage("welcome to the team");

                        Member member = new Member(player.getUniqueId(), Rank.DEFAULT);

                        team.addMember(member);
                        team.removeInvite(invite);
                    }

                } else {//team doesnt require password check invites anyway

                    player.sendMessage("welcome to the team");

                    Member member = new Member(player.getUniqueId(), Rank.DEFAULT);

                    team.addMember(member);

                    if (invite != null) {
                        team.removeInvite(invite);
                    }
                }

            } else {//password entered check for spaces

                if (args.length > 3) {
                    player.sendMessage("passwords can not have spaces!");
                    return true;
                }

                Invite invite = team.getInvite(player.getUniqueId());

                if (team.getPassword() == null) {//team does not reqire a password add member regardless

                    player.sendMessage("welcome to the team");

                    Member member = new Member(player.getUniqueId(), Rank.DEFAULT);

                    team.addMember(member);

                    if (invite != null) {
                        team.removeInvite(invite);
                    }

                } else {//team does require password, check if player has an invite if they get it wrong

                    String password = args[2];

                    if (team.getPassword().equals(password)) {

                        player.sendMessage("welcome to the team!");

                        Member member = new Member(player.getUniqueId(), Rank.DEFAULT);

                        team.addMember(member);

                        if (invite != null) {
                            team.removeInvite(invite);
                        }

                    } else {//wrong password check invites

                        if (invite == null) {

                            player.sendMessage("incorrect password!");

                        } else {

                            player.sendMessage("welcome to the team!");

                            Member member = new Member(player.getUniqueId(), Rank.DEFAULT);

                            team.addMember(member);
                            team.removeInvite(invite);
                        }
                    }
                }
            }


        } else if (args[0].equalsIgnoreCase("leave")) {

            if (args.length > 1) {
                player.sendMessage("incorrect usage!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (rank == Rank.OWNER) {

                if (team.getMembers().size() == 1) {
                    player.sendMessage("you have disband the team!");

                    teamHandler.removeTeam(team);

                } else {

                    player.sendMessage("you are the owner you must promote another member!");
                }

            } else {

                player.sendMessage("you have left the team!");

                team.removeMember(member);
            }

        } else if (args[0].equalsIgnoreCase("invite")) {

            if (args.length < 2) {
                player.sendMessage("specify a player!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (rank == Rank.DEFAULT) {
                player.sendMessage("you do not have permission to use this command!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("player not found or does not exist!");
                return true;
            }

            if (team.getInvite(target.getUniqueId()) != null) {
                player.sendMessage("you already have a invite sent to this player!");
                return true;
            }

            Invite invite = new Invite(team, target.getUniqueId(), 0);
            team.createInvite(invite);

            player.sendMessage("invite sent to " + target.getName());
            target.sendMessage("invite received from " + player.getName());

        } else if (args[0].equalsIgnoreCase("sethq")) {

            if (args.length > 1) {
                player.sendMessage("incorrect usage!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (rank == Rank.DEFAULT) {
                player.sendMessage("you do not have permission to use this command!");
                return true;
            }

            player.sendMessage("team HQ set");

            Location loc = player.getLocation();

            team.setHQ(loc);

        } else if (args[0].equalsIgnoreCase("delhq")) {

            if (args.length > 1) {
                player.sendMessage("incorrect usage!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (rank == Rank.DEFAULT) {
                player.sendMessage("you do not have permission to use this command!");
                return true;
            }

            if (team.getHQ() == null) {
                player.sendMessage("nothing changed! there is no HQ set!");
                return true;
            }

            player.sendMessage("team HQ removed");

            team.setHQ(null);

        } else if (args[0].equalsIgnoreCase("hq")) {

            if (args.length > 1) {
                player.sendMessage("incorrect usage!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }

            if (team.getHQ() == null) {
                player.sendMessage("there is no team HQ set");
                return true;
            }

            Location hq = team.getHQ();

            player.sendMessage("teleporting to team HQ");

            player.teleport(hq);

        } else if (args[0].equalsIgnoreCase("chat")) {

            if (args.length > 1) {
                player.sendMessage("incorrect usage!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }
            //todo

        } else if (args[0].equalsIgnoreCase("setpass")) {

            if (args.length < 2) {
                player.sendMessage("specify a password");
                return true;
            }

            if (args.length > 2) {
                player.sendMessage("passwords can not have spaces!");
                return true;
            }

            String password = args[1];

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (rank == Rank.DEFAULT) {
                player.sendMessage("You do not have permission to use this command!");
                return true;
            }

            player.sendMessage("you changed the password to " + password);
            team.setPassword(password);

        } else if (args[0].equalsIgnoreCase("delpass")) {

            if (args.length > 1) {
                player.sendMessage("incorrect usage!");
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (rank == Rank.DEFAULT) {
                player.sendMessage("you do not have permission to use this command!");
                return true;
            }

            if (team.getPassword() == null) {
                player.sendMessage("the team does not have a password!");
                return true;
            }

            player.sendMessage("password removed!");

            team.setPassword(null);

        } else if (args[0].equalsIgnoreCase("promote")) {

            if (args.length < 2) {
                player.sendMessage("specify a player!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (rank != Rank.OWNER) {
                player.sendMessage("you do not have permission to use this command!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("player not online or does not exist!");
                return true;
            }

            Member teamMember = team.getMember(target.getUniqueId());

            if (teamMember == null) {
                player.sendMessage("player is not in this team!");
                return true;
            }

            if (teamMember.getRank() != Rank.DEFAULT) {
                player.sendMessage("player is already promoted!");
                return true;
            }

            player.sendMessage("player promoted!");

            teamMember.setRank(Rank.ADMIN);

            target.sendMessage("you have been promoted!");

        } else if (args[0].equalsIgnoreCase("demote")) {

            if (args.length < 2) {
                player.sendMessage("specify a player!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("You are not in a team!");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (rank != Rank.OWNER) {
                player.sendMessage("you do not have permission to use this command!");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                player.sendMessage("player not online or does not exist!");
                return true;
            }

            Member teamMember = team.getMember(target.getUniqueId());

            if (teamMember == null) {
                player.sendMessage("this player is not in your team!");
                return true;
            }

            if (teamMember.getRank() == Rank.DEFAULT) {
                player.sendMessage("this player has no rank!");
                return true;
            }

            player.sendMessage("player demoted!");

            teamMember.setRank(Rank.DEFAULT);

        } else if (args[0].equalsIgnoreCase("setowner")) {

            if (args.length < 2) {
                player.sendMessage("specify a team member!");
                return true;
            }

            Team team = teamHandler.getTeam(player.getUniqueId());

            if (team == null) {
                player.sendMessage("you are not in a team!");
                return true;
            }

            Member member = team.getMember(player.getUniqueId());
            Rank rank = member.getRank();

            if (team.getOwnerUUID().equals(player.getUniqueId())) {

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage("player not online or does not exist!");
                    return true;
                }

                if (team.getMember(target.getUniqueId()) == null) {
                    player.sendMessage("player is not in your team!");
                    return true;
                }

                Member teamMember = team.getMember(target.getUniqueId());

                System.out.println(team.getOwnerUUID());
                System.out.println(member.getRank());
                System.out.println(teamMember.getRank());

                player.sendMessage("you have updated the team owner!");

                member.setRank(Rank.DEFAULT);

                team.setOwnerUUID(teamMember.getUUID());
                teamMember.setRank(Rank.OWNER);

                target.sendMessage("you have been promoted to team Owner!");

                System.out.println(team.getOwnerUUID());
                System.out.println(member.getRank());
                System.out.println(teamMember.getRank());


            } else {

                player.sendMessage("you do not have permission to use this command!");

            }

        } else if (args[0].equalsIgnoreCase("info")) {

        } else {

            //todo help
            player.sendMessage("Incorrect usage! /team help");
        }

        return true;
    }
}
