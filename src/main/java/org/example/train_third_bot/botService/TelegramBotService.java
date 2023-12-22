package org.example.train_third_bot.botService;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.example.train_third_bot.configuration.BotConfig;
import org.example.train_third_bot.model.BotRepository;
import org.example.train_third_bot.model.BotUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {

    @Autowired
    private BotRepository botRepository;

    private final BotConfig botConfig;

//    menu link buttonslar
    private static final String HELP_TEXT = "Sizga qanday yordam bera olaman?";
    private static final String START = "Bot ishga tushirildi va undan siz foydalana olasiz";
    private static final String DATA = "Botda sizga tegishli ma'lumotlardan foydalanasiz";
    private static final String DELETE = "Botda sizga tegishli ma'lumotlarni o'chirishingiz mumkin";
    private static final String SETTINGS = "Botda o'zingizga tegishli sozlamalarni o'zgartirasiz";


//    inline buttonslar texti
    private static final String YES_BUTTON = "YES_BUTTON";
    private static final String NO_BUTTON = "NO_BUTTON";

    private static final String ERROR_TEXT = "Text yuborishda xatolik mavjud!";


    public TelegramBotService (BotConfig botConfig) {

        this.botConfig = botConfig;

//        Telegram botga pastdan commandalarni qo'shadi va ularga tavfsif qiymatini shakllantiradi
        List<BotCommand> command_list = new ArrayList<>();
        command_list.add(new BotCommand("/start", START));
        command_list.add(new BotCommand("/data", DATA));
        command_list.add(new BotCommand("/delete", DELETE));
        command_list.add(new BotCommand("/help", HELP_TEXT));
        command_list.add(new BotCommand("/settings", SETTINGS));


        try {

            this.execute(new SetMyCommands(command_list, new BotCommandScopeDefault(), null));

        } catch (TelegramApiException t) {
            log.error("Commandalar bilan ishlashda xatolik chiqariladi" + t.getMessage());
        }
    }


    @Override
    public String getBotUsername() {
        return botConfig.botUsername;
    }

    @Override
    public String getBotToken() {
        return botConfig.botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {


            long chat_id = update.getMessage().getChatId();
            String chat_text = update.getMessage().getText();
            String get_come_text = "";


            if (chat_text.contains("/send") && botConfig.getBotId() == chat_id) {
                var textToSent = EmojiParser.parseToUnicode(chat_text.substring(chat_text.indexOf(" ")));
                var bot_users = botRepository.findAll();

                for (BotUser botUser: bot_users) {
                    sendAndMessageFunc(botUser.getChat_ID(), textToSent);
                }
                
            } else {
                switch (chat_text) {
                    case "/start":
                        get_come_text = "Salom bot ishga tushirildi!";
                        break;

                    case "/settings":
                        get_come_text = "Botdagi sozlamalarni sozlash qismi hali o'rnatilmagan";
                        break;

                    case "/help":
                        get_come_text = "Sizga nima yordam bera olaman!";
                        break;

                    case "/delete":
                        get_come_text = "Botdagi ma'lumotlarni o'chirish qismi kiritilmagan";
                        break;

                    case "/data":
                        get_come_text = "Botdagi ma'lumotlaringiz o'rnatilmagan";
                        break;

//                    Buttons case:
                    case "Register":

                        register_button(chat_id);
                        break;

                    default:
                        sendAndMessageFunc(chat_id, "Botga Feruz hali boshqa qo'shimchalarni qo'shmagan!");

                }
            }

            registrationUsers(update.getMessage());
            String ansverText = EmojiParser.parseToUnicode(get_come_text); // smilelar qushish qismi
            ansverYou(chat_id, ansverText);

        } else if (update.hasCallbackQuery()) {

            String callBackData = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if (callBackData.equals(YES_BUTTON)) {

                String text = "Siz registratsiya qilishga rozilik berdingiz!";
                editMessageWith(text, chat_id, message_id);

            } else if (callBackData.equals(NO_BUTTON)) {

                String text = "Siz registratsiya qilishdan bosh tortdingiz!";
                editMessageWith(text, chat_id, message_id);

            }
        }

    }

//    edit message bilan ishlash

    private void editMessageWith(String text, long chat_id, long message_id) {

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chat_id));
        editMessageText.setText(text);
        editMessageText.setMessageId((int) message_id);

        istisnoEditFunc(editMessageText);

    }

//    Repositoriy bilan bog'langan holda ishlovchi qism
    private void registrationUsers(Message mg) {

        if (botRepository.findById(mg.getChatId()).isEmpty()) {
            var chat_id = mg.getChatId();
            var chat_x = mg.getChat();

            BotUser botUser = new BotUser();

            botUser.setIds(chat_id);
            botUser.setFirstname(chat_x.getFirstName());
            botUser.setLastname(chat_x.getLastName());
            botUser.setUsername(chat_x.getUserName());
            botUser.setChat_ID(chat_x.getId());
            botUser.setTimestamp(new Timestamp(System.currentTimeMillis()));


            botRepository.save(botUser);
            log.info("Ma'lumot bazaga saqlandi!");

        }

    }

//    register button
    private void register_button(long chat_id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chat_id));
        sendMessage.setText("Siz registratsiya qilishga rozimisiz yoki yo'qligini belgilang:");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> in_inline_buttons_row = new ArrayList<>();

        List<InlineKeyboardButton> inline_buttons_row = new ArrayList<>();

        var yes_button = new InlineKeyboardButton();
        yes_button.setText("Ha");
        yes_button.setCallbackData(YES_BUTTON);

        var no_button = new InlineKeyboardButton();
        no_button.setText("Yoq");
        no_button.setCallbackData(NO_BUTTON);

        inline_buttons_row.add(yes_button);
        inline_buttons_row.add(no_button);

        in_inline_buttons_row.add(inline_buttons_row);

        inlineKeyboardMarkup.setKeyboard(in_inline_buttons_row);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        istisnoSendFunc(sendMessage);

    }


//    Ma'lumotni uzatuvchi qism
    private void ansverYou(long chat_id, String chat_text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id);
        sendMessage.setText(chat_text);

//        Keyboard qismini chiqarish

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> button_lists = new ArrayList<>();
        KeyboardRow button_rows = new KeyboardRow();

        button_rows.add("ob-havo");
        button_rows.add("hazillar");

        button_lists.add(button_rows);


        button_rows = new KeyboardRow();

        button_rows.add("Register");
        button_rows.add("My data");
        button_rows.add("Delete data");

        button_lists.add(button_rows);

        replyKeyboardMarkup.setKeyboard(button_lists);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);



        istisnoSendFunc(sendMessage);

    }


//    SendMessage uchun istisno functioni
    private void istisnoSendFunc(SendMessage ms) {
        try {
            execute(ms);
        } catch (TelegramApiException t) {
            log.error(ERROR_TEXT + t.getMessage());
        }
    }


//    EditMessage uchun istisno functioni
    private void istisnoEditFunc(EditMessageText ems) {
        try {
            execute(ems);
        } catch (TelegramApiException t) {
            log.error(ERROR_TEXT + t.getMessage());
        }
    }


//    sendMessage bilan ishlash functioni
    private void sendAndMessageFunc(long chat_id, String chat_text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id);
        sendMessage.setText(chat_text);
    }

}
