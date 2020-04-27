import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class Main {

    public static final int POPULATION_SIZE = 100;
    public static final int GENERATION_NUMBER = 10000;

    public static void main(String[] args) {
        long startProgram = System.currentTimeMillis();

//        Input of parameters: initial random number (Random is deterministic) (ex: 1),
//        name of input image (ex: image01.png), name of output folder (ex: AI_image01)
        int rand = 0;
        String imageName = null;
        String folderName = null;

        if (args.length >= 3) {
            rand = Integer.parseInt(args[0]);
            imageName = args[1];
            folderName = args[2];
        } else {
            try {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Write initial random int:");
                rand = Integer.parseInt(reader.readLine());
                System.out.println("Write input image name:");
                imageName = reader.readLine();
                System.out.println("Write output directory:");
                folderName = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

//        Initializing random
        Art.setRandom(rand);
        Random random = new Random(rand);

//        Reading reference picture
        BufferedImage img = null;
        try {
            File input = new File("AI_input" + File.separator + imageName + ".png");
            System.out.println("Reading from " + input.getAbsolutePath());
            img = ImageIO.read(input);
        } catch (IOException e) {
            System.out.println("Can not read the input file");
            e.printStackTrace();
            return;
        }

//        Setting reference picture
        Art.setReference(Art.toArray(img));


//        Making population of random canvases
        Art[] population = new Art[POPULATION_SIZE];
        for (int i = 0; i < population.length; ++i)
            population[i] = new Art();

//        Starting EA
        long startTime = System.currentTimeMillis();
        long sumSurvived = 0;
        double maxScore = 0;
        for (int i = 1; i <= GENERATION_NUMBER; ++i) {

//            Create new population
            Art[] newPopulation = new Art[POPULATION_SIZE];
            int k = 0;

//            Choose who survived
            for (int j = 0; j < population.length; ++j) {
                if (j <= random.nextInt(POPULATION_SIZE ) * 1.15) {
                    newPopulation[k++] = population[j];
                }
            }

//            Debug output
            sumSurvived += k;
            System.out.println(String.format("\n\nPhase: %d\nAvg survived: %d", i, sumSurvived / i));

            while (k < POPULATION_SIZE) {
//                Choose crossover or mutation
                int i1 = (int)Math.abs(random.nextGaussian() * POPULATION_SIZE % POPULATION_SIZE); // random.nextInt(population.length);
                if (random.nextInt(3) == 0) {
//                    Crossover
                    int i2 = random.nextInt(i1 + 1);
                    newPopulation[k++] = Art.crossover(population[i1], population[i2]);
                } else {
//                    Multiple mutations
                    int p =  random.nextInt(15) + 1;//(int) Math.max(1, random.nextGaussian() * 3 + 3);; ;
                    newPopulation[k] = new Art(population[i1]);
                    for (int j = 0; j < p; ++j)
                        newPopulation[k].mutate();
                    k++;
                }
            }

//            Swapping to new generation
            population = newPopulation;

//            Define strongest art by art.getValue()
            Arrays.sort(population);

            if (folderName != null) {
                new File(folderName).mkdir();
            }


//            Debug output
            if (maxScore < population[0].getValue())

                for (int j = 0; (j == 0) && j < population.length; ++j)
                    try {
                        maxScore = population[j].getValue();
                        String name = String.format("%09d_points_image%02d.png", (int) population[j].getValue(), j);
//                    new File(folderName + File.separator + String.format("%02dgen", i)).mkdir();
                        File output = new File(folderName + File.separator + String.format("%02dgen", i) /*+ File.separator*/ + name);
                        System.out.println("Writing to " + output.getAbsolutePath());
                        img = population[j].show();
                        ImageIO.write(img, "png", output);
                    } catch (Exception e) {
                        System.out.println("Can not write the output file");
                        e.printStackTrace();
                        continue;
                    }

            long stopTime = System.currentTimeMillis();
            long elapsedTime = (stopTime - startTime) / 1000;
            System.out.println("From start " + elapsedTime / 60 + " m, " + elapsedTime % 60 + " s");

        }

        long endProgram = System.currentTimeMillis();
        long avg = endProgram - startProgram;
        System.out.println("Sum time: " + avg);

    }
}
