package net.storybook.session;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

/**
 * Manages the active Storybook Engine session.
 *
 * One instance is alive per server lifetime. Use {@link #getInstance()} to access it.
 *
 * Responsibilities:
 *  - Track session state (idle / active / paused / cinematic)
 *  - Know who the DM is
 *  - Enforce pause/cinematic freeze on all relevant systems
 *  - Provide entry points for encounter, narration, and dialogue systems
 *
 * TODO: persist session state to disk so crashes don't lose progress
 */
public class StorySession {

    private static StorySession instance;

    private SessionState state = SessionState.IDLE;

    /**
     * The UUID of the DM player. Only this player can issue Storybook commands.
     * TODO: add a co-DM whitelist in future
     */
    private UUID dmUUID;

    /**
     * Set of entity UUIDs exempt from cinematic pause.
     * Populated by the DM at runtime via the control panel or command.
     */
    private final Set<UUID> cinematicWhitelist = new HashSet<>();

    private StorySession() {}

    public static StorySession getInstance() {
        if (instance == null) {
            instance = new StorySession();
        }
        return instance;
    }

    // -------------------------------------------------------------------------
    // Session lifecycle
    // -------------------------------------------------------------------------

    /**
     * Start a new session. Transitions state from IDLE to ACTIVE.
     *
     * @param dm the player who will act as DM for this session
     */
    public void startSession(ServerPlayerEntity dm) {
        this.dmUUID = dm.getUuid();
        this.state = SessionState.ACTIVE;
        cinematicWhitelist.clear();
        // TODO: broadcast "session started" narration to all players
        // TODO: load session preset if one is configured
    }

    /**
     * End the session cleanly. Resumes the world if paused, then transitions to IDLE.
     */
    public void endSession(MinecraftServer server) {
        if (state == SessionState.PAUSED || state == SessionState.CINEMATIC) {
            resumeWorld(server);
        }
        this.state = SessionState.IDLE;
        this.dmUUID = null;
        // TODO: broadcast "session ended" narration
        // TODO: export session log
    }

    // -------------------------------------------------------------------------
    // Pause / resume
    // -------------------------------------------------------------------------

    /**
     * Hard pause: freeze all players and stop time.
     * Every player, entity, and projectile freezes in place.
     */
    public void hardPause(MinecraftServer server) {
        if (state != SessionState.ACTIVE) return;
        state = SessionState.PAUSED;
        freezeAllPlayers(server);
        // TODO: freeze server tick time (gamerule freeze or tick-rate control)
        // TODO: broadcast dramatic pause sound/effect
    }

    /**
     * Cinematic pause: freeze all except the current cinematic whitelist.
     */
    public void cinematicPause(MinecraftServer server) {
        if (state != SessionState.ACTIVE) return;
        state = SessionState.CINEMATIC;
        freezeAllPlayers(server);
        // TODO: freeze tick except for whitelisted entities
    }

    /**
     * Resume from any paused state back to ACTIVE.
     */
    public void resumeWorld(MinecraftServer server) {
        if (state == SessionState.IDLE || state == SessionState.ACTIVE) return;
        unfreezeAllPlayers(server);
        state = SessionState.ACTIVE;
        // TODO: restore tick rate
        // TODO: broadcast resume effect
    }

    // -------------------------------------------------------------------------
    // Cinematic whitelist
    // -------------------------------------------------------------------------

    public void addCinematicActor(UUID entityUUID) {
        cinematicWhitelist.add(entityUUID);
    }

    public void removeCinematicActor(UUID entityUUID) {
        cinematicWhitelist.remove(entityUUID);
    }

    public boolean isCinematicActor(UUID entityUUID) {
        return cinematicWhitelist.contains(entityUUID);
    }

    // -------------------------------------------------------------------------
    // DM checks
    // -------------------------------------------------------------------------

    public boolean isDM(ServerPlayerEntity player) {
        return player.getUuid().equals(dmUUID);
    }

    public SessionState getState() {
        return state;
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void freezeAllPlayers(MinecraftServer server) {
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (dmUUID != null && p.getUuid().equals(dmUUID)) continue;
            // TODO: apply freeze effect (velocity zero, input disabled) to p
            // Options: status effect, packet manipulation, or custom freeze flag
        }
    }

    private void unfreezeAllPlayers(MinecraftServer server) {
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            // TODO: remove freeze effect from p
        }
    }
}
