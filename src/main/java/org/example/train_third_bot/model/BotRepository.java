package org.example.train_third_bot.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRepository extends CrudRepository<BotUser, Long>, JpaRepository<BotUser, Long> {


}
