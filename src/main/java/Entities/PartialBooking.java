package Entities;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartialBooking {
    String firstname;
    double  totalprice;
}
