package maverick;

import static maverick.Maverick.*;
import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;

public class Background extends Entity {

    int bgNum;
    Image img;
    int width;
    int height;

    public Background(int x, int y, int bgNum_) {
        super(x, y, 0, 0, 0, null);

        bgNum = bgNum_;
        checkBackgroundNum();
    }

    public void render() {
        g.drawImage(img, getX(), getY(), null);
    }

    void checkBackgroundNum() {
        if (bgNum == 1 || bgNum == 0) {
            try {
                img = ImageIO.read(new File(System.getProperty("user.dir") + "/res/level1.jpg"));

                img = img.getScaledInstance(-1, height(), Image.SCALE_REPLICATE);

                width = img.getWidth(null);
                height = img.getHeight(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
