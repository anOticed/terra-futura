package sk.uniba.fmph.dcs.terra_futura;

/**
 * Identifies a card by its deck and its index within that deck.
 *
 * @param deck  deck containing the card
 * @param index zero-based index of the card within the deck
 */
public record CardSource(Deck deck, int index) { }
