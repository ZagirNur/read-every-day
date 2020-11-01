package ga.imbir.telegrambot.service

import ga.imbir.telegrambot.bot.Bot
import org.springframework.stereotype.Service
import reactor.core.scheduler.Schedulers
import javax.annotation.PostConstruct

@Service
class BotService(val bot: Bot) {

    @PostConstruct
    fun init() {
        bot.message()
                .subscribeOn(Schedulers.parallel())
                .subscribe { m -> bot.sendMessage(m.chat().id(), m.text()) }
        bot.command()
                .subscribeOn(Schedulers.parallel())
                .subscribe { m -> bot.sendMessage(m.chat().id(), "this is command: ${m.text()}") }
    }
}