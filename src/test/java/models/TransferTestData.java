package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferTestData {
    private String senderUsername;
    private String senderPassword;
    private Long senderAccountId;
    private String senderAccountNumber;
    private Float senderInitialBalance;

    private String receiverUsername;
    private String receiverPassword;
    private Long receiverAccountId;
    private String receiverAccountNumber;
}
