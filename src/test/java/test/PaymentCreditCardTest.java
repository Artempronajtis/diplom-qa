package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.HomePages;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.cleanDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentCreditCardTest {
    HomePages homePages;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("Allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        homePages = open("http://localhost:8080/", HomePages.class);
    }

    @AfterEach
    void tearDownAllDatabase() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Successful purchase of a tour with payment on credit with valid APPROVED card.")
    void shouldSuccessfulPurchaseOfTourInCreditWithValidApprovedDebitCard() {
        homePages.chooseByInCredit("Кредит по данным карты");
        homePages.enteringApprovedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLineCreditRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("The bank's refusal to purchase a tour on credit when filling out the form with valid DECLINED card data.")
    void shouldUnsuccessfulPurchaseOfTourInCreditWithValidDeclancedDebitCard() {
        homePages.chooseByInCredit("Кредит по данным карты");
        homePages.enteringDeclinedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
        var actualStatusLastLineCreditRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "DECLINED";
        assertEquals(actualStatusLastLineCreditRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("The bank's refusal to purchase a tour on credit when filling out the form with card data that is not registered in the system.")
    void shouldUnsuccessfulPurchaseOfTourInCreditWithDebitCardNotRegisteredInSystem() {
        homePages.chooseByInCredit("Кредит по данным карты");
        homePages.enteringRandomCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
    }
}
