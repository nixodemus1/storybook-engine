package net.storybook.narration;

/**
 * Visual and tonal style for a DM narration message.
 *
 * Controls how the message appears in chat/screen so players can tell at a
 * glance whether it is ambient flavour, urgent action, or system info.
 */
public enum NarrationStyle {
    /**
     * Story/atmosphere text. Appears in a distinct colour to signal "narrator voice".
     * Use for scene-setting, ambience, and descriptive prose.
     */
    STORY,

    /**
     * Urgent event text. Used for sudden action, warnings, or dramatic reveals.
     */
    DRAMATIC,

    /**
     * Quiet ambient flavour. Subtle, no fanfare. Background details / environmental hints.
     */
    AMBIENT,

    /**
     * DM system message. Mechanical info (e.g., "session starting", "save point reached").
     */
    SYSTEM
}
