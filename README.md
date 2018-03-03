/*

EDIT: File updated 03/02/2018 to include remarks on final changes.
		
	HighScoresActivity added.

	Extra-Credit parameters implemented.

	MVC to control high score persistence implemented within StartActivity.

	NOTE:- Pressing the back button during gameplay will forfeit
	the user's ability to have their current score persisted.

*/

This is the first project assignment for the Spring 2018 section
of CMP SCI 5020. It is an adaption of the Simon game first released
by Milton Bradley in 1978. As of the initial commit, this project only
implements the 1-Player mode of the game.

The project directions, as well as a pdf from a 2002 release of the
game are included in the repository for reference.

This interpretation of those two sources contains four activites (with the
fourth, and final, activity listed in the comments above):

	StartActivity

	GameActivity

	ResultsActivity

The StartActivity substitutes for the 'MainActivity', and presents
a screen where the user selects a difficulty level and begins the game.
The project directions loosely specify three levels of difficulty.
However, the MB directions outline four skill levels, numbered 1-4. This
project guilds on MB's outline in this instance (interpreting skill levels
as; easy, medium, hard and advanced).

Each skill level is associated with a maximum sequence length per-round.
As per the MB directions, the app will automatically speed up its execution
of the stored sequence after the 5th, 9th and 13th signals have been added
to the sequence (within the confines of one full execution of the stored
sequence). This interpretation circumvents the project directions where the
difficulty level chosen will determine the speed (and, as well, increase
thereof) of the button flashes.

Milton Bradley's Simon bounds the time between user selections to a maximum
countdown value. Given that this project implements a maximum sequence length,
retaining this aspect made more sense than decrementing the user selection
countdown based on the skill level chosen. The latter would be very easy to
implement. Difficulty levels are grouped together as an enum class, contaning
a data class that holds initial parameters persuant to the difficulty levels.
A parameter already exists within this class called timeToSelect, however it is
currently implemented as a constant across all skill levels. In contrast to MB's
directions, skill levels do also contain an initial-sequence-length parameter that
does mirror the project directions.

In place of maintaining a high-score and current-score display on the GameActivity
screen, this project presents those updates through the use of Toasts so as to
preserve the layout and UI of the app. Current scores are incremented on every valid
selection (not on every valid repetition of a sequence). Only the most current
(greatest) high score is displayed during gameplay.

The StartActivity controls the presentation of the views/activities (similar-ish to
presenting views modally in IOS).

The GameActivity utilizes three fragments:

	SequenceFragment

	TimerFragment

	EndRoundFragment

The first fragment controls thedisplaying of the longest stored sequence. The second
fragment controls the background thread containing the CountDownTimer. Finally, the
third fragment controls the exit process from the GameActivity view.

Buttons are animated using an AnimationDrawable. Associated tones with each
button are executed by means of a MediaPlayer (the tones themselves are short MP3
audio clips created in GarageBand).

This project served as an effective introduction
to the Kotlin programming language.
