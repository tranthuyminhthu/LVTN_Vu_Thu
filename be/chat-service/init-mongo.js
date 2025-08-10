// Initialize MongoDB for Chat Service
print('Initializing Chat Service Database...');

// Switch to chatdb database
db = db.getSiblingDB('chatdb');

// Create collections
db.createCollection('conversations');
db.createCollection('messages');

// Create indexes for better performance
db.conversations.createIndex({ "participantsHash": 1 }, { unique: true });
db.conversations.createIndex({ "createdAt": -1 });
db.conversations.createIndex({ "participants.userId": 1 });

db.messages.createIndex({ "conversationId": 1 });
db.messages.createIndex({ "createdAt": -1 });
db.messages.createIndex({ "senderId": 1 });

// Create a test conversation (optional)
db.conversations.insertOne({
    _id: ObjectId(),
    participantsHash: "test_user1_test_user2",
    participants: [
        {
            userId: "test_user1",
            username: "Test User 1",
            avatar: "https://example.com/avatar1.jpg"
        },
        {
            userId: "test_user2", 
            username: "Test User 2",
            avatar: "https://example.com/avatar2.jpg"
        }
    ],
    createdAt: new Date(),
    updatedAt: new Date()
});

print('Chat Service Database initialized successfully!');
print('Collections created: conversations, messages');
print('Indexes created for better performance'); 