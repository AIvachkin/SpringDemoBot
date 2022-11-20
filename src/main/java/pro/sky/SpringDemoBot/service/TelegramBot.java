package pro.sky.SpringDemoBot.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.SpringDemoBot.config.BotConfig;
import pro.sky.SpringDemoBot.model.User;
import pro.sky.SpringDemoBot.model.UserRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component // позволит автоматически создать экземпляр спрингу
public class TelegramBot extends TelegramLongPollingBot {

    //    добавляем все зависимости
    final BotConfig config;

//    инджектим репозиторий

    @Autowired
    private UserRepository userRepository;

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

//                    привязываем вызов /start ко времени регистрации
                    registerUser(update.getMessage());

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

    private void registerUser(Message msg) {
//        проверка на существование пользователя в базе (чтобы не перезаписывать время регистрации при каждом вызове start)
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
//            определяем временные переменные
            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }

    private void startCommandReceived(long chatId, String name) {

//        добавляем в ответ смайлы
        String answer = EmojiParser.parseToUnicode("Hi, " + name + ", nice to meet you!" + " :blush:");


//        String answer = "Hi, " + name + ", nice to meet you!";
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

//        размечаем клавиатуру
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        создаем список из рядов кнопок
        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        создаем сам ряд
        KeyboardRow row = new KeyboardRow();

        row.add("weather");
        row.add("get random joke");

//        порядок добавления рядов имеет значение: первый - верхний
        keyboardRows.add(row);

//        второй ряд
        row = new KeyboardRow();
        row.add("register");
        row.add("check my data");
        row.add("delete my data");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

//        привязываем созданную клавиатуру к сообщению
        message.setReplyMarkup(keyboardMarkup);

//        т.к. мы эту клавиатуру определяем в методе sendMessage, она будет показываться постоянно
//        чтобы для каждого сообщения определять свою клавиатуру, лучше блок этот вынести в отдельный метод,
//        создавать ее там, и в sendMessage передавать готовый объект типа ReplyKeyboardMarkup



        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }
}
