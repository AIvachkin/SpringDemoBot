package pro.sky.SpringDemoBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.SpringDemoBot.model.Ads;

public interface AdsRepository extends JpaRepository<Ads, Long> {

}
