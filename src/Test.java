import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Test {

    public static void main(String[] args) {
        long startProgram = System.currentTimeMillis();
        long n = 25, cnt = 0;
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        String folderName = null;
        try {
             folderName = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 1; i < n; ++i) {
            long startTime = System.currentTimeMillis();

            BufferedImage img = null;
            String ind = String.format("%02d", i);
            try {
                File input = new File("input" + File.separator + "image" + ind + ".png");
                System.out.println("Reading from " + input.getAbsolutePath());
                img = ImageIO.read(input);
            } catch (IOException e) {
                System.out.println("Can not read the input file");
                e.printStackTrace();
                continue;
            }

            BufferedImage img2 = null;
            Art art1 = null;
            try {
//                art1 = new Art(Art.getArray(img));
//                art1.showHueSpots();
//                art1.showSaturationSpots();
//                img2 = art1.showContrastSpots();
                img2 = art1.show();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            try {
                String points = String.format("%02d_%s", art1.getValue(), art1.getColorCombination()[1] == -1 ? "no_palette" : Art.COLOR_PALETTES_NAME[(int) art1.getColorCombination()[1]]);
                File output = new File("output" + File.separator + folderName + File.separator + points + "_points_image" + ind + ".png");
                System.out.println("Writing to " + output.getAbsolutePath());
                ImageIO.write(img2, "png", output);
            } catch (Exception e) {
                System.out.println("Can not write the output file");
                e.printStackTrace();
                continue;
            }

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println("Took " + elapsedTime + " ms");
            cnt++;
        }

        long endProgram = System.currentTimeMillis();
        long avg = endProgram - startProgram;
        System.out.println("Sum time: " + avg);
        if (cnt > 0)
            avg /= cnt;
        System.out.println("Average time: " + avg);
    }

}
