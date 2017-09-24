package com.henriksmeds.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import static com.badlogic.gdx.Gdx.files;

/**
 * Created by Henrik on 2017-05-30.
 */

public class HighScore {
    private static FileHandle fileHandle;
    public static int[] scores = new int[] {0, 0, 0, 0, 0};

    private HighScore() {
    }

    public static void load() {

        try {
            fileHandle = Gdx.files.internal("scores.txt");
            if(!fileHandle.readString().isEmpty()) {
                String[] string = fileHandle.readString().split("\n");
                for(int i = 0; i < 5; i++) {
                    scores[i] = Integer.parseInt(string[i]);
                }
                fileHandle.delete();

            }

        } catch (Exception e) {
            System.out.print("WARNING: External Storage not available");
        }

        }

    public static void save() {
        try {
            fileHandle = Gdx.files.internal("scores.txt");
            for(int i = 0; i < 5; i++) {
                fileHandle.writeString(Integer.toString(scores[i]) + "\n", true);
            }

        } catch (Exception e) {
            System.out.println("WARNING: External Storage not available");
        }
    }

    public static void addScore1(int Score) {
        for(int i = 4; i >= 0; i--) {
            if(Score > scores[i]) {
                for(int j = i; j > 0; j--) {
                    scores[j - 1] = scores[j];
                }
                scores[i] = Score;
                break;
            }
        }
    }

    public static void addScore (int score) {
        for (int i = 0; i < 5; i++) {
            if (scores[i] < score) {
                for (int j = 4; j > i; j--)
                    scores[j] = scores[j - 1];
                scores[i] = score;
                break;
            }
        }
    }

}
