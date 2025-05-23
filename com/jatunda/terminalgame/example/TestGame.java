package com.jatunda.terminalgame.example;

import com.jatunda.terminalgame.core.KeyCode;
import com.jatunda.terminalgame.core.KeyEvent;
import com.jatunda.terminalgame.core.TerminalGame;

public class TestGame extends TerminalGame {

    private KeyEvent keyEvent = null;
    private int x = 0;

    @Override
    public void onStart() {
        System.out.println("Game started");
    }

    @Override
    public void onUpdate() {
        x++;
        
    }

    @Override
    public void onKeyPress(KeyEvent keyEvent) {
        this.keyEvent = keyEvent;
        if(keyEvent.keyCode == KeyCode.VK_ESCAPE) {
            initiateShutdown();
        }
    }

    @Override
    public void onRender() {
        System.out.println("Game render");
        System.out.println(System.nanoTime());
        if(keyEvent != null) {
            System.out.println(keyEvent);
        } else {
            System.out.println("Press a key to see it's KeyEvent");
        }
        if(isShuttingDown()) {
            System.out.println("Game shut down");
        }
    }

    @Override
    public void onShutdown() {
    }

    public static void main(String[] args) {
        TestGame game = new TestGame();
        game.play();
    }
}
