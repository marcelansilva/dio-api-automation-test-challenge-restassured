package Entities;

import com.github.javafaker.Faker;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class BookingBuilder {
    private static final Faker FAKER = Faker.instance();

    public static Booking getBooking() {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        return Booking.builder()
                .firstname(FAKER.name().firstName())
                .lastname(FAKER.name().lastName())
                .totalprice(FAKER.number().numberBetween(1, 2000))
                .depositpaid(true)
                .bookingdates(BookingDates.builder().checkin(formatter.format(FAKER.date().past(20, TimeUnit.DAYS)))
                        .checkout(formatter.format(FAKER.date().future(5, TimeUnit.DAYS)))
                        .build())
                .additionalneeds("Breakfest")
                .build();

    }

    public static PartialBooking getPartialBookingData() {
        return PartialBooking.builder ()
                .firstname (FAKER.name ()
                        .firstName ())
                .totalprice (FAKER.number ()
                        .numberBetween (100, 5000))
                .build ();
    }
}
