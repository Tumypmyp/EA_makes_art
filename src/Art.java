import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Function;


public class Art implements Comparable<Art>, Cloneable {
    public static final int W = 512;
    public static final int H = 512;
    public static final int SPOT_K = 3;
    public static final int SPOT_W = 60;
    public static final int SPOT_H = SPOT_W;
    public static final int SPOT_P = SPOT_W * SPOT_H / 3;
    public static final int WHEEL_N = 360;
    public static final int COLOR_N = 12;
    // Analogous, Complementary, Split Complementary, Triadic, Tetradic
    public static final String[] COLOR_PALETTES_NAME = new String[]{"Analogous", "Complementary", "Split Complementary", "Triadic", "Tetradic"};
    public static final int[][] COLOR_PALETTES = new int[][]{{0, 1, 2}, {0, 6}, {0, 5, 7}, {0, 4, 8}, {0, 3, 6, 9}};
    public static final double MAX_SUM_DIFF = 512 * 511 * (360 + 100 + 100) * 2;

    public static final int[] HSL_MAX = new int[]{360, 100, 100};
    public static final int R = 16;
    public static final int DAP_W = W / (R * 2) + 1;
    public static final int DAP_H = DAP_W;

    private static Random random;
    private static int ref[];

    private int[] data;
    private int[][][] dap;
    private Pair[] hueSpots;
    private Pair[] saturationSpots;
    private Pair[] luminanceSpots;
    private Pair[] contrastSpots = null;
    private float[] colors;
    private float sumColors;
    private double[] sumDiff = new double[]{0, 0, 0};
    private float[] colorCombination;
    private double similarity;


    public static void setReference(int[] x) {
        ref = x;
    }

    public static void setRandom(int x) {
        random = new Random(x);
    }

    public static final int[][] NEIGHBOR = new int[][]{{0, 1}, {1, 0}, {1, 1}, {2, 0}, {0, 2}, {2, 1}, {1, 2}, {2, 2}};

    public static int[] toArray(BufferedImage image) {
        int[] arr = new int[W * H];
        image.getRGB(0, 0, W, H, arr, 0, W);
        return arr;
    }

