package pro.sky.SpringDemoBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.SpringDemoBot.model.User;

public interface UserRepository extends JpaRepository <User, Long> {
}
