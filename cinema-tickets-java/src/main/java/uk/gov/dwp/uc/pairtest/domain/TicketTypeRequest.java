package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.exception.InvalidTicketTypeRequestException;

/**
 * Immutable Object
 */

public class TicketTypeRequest {

    private final int noOfTickets;
    private final Type type;

    public TicketTypeRequest(Type type, int noOfTickets) {
        if (type == null) throw new InvalidTicketTypeRequestException("TicketType cannot be null");
        if (noOfTickets < 1) throw new InvalidTicketTypeRequestException("Quantity must be >= 1");
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        ADULT, CHILD , INFANT
    }
}
