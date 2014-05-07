+ OSXPlatform {

	startupFiles {
		^[];
	}

	startup {

		helpDir = this.systemAppSupportDir++"/Help";

		// Server setup
		Server.program = "./Resources/scsynth -U plugins -D 0";

		// Reset the Score to find scsynth (mandatory for SCMIR USE)
		Score.program = Platform.resourceDir +/+ "/scsynth";

		// Reset SynthDef and Plugin Paths
	"SC_SYNTHDEF_PATH".setenv("./synthdefs/");

		"SC_PLUGIN_PATH".setenv("./plugins/");

		"The Synth def Path 1".postln;
	"SC_SYNTHDEF_PATH".getenv.postln;

		"The SC Plugin Path".postln;
		"SC_PLUGIN_PATH".getenv.postln;


		// SynthDef directory
		SynthDef.synthDefDir = ("./synthdefs/");

		// load user startup file
		//this.loadStartupFiles;
	}

}



// The above should be PlatformDir -----