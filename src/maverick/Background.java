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
        try {
            img = ImageIO.read(new File(systemPath.substring(0, systemPath.length() - 3) + "/res/level" + (bgNum + 1) + ".jpg"));

            img = img.getScaledInstance(-1, height(), Image.SCALE_REPLICATE);

            width = img.getWidth(null);
            height = img.getHeight(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
