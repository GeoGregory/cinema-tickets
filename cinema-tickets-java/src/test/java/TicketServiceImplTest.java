import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.exception.InvalidTicketTypeRequestException;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {

    private final Long VALID_ID = 1L;
    private final Long INVALID_ID = 0L;

    @Mock
    private TicketPaymentService paymentService;

    @Mock
    private SeatReservationService seatService;

    @InjectMocks
    private TicketServiceImpl ticketService;


    @Test
    @DisplayName("Successfully purchase adults only")
    void successfulPurchaseAdultsOnly() {
        ticketService.purchaseTickets(VALID_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3));

        // Verify payment service called
        verify(paymentService, times(1)).makePayment(
                VALID_ID,
                3*2500);

        // Verify seat reservation service called
        verify(seatService, times(1)).reserveSeat(
                VALID_ID,
                3);
    }

    @Test
    @DisplayName("Successfully purchase the maximum of 25 tickets")
    void successfulPurchaseMaxTickets() {
        ticketService.purchaseTickets(VALID_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25));

        // Verify payment service called
        verify(paymentService, times(1)).makePayment(
                VALID_ID,
                25*2500);

        // Verify seat reservation service called
        verify(seatService, times(1)).reserveSeat(
                VALID_ID,
                25);
    }

    @Test
    @DisplayName("Successfully purchase adults, children, and infants tickets")
    void successfulPurchaseAdultsAndChildren() {
        ticketService.purchaseTickets(VALID_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));

        // Verify payment service called
        verify(paymentService, times(1)).makePayment(
                VALID_ID,
                2*2500 + 3*1500);

        // Verify seat reservation service called
        verify(seatService, times(1)).reserveSeat(
                VALID_ID,
                5);
    }

    @Test
    @DisplayName("Infants do not pay or require seats")
    void infantsDoNotPayOrRequireSeats() {
        ticketService.purchaseTickets(VALID_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));

        // Verify payment service called
        verify(paymentService, times(1)).makePayment(
                VALID_ID,
                2500);

        // Verify seat reservation service called
        verify(seatService, times(1)).reserveSeat(
                VALID_ID,
                1);
    }

    @Test
    @DisplayName("Cannot buy a child ticket without an adult ticket")
    void cannotBuyChildWithoutAdult() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(
                        VALID_ID, new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)));
    }

    @Test
    @DisplayName("Cannot buy an infant ticket without an adult ticket")
    void cannotBuyInfantWithoutAdult() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(
                        VALID_ID, new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)));
    }

    @Test
    @DisplayName("Cannot buy child and infant tickets without an adult")
    void cannotBuyChildAndInfantWithoutAdult() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(
                        VALID_ID,
                        new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                        new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)));
    }

    @Test
    @DisplayName("Cannot buy more than 25 tickets")
    void cannotBuyMoreThanMaxTickets() {
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(
                VALID_ID, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26)));
    }

    @Test
    @DisplayName("Cannot buy 0 tickets")
    void cannotBuyZeroTickets() {
        assertThrows(InvalidTicketTypeRequestException.class, () -> ticketService.purchaseTickets(
                VALID_ID, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0)));
    }

    @Test
    @DisplayName("Cannot buy negative tickets")
    void cannotBuyNegativeTickets() {
        assertThrows(InvalidTicketTypeRequestException.class, () ->
                ticketService.purchaseTickets(
                        VALID_ID, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1)));
    }

    @Test
    @DisplayName("Null TicketTypeRequest type")
    void ticketTypeRequestCannotBeNull() {
        assertThrows(InvalidTicketTypeRequestException.class, () -> ticketService.purchaseTickets(
                VALID_ID, new TicketTypeRequest(null, 1)));
    }

    @Test
    @DisplayName("Invalid ID check")
    void invalidAccountIdRejected() {
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(
                INVALID_ID,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)));
    }

    @Test
    @DisplayName("Throw an exception when trying to purchase tickets with no requests")
    void purchaseTickets_withNoRequests() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(VALID_ID)
        );
    }

    @Test
    @DisplayName("Throw an exception when trying to purchase tickets with null request")
    void purchaseTickets_withNullRequest() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(null)
        );
    }
}
