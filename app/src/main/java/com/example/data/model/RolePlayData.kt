package com.example.data.model

object RolePlayData {
    val scenarios: List<RolePlayScenario> = listOf(
        RolePlayScenario(
            id = "intro",
            title = "Introducing Yourself",
            roleAi = "Friendly Colleague / Neighbor",
            roleUser = "Newcomer / Learner",
            objective = "State your name, where you are from, and what you do.",
            initialMessage = "Hi there! I don't think we've met yet. I'm Alex. What's your name?",
            difficulty = "Beginner",
            suggestedPhrases = listOf(
                "My name is...",
                "Nice to meet you, Alex.",
                "I am originally from...",
                "Currently, I work as..."
            ),
            iconName = "person"
        ),
        RolePlayScenario(
            id = "restaurant",
            title = "Ordering Food at a Restaurant",
            roleAi = "Restaurant Server",
            roleUser = "Customer",
            objective = "Order a meal, ask for recommendations, and request the bill.",
            initialMessage = "Good evening! Welcome to Bistro Garden. Are you ready to order, or would you like a few more minutes?",
            difficulty = "Beginner",
            suggestedPhrases = listOf(
                "Could I see the menu, please?",
                "What do you recommend?",
                "I'd like to have...",
                "Could we get the check, please?"
            ),
            iconName = "restaurant"
        ),
        RolePlayScenario(
            id = "directions",
            title = "Asking for Directions",
            roleAi = "Helpful Local Passerby",
            roleUser = "Lost Visitor",
            objective = "Ask politely how to get to the central station or museum.",
            initialMessage = "Excuse me, you look a bit lost. Can I help you find something?",
            difficulty = "Beginner",
            suggestedPhrases = listOf(
                "Excuse me, could you tell me where...",
                "How far is the train station from here?",
                "Should I turn left or right?",
                "Thank you so much for your help!"
            ),
            iconName = "map"
        ),
        RolePlayScenario(
            id = "friend",
            title = "Talking with a Friend",
            roleAi = "Close Friend",
            roleUser = "Yourself",
            objective = "Chat about your weekend, movies, or future plans.",
            initialMessage = "Hey! Long time no see! How have you been doing lately?",
            difficulty = "Beginner",
            suggestedPhrases = listOf(
                "I've been quite busy with work.",
                "Did you do anything exciting this weekend?",
                "Let's grab a coffee sometime soon.",
                "Have you watched any good movies lately?"
            ),
            iconName = "favorite"
        ),
        RolePlayScenario(
            id = "shopping",
            title = "Shopping at a Store",
            roleAi = "Store Assistant",
            roleUser = "Shopper",
            objective = "Inquire about prices, sizes, and payment methods.",
            initialMessage = "Hello! Let me know if you are looking for any particular size or color today.",
            difficulty = "Beginner",
            suggestedPhrases = listOf(
                "Do you have this in a medium size?",
                "How much does this cost?",
                "Can I try this on in the fitting room?",
                "Do you accept card payments?"
            ),
            iconName = "shopping_bag"
        ),
        RolePlayScenario(
            id = "interview",
            title = "Job Interview",
            roleAi = "Hiring Manager",
            roleUser = "Job Applicant",
            objective = "Describe your strengths, experience, and answer standard interview questions.",
            initialMessage = "Welcome to our office. Thank you for applying. To start, could you please tell me about yourself and your background?",
            difficulty = "Advanced",
            suggestedPhrases = listOf(
                "I have over 3 years of experience in...",
                "My key strength is problem solving.",
                "I am eager to contribute to your team.",
                "Could you tell me more about the team culture?"
            ),
            iconName = "work"
        ),
        RolePlayScenario(
            id = "office",
            title = "Office Conversation",
            roleAi = "Team Lead / Project Manager",
            roleUser = "Team Member",
            objective = "Give a project status update and discuss project deadlines.",
            initialMessage = "Good morning! Quick check-in: how is progress on the current project milestone?",
            difficulty = "Intermediate",
            suggestedPhrases = listOf(
                "I've finished the initial draft.",
                "We might need an extra day for testing.",
                "Let's schedule a brief sync meeting.",
                "I will send the report by this afternoon."
            ),
            iconName = "business"
        ),
        RolePlayScenario(
            id = "teacher",
            title = "Speaking with a Teacher",
            roleAi = "English Professor",
            roleUser = "Student",
            objective = "Ask for feedback on an assignment and clarify difficult concepts.",
            initialMessage = "Hello! Come in and have a seat. What part of yesterday's lecture would you like to review?",
            difficulty = "Beginner",
            suggestedPhrases = listOf(
                "I found the grammar rule a bit confusing.",
                "Could you give me an example sentence?",
                "How can I improve my speaking fluency?",
                "Thank you for explaining that clearly."
            ),
            iconName = "school"
        ),
        RolePlayScenario(
            id = "telephone",
            title = "Telephone Conversation",
            roleAi = "Receptionist at a Clinic",
            roleUser = "Caller",
            objective = "Book an appointment and confirm timing over the phone.",
            initialMessage = "Thank you for calling City Wellness Clinic. My name is Sarah. How may I direct your call?",
            difficulty = "Intermediate",
            suggestedPhrases = listOf(
                "I would like to schedule an appointment.",
                "Do you have any availability this Thursday?",
                "Could you repeat that date, please?",
                "Please confirm my booking via text."
            ),
            iconName = "phone"
        ),
        RolePlayScenario(
            id = "travel",
            title = "Travelling & Airport",
            roleAi = "Airline Check-in Agent",
            roleUser = "Passenger",
            objective = "Check baggage, select a window or aisle seat, and find the departure gate.",
            initialMessage = "Good morning, passport and ticket please. How many bags are you checking in today?",
            difficulty = "Intermediate",
            suggestedPhrases = listOf(
                "Here is my passport and booking code.",
                "I just have one checked bag and one carry-on.",
                "Could I please get a window seat?",
                "Which gate does the flight board from?"
            ),
            iconName = "flight"
        ),
        RolePlayScenario(
            id = "smalltalk",
            title = "Everyday Small Talk",
            roleAi = "Friendly Commuter / Barista",
            roleUser = "Yourself",
            objective = "Make light polite conversation while waiting in line.",
            initialMessage = "It's quite a busy morning today, isn't it? Are you heading to work or just enjoying the morning?",
            difficulty = "Beginner",
            suggestedPhrases = listOf(
                "Yes, quite a crowd today!",
                "Just heading to the office nearby.",
                "The weather has been lovely lately.",
                "Have a great day ahead!"
            ),
            iconName = "coffee"
        )
    )
}
