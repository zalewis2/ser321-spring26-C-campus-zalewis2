# Assignment 3 Task 2: Hangman Game Protocol

**Author:** [Your Name]
**Date:** [Date]

---

## How to Run
You can use Gradle to run things, running with ./gradlew is of course also an option
**Server:**
Default
```bash
gradle Server
```

With arguments
```bash
gradle Server -Pport=8888
```

**Client:**
Default but running more quietly on Gradle
```bash
gradle Client --console=plain -q
```

With arguments
```bash
gradle Client -Phost=localhost -Pport=8888
```

---

## Video Demonstration

**Link:** [Insert link to your 4-7 minute video demonstration here]

The video demonstrates:
- Starting server and client
- Complete game playthrough
- All implemented features

---

## Implemented Features Checklist

### Core Features (Required)
- [x] Set Player Name (provided as example)
- [ ] Start New Game
- [ ] Guess Letter
- [ ] Game State
- [ ] Win/Lose Detection
- [x] Graceful Quit

### Medium Features (Enhanced Gameplay)
- [ ] Hint feature
- [ ] Word Guessing
- [ ] Guessed Letters Command
- [ ] Give Up

### Advanced Features (Competition)
- [ ] Scoring System
- [ ] Leaderboard

**Note:** Mark [x] for completed features, [ ] for not implemented.

---

## Protocol Specification

### Overview
[Provide a brief overview of your protocol design - what patterns did you use, how does communication work, etc.]

---

### 1. Set Player Name

**Request:**
```json
{
    "type": "name",
    "name": "<string>"
}
```

**Success Response:**
```json
{
    "type": "name",
    "ok": true,
    "message": "Welcome <name>! ..."
}
```

**Error Response:**
```json
{
    "ok": false,
    "message": "Name cannot be empty"
}
```

---

### 2. Start New Game

**Request:**
```json
{
  "type" : "start",
}
```

**Success Response:**
```json
{
  "type" : "start",
  "ok" : true,
  "hiddenWord" : <String>, -- the word to be guessed but filled with underscores, not letters
  "wordLength" : <int>, -- number of letters in the word
  "stage" : <String>, -- art of the current game stage
  "misses" : <int>, -- 0 at start
  "points" : <int>, -- 0 at start
  "message" : <String>, -- message at the beginning of the game

}
```

**Error Response(Name not set):**
```json
{
  "ok" : false,
  "message" : "Please set your name before you start the game."
}
```
**Error Response(Game already going):**
```json
{
  "ok" : false,
  "message" : "A game is already in session."
}
```

### 3. Guess Letter

**Request:**
```json
{
  "type" : "guess",
  "letter" : <String>, -- letter to be guessed
}
```

**Success Response (letter guessed was right):**
```json
{
  "type" : "guess",
  "ok" : true,
  "correct" : true, 
  "letter" : <String>, -- guessed letter
  "hiddenWord" : <String>, -- word display updated
  "stage" : <String>, -- art of the current game stage
  "misses" : <int>, 
  "points" : <int>, 
  "gameOver" : false, 
  "message" : <String>, -- message after the guess

}

```
**Success Response (letter guessed was right, and game over):**
```json
{
  "type" : "start",
  "ok" : true,
  "correct" : true, 
  "letter" : <int>, -- guessed letter
  "hiddenWord" : <String>, -- word display updated
  "stage" : <String>, -- art of the current game stage
  "misses" : <int>, 
  "points" : <int>, 
  "gameOver" : true, --,
  "win" : true,
  "solution" : <String>, the full hidden word, shown
  "message" : <String>, -- message after the guess

}
```
**Success Response (letter guessed was wrong):**
```json
{
  "type" : "start",
  "ok" : true,
  "correct" : false, 
  "letter" : <int>, -- guessed letter
  "hiddenWord" : <String>, -- word display updated
  "stage" : <String>, -- art of the current game stage
  "misses" : <int>, 
  "points" : <int>, 
  "gameOver" : false, -- 
  "message" : <String>, -- message after the guess

}
```
**Success Response (letter guessed was wrong, and game over):**
```json
{
  "type" : "start",
  "ok" : true,
  "correct" : false, 
  "letter" : <int>, -- guessed letter
  "hiddenWord" : <String>, -- word display updated
  "stage" : <String>, -- art of the current game stage
  "misses" : <int>, 
  "points" : <int>, 
  "gameOver" : true, --,
  "win" : false,
  "solution" : <String>, the full hidden word, shown
  "message" : <String>, -- message after the guess

}
```

