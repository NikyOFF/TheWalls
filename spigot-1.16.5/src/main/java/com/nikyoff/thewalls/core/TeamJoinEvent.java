package com.nikyoff.thewalls.core;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamJoinEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Team team;

    public TeamJoinEvent(Player player, Team team) {
        this.player = player;
        this.team = team;
    }

    public Player GetPlayer() {
        return this.player;
    }

    public Team GetTeam() {
        return this.team;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
