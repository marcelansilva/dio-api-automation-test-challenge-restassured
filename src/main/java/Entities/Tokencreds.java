package Entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tokencreds {
    private String username;
    private String password;
}
