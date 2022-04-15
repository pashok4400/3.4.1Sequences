fun main() {

}
object WalService {
    var chats: MutableList<Chat> = ArrayList<Chat>()

    fun createMessage(userOutId: Int, userInId: Int, text: String, messageID: Int = 0, isRead: Boolean = false) {
        var chat: Chat? = chats.find {
            !it.messages.filter {
                it.userOutId == userOutId && it.userInId == userInId ||
                        it.userOutId == userInId && it.userInId == userOutId
            }.isEmpty()
        }
        if (chat != null) {
            deleteChat(chat.chatId)
            chat.messages.add(
                Message(
                    chat.messages.last().messageId + 1, userOutId, userInId,
                    false, text
                )
            )
            chat.countMessage++
            chats.add(chat.copy(chatId = 1))
        } else {
            var mess: MutableList<Message> = ArrayList<Message>()
            mess.add(
                Message(
                    1, userOutId, userInId,
                    false, text
                )
            )
            createChat(Chat(0, mess, 0, 1, 1))
        }
    }

    fun editMessage(chatId: Int, message: Message): Boolean {
        if (deleteMessage(chatId, message.messageId)) {
            createMessage(message.userOutId, message.userInId, message.text, message.messageId)
            return true
        }
        return false
    }

    fun deleteMessage(chatId: Int, messageId: Int): Boolean {
        var chat: Chat? = chats.find { it.chatId == chatId }
        if (chat == null) {
            return false
        }
        val mes: Message? = chat.messages.find { it.messageId == messageId }
        if (mes == null) {
            return false
        }
        if (chat.messages.size == 1) {
            deleteChat(chatId)
            return true
        }
        chat.messages.remove(mes)
        return true
    }

    fun getAllChatsUser(userId: Int): List<Chat> {
        return chats.filterNot { it.messages.filter { it.userInId == userId || it.userOutId == userId }.isEmpty()}
    }

    fun getUnreadChatsCount(userId: Int): Int { // Переделано
        return chats.filterNot { it.messages.filter { it.userInId == userId && it.isRead == false }.isEmpty() }
            .size
    }

    fun getLastMessages(chatId: Int, lastId: Int, count: Int): List<Message> {
        var res: MutableList<Message> = ArrayList()
        var c = 0;
        chats.map{if(it.chatId == chatId) it.messages.map{if(it.messageId >= lastId && c <= count) {res.add(it); c++; it.isRead = true}}}
        return res
    }

    fun getMessages(): List<Message> {
        var res: MutableList<Message> = ArrayList()
        chats.map{it.messages.map{res.add(it)}}
        return res
    }

    fun getMessagesUser(userId: Int): List<Message> {
        var res: MutableList<Message> = ArrayList()
        chats.map{it.messages.map{if(it.userInId == userId) {res.add(it); it.isRead = true}}}
        return res
    }

    fun deleteChat(id: Int): Boolean {
        val oldChats = chats;
        chats = chats.filter { it.chatId != id } as MutableList<Chat>
        if (chats.size < oldChats.size) {
            return true
        } else {
            return false
        }
    }

    private fun createChat(chat: Chat) {
        if (chats.isEmpty()) {
            chats.add(chat.copy(chatId = 1))
        } else {
            var id = chats.last().chatId
            chats.add(chat.copy(chatId = ++id))
        }
    }
}

data class Chat(
    val chatId: Int,
    var messages: MutableList<Message>,
    val adminId: Int,
    var countMessage: Int = 0,
    var countNoRed: Int
)

data class Message(
    val messageId: Int,
    val userOutId: Int,
    val userInId: Int,
    var isRead: Boolean,
    val text: String
)