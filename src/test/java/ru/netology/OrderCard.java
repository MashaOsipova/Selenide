package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class OrderCard {

    private String formatDeliveryDate(int plusDays) {
        LocalDate localDate = LocalDate.now();
        LocalDate deliveryDate = localDate.plusDays(plusDays);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String dateText = deliveryDate.format(formatter);
        return dateText;
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        $("[data-test-id='date']").$("[class='input__control']").click();
        $("[data-test-id='date']").$("[class='input__control']").sendKeys(Keys.chord(Keys.CONTROL + "A", Keys.DELETE));
    }


    @Test
    void shouldPlaceOrderPositiveTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+79087689678");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='notification']").shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Успешно!\n" + "Встреча успешно забронирована на " + formatDeliveryDate(3)));
    }

    @Test
    void shouldRequireValidCityIfCityNotIncludeTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Вашингтон");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldBe(visible).
                shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldRequireValidDateIfTwoDaysTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[class='input__control']").setValue(formatDeliveryDate(2));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='date'] .input__sub").shouldBe(visible).
                shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldPlaceOrderIfFourDaysPositiveTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(4));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='notification']").shouldBe(visible, Duration.ofSeconds(15)).
                shouldHave(exactText("Успешно!\n" + "Встреча успешно забронирована на " + formatDeliveryDate(4)));
    }

    @Test
    void shouldRequireValidNameIfNonCyrillicTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Peter Parker");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'].input_invalid .input__sub").shouldBe(visible)
                .shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldRequireAgreementCheckboxTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+71234567890");
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='agreement'].input_invalid .checkbox__text").shouldBe(visible)
                .shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }

    @Test
    void shouldRequireValidPhoneNumberIfNoPlusTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("71234567890");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldBe(visible)
                .shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldRequireValidPhoneNumberIfTenNumbersTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+7123456789");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldBe(visible)
                .shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldRequireValidPhoneNumberIfTwelveNumbersTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+712345678901");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldBe(visible)
                .shouldHave(exactText("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    void shouldRequireValidCityIfEmptyCityFieldTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='city'].input_invalid .input__sub").shouldBe(visible).
                shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldRequireValidDateIfEmptyDateFieldTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[class='input__control']").setValue("");
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='date'] .input__sub").shouldBe(visible).
                shouldHave(exactText("Неверно введена дата"));
    }

    @Test
    void shouldRequireValidNameIfEmptyNameFieldTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("");
        $("[data-test-id='phone']").$("[name='phone']").setValue("+71234567890");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='name'].input_invalid .input__sub").shouldBe(visible)
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldRequireValidPhoneNumberIfEmptyPhoneFieldTest() {
        $("[data-test-id='city']").$("[placeholder='Город']").setValue("Санкт-Петербург");
        $("[data-test-id='date']").$("[placeholder='Дата встречи']").setValue(formatDeliveryDate(3));
        $("[data-test-id= 'name']").$("[name ='name']").setValue("Михаил Иванов");
        $("[data-test-id='phone']").$("[name='phone']").setValue("");
        $("[data-test-id='agreement']").click();
        $$("button").find(exactText("Забронировать")).click();
        $("[data-test-id='phone'].input_invalid .input__sub").shouldBe(visible)
                .shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldSuccessfulFormSubmissionAfterInteractingWithComplexElements() {
        open("http://localhost:9999");
        $("[data-test-id=city] input").setValue("Че");
        $(Selectors.byText("Черкесск")).click();
        $("[data-test-id=date] input").click();
        int days = 4;
        for ( int cycle = 0; cycle < days; cycle++) {
            $(".calendar").sendKeys(Keys.ARROW_RIGHT);
        }
        $(".calendar").sendKeys(Keys.ENTER);
        $("[data-test-id=name] input").setValue("Михаил Иванов");
        $("[data-test-id=phone] input").setValue("+79014345676");
        $("[data-test-id=agreement]").click();
        $(".button").shouldHave(Condition.text("Забронировать")).click();
        String verificationDate = LocalDate.now().plusDays(7)           
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id=notification]")
                .shouldHave(Condition.text("Успешно! Встреча успешно забронирована на " + verificationDate),
                        Duration.ofSeconds(15));
    }
}