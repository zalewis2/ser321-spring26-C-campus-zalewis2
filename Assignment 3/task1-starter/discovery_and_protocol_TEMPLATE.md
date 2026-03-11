# Task 1.2: Mystery Service Discovery and Protocol Documentation

**Your Name:**
**How I tested:** [Unit Tests / Extended Client / Both]

---

## Part 1: Discovery Log

Document at least 8 test attempts showing your systematic investigation.

### Attempt 1
**Request Sent:**
```json
{
  "type", analyzer
}
```

**Response Received:**
```json
{
  "ok":false,"message":"Field 'action' does not exist in request. Hint: what action do you want to perform?",
}
```

**What I Learned:**
I learned that the analyzer needs and action field.

---

### Attempt 2
**Request Sent:**
```json
{
  "type", analyzer /** and **/ "action", "test"
}
```

**Response Received:**
```json
{
  Got response: {"ok":false,"message":"Field 'text' does not exist in request"}

}
```

**What I Learned:**
I learned I need a text field in the request too.


### Attempt 3
**Request Sent:**
```json
{
  "type", analyzer /** and **/ "action", "test"  /** and **/ "text", "fives"
}
```

**Response Received:**
```json
{
  Got response: {"ok":false,"message":"Action 'test' not supported. Valid actions: wordcount, charcount, search"}
  Action 'test' not supported. Valid actions: wordcount, charcount, search
}
```

**What I Learned:**
I learned the valida actions that analyzer can do.

### Attempt 4
**Request Sent:**
```json
{
  json.put("type", "analyzer");
  json.put("action", "wordcount");
  json.put("text", "fives");
}
```

**Response Received:**
```json
{
  Got response: {"count":1,"action":"wordcount","type":"analyzer","ok":true} 
  Analyzer

}
```

**What I Learned:**
I learned that word count works for a single word.


### Attempt 5
**Request Sent:**
```json
{
  json.put("type", "analyzer");
  json.put("action", "charcount");
  json.put("text", "fives");
}
```

**Response Received:**
```json
{
  Got response: {"count":5,"action":"charcount","type":"analyzer","ok":true}


}
```
**What I Learned:**
I learned charcount seems to work properly, with one word.



### Attempt 6
**Request Sent:**
```json
{
  json.put("type", "analyzer");
  json.put("action", "search");
  json.put("text", "fives");
  
}
```

**Response Received:**
```json
{
  Got response: {"ok":false,"message":"Field 'find' does not exist in request"}

}
```

**What I Learned:**
I need to have a field find, im guessing this will be the  word that is searched for in the text



### Attempt 7
**Request Sent:**
```json
{
  json.put("type", "analyzer");
  json.put("action", "search");
  json.put("text", "hello five");
  json.put("find", "five");
}
```

**Response Received:**
```json
{
  Got response: {"found":true,"find":"five","count":1,"action":"search","positions":[6],"type":"analyzer","ok":true}
}
```

**What I Learned:**
I learned it finds the word. responds with true or false, if found or not, and the position the word starts at.
using indexing ( starting at 0 ) this word starts at index 6.




### Attempt 8
**Request Sent:**
```json
{
  json.put("type", "analyzer");
  json.put("action", "search");
  json.put("text", "hello five");
  json.put("find", "Five");
}
```

**Response Received:**
```json
{
  Got response: {"found":false,"find":"Five","count":0,"action":"search","positions":[],"type":"analyzer","ok":true}

}
```

**What I Learned:**
I learned that the search is case sensitive. So the case of the word being search for, or put into "find", 
case must match exactly in text to be found.

---

[Continue for at least 8 attempts - show your progression from initial testing to complete understanding]












---

## Part 2: Complete Protocol Specification

Follow the same format as Task 1.1 README protocols.

### [Analyzer Tool]

[Analyzer looks at text given from the client side. Three operations/ actions can be done on the text,
word count, character count and search for a word within the text.]


#### [Action: Word Count]

**Request:**
```
{
    "type" : "analyzer", -- type of request
    "action" : "wordcount"  -- count the words in the text
    "text": <String> -- text being analyzed
}
```

**Success Response:**
```
{
    "type" : "analyzer", -- type of request
    "ok" : true
    "action" : "wordcount"  -- count the words in the text
    "count": <int> -- number of words in the text
}
```





#### [Action: Char Count]

**Request:**
```
{
    "type" : "analyzer", -- type of request
    "action" : "charcount"  -- to count characters in the text 
    "text": <String> -- text being analyzed
}
```

**Success Response:**
```
{
    "type" : "analyzer", -- type of request
    "ok" : true
    "action" : "charcount"
    "count": <int> -- number of characters in the text
}
```

#### [Action: Search]

**Request:**
```
{
    "type" : "analyzer", -- type of request
    "action" : "search"  -- to search for the word in the text
    "text": <String> -- text being analyzed
    "find": <String> -- word to be searched for in text ( case sensitive )

}
```

**Success Response:**
```
{
    "type" : "analyzer", -- type of request
    "ok" : true
    "action" : "search"
    "find" : <String> -- The word to be searched for
    "found" : <bool> -- true if word is found, false if not
    "count": <int> -- number of times the word was found
    "positions" : <JSONArray> -- array of starting positions where the words have been found. 
                                 The array is empty if the word is not found.
}
```
**Error Response (Missing action):**
```
{
    "ok" : false, 
    "message" : "Field 'action' does not exist in request. Hint: what action do you want to perform?"
}
```
**Error Response (Missing text):**
```
{
    "ok" : false, 
    "message" : "Field 'text' does not exist in request."
}
```
**Error Response (Missing find):**
```
{
    "ok" : false, 
    "message" : "Field 'find' does not exist in request."
}
```
**Error Response (invalid action):**
```
{
    "ok" : false, 
    "message" : "Action '<action>' not supported. Valid actions: wordcount, charcount, search"
}
```

---

[Document ALL operations you discovered]

---

## Part 3: Summary

**Total Operations Discovered:**
```
three operations, wordcount, charcount, search.
```

**How I approached discovery:**
```
I began with a very basic request to see what I would be 
given in an error message.
```
**Most challenging part:**
```
The most challenging part was thinking about creating a new 
case for and beginning the whole process.
```