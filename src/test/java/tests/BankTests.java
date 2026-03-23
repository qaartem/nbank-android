package tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;

public class BankTests extends BaseTest {

    @Test
    @DisplayName("User can login with valid credentials")
    public void userCanLogin() {
        new LoginPage()
                .enterUsername(ADMIN.getUsername())
                .enterPassword(ADMIN.getPassword())
                .clickLoginButton()
                .checkAdminPanelLoaded();
    }
}
