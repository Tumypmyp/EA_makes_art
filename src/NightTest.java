import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class NightTest {

    public static void main(String[] args) {

        long startProgram = System.currentTimeMillis();
        int n = 25;
        for (int cnt = 1; cnt < 5; ++cnt) {
            long startTime = System.currentTimeMillis();
            for (int i = 1; i < n; ++i) {

                String k = String.format("%02d", cnt);
                String ind = String.format("%02d", i);

                new File("AI_" + k).mkdir();
                Main.main(new String[]{k, "image" + ind, "AI_" + k + File.separator + "_image" + ind});
                System.out.println("Testing AI_" + k + "image" + ind);
            }
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Testing took " + elapsedTime + " ms\n\n");
        }

        long endProgram = System.currentTimeMillis();
        long avg = endProgram - startProgram;
        System.out.println("Sum time: " + avg);
    }
}
