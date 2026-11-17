
public class Main {
    public static void main(String[] args) {

        FlightBookingFacade booking = new FlightBookingFacade();

        // TEST 1: Successful booking
        System.out.println("=== TEST 1 ===");

        String ticket1 = booking.bookFlight(
                "Amina Suleimenova",
                "KZ204",
                "14B",
                320.0,
                2,
                24.5,
                "EUR",
                "4532015112830366"
        );

        System.out.println("Result 1: " + ticket1);


        // TEST 2: Blocked seat
        System.out.println("\n=== TEST 2 ===");

        String ticket2 = booking.bookFlight(
                "Daniyar Kassymov",
                "KZ204",
                "13A",
                320.0,
                1,
                15.0,
                "USD",
                "4532015112830366"
        );

        System.out.println("Result 2: " + ticket2);


        // TEST 3: Invalid payment
        System.out.println("\n=== TEST 3 ===");

        String ticket3 = booking.bookFlight(
                "Timur Bekov",
                "KZ204",
                "08C",
                320.0,
                1,
                18.0,
                "KZT",
                "1234"
        );

        System.out.println("Result 3: " + ticket3);

        // TEST 4: SILVER loyalty tier
        System.out.println("\n=== TEST 4: SILVER ===");

        String ticket4 = booking.bookFlight(
                "Amina Suleimenova",
                "KZ204",
                "15A",
                320.0,
                3,
                30.0,
                "EUR",
                "4532015112830366",
                "SILVER"
        );

        System.out.println("Result 4: " + ticket4);

    }
}