package pro.sky.SpringDemoBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * В данном классе опишем структуру для нашей таблицы, которая будет содержать
 * информацию для периодической рассылки всем пользователям
* */
@Getter
@Setter
@Entity(name = "adsTable")
public class Ads {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String ad;



}
