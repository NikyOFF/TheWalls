package com.nikyoff.thewalls.core;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RoundEndEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Map currentMap;
    private final Team victoryTeam;

    public RoundEndEvent(Map currentMap, Team victoryTeam) {
        this.currentMap = currentMap;
        this.victoryTeam = victoryTeam;
    }

    public Map GetCurrentMap() {
        return this.currentMap;
    }

    public Team GetVictoryTeam() {
        return this.victoryTeam;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
