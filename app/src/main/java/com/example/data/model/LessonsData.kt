package com.example.data.model

object LessonsData {
    val lessons: List<DailyLesson> = listOf(
        DailyLesson(
            id = "lesson_intro",
            title = "Self-Introduction Essentials",
            category = "Beginner Conversation",
            durationMinutes = 8,
            difficulty = "Beginner",
            objective = "Learn how to comfortably introduce yourself, your job, and your hometown in 3 clear sentences.",
            steps = listOf(
                LessonStep(
                    title = "Greeting & Name",
                    explanation = "Start politely with 'Hello' or 'Hi', then state your full or preferred name.",
                    exampleEn = "Hello! My name is Tanvir, but you can call me Tan.",
                    exampleBn = "হ্যালো! আমার নাম তানভীর, কিন্তু আপনি আমাকে তান বলে ডাকতে পারেন।",
                    speakingPrompt = "Speak aloud introducing yourself with your preferred nickname.",
                    expectedKeywords = listOf("name", "call", "hello", "hi", "am")
                ),
                LessonStep(
                    title = "Where You Are From",
                    explanation = "Use 'I am from...' or 'I grew up in...' to mention your city or country.",
                    exampleEn = "I am originally from Dhaka, Bangladesh.",
                    exampleBn = "আমি মূলত ঢাকা, বাংলাদেশ থেকে এসেছি।",
                    speakingPrompt = "Say aloud which city or district you are from.",
                    expectedKeywords = listOf("from", "live", "originally", "born")
                ),
                LessonStep(
                    title = "Profession & Hobbies",
                    explanation = "State what you do and one thing you enjoy in your spare time.",
                    exampleEn = "I work as a software engineer and I love playing cricket.",
                    exampleBn = "আমি একজন সফটওয়্যার ইঞ্জিনিয়ার হিসেবে কাজ করি এবং ক্রিকেট খেলতে ভালোবাসি।",
                    speakingPrompt = "Say what your profession is or what you love doing in free time.",
                    expectedKeywords = listOf("work", "student", "enjoy", "like", "love")
                )
            )
        ),
        DailyLesson(
            id = "lesson_routine",
            title = "Daily Routine & Habits",
            category = "Common Daily Sentences",
            durationMinutes = 10,
            difficulty = "Beginner",
            objective = "Master simple present tense to describe what you do from morning to evening.",
            steps = listOf(
                LessonStep(
                    title = "Morning Routine",
                    explanation = "Use Present Simple (V1) without 'am' or 'is' for habitual actions.",
                    exampleEn = "I wake up at 7 AM and have a cup of tea.",
                    exampleBn = "আমি সকাল ৭টায় ঘুম থেকে উঠি এবং এক কাপ চা খাই।",
                    speakingPrompt = "Say what time you wake up and what you drink in the morning.",
                    expectedKeywords = listOf("wake", "get up", "tea", "coffee", "breakfast")
                ),
                LessonStep(
                    title = "Work or Study",
                    explanation = "Connect actions with 'then' and 'after that'.",
                    exampleEn = "Then I start work at 9 AM and attend my morning meeting.",
                    exampleBn = "তারপর আমি সকাল ৯টায় কাজ শুরু করি এবং মিটিংয়ে অংশ নিই।",
                    speakingPrompt = "Say when you start work or study each day.",
                    expectedKeywords = listOf("start", "work", "study", "at", "attend")
                ),
                LessonStep(
                    title = "Evening Wind Down",
                    explanation = "Describe how your day ends.",
                    exampleEn = "In the evening, I spend time with my family and read books.",
                    exampleBn = "সন্ধ্যায় আমি পরিবারের সাথে সময় কাটাই এবং বই পড়ি।",
                    speakingPrompt = "Tell us what you normally do in the evening.",
                    expectedKeywords = listOf("evening", "family", "read", "relax", "sleep")
                )
            )
        ),
        DailyLesson(
            id = "lesson_questions",
            title = "Asking Questions with Do, Does & Did",
            category = "Question and Answer Practice",
            durationMinutes = 10,
            difficulty = "Beginner",
            objective = "Learn how to ask questions correctly without mixing up auxiliary verbs.",
            steps = listOf(
                LessonStep(
                    title = "Using 'Do' (I, You, We, They)",
                    explanation = "Do you + base verb? Never add 's' or 'ed' after Do.",
                    exampleEn = "Do you speak English every day?",
                    exampleBn = "তুমি কি প্রতিদিন ইংরেজিতে কথা বলো?",
                    speakingPrompt = "Ask the tutor if they enjoy listening to music.",
                    expectedKeywords = listOf("do", "you", "like", "listen", "music")
                ),
                LessonStep(
                    title = "Using 'Does' (He, She, It)",
                    explanation = "Does he/she + base verb? The 's' is already in 'Does'!",
                    exampleEn = "Does your brother work in an office?",
                    exampleBn = "তোমার ভাই কি অফিসে কাজ করেন?",
                    speakingPrompt = "Ask a question starting with 'Does he' or 'Does she'.",
                    expectedKeywords = listOf("does", "he", "she", "work", "live")
                ),
                LessonStep(
                    title = "Using 'Did' (Past Tense Questions)",
                    explanation = "Did + subject + base verb! Notice we say 'Did you go?', NOT 'Did you went?'.",
                    exampleEn = "Did you watch the cricket match yesterday?",
                    exampleBn = "তুমি কি গতকাল ক্রিকেট ম্যাচটি দেখেছিলে?",
                    speakingPrompt = "Ask the tutor what they did yesterday using 'Did you...'.",
                    expectedKeywords = listOf("did", "you", "yesterday", "watch", "see")
                )
            )
        ),
        DailyLesson(
            id = "lesson_past_tense",
            title = "Talking About Yesterday",
            category = "Present, Past, and Future Tense",
            durationMinutes = 12,
            difficulty = "Intermediate",
            objective = "Confidently share past events using irregular verbs (went, saw, ate, had).",
            steps = listOf(
                LessonStep(
                    title = "Irregular Past Verbs",
                    explanation = "Go becomes Went, Eat becomes Ate, Buy becomes Bought.",
                    exampleEn = "Yesterday I went to the market and bought fresh fruit.",
                    exampleBn = "গতকাল আমি বাজারে গিয়েছিলাম এবং তাজা ফল কিনেছিলাম।",
                    speakingPrompt = "Say two things you did yesterday using 'went' or 'bought'.",
                    expectedKeywords = listOf("yesterday", "went", "bought", "saw", "had")
                ),
                LessonStep(
                    title = "Negative Past Sentences",
                    explanation = "Use 'didn't' + base verb (V1). E.g., 'I didn't have time', NOT 'didn't had'.",
                    exampleEn = "I didn't go outside because it rained heavily.",
                    exampleBn = "ভারী বৃষ্টি হওয়ার কারণে আমি বাইরে যাইনি।",
                    speakingPrompt = "State something you did not do yesterday using 'didn't'.",
                    expectedKeywords = listOf("didn't", "not", "could", "because")
                )
            )
        ),
        DailyLesson(
            id = "lesson_prepositions",
            title = "Prepositions in Context (In, On, At)",
            category = "Vocabulary in Context",
            durationMinutes = 9,
            difficulty = "Beginner",
            objective = "Never confuse 'at 5 PM', 'on Monday', and 'in December' again.",
            steps = listOf(
                LessonStep(
                    title = "Time Prepositions",
                    explanation = "AT for exact times (at 3 PM), ON for days (on Friday), IN for months/years (in July).",
                    exampleEn = "The meeting starts at 10 AM on Monday.",
                    exampleBn = "সোমবার সকাল ১০টায় মিটিং শুরু হবে।",
                    speakingPrompt = "Say a complete sentence using both 'at' and 'on'.",
                    expectedKeywords = listOf("at", "on", "am", "pm", "day")
                ),
                LessonStep(
                    title = "Place Prepositions",
                    explanation = "AT for specific points (at home, at the bus stop), IN for enclosed spaces or cities (in Dhaka).",
                    exampleEn = "I am waiting at the bus stop in front of the bank.",
                    exampleBn = "আমি ব্যাংকের সামনে বাস স্টপে অপেক্ষা করছি।",
                    speakingPrompt = "Say where you currently are using 'at' or 'in'.",
                    expectedKeywords = listOf("at", "in", "home", "office", "room")
                )
            )
        ),
        DailyLesson(
            id = "lesson_natural_expressions",
            title = "Natural English Expressions",
            category = "Natural English Expressions",
            durationMinutes = 10,
            difficulty = "Intermediate",
            objective = "Replace stiff textbook phrases with natural, polite spoken English.",
            steps = listOf(
                LessonStep(
                    title = "Polite Requests",
                    explanation = "Instead of 'Give me water', say 'Could I please have a glass of water?'",
                    exampleEn = "Could you please give me a hand with this bag?",
                    exampleBn = "আপনি কি দয়া করে এই ব্যাগটি তুলতে একটু সাহায্য করবেন?",
                    speakingPrompt = "Make a polite request starting with 'Could you please...'",
                    expectedKeywords = listOf("could", "please", "would", "mind")
                ),
                LessonStep(
                    title = "Expressing Agreement & Opinions",
                    explanation = "Instead of 'I am agree', say 'I agree' or 'I see your point'.",
                    exampleEn = "I see your point, and I completely agree with you.",
                    exampleBn = "আমি আপনার বক্তব্য বুঝতে পেরেছি এবং আপনার সাথে সম্পূর্ণ একমত।",
                    speakingPrompt = "Say that you agree with the tutor on something.",
                    expectedKeywords = listOf("agree", "point", "think", "opinion")
                )
            )
        ),
        DailyLesson(
            id = "lesson_listening_response",
            title = "Listening & Quick Responses",
            category = "Listening and Response Practice",
            durationMinutes = 10,
            difficulty = "Beginner",
            objective = "Practice giving spontaneous, confident conversational answers without hesitation.",
            steps = listOf(
                LessonStep(
                    title = "Answering 'How was your day?'",
                    explanation = "Give a concise 2-sentence response with an adjective and a reason.",
                    exampleEn = "It was pretty productive! I managed to finish all my tasks on time.",
                    exampleBn = "দিনটি বেশ ফলপ্রসূ ছিল! আমি সময়মতো সব কাজ শেষ করতে পেরেছি।",
                    speakingPrompt = "Answer the question: How has your day been so far?",
                    expectedKeywords = listOf("day", "good", "busy", "fine", "great", "productive")
                ),
                LessonStep(
                    title = "Asking a Return Question",
                    explanation = "Keep the conversation flowing by asking 'How about you?' or 'What about your day?'",
                    exampleEn = "I had a busy morning. How about you?",
                    exampleBn = "আমার সকালটা বেশ ব্যস্ত কেটেছে। আপনার কেমন ছিল?",
                    speakingPrompt = "Respond to the tutor and ask them how their day is going.",
                    expectedKeywords = listOf("how", "about", "you", "what", "and")
                )
            )
        )
    )
}
