package pro.sky.SpringDemoBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration //класс может использоваться контейнером Spring IoC в качестве источника определений bean-компонентов
@Data // автоматически создаст конструктор и геттеры/сеттеры для полей класса
@PropertySource("application.properties") //указываем, где находятся свойства, считываемые через @Value
public class BotConfig {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;

    @Value("${bot.owner}")
    Long ownerId;

}
