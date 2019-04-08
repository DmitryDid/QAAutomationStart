package RestAssured.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject {

    private String safari;
    private String chrome;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firefox;
}
