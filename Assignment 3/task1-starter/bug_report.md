# StringConcatenation Debugging Exercise

## Overview
The stringconcatenation service is implemented in both client and server, but has **4 bugs** that prevent it from working correctly according to the protocol specification.

The Correct Protocol is in the README.md

---

## The 4 Bugs

### Bug #1:  String concat wrong reference

**Location:** `SockServer.java`, line 191

**The Problem:**
```Describe
The field was set the the wrong variable, concact.
```

**The Fix:**
```Solution
It needs to be stringconcatenation instead.
```

**Why it matters:**
The client checks the exact name, stringconcatenation, when using "type". Without being the correct name exactly,
it never retrieves correctly.

**How did you find this:**
I was scrolling through and saw no other locations of concat, then I realized it did not match.

### Bug #2:  Result not combined.

**Location:** `SockServer.java`, line 198

**The Problem:**
```Describe
The result field was named as combined.
```

**The Fix:**
```Solution
Change the name to result instead of combined.
```

**Why it matters:**
The client looks for the reference "result" and not "combined". If not changed it will not run properly.

**How did you find this:**
Again I was searching for the bugs, reading through, and realized the "combined" looked out of place.


### Bug #3:  String1/2 not str1/2

**Location:** `SockClient.java`, line 77

**The Problem:**
```Describe
Case 3, the first string was sent as str1.
```

**The Fix:**
```Solution
Change str1 to String1, this matches what is created above.
```

**Why it matters:**
The server would try to call for string1 in a req.getString... but wouldn't get anything if the name was incorrect.

**How did you find this:**
I saw that the names given to the strings were not str1 / 2, and 
                                            then when I saw these labeled as such I knew it was wrong.


### Bug #4:  String not Int

**Location:** `SockClient.java`, line 198

**The Problem:**
```Describe
Response handler was using res.getInt("result"). The result is not an int.
```

**The Fix:**
```Solution
To fix this change getInt to getString, res.getString("result").
```

**Why it matters:**
When running it will cause and error, since it is type clashing.

**How did you find this:**
I was running and noticed from the errors being given.




