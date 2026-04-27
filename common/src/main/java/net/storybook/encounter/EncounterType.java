package net.storybook.encounter;

/**
 * Preset encounter categories.
 * Used by the control panel to filter available encounters by type.
 */
public enum EncounterType {
    /** Standard fight-or-flight hostile mob group */
    COMBAT,

    /** Non-hostile surprise: animals, environmental events, etc. */
    AMBIENT,

    /** High-stakes single powerful mob */
    BOSS,

    /** Chase or flee scenario */
    CHASE,

    /** Discovery / puzzle encounter with no direct combat */
    DISCOVERY
}
