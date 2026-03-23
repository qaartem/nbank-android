package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositTestData {
    private String username;
    private String password;
    private Long accountId;
    private String accountNumber;
    private Float initialBalance;
}
