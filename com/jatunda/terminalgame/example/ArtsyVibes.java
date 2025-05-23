package com.jatunda.terminalgame.example;

import com.jatunda.terminalgame.core.KeyEvent;
import com.jatunda.terminalgame.core.TerminalGame;
import com.jatunda.terminalgame.util.TerminalDimensions;
import com.jatunda.terminalgame.util.TerminalHelper;

public class ArtsyVibes extends TerminalGame{

    char[][] map;

    @Override
    public void onStart() {
        TerminalDimensions td = TerminalHelper.getTerminalDimensions();
        map = new char[td.height-1][td.width];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = randomChar();
            }
        }
    }

    @Override
    public void onUpdate() {
        TerminalDimensions td = TerminalHelper.getTerminalDimensions();
        if(map == null || td.width != map[0].length || td.height-1 != map.length) {
            map = new char[td.height-1][td.width];
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    map[i][j] = randomChar();
                }
            }
        } else {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (Math.random() < 0.05) {
                        map[i][j] = randomChar();
                    }
                }
            }
        }
    }

    @Override
    public void onKeyPress(KeyEvent keyEvent) {
    }

    @Override
    public void onRender() {
         for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    @Override
    public void onShutdown() {
    }

    private char randomChar() {
        return (char) ('a' + Math.random() * 26);
    }


    public static void main(String[] args) {
        ArtsyVibes game = new ArtsyVibes();
        game.play();
    }
    
}
