package uk.gov.dwp.uc.pairtest.exception;

public class InvalidTicketTypeRequestException extends RuntimeException {
    public InvalidTicketTypeRequestException(String message) {
        super(message);
    }
}
