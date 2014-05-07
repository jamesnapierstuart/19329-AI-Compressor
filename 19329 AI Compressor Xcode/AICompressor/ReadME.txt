AI COMPRESSOR:

CANDIDATE NUMBER: 19329
MUSIC INFORMATICS FINAL YEAR PROJECT
University of Sussex
MAY 2014

BUILT IN XCODE 4.6.2 
MAC OSX VERSION 10.8.4
Tested in AU Lab and Logic Pro 9
--------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------

AI Compressor About:

An Audio Unit Plugin for digital audio workstations, the AI Compressor is an Intelligent Autonomous Dynamic Range Compressor. When using, the plugin launches an external application "AI Compressor: Machine Listening Application". Drag and drop the current audio you have the AI plugin on, into the external application and click analyse. This extracts information from the audio and determines the most suitable compressor parameters for the audio track. The external application automatically sends and updates the threshold, ratio, attack, release and makeup gain values of the AU plugin.

The plugin can also be used individually from the host application as standalone. Automation and presets capabilities are all available.
--------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------

Install Via Xcode Execution:

1. Open up the AICompressor.xcodeproj file, and click run at the top left of the project. Some minor errors might flag but these don't impede the audio plugin.
2. Next click on the 'show the project navigator' icon, underneath the run button. This will show the project files. At the bottom is a folder 'products' click down the triangle to reveal its contents.
3. Click on the AICompressor.component and on the right if 'file inspector' is selected (also on the right side as a file icon) there will be a text field that reads 'full path'.
At the end of the full path will be an arrow. Click the arrow.
4. This reveals the location of the AICompressor Component in the finder.
5. Copy and Paste the component to /MacintoshHD/Library/Audio/Plug-Ins/Components
6. This should now have installed the plugin, if not another extra step can be taken by 
Copy and Pasting the component to the user Library/Audio/Plug-Ins/Components
(On Recent Mac OS' the library is hidden, to find it - open a new finder window, click go at the top of the screen, hold the alt/option key and then the library will show up).
7. Test by loading AU Lab, and under effects there should now be an extra plugin titled:
Jamie Stuart 19329 Audio

The code assumes you have the Core Audio Utility Classes package installed in
`/Library/Developer/CoreAudio`
[Link to the classes] https://developer.apple.com/library/mac/samplecode/CoreAudioUtilityClasses/CoreAudioUtilityClasses.zip
-------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------

Install Via Disk Image/Component:

The Disk Image file will give the option of dragging and dropping the AI Compressor to the correct library folders for Mac OSX users. However, if this option isn't available then follow these steps:

1. Drag the AU Plugin to /Macintosh HD/Library/Audio/Plug-Ins/Components

2. Step one should take care of initialling the AU plugin, however another extra measure   is to drag the AU Plugin to your user library but this shouldn't be required.
Drag the AU Plugin to Users/'your user name'/library/Audio/Plug-Ins/Components 
(On Recent Mac OS' the library is hidden, to find it - open a new finder window, click go at the top of the screen, hold the alt/option key and then the library will show up).
-------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------

Development of this AU Plugin was possible from using the modified Template AU from Lorenzo Stoakes (https://github.com/lorenzo-stoakes/TemplateAU, last accessed 24/04/14).
Notes from his original read me regarding the Fred Ghilini template and AU tutorial can be found below.
-------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------

# TemplateAU #

This is a slightly modified version of the code described by [Fred Ghilini][0] in his excellent
[blog post][1], adjusted to be compatible with XCode 4.6.3 and OS.X 10.8 and to build a dual
32/64-bit AU.

The code assumes you have the Core Audio Utility Classes package installed in
`/Library/Developer/CoreAudio` - [download link][2] / [development library page][3] - note zip
contains a 'CoreAudio' folder, create `/Library/Developer` if it doesn't already exist and move
this folder into it.

I have slightly adjusted the description fields in the project (sorry Fred, I wanted to keep
them totally neutral :) so to run the final validation test as described in the blog post you
need to run the following:-

    auval -v aufx TEST TEST
    auval -64 -v aufx TEST TEST

The purpose of the project is to provide a basis for developing [Audio Units][4] in OS X -
unfortunately Apple's material on this is very outdated + without Fred providing the means to
get to this point, getting started would be quite considerably more painful.

[0]:http://sample-hold.com/about/
[1]:http://sample-hold.com/2011/11/23/getting-started-with-audio-units-on-os-x-lion-and-xcode-4-2-1/
[2]:https://developer.apple.com/library/mac/samplecode/CoreAudioUtilityClasses/CoreAudioUtilityClasses.zip
[3]:https://developer.apple.com/library/mac/samplecode/CoreAudioUtilityClasses/Introduction/Intro.html
[4]:http://en.wikipedia.org/wiki/Audio_Units

--------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------