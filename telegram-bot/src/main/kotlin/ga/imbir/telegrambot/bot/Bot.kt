package ga.imbir.telegrambot.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.GetUpdates
import com.pengrad.telegrambot.request.SendMessage
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicInteger

@Service
class Bot () {

    val bot = TelegramBot("<тут токен>")
    val lastUpdate = AtomicInteger()
    val flux = Flux.generate<Update> {
        it.next(getNextMessage())
    }.cache()

    private fun getNextMessage(): Update {
        val getUpdates = GetUpdates()
                .offset(lastUpdate.get())
                .timeout(1)
        val updates = bot.execute(getUpdates).updates()
        return if (updates != null && updates.isNotEmpty()) {
            lastUpdate.set(updates[0].updateId() + 1)
            updates[0]
        } else {
            getNextMessage();
        }
    }

    fun command() = flux
            .map(Update::message)
            .filter { m -> m.text().startsWith("/") }

    fun message() = flux
            .map(Update::message)
            .filter { m -> !m.text().startsWith("/") }


    fun sendMessage(chatId: Long, text: String) {
        bot.execute(SendMessage(chatId, text))
    }


}