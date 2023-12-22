package org.example.train_third_bot.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "tg_users")
public class BotUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long ids;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column
    private String username;

    @Column
    private long chat_ID;

    @Column
    @UpdateTimestamp
    private Timestamp timestamp;

//    public String getChat_ID() {
//        return chat_ID;
//    }
//
//    public void setChat_ID(String chat_ID) {
//        this.chat_ID = chat_ID;
//    }
}
