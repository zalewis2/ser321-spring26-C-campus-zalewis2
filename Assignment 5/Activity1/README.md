

**How to Run:**

Start the Leader first, workers wont be able to connect otherwise:

    gradle runLeader -Pport=9000

Start each Worker in a separate terminal:

    gradle runWorker -PworkerName=Worker1 -Phost=localhost -Pport=9000
    gradle runWorker -PworkerName=Worker2 -Phost=localhost -Pport=9000
    gradle runWorker -PworkerName=Worker3 -Phost=localhost -Pport=9000

Running things without Gradle being so verbose:

    gradle runLeader -Pport=9000 --console=plain -q
    gradle runWorker -PworkerName=Worker1 -Phost=localhost -Pport=9000 --console=plain -q

Default values if no arguments are provided:

    gradle runLeader              -- defaults to port 9000
    gradle runWorker              -- defaults to Worker1, localhost, port 9000

---

## Protocol ##

This project uses JSON over TCP. All messages are JSON objects.
The Leader acts as the server and Workers act as clients.

### register ###

First thing a worker sends when it connects.

Request:

    {
        "type" : "register",
        "name" : <String> -- the worker's name e.g. "Worker1"
    }

### task ###

Leader sends this to all workers to kick off a round.

Request:

    {
        "type" : "task",
        "expression" : <String> -- arithmetic expression e.g. "5 + 3"
    }

### response ###

Worker sends this back after the user types their answer.

Request:

    {
        "type" : "response",
        "name" : <String>, -- the worker's name
        "answer" : <int>   -- the worker's manually entered answer
    }

### consensus ###

Sent by Leader to all Workers when a majority is reached.

Response:

    {
        "type"   : "consensus",
        "result" : <int>,        -- the winning answer
        "votes"  : <JSONObject>, -- vote distribution e.g. {"42": 4, "43": 1}
        "count"  : <int>,        -- number of workers that agreed on result
        "total"  : <int>         -- total connected workers in the round
    }

### failedConsensus ###

Leader sends this when there was no consensus.

Response:

    {
        "type"  : "failedConsensus",
        "votes" : <JSONObject> -- full vote distribution e.g. {"8": 1, "5": 1, "3": 1}
    }

---

## Consensus Algorithm Design ##

Majority voting. Leader gives everyone 10 seconds to respond, then tallies it up.
To reach consensus an answer needs at least 50% of the total connected workers to agree,
not just whoever responded. If nobody hits that threshold it announces no consensus and
shows how the votes were split.

**Ties:** if two answers tie, whichever one comes up first in the HashMap wins.

---

## How Worker Failures are Handled ##

If a worker drops out mid round the leader catches it and removes them from the group.
The round still finishes with whoever is left. Next round they won't be there.

---

## Edge Cases and Limitations ##

- Need at least 3 workers or the leader just sits there waiting.
- If a worker doesn't respond in time they don't get counted, nothing breaks.
- Workers need to type a whole number, anything else will cause an error on their end.



---

## Issue Encountered ##

**Problem:** The Leader was printing the worker's IP address and port number
instead of the worker's name when a worker connected.

**Diagnosis:** The workerName field in workerHelper was being set in the constructor using
workerSock.getInetAddress() + ":" + workerSock.getPort() before any message had been received
from the worker. The actual worker name was never being read.

**Fix:** The constructor keeps the port-based string as a temporary placeholder. The run() method
now reads the first incoming message, checks that it is a REGISTER message, and overwrites
workerName with the actual name sent by the Worker — so the correct name is set before any
further logging occurs.