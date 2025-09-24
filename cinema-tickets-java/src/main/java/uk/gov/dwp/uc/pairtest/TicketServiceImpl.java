package uk.gov.dwp.uc.pairtest;

import java.util.Arrays;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {

    private static final int MAX_TICKETS = 25;
    private static final int PRICE_ADULT_PENCE = 2500;
    private static final int PRICE_CHILD_PENCE = 1500;

    private final TicketPaymentService paymentService;
    private final SeatReservationService seatService;

    private TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService seatService) {
        this.paymentService = paymentService;
        this.seatService = seatService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        validateAccount(accountId);
        if (ticketTypeRequests == null || ticketTypeRequests.length == 0) {
            throw new InvalidPurchaseException("No ticket requests provided");
        }

        // For each ticket request add the amount of tickets.
        int totalTickets = Arrays.stream(ticketTypeRequests)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
        if (totalTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException("Cannot purchase more than " + MAX_TICKETS + " tickets at a time");
        }

        // Go through each ticket request and add up the totals per type.
        int adultCount = 0, childCount = 0, infantCount = 0;
        for (TicketTypeRequest req : ticketTypeRequests) {
            switch (req.getTicketType()) {
                case ADULT:
                    adultCount += req.getNoOfTickets();
                    break;
                case CHILD:
                    childCount += req.getNoOfTickets();
                    break;
                case INFANT:
                    infantCount += req.getNoOfTickets();
                    break;
            }
        }

        if ((childCount > 0 || infantCount > 0) && adultCount == 0) {
            throw new InvalidPurchaseException("Child and Infant tickets require at least one Adult ticket");
        }

        if (infantCount > adultCount) {
            throw new InvalidPurchaseException("Each infant must be accompanied by an adult");
        }

        int seatsToReserve = adultCount + childCount;
        int amountToPay = adultCount * PRICE_ADULT_PENCE + childCount * PRICE_CHILD_PENCE;

        if (amountToPay > 0) paymentService.makePayment(accountId, amountToPay);
        if (seatsToReserve > 0) seatService.reserveSeat(accountId, seatsToReserve);
    }

    private void validateAccount(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Invalid account id");
        }
    }
}
