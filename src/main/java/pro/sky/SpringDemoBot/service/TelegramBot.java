package pro.sky.SpringDemoBot.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.SpringDemoBot.config.BotConfig;

@Component // позволит автоматически создать экземпляр спрингу
public class TelegramBot extends TelegramLongPollingBot {

    //    добавляем все зависимости
    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    //    через этот метод передаем username
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    //    через этот метод передаем api ключ
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    //    что должен делать бот, когда ему кто-то пишет
    //     Update - класс, содержащий сообщение, посылаемое боту юзером (+доп.инфо. В т.ч. инфо о самом пользователе)
    public void onUpdateReceived(Update update) {

        // проверяем, что что-то прислали и что там есть текст, чтобы не попасть на NPE
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            // chatId нужен, чтобы знать, в какой чат отправлять ответ на запрос
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":

                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                // если пользователем не введены поддерживаемые ботом команды
                default:
                    sendMessage(chatId, "Sorry, command was not recognized");

            }
        }

    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Hi, " + name + ", nice to meet you!";

        sendMessage(chatId, answer);

    }

    // метод для отправки сообщений
    private void sendMessage(long chatId, String textToSend) {
        // SendMessage - спец.класс для отправки сообщений
        SendMessage message = new SendMessage();
        // для присваивания chatId исходящему сообщению используется String
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