**Error Response(Guess not just a letter.):**
```json
{
  "ok" : false,
  "message" : "Guess can only be one letter."
}
```
**Error Response(Letter has already been guessed.):**
```json
{
  "ok" : false,
  "message" : "The letter 'a' has been guessed already."
}
```
**Error Response(No game going on.):**
```json
{
  "ok" : false,
  "message" : "There is no active game. You may start a new one."
}
```

### 4. Guess Word

**Request:**
```json
{
  "type" : "guessword",
  "word" : <String>, -- word to be guessed
}
```

**Success Response (Guess right, game won):**
```json
{
  "type" : "guessword",
  "ok" : true,
  "correct" : true, 
  "solution" : <String>,
  "points" : <int>, 
  "gameOver" : true, 
  "win" : true,
  "message" : <String>, -- message showing player guess the word.

}

```
**Success Response (Guess wrong, game not won):**
```json
{
  "type" : "guessword",
  "ok" : true,
  "correct" : true,
  "hiddenWord" : <String>, -- word display updated
  "solution" : <String>,
  "stage" : <String>, -- art of the current game stage
  "misses" : <int> -- now increased by 2 points
  "points" : <int>,
  "gameOver" : false,
  "message" : <String>, -- message showing player guess the word.
          
}
```
**Error Response(No game going on.):**
```json
{
  "ok" : false,
  "message" : "There is no active game. You may start a new one."
}
```
### 5. Game State

**Request:**
```json
{
    "type": "gamestate",
  
}
```

**Success Response ():**
```json
{
  "type" : "gamestate",
  "ok" : true,
  "hiddenWord" : <String>, -- word display updated
  "stage" : <String>, -- art of the current game stage
  "misses" : <int>, 
  "points" : <int>,
  "message" : <String>, -- 

}
```
**Error Response(No game going on.):**
```json
{
  "ok" : false,
  "message" : "There is no active game. You may start a new one."
}
```
### 6. Guessed Letters

**Request:**
```json
{
    "type": "guessedletters",
}
```
**Success Response ():**
```json
{
  "type" : "guessedletters",
  "ok" : true,
  "hiddenWord" : <JSONArray>, JSONArray of the guessed letters

}
```
**Error Response(No game going on.):**
```json
{
  "ok" : false,
  "message" : "There is no active game. You may start a new one."
}
```

### 7. Hint

**Request:**
```json
{
    "type": "hint",
}
```
**Success Response ():**
```json
{
  "type" : "hint",
  "ok" : true,
  "letter" : <String>, -- the revealed letter
  "hiddenWord" : <JSONArray>, JSONArray of the guessed letters
  "points" : <int>, -- points shown is the amount after the deduction
  "message" : <String>, -- hint to whether or not a letter is in the word or not, deducts 8 points


}
```
**Error Response(No game going on.):**
```json
{
  "ok" : false,
  "message" : "There is no active game. You may start a new one."
}
```

### 8. Hint

**Request:**
```json
{
    "type": "giveup",
}
```
**Success Response ():**
```json
{
  "type" : "giveup",
  "ok" : true,
  "solution" : <String>, -- the revealed word
  "message" : <String>, -- hint to whether or not a letter is in the word or not, deducts 8 points


}
```
**Error Response(No game going on.):**
```json
{
  "ok" : false,
  "message" : "There is no active game. You may start a new one."
}
```



## Error Handling Strategy

[Explain your approach to error handling:]

**Server-side validation:**
- [What validations does your server perform?]
  <Your answer>

- [How do you handle missing fields?]
  <Your answer>

- [How do you handle invalid data types?]
  <Your answer>

- [How do you handle game state errors?]
  <Your answer>

---

## Robustness

[Explain how you ensured robustness:]

**Server robustness:**
- [How does server handle invalid input without crashing?]
- <Your answer>


**Client robustness:**
- [How does client handle unexpected responses?]
- <Your answer>

- [What happens if server is unavailable?]
- <Your answer>

---

## Assumptions (if applicable)

[List any assumptions you made about the protocol or game rules]

1. [Assumption 1]
2. [Assumption 2]
3. [etc.]

---

## Known Issues

[List any known bugs or limitations]

1. [Issue 1]
2. [Issue 2]

---
