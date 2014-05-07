
// if you want to set a different port for your standalone.
// s = Server("standalone_name",NetAddr("localhost",57556));
// Server.default = s;

// Set my own for the project and state in the read me that it uses this port (a potential downside)

s.boot;
s.doWhenBooted{
	AICompressor();
};


// "http://new-supercollider-mailing-lists-forums-use-these.2681727.n2.nabble.com/SCMIR-and-SC-3-5-rc3-td7342938.html"

// Above link is problem 