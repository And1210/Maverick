package maverick;

import java.awt.*;
import static maverick.Maverick.*;

public class GameManager {

    int toSpawn = 0;
    int wave = 0;
    int level;
    double lastSpawn;
    double spawnRate = 2000000000;
    double waveChange = 0;

    public GameManager(int level_) {
        level = level_;
        lives = 3;
        score = 0;

        bg = new Background(0, 0, level);
        initializePlayer();

        waveCheck();
        toSpawn = 5 + level;
        lastSpawn = System.nanoTime();
    }

    public void update() {
        spawnEnemies();

        waveCheck();
        waveAnimation();
    }

    public void initializePlayer() {
        p = new Player(width() / 2, height() / 2, 0, 0, 10, new Color(0, 255, 0), keyHandler, playerType);
        p.applyForce(force(p.terminalVelocity, 0.000001));
        p.setSprite(planeSprites[p.type][12]);
    }

    public void spawnEnemies() {
        if (toSpawn > 0 && enemies.size() < 20) {
            double timer = System.nanoTime() - lastSpawn;
            if (timer > spawnRate) {
                addEnemies(1);
                toSpawn--;
                lastSpawn = System.nanoTime();
            }
        }
    }

    public void waveCheck() {
        if (toSpawn <= 0 && enemies.size() <= 0) {
            if (!survival) {
                if (wave >= 12) {
                    finished = true;
                    if (finishTime <= 0)
                        finishTime = System.nanoTime();
                }
            }

            if (!finished) {
                wave++;
                toSpawn += 5 + wave * 2 + level;
                waveChange = System.nanoTime();
            }
        }
    }

    public void waveAnimation() {
        double delta = System.nanoTime() - waveChange;
        waveAlpha = toAlpha(delta);
        Maverick.wave = this.wave;
    }

    public int toAlpha(double d) {
        if (d < 1500000000) {
            return 255;
        } else if (d < 3000000000.0) {
            return (int) (255 - 255 * (d / 3000000000.0));
        } else {
            return 0;
        }
    }
}
