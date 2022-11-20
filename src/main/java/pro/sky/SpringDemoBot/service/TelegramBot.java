package pro.sky.SpringDemoBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.SpringDemoBot.config.BotConfig;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component // позволит автоматически создать экземпляр спрингу
public class TelegramBot extends TelegramLongPollingBot {

    //    добавляем все зависимости
    final BotConfig config;

    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities. \n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a welcome message \n\n" +
            "Type /mydata to see data stored about yourself \n\n" +
            "Type /help to see this message again";

    public TelegramBot(BotConfig config) {
        this.config = config;
//        добавляем в конструктор создание меню - лист, сожержащий команды бота
//        !!! не использовать CamelCase в командах
        List<BotCommand> listOfCommands = new ArrayList<>();
//        аргументы у BotCommand: 1 - сама команда, которая будет вставляться ботом при нажатии соответствующего пункта меню
//        2 - краткое описание команды
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));

//        можно ознакомиться с данными, сохраненными после взаимодействия пользователя с ботом. Можно в т.ч. попросить их удалить
        listOfCommands.add(new BotCommand("/mydata", "get your data stored"));
        listOfCommands.add(new BotCommand("/deletedata", "delete my data"));

        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }

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

                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;

                // если пользователем не введены поддерживаемые ботом команды
                default:
                    sendMessage(chatId, "Sorry, command was not recognized");

            }
        }

    }

    private void startCommandReceived(long chatId, String name) {

        String answer = "Hi, " + name + ", nice to meet you!";
        log.info("Replied to user " + name);

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
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
