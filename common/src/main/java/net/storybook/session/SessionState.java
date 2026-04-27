package net.storybook.session;

/**
 * Represents the current lifecycle state of a Storybook Engine session.
 * A session is one play day with your group — from fire-up to end credits.
 */
public enum SessionState {
    /**
     * No session is active. Server is idle or in prep mode.
     */
    IDLE,

    /**
     * Session is live. Players are active and the DM is running the story.
     */
    ACTIVE,

    /**
     * Hard pause: all players, entities, and time are frozen.
     * Used for cliffhangers and between-scene cuts.
     */
    PAUSED,

    /**
     * Cinematic pause: world is frozen except for whitelisted entities/players.
     * Used for NPC monologues, dramatic reveals, scripted sequences.
     */
    CINEMATIC
}
