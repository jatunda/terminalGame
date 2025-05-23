package com.jatunda.terminalgame.core;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.jatunda.terminalgame.util.*;

public abstract class TerminalGame {

    private static boolean shouldShutDown = false;
    private float updatesPerSecond = 30;
    private ScheduledExecutorService updateExecutor;
    private boolean shouldRenderAfterUpdate = true;
    private boolean shouldRenderAfterKeyPress = false;
    private boolean shouldRenderAfterShutdown = true;

    public final void play() {
        // set console to raw mode
        // raw mode is how we are able to get instant input for onKeyPress
        TerminalHelper.setTerminalRawMode(true);
        TerminalHelper.setTerminalWrapping(false);
        TerminalHelper.clearScreenANSI();

        onStart();
        startUpdateExecutor();
        Thread keypressThread = startOnKeypressThread();

        while (!shouldShutDown) {
            try {
                Thread.sleep(1); // sleep for 1ms to prevent busy waiting
            } catch (InterruptedException e) {
                initiateShutdown();
                System.out.println("InterruptedException in TerminalManager::main");
                System.out.println(e.getStackTrace());
                System.out.println(e.getMessage());
            }
        }

        updateExecutor.shutdown();
        keypressThread.interrupt();
        onShutdown();
        if (shouldRenderAfterShutdown) render();

        TerminalHelper.setTerminalWrapping(true);
        TerminalHelper.setTerminalRawMode(false);
    }

    private Thread startOnKeypressThread() {
        Thread keypressThread = new Thread() {
            public void run() {
                try {
                    while (!this.isInterrupted()) {
                        KeyEvent keyEvent = readKeyEvent(System.console().reader());
                        if (!this.isInterrupted()) {
                            onKeyPress(keyEvent);
                            if (shouldRenderAfterKeyPress) {
                                render();
                            }
                        }
                    }
                } catch (IOException e) {
                    initiateShutdown();
                    System.out.println("IOException in TerminalManager::main");
                    System.out.println(e.getStackTrace());
                    System.out.println(e.getMessage());
                }
            }
        };
        keypressThread.start();
        return keypressThread;
    }  

    private KeyEvent readKeyEvent(Reader reader) throws IOException {
        char[] cbuff = new char[10];
        if (reader.read(cbuff) == -1) return null;
        KeyCode keyCode = KeyCode.fromCharArray(cbuff);
        return keyCode == null ? null : new KeyEvent(System.nanoTime(), keyCode);
    }

    private void render() {

        // redirect System.out, and capture myGame.render()
        PrintStream originalOut = System.out; // Save original System.out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        onRender();
        System.out.flush();
        System.setOut(originalOut); // Restore original System.out
        String capturedOutput = baos.toString();

        // Split capturedOutput into lines and store in frameBuffer
        List<String> newFrame = Arrays.asList(capturedOutput.split("\\R"));
        if(newFrame == null) newFrame = new ArrayList<>();

        String output = "";

        // create output
        TerminalDimensions td = TerminalHelper.getTerminalDimensions();
        for (int i = 0; i < td.height-1; i++) {
            if( i != 0) {
                output += "\n\r";
            }
            
            if(newFrame.size() <= i) {
                // if we are out of lines, fill with empty lines
                output += " ".repeat(td.width);
                continue;
            }

            // add output one line at a time
            // if the line is too long, truncate it
            // if the line is too short, pad it with spaces
            int pad = td.width - newFrame.get(i).length();

            String line = newFrame.get(i).substring(0, Math.min(td.width, newFrame.get(i).length()));
            output += line + " ".repeat(Math.max(0, pad));
        }

        TerminalHelper.moveCursor(1, 1);
        System.out.println(output);
  }

    private void startUpdateExecutor() {
        updateExecutor = Executors.newSingleThreadScheduledExecutor();
        long nanosPerUpdate = (long) (1_000_000_000L / updatesPerSecond);
        updateExecutor.scheduleAtFixedRate(() -> {
            if (!shouldShutDown) {
                onUpdate();
                if (shouldRenderAfterUpdate) render();
            }
        }, 0, nanosPerUpdate, TimeUnit.NANOSECONDS);
    }

    public void setUpdatesPerSecond(float newRate) {
        updatesPerSecond = newRate;
        if (updateExecutor != null && !updateExecutor.isShutdown()) {
            updateExecutor.shutdown();
        }
        startUpdateExecutor();
    }

    public void initiateShutdown() { shouldShutDown = true; }
    public void setShouldRenderAfterUpdate(boolean v) { shouldRenderAfterUpdate = v; }
    public void setShouldRenderAfterKeyPress(boolean v) { shouldRenderAfterKeyPress = v; }
    public void setShouldRenderAfterShutdown(boolean v) { shouldRenderAfterShutdown = v; }
    public boolean isShuttingDown() { return shouldShutDown; }
    
    public abstract void onStart();
    public abstract void onUpdate();
    public abstract void onKeyPress(KeyEvent keyEvent);
    public abstract void onRender();
    public abstract void onShutdown();

}
