import java.util.*

data class Chat(
    var senderId: String = "",
    var senderName: String = "",
    var receiverId: String = "",
    var receiverName: String = "",
    var message: String = "",
    var timestamp: Date? = Date(),
    var roomId: String = ""
) 