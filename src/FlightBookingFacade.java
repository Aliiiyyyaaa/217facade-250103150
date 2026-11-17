public class FlightBookingFacade {

    private final SeatInventorySystem seatSystem;
    private final BaggagePricingEngine baggageEngine;
    private final CurrencyConverter currencyConverter;
    private final PaymentProcessor paymentProcessor;
    private final TicketIssuer ticketIssuer;

    public FlightBookingFacade() {
        seatSystem = new SeatInventorySystem();
        baggageEngine = new BaggagePricingEngine();
        currencyConverter = new CurrencyConverter();
        paymentProcessor = new PaymentProcessor();
        ticketIssuer = new TicketIssuer();
    }

    // Old method: STANDARD by default
    public String bookFlight(String passengerName,
                             String flightCode,
                             String seatNumber,
                             double baseFareUSD,
                             int bagCount,
                             double bagWeightKg,
                             String currency,
                             String cardNumber) {

        return bookFlight(passengerName, flightCode, seatNumber,
                baseFareUSD, bagCount, bagWeightKg,
                currency, cardNumber, "STANDARD");
    }

    // New method: with loyalty tier
    public String bookFlight(String passengerName,
                             String flightCode,
                             String seatNumber,
                             double baseFareUSD,
                             int bagCount,
                             double bagWeightKg,
                             String currency,
                             String cardNumber,
                             String loyaltyTier) {

        String lockToken = null;

        try {
            // 1. Connect and lock seat
            seatSystem.connectToReservationServer();

            lockToken = seatSystem.lockSeat(
                    flightCode, seatNumber, passengerName);

            // 2. Calculate baggage fee
            double baggageFee = 0.0;

            if (!"SILVER".equalsIgnoreCase(loyaltyTier)
                    && !"GOLD".equalsIgnoreCase(loyaltyTier)) {

                baggageFee = baggageEngine.calculateBaggageFeeUSD(
                        bagCount, bagWeightKg);
            }

            double totalUSD = baseFareUSD + baggageFee;

            // 3. Convert currency
            double convertedTotal = currencyConverter.convertFromUSD(
                    totalUSD, currency);

            // 4. Initialize payment
            paymentProcessor.initializeGateway();

            // SILVER gets 10% discount
            if ("SILVER".equalsIgnoreCase(loyaltyTier)) {
                paymentProcessor.applyPromoCode("SILVER-FREE-BAG");
            }

            // 5. Charge card
            String receiptId = paymentProcessor.chargeCard(
                    cardNumber, convertedTotal, currency);

            // 6. Generate ticket
            return ticketIssuer.generateTicket(
                    passengerName, flightCode, lockToken, receiptId);

        } catch (RuntimeException ex) {

            if (lockToken != null) {
                seatSystem.releaseSeatLock(lockToken);
            }

            System.out.println("Booking failed: " + ex.getMessage());
            return "BOOKING_FAILED: " + ex.getMessage();

        } finally {
            seatSystem.disconnect();
        }
    }
}