    Art() {
        dap = new int[2][DAP_W][DAP_H];
        for (int i = 0; i < 2; ++i)
            for (int x = 0; x < DAP_W; ++x)
                for (int y = 0; y < DAP_H; ++y)
                    dap[i][x][y] = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()).hashCode();
        data = new int[W * H];
        refresh();
    }


    Art(Art art) {
        dap = new int[2][DAP_W][DAP_H];
        for (int i = 0; i < 2; ++i)
            for (int x = 0; x < DAP_W; ++x)
                for (int y = 0; y < DAP_H; ++y)
                    dap[i][x][y] = art.dap[i][x][y];
        data = new int[W * H];
        for (int i = 0; i < data.length; ++i)
            data[i] = art.data[i];

        colorCombination = new float[]{art.colorCombination[0], art.colorCombination[1]};
        sumDiff = new double[3];
        for (int i = 0; i < 3; ++i)
            sumDiff[i] = art.sumDiff[i];

        similarity = art.similarity;
    }

    public void refresh() {
//        hueSpots = findBrightestSpots(SPOT_K, SPOT_W, SPOT_H, this::hue);
//        saturationSpots = findBrightestSpots(SPOT_K, SPOT_W, SPOT_H, this::saturation);
//        luminanceSpots = findBrightestSpots(SPOT_K, SPOT_W, SPOT_H, this::luminance);
//        contrastSpots = findContrastSpots(SPOT_K, SPOT_W, SPOT_H, SPOT_P);
        colors = countColors();
        colorCombination = findBestColorCombination(COLOR_N);
        sumDiff = countSumDiff();
        for (int i = 0; i < 2; ++i)
            for (int x = 0; x < DAP_W; ++x)
                for (int y = 0; y < DAP_H; ++y)
                    paint(i * R + x * R * 2, i * R + y * R * 2, dap[i][x][y]);
        similarity = getSimilarity();
    }

    private void paint(int x, int y, int col) {
        for (int i = Math.max(0, x - R); i <= Math.min(W - 1, x + R); ++i)
            for (int j = Math.max(0, y - R); j <= Math.min(H - 1, y + R); ++j)
                if (dist(i, j, x, y) * 2 < (R + 1) * (R + 1))
                    data[i + j * W] = col;
    }


    private static int dist(int x1, int y1, int x2, int y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    public void mutate() {
        int i = random.nextInt(4) > 0 ? 1 : 0;
        int x = random.nextInt(DAP_W);
        int y = random.nextInt(DAP_H);
        float[] hsl = HSLColor.fromRGB(new Color(dap[i][x][y]));
        for (int j = 0; j < 3; ++j) {
            hsl[i] += (float) (random.nextGaussian() * HSL_MAX[i]);
            if (i == 0) {
                hsl[i] %= HSL_MAX[i];
                hsl[i] += HSL_MAX[i];
                hsl[i] %= HSL_MAX[i];
            } else {
                hsl[i] = Math.max(0, hsl[i]);
                hsl[i] = Math.min(HSL_MAX[i], hsl[i]);
            }
        }
        dap[i][x][y] = HSLColor.toRGB(hsl).hashCode();
        refresh();
    }

    public static Art crossover(Art art1, Art art2) {
        Art[] arts = new Art[]{art1, art2};
        Art res = new Art();
        int id = 0;
        for (int i = 0; i < 2; ++i)
            for (int x = 0; x < DAP_W; ++x)
                for (int y = 0; y < DAP_H; ++y) {
                    res.dap[i][x][y] = arts[id].dap[i][x][y];
                    id ^= random.nextBoolean() ? 1 : 0;
                }
        res.refresh();
        return res;
    }

    private double[] countSumDiff() {
        double[] result = new double[3];
        for (int i = 0; i < 3; ++i) {
            for (int x = 0; x < W; ++x)
                for (int y = 0; y < H; ++y) {
                    for (int[] step : NEIGHBOR) {
                        if (x + step[0] >= W || y + step[1] >= H)
                            continue;
                        float[] c1 = HSLColor.fromRGB(new Color(data[x + y * W]));
                        float[] c2 = HSLColor.fromRGB(new Color(data[x + step[0] + (y + step[1]) * W]));
                        float p = (float) (i == 0 ? WHEEL_N : 100) / 12;
                        float add = Math.min(Math.abs(c1[i] - c2[i]), Math.abs(c1[i] + (i == 0 ? WHEEL_N : 0) - c2[i]));
                        add = add * 100 / HSL_MAX[i];
                        add = 100 - add;
                        result[i] += add; // < p ? 0 : Math.pow(add, 1.5);
                    }
                }
        }
        return result;
    }

    public float[] getColorCombination() {
        return colorCombination;
    }

    private double getSimilarity() {
        double[] result = new double[3];
        for (int x = 0; x < W; ++x)
            for (int y = 0; y < H; ++y) {
                float[] c1 = HSLColor.fromRGB(new Color(data[x + y * W]));
                float[] c2 = HSLColor.fromRGB(new Color(ref[x + y * W]));
                for (int i = 0; i < 3; ++i) {
                    double add = Math.min(Math.abs(c1[i] - c2[i]), Math.abs(c1[i] + HSL_MAX[i] - c2[i]));
                    add = add * 100 / HSL_MAX[i];
                    add = Math.pow(100 - add, 1);
                    result[i] += add;
                }
            }
        return (result[0] + result[1] + result[2]);
    }

    public double getValue() {
//        System.out.println(String.format("%.2f %.2f %.2f %.2f", getColorCombination()[0], sumDiff[0], sumDiff[1], sumDiff[2]));
        double res1 = Math.max(0, getColorCombination()[0]) * 100;
        double res2 = Math.max(0, (sumDiff[0] + sumDiff[1] + sumDiff[2]) / 100); // MAX_SUM_DIFF);
        double res3 = similarity;
        System.out.println(String.format("%f %f %f", res1, res2, res3));

        return (int) (res1 + res2 + res3);
    }

    private float[] findBestColorCombination(int n) {
        int k = WHEEL_N / n;
        float ans = -1, ans_i = -1;
        for (int i = 0; i < WHEEL_N; ++i) {
            int[] have = new int[n];
            for (int c = 0; c < WHEEL_N; ++c)
                have[(c + i) % WHEEL_N / k] += colors[c];
            for (int j = 0; j < COLOR_PALETTES.length; ++j) {
                float sum = 0;
                for (int j2 = 0; j2 < COLOR_PALETTES[j].length; ++j2)
                    sum += have[COLOR_PALETTES[j][j2]];
                if (ans < sum * 100 / sumColors) {
                    ans = sum * 100 / sumColors;
                    ans_i = j;
                }
            }
        }
        return new float[]{ans * 1000, ans_i};
    }

    private float[] countColors() {
        float[] result = new float[WHEEL_N];
        sumColors = 0;
        for (int x = 0; x < W; ++x)
            for (int y = 0; y < H; ++y) {
                float[] c = HSLColor.fromRGB(new Color(data[x + y * W]));
                c[1] /= 100;
                c[2] /= 100;
                c[1] *= Math.abs(c[2] - 0.5) * 2;
                c[1] = c[1] <= 0.6 ? (float) Math.pow(c[1], 2) : c[1];
                result[(int) c[0]] += c[1];
                sumColors += c[1];
            }

        return result;
    }

    //*/
    public BufferedImage show() {
        BufferedImage result = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, W, H, data, 0, W);
        return result;
    }

    /*
    private BufferedImage showSpots(Pair[] arr, int K, int w, int h, int col) {
        BufferedImage result = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        if (arr == null)
            return result;
        for (int i = 0; i < Math.min(K, arr.length); ++i)
            showSpot(arr[i].x - h + 1, arr[i].y - h + 1, w, h, col);
        result.setRGB(0, 0, W, H, data, 0, W);
        return result;
    }

    public void showSpot(int startX, int startY, int w, int h, int col) {
        for (int x = 0; x < w; ++x)
            for (int y = 0; y < h; ++y)
                if ((x + y + startX + startY) % 2 == 0 || x == 0 || y == 0 || x == w - 1 || y == h - 1)
                    data[(y + startY) * W + (x + startX)] = col;
    }

    public BufferedImage showHueSpots() {
        return showSpots(hueSpots, SPOT_K, SPOT_W, SPOT_H, new Color(12341133).hashCode());
    }

    public BufferedImage showSaturationSpots() {
        return showSpots(saturationSpots, SPOT_K, SPOT_W, SPOT_H, new Color(11930).hashCode());
    }

    public BufferedImage showLuminanceSpots() {
        return showSpots(luminanceSpots, SPOT_K, SPOT_W, SPOT_H, new Color(51011590).hashCode());
    }
    //*/
    /*
    public BufferedImage showContrastSpots() {
        return showSpots(contrastSpots, SPOT_K, SPOT_W, SPOT_H, new Color(45633335).hashCode());
    }

     */
    private double brightness(int a) {
        Color c = new Color(a);
        return 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
    }

    //*/
    private double hue(int a) {
        HSLColor col = new HSLColor(new Color(a));
        return col.getHue() * 100;
    }

    private double saturation(int a) {
        HSLColor col = new HSLColor(new Color(a));
        return col.getSaturation() * 100;
    }

    private double luminance(int a) {
        HSLColor col = new HSLColor(new Color(a));
        return col.getLuminance() * 100;
    }


    // O(k * W * H)
    private Pair[] findBrightestSpots(int k, int w, int h, Function<Integer, Double> f) {
        int[][] prefSum = new int[W][H];
        for (int x = 0; x < W; ++x)
            for (int y = 0; y < H; ++y) {
                int brightness = f.apply(data[x + y * W]).intValue();
                prefSum[x][y] = brightness
                        + (x > 0 ? prefSum[x - 1][y] : 0)
                        + (y > 0 ? prefSum[x][y - 1] : 0)
                        - (y > 0 && x > 0 ? prefSum[x - 1][y - 1] : 0);
            }
        Pair[] result = new Pair[k];
        for (int i = 0; i < k; ++i) {
            int maxSum = -1;
            Pair max = null;
            for (int x = w - 1; x < W; ++x)
                for (int y = h - 1; y < H; ++y) {
                    int sum = prefSum[x][y]
                            - (x - w >= 0 ? prefSum[x - w][y] : 0)
                            - (y - h >= 0 ? prefSum[x][y - h] : 0)
                            + (x - w >= 0 && y - h >= 0 ? prefSum[x - w][y - h] : 0);
                    if (sum > maxSum && !crossingSpots(i, w, h, result, x, y)) {
                        maxSum = sum;
                        max = new Pair(x, y);
                    }
                }
            result[i] = max;
        }
        return result;
    }

    private boolean crossingSpots(int k, int w, int h, Pair[] res, int x, int y) {
        for (int i = 0; i < k; ++i)
            if (crossingSegments(res[i].x, x, w) && crossingSegments(res[i].y, y, h))
                return true;
        return false;
    }

    private boolean crossingSegments(int r1, int r2, int d) {
        return (r1 - d < r2 && r2 <= r1) || (r2 - d < r1 && r1 <= r2);
    }

    public int compareTo(Art art) {
        return Double.compare(art.getValue(), getValue());
    }
}


