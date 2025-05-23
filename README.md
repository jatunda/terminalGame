# Terminal Game
template repository for a very basic game engine for running realtime keyboard-only games in the codespaces terminal. 

## Instructions
- To create a game, create a child class of 'TerminalGame'. 
- To run a game, create an object of your child class, and call `ChildClassObject.play()`
- to end the game, use `initiateShutdown()`

### Terminal Game Required Methods
- `onStart()`: runs once when the game starts, before any updates or keypresses. 
- `onUpdate()`: runs at the set framerate (default: 30 fps). onRender happens right after this. Can set the framerate using `setUpdatesPerSecond(int)`
- `onKeyPress(KeyEvent)`: runs each time a detectable key is pressed. See 'KeyCode.java' class for supported keys.
- `onShutdown()`: runs once after the game is shut down. This is guaranteed to be the last thing your object runs, besides the final render.
- `onRender()`: by default, runs after every update. Make `System.out` print calls here to render to the screen. Screen renders starting from upper left. Screen clears every render. 

## Other Useful Methods
- `TerminalHelper.getTerminalDimensions()`: returns an object that has the width and height of the current terminal

## Examples
Includes example games: Tetris, ArtsyVibes, and TestGame