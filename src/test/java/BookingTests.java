import Entities.*;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;

import static Entities.BookingBuilder.*;
import static Entities.TokenBuilder.getToken;
import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import static org.hamcrest.Matchers.*;

public class BookingTests {
    public static Faker faker;
    private static RequestSpecification request;
    private static Booking booking;
    private static BookingDates bookingDates;
    private static User user;
    private static Booking updatedBooking;
    private static PartialBooking partialUpdatedBooking;
    private static Tokencreds tokenCreds;
    private static int bookingId;

    @BeforeAll
    public static void Setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        booking = getBooking();
        updatedBooking = getBooking();
        tokenCreds = getToken();
        partialUpdatedBooking = getPartialBookingData();

        Faker faker = new Faker();
        bookingId = (faker.number().numberBetween(2, 300));

        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter(), new ErrorLoggingFilter());
    }


    @BeforeEach
    void setRequest() {
      request = given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
             .contentType(ContentType.JSON).auth().preemptive().basic("admin", "password123");
    }

    @Test
    public void getAllBookingsById_returnOk() {
        Response response = request
                .when()
                .get("/booking")
                .then()
                .extract()
                .response();


        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void getAllBookingsByUserFirstName_BookingExists_returnOk() {
        request
                .when()
                .queryParam("firstName", "sally")
                .get("/booking")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .and()
                .body("results", hasSize(greaterThan(0)));

    }

    @Test
    void getBookingIdsByDate_return200() {
        request
                .when()
                .queryParam("bookingDates", "2023-10-02")
                .get("/booking")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    public void CreateBooking_WithValidData_returnOk() {

        Booking test = booking;
        given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .when()
                .body(booking)
                .post("/booking")
                .then()
                .body(matchesJsonSchemaInClasspath("createBookingRequestSchema.json"))
                .and()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON).and().time(lessThan(3000L));

    }

    @Test
    public void UpdateBooking_WithValidData_returnOk() {

        request
                .given().body(updatedBooking)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .and()
                .assertThat()
                .body("firstName", equalTo(updatedBooking.getFirstname()))
                .body("lastName", equalTo(updatedBooking.getLastname()))
                .body("totalPrice", equalTo(updatedBooking.getTotalprice()))
                .body("depositPaid", equalTo(updatedBooking.isDepositpaid()))
                .body("bookingDates.checkIn", equalTo(updatedBooking.getBookingdates().getCheckin()))
                .body("bookingDates.checkOut", equalTo(updatedBooking.getBookingdates().getCheckout()))
                .body("additionalNeeds", equalTo(updatedBooking.getAdditionalneeds()));
    }


    @Test
    public void UpdatePartialBookingTest_returnOk() {
        request
                .given().body (partialUpdatedBooking)
                .when()
                .patch ("/booking/" + bookingId)
                .then()
                .statusCode (200)
                .and()
                .assertThat()
                .body("firstname", equalTo (partialUpdatedBooking.getFirstname ()), "lastname",
                        equalTo (updatedBooking.getLastname ()), "totalprice", equalTo (partialUpdatedBooking.getTotalprice ()),
                        "depositpaid", equalTo (updatedBooking.isDepositpaid ()), "bookingdates.checkin", equalTo (
                                updatedBooking.getBookingdates ()
                                        .getCheckin ()), "bookingdates.checkout", equalTo (updatedBooking.getBookingdates ()
                                .getCheckout ()), "additionalneeds", equalTo (updatedBooking.getAdditionalneeds ()));

    }

    @Test
    public void DeleteBookingTest_ReturnOk() {
        given ().header ("Cookie", "token=" + generateToken())
                .when()
                .delete ("/booking/" + bookingId)
                .then()
                .statusCode (201);
    }

    private String generateToken () {

        return given ().body (tokenCreds)
                .when()
                .header("Content-Type", "application/json")
                .post ("/auth")
                .then ()
                .assertThat ()
                .body ("token", is (notNullValue ()))
                .extract ()
                .path ("token");
    }

}



