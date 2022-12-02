package pro.sky.SpringDemoBot.service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static net.serenitybdd.rest.RestRequests.given;


class TelegramBotTest {

    @Test
    void successSendMessage() {
//        https://api.telegram.org/bot5775861983:AAFDGQJ4VeSdNL9l9nnXamB_HzDino_rWx4/sendMessage?chat_id=183638243&text=/start
        RestAssured.baseURI="https://api.telegram.org/bot5775861983:AAFDGQJ4VeSdNL9l9nnXamB_HzDino_rWx4";
        given()
                .param("chat_id", 183638243)
                .param("text", "TEST")
                .when()
                .get("/sendMessage")
                .then()
                .statusCode(200);
    }
}