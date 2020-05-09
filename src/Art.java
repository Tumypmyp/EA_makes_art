import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
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
    public static final String[] COLOR_PALETTES_NAME = new String[]{"Analogous", "Complementary", "Split Complementary", "Triadic"};//, "Tetradic"};
    public static final int[][] COLOR_PALETTES = new int[][]{{0, 1, 2}, {0, 6}, {0, 5, 7}, {0, 4, 8}};//, {0, 3, 6, 9}};
    public static final double MAX_SUM_DIFF = 512 * 511 * (360 + 100 + 100) * 2;

    public static final int[] HSL_MAX = new int[]{360, 100, 100};
    public static final int R = 16;

    private static Random random;
    private static int ref[];

    private int[] data;
    private Pair[] hueSpots;
    private Pair[] saturationSpots;
    private Pair[] luminanceSpots;
    private float[] colors;
    private float sumColors;
    private double[] sumDiff = new double[]{0, 0, 0};
    private float[] colorCombination = new float[]{0, 0};
    private long similarity;


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
        data = new int[W * H];
        for (int i = 0; i < W * H; ++i)
            data[i] = Color.WHITE.hashCode();
        similarity = getTotalSimilarity();
        refresh();
    }


    Art(Art art) {
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
//        colors = countColors();
//        colorCombination = findBestColorCombination(COLOR_N);
//        sumDiff = countSumDiff();
//        for (int i = 0; i < 2; ++i)
//            for (int x = 0; x < DAP_W; ++x)
//                for (int y = 0; y < DAP_H; ++y)
//                    paint(i * R + x * R * 2, i * R + y * R * 2, dap[i][x][y]);
//        similarity = getTotalSimilarity();
    }

    private void paint(int x, int y, int r, int col) {
        for (int i = Math.max(0, x - r); i <= Math.min(W - 1, x + r); ++i)
            for (int j = Math.max(0, y - r); j <= Math.min(H - 1, y + r); ++j) {
                long sim0 = getSimilarity(i, j);
//                if (dist(i, j, x, y) * 2 < (r + 1) * (r + 1))
                data[i + j * W] = col;
                long sim1 = getSimilarity(i, j);
                similarity = similarity - sim0 + sim1;
            }
    }


    private static int dist(int x1, int y1, int x2, int y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    public void mutate() {
//        int i = random.nextInt(4) > 0 ? 1 : 0;
        int x = random.nextInt(W);
        int y = random.nextInt(H);
        int r = random.nextInt(9) + 2;
        float[] hsl = HSLColor.fromRGB(new Color(data[x + y * W]));
        hsl[2] += (((random.nextGaussian() - 0.5) * HSL_MAX[2]));
        hsl[2] = Math.max(Math.min(hsl[2], HSL_MAX[2]), 0);
        hsl[1] += (((random.nextGaussian() - 0.5) * HSL_MAX[1]));
        hsl[1] = Math.max(Math.min(hsl[1], HSL_MAX[1]), 0);

        hsl[0] += (((random.nextGaussian() - 0.5) * HSL_MAX[0]));
        hsl[0] %= HSL_MAX[0];
        hsl[0] += HSL_MAX[0];
        hsl[0] %= HSL_MAX[0];

//        for (int i = 2; i < 3; ++i) {
//            if (i == 0) {
//                hsl[i] %= HSL_MAX[i];
//                hsl[i] += HSL_MAX[i];
//                hsl[i] %= HSL_MAX[i];
//            } else {
//                hsl[i] = Math.max(0, hsl[i]);
//                hsl[i] = Math.min(HSL_MAX[i], hsl[i]);
//            }
//        }
        paint(x, y, r, HSLColor.toRGB(hsl).hashCode());
        refresh();
    }

    private double[] countSumDiff() {
        double[] result = new double[3];
        for (int i = 0; i < 2; ++i) {
            for (int x = 0; x < W; ++x)
                for (int y = 0; y < H; ++y) {
                    for (int[] step : NEIGHBOR) {
                        if (x + step[0] >= W || y + step[1] >= H)
                            continue;
                        float[] c1 = HSLColor.fromRGB(new Color(data[x + y * W]));
                        float[] c2 = HSLColor.fromRGB(new Color(data[x + step[0] + (y + step[1]) * W]));
                        float add = Math.min(Math.abs(c1[i] - c2[i]), Math.abs(c1[i] + (i == 0 ? WHEEL_N : 0) - c2[i]));
                        result[i] -= Math.abs(c1[2] - c2[2]) < 10 ? add : 0; // < p ? 0 : Math.pow(add, 1.5);
                    }
                }
        }
        return result;
    }

    public float[] getColorCombination() {
        return colorCombination;
    }

    private long getTotalSimilarity() {
        long result = 0;
        for (int x = 0; x < W; ++x)
            for (int y = 0; y < H; ++y)
                result += getSimilarity(x, y);
        return result;
    }

    private long getSimilarity(int x, int y) {
        double[] result = new double[3];
        float[] c1 = HSLColor.fromRGB(new Color(data[x + y * W]));
        float[] c2 = HSLColor.fromRGB(new Color(ref[x + y * W]));
        for (int i = 1; i < 3; ++i) {
//          double add = Math.min(Math.abs(c1[i] - c2[i]), Math.abs(c1[i] + HSL_MAX[i] - c2[i]));
            double add = Math.abs(c1[i] - c2[i]);
            add = 100 - add * 100 / HSL_MAX[i];
            result[i] += add;
        }
        result[0] += 100 - Math.min(Math.abs(c1[0] - c2[0]), Math.abs(c1[0] + HSL_MAX[0] - c2[0])) * 100 / HSL_MAX[0];
        return (long) (result[0] + result[1] + result[2]);

    }


    public double getValue() {
//        System.out.println(String.format("%.2f %.2f %.2f %.2f", getColorCombination()[0], sumDiff[0], sumDiff[1], sumDiff[2]));
        double res1 = Math.max(0, colorCombination[0]) * 100;
        double res2 = (sumDiff[0] + sumDiff[1] + sumDiff[2]) / 100; // MAX_SUM_DIFF);
        double res3 = similarity;
//        System.out.println(String.format("%f %f %f", res1, res2, res3));
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
        return new float[]{ans * 10000, ans_i};
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


