package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.HomePages;

import static com.codeborne.selenide.Selenide.open;
import static data.SQLHelper.cleanDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentDebitCardTest {
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
    @DisplayName("Purchase of a tour when filling out the form with valid debit card data APPROVED")
    void shouldSuccessfulPurchaseOfTourWithValidApprovedDebitCard() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringApprovedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotification("Операция одобрена Банком.");
        var actualStatusLastLinePaymentRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "APPROVED";
        assertEquals(actualStatusLastLinePaymentRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("The bank's refusal to purchase a tour using a debit card when filling out the form with valid DECLINED card data.")
    void shouldUnsuccessfulPurchaseOfTourWithValidDeclancedDebitCard() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringDeclinedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
        var actualStatusLastLinePaymentRequestEntity = SQLHelper.getStatusLastLinePaymentRequestEntity();
        var expectedStatus = "DECLINED";
        assertEquals(actualStatusLastLinePaymentRequestEntity, expectedStatus);
    }

    @Test
    @DisplayName("The bank's refusal to purchase a tour using a debit card when filling out the form with card data that is not registered in the system")
    void shouldUnsuccessfulPurchaseOfTourWithDebitCardNotRegisteredInSystem() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringRandomCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifyErrorNotification("Ошибка! Банк отказал в проведении операции.");
    }

    @Test
    @DisplayName("Getting an error when submitting a form with letters in the card number.")
    void shouldReturnErrorWhenlettersInCardNumber() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringInvalidCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyErrorCardNumberField("Неверный формат");
    }

    @Test
    @DisplayName("Receiving an error when filling out the card payment form with expired card data.")
    void shouldReturnErrorWhenCardWithExpiredCardData() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringApprovedCard();
        homePages.enteringInvalidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyPeriodErrorYearField("Истёк срок действия карты");
    }

    @Test
    @DisplayName("Error when buying a tour with invalid cardholder data on the form.")
    void shouldReturnErrorWhenCardWithInvalidCardholder() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringApprovedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringInValidOwner();
        homePages.enteringValidCVC();
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyErrorOwnerField("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Error occurred when submitting a completed form with invalid CVC/CVV data.")
    void shouldReturnErrorWhenCardWithInvalidCVC() {
        homePages.chooseBy("Оплата по карте");
        homePages.enteringApprovedCard();
        homePages.enteringValidCardValidityPeriod();
        homePages.enteringValidOwner();
        homePages.enteringInValidCVC();
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyErrorCVCField("Неверный формат");
    }

    @Test
    @DisplayName("Receiving an error when sending an empty application form for the purchase of a tour.")
    void shouldReturnErrorWhenEmptyForm() {
        homePages.chooseBy("Оплата по карте");
        homePages.verifySuccessfulNotificationIsNotVisible();
        homePages.verifyErrorCardNumberField("Неверный формат");
        homePages.verifyErrorMonthField("Неверный формат");
        homePages.verifyErrorYearField("Неверный формат");
        homePages.verifyErrorOwnerField("Поле обязательно для заполнения");
        homePages.verifyErrorCVCField("Неверный формат");
    }
}
