Graphetry
=========

A haphazard collection of sentence generation and Markov chains.
---------

There's not so much a stated goal or direction this is going in.  It's the result of many, many tries to make an AIM chatter bot that uses Markov chains to learn and respond to replies.  The short term goal right now is to randomly generate limericks, which means I need to:
- Figure out how to detect rhyme
- Maybe look-up nodes in the database based on rhyme
- Count syllables
- Have a method for generating full limericks (i.e. giving up if there are no rhymes for a word, etc)

Long term, I'd like to:
- Integrate this back into an AIM chatterbot
- Possibly just have a mode where the bot always responds to a sentence with its own rhyming sentence
- Find a reliable way to "score" candidate responses and pick the best one.