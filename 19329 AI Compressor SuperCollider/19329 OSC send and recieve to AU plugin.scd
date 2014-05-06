/*
OSC Communication Demonstration between SuperCollider and the AI Compressor Audio Unit Plugin.

A short test file, to test sending

Part of the AI Compressor Plugin Project.
© 2013-2014, Candidate Number 19329
Music Informatics Final Year Project, University of Sussex, Spring 2014
*/

// Make sure the AI Compressor: Analysis Application is not running at the same time.

// 1. Set up SuperCollider to send and recieve from this port number
o.free; o = OSCFunc({ |msg| msg.postln; }, '/AUPort', nil, 6880,);

// 2. Load up an instance of the audio unit plugin in AU Lab (a port number wil be displayed in the post window)

// 3. Set up Port to send to (replace 7001 with the AU port sent from the AU plugin and displayed in the post window)
t = NetAddr("127.0.0.1", 7001); // loopback|

// 4. Send messages to the AU plugin from SuperCollider (change value in arguments to change compressor parameter value)

// makeUp gain to 20 is a good test to show that sending and changing the compressor paraemeters works
t.sendMsg("/makeupgain",15.0);

// Execute one by one or all together
(
t.sendMsg("/ratio",4.0);
t.sendMsg("/threshold",-0.0);
t.sendMsg("/makeupgain",20.0);
t.sendMsg("/attack",300.0);
t.sendMsg("/release",5.0);
)