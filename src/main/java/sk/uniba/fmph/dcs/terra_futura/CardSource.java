package sk.uniba.fmph.dcs.terra_futura;

/**
 * Identifies a card by its deck and its index within that deck.
 */
public record CardSource(Deck deck, int index) { }