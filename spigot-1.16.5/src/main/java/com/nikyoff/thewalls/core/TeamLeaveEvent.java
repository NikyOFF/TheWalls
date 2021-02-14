package com.nikyoff.thewalls.core;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamLeaveEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final String entry;
    private final Team team;

    public TeamLeaveEvent(String entry, Team team) {
        this.entry = entry;
        this.team = team;
    }

    public String GetEntry() {
        return this.entry;
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
