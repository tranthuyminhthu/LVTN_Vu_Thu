import axiosInstance from ".";

export const getMyConversations = async () => {
  try {
    const res = await axiosInstance.get("/api/chat/conversations/my-conversations");
    return res.data;
  } catch (error) {
    console.error("Error fetching conversations:", error);
    throw error;
  }
};

export const sendMessageToConversation = async ({ conversationId, message }: { conversationId: string, message: string }) => {
  try {
    const res = await axiosInstance.post("/api/chat/messages", { conversationId, message });
    return res.data;
  } catch (error) {
    console.error("Error sending message:", error);
    throw error;
  }
};

export const getMessagesByConversationId = async (conversationId: string) => {
  try {
    const res = await axiosInstance.get(`/api/chat/messages?conversationId=${conversationId}`);
    return res.data;
  } catch (error) {
    console.error("Error fetching messages:", error);
    throw error;
  }
};

export const createConversation = async (data: Array<string>) => {
  try {
    const res = await axiosInstance.post("/api/chat/conversations", {
      participantIds: data,
    });
    return res.data;
  } catch (error) {
    console.error("Error creating conversation:", error);
    throw error;
  }
};
