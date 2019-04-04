import com.sun.org.apache.regexp.internal.RE;
import fourier.ComplexNumber;
import sun.plugin.dom.css.RGBColor;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import static java.lang.Math.exp;

public class MyImage {

    public enum Channel {
        RED,BLUE,GREEN,ALPHA
    }

    private BufferedImage imageFile;

    public MyImage(String imagePath) {
        try {
            this.imageFile = ImageIO.read(new FileInputStream(imagePath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public BufferedImage getImageFile() {
        return imageFile;
    }

    public int getPixel(int x, int y, Channel channel) {
        int pixel  = imageFile.getRGB(x,y);

        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        switch (channel) {
            case RED:
                return red;
            case BLUE:
                return green;
            case GREEN:
                return blue;
            case ALPHA:
                return alpha;
                default:
                    return -1;

        }

    }


    public int getPixel(BufferedImage img, int x, int y, Channel channel) {
        int pixel  = img.getRGB(x,y);

        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        switch (channel) {
            case RED:
                return red;
            case BLUE:
                return green;
            case GREEN:
                return blue;
            case ALPHA:
                return alpha;
            default:
                return -1;

        }

    }

    public void setPixel(int x, int y, Channel channel, int newValue) {

        int pixel  = imageFile.getRGB(x,y);

        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        switch (channel) {
            case RED:
                red = newValue;
                break;
            case BLUE:
                blue = newValue;
                break;
            case GREEN:
                green = newValue;
                break;
            case ALPHA:
                alpha = newValue;
                break;
        }

        int sum = (alpha << 24) + (red << 16) + (green << 8) + blue;
        imageFile.setRGB(x,y,sum);


    }

    public void setPixel(BufferedImage img, int x, int y, Channel channel, int newValue) {

        int pixel  = img.getRGB(x,y);

        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        switch (channel) {
            case RED:
                red = newValue;
                break;
            case BLUE:
                blue = newValue;
                break;
            case GREEN:
                green = newValue;
                break;
            case ALPHA:
                alpha = newValue;
                break;
        }

        int sum = (alpha << 24) + (red << 16) + (green << 8) + blue;
        img.setRGB(x,y,sum);


    }

    public void setPixelNoAlpha(BufferedImage img, int x, int y, Channel channel, int newValue) {

        int pixel  = img.getRGB(x,y);

        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        switch (channel) {
            case RED:
                red = newValue;
                break;
            case BLUE:
                blue = newValue;
                break;
            case GREEN:
                green = newValue;
                break;

        }

        int sum = (alpha << 24) | (red << 16) | (green << 8) | blue;
        img.setRGB(x,y,sum);


    }


    public void copyImage(String pathAndFile) {
        try {
            ImageIO.write(imageFile, "png", new File(pathAndFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createImageFile(BufferedImage img, String pathAndFile) {
        try {
            ImageIO.write(img, "png", new File(pathAndFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rgb2GreyScale() {

        int red;
        int green;
        int blue;

        // Luminosity constants
        float r = 0.21f;
        float g = 0.72f;
        float b = 0.07f;


        for(int x = 0; x < imageFile.getWidth(); x++) {
            for(int y = 0; y < imageFile.getHeight(); y++) {
                red = getPixel(x,y,Channel.RED);
                blue = getPixel(x,y,Channel.BLUE);
                green = getPixel(x,y,Channel.GREEN);

                float grayValue = r*red + g*green + b*blue;
                setPixel(x,y, Channel.RED,(int)grayValue);
                setPixel(x,y, Channel.BLUE,(int)grayValue);
                setPixel(x,y, Channel.GREEN,(int)grayValue);

            }
        }
        copyImage("res/greyscaleOfImage.jpg");

    }


    public void fourierImageFrom1DArrays(double[] r, double[] g, double[] b, int width, double avg, double max) {


        BufferedImage img = new BufferedImage(width, width*2, BufferedImage.TYPE_INT_RGB);

        System.out.println("image size: " + img.getHeight()*img.getWidth());
        System.out.println("size from width: " + width*width*2);

        int constant = 1;
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < width*2; y++) {

                img.setRGB(x,y, new Color((int) Math.abs(Math.min(((r[x*width + y]-avg)/max)*255, 255)), (int) Math.abs(Math.min(255,((b[x*width + y] - avg)/max)*255)), (int) Math.abs(Math.min(255,((g[x*width + y] - avg)/max)*255))).getRGB());

            }

        }
        createImageFile(img,"res/fourierOfImage.jpg");

    }










    public void addSaltAndPepper(int oneInNChance) {

        Random rand = new Random();
        for(int x = 0; x < imageFile.getWidth(); x++) {
            for(int y = 0; y < imageFile.getHeight(); y++) {

                int oneInRange = rand.nextInt(oneInNChance);
                int blackOrWhite = rand.nextInt(2);

                if(oneInRange == 0) { // 1 out of N probability, the pixel will be altered
                    if (blackOrWhite == 0) { // either max out or minimize value
                        setPixel(x, y, Channel.RED, 255);
                        setPixel(x, y, Channel.BLUE, 255);
                        setPixel(x, y, Channel.GREEN, 255);
                    } else {
                        setPixel(x, y, Channel.RED, 0);
                        setPixel(x, y, Channel.BLUE, 0);
                        setPixel(x, y, Channel.GREEN, 0);
                    }
                }
            }
        }
        copyImage("res/saltAndPepper.jpg");
    }

    public void removeSaltAndPepper() {

        double[][] filter = getBoxForSP();
        double[][] filter2 = getIdentityFilter();

        double linearSumRed = 0;
        double linearSumBlue = 0;
        double linearSumGreen = 0;

        for (int i = 0; i < imageFile.getWidth() - filter.length; i++) {
            for(int j = 0; j < imageFile.getHeight() - filter.length; j++) {

                if(((getPixel(i + 1,j + 1, Channel.RED) <= 0) && (getPixel(i + 1,j + 1, Channel.BLUE) <= 0) && (getPixel(i + 1,j + 1, Channel.GREEN) <= 0)) ||
                        ((getPixel(i + 1,j + 1, Channel.RED) >= 255) && (getPixel(i +1 ,j + 1, Channel.BLUE) >= 255) && (getPixel(i + 1,j + 1, Channel.GREEN) >= 255))) {

                //if(getPixel(i+1, j + 1, Channel.RED) == 0 || getPixel(i + 1, j + 1, Channel.RED) == 255) {

                  //  System.out.println("Entered statement...");
                    for (int k = 0; k < filter.length; k++) {
                        for (int l = 0; l < filter.length; l++) {

                            if(((getPixel(i + k,j + l, Channel.RED) <= 0) && (getPixel(i + k,j + 1, Channel.BLUE) <= 0) && (getPixel(i + k,j + l, Channel.GREEN) <= 0)) ||
                                    ((getPixel(i + l,j + l, Channel.RED) >= 255) && (getPixel(i +l ,j + l, Channel.BLUE) >= 255) && (getPixel(i + k,j + l, Channel.GREEN) >= 255))) {

                                System.out.println("Entered");
                                linearSumRed += 75;
                                linearSumBlue += 75;
                                linearSumGreen += 75;
                            }
                            else {
                                linearSumRed += ((double) getPixel(i + k, j + l, Channel.RED)) * filter[k][l];
                                linearSumBlue += ((double) getPixel(i + k, j + l, Channel.BLUE)) * filter[k][l];
                                linearSumGreen += ((double) getPixel(i + k, j + l, Channel.GREEN)) * filter[k][l];
                            }

                        }
                    }
                }

                else {

                    for (int k = 0; k < filter2.length; k++) {
                        for (int l = 0; l < filter2.length; l++) {

                           // System.out.println("Pixel: " + getPixel(i + 1, j + 1, Channel.RED) + " " + getPixel(i + 1, j + 1, Channel.BLUE) + " " + getPixel(i + 1, j + 1, Channel.GREEN));

                            linearSumRed += ((double) getPixel(i + k, j + l, Channel.RED)) * filter2[k][l];
                            linearSumBlue += ((double) getPixel(i + k, j + l, Channel.BLUE)) * filter2[k][l];
                            linearSumGreen += ((double) getPixel(i + k, j + l, Channel.GREEN)) * filter2[k][l];

                        }
                    }

                }


                if(linearSumRed > 255) {
                    linearSumRed = 255;
                }
                if(linearSumGreen > 255) {
                    linearSumGreen = 255;
                }
                if(linearSumBlue > 255) {
                    linearSumBlue = 255;
                }
                if(linearSumRed < 0) {
                    linearSumRed = 0;
                }
                if(linearSumGreen < 0) {
                    linearSumGreen = 0;
                }
                if(linearSumBlue < 0) {
                    linearSumBlue = 0;
                }


                //imageFile.setRGB(i + 1 , j + 1, new Color((int)linearSumRed,(int)linearSumBlue,(int)linearSumGreen).getRGB());

                setPixel(i+1,j+1, Channel.RED, (int)linearSumRed);
                setPixel(i+1,j+1, Channel.GREEN, (int)linearSumBlue);
                setPixel(i+1,j+1, Channel.BLUE, (int)linearSumGreen);

                linearSumRed = 0;
                linearSumBlue = 0;
                linearSumGreen = 0;
            }
        }
        copyImage("res/cleanedFromSaltAndPepper.jpg");

    }


    public void addGaussianNoise(double variance, double mean) {

        Random rand = new Random();
        for(int x = 0; x < imageFile.getWidth(); x++) {
            for(int y = 0; y < imageFile.getHeight(); y++) {

                double gaussianRed = rand.nextGaussian() * Math.sqrt(variance) + mean;
                double gaussianBlue = rand.nextGaussian() * Math.sqrt(variance) + mean;
                double gaussianGreen = rand.nextGaussian() * Math.sqrt(variance) + mean;

                gaussianRed = getPixel(x,y,Channel.RED) + gaussianRed;
                gaussianBlue = getPixel(x,y,Channel.GREEN) + gaussianBlue;
                gaussianGreen = getPixel(x,y,Channel.BLUE) + gaussianGreen;


                if(gaussianRed > 255) {
                    gaussianRed = 255;
                }
                if(gaussianGreen > 255) {
                    gaussianGreen = 255;
                }
                if(gaussianBlue > 255) {
                    gaussianBlue = 255;
                }
                if(gaussianRed < 0) {
                    gaussianRed = 0;
                }
                if(gaussianGreen < 0) {
                    gaussianGreen = 0;
                }
                if(gaussianBlue < 0) {
                    gaussianBlue = 0;
                }

                setPixel(x, y, Channel.RED, (int) gaussianRed);
                setPixel(x, y, Channel.BLUE, (int)gaussianBlue);
                setPixel(x, y, Channel.GREEN, (int)gaussianGreen);




            }
        }


        copyImage("res/withGaussianNoise.jpg");
    }



    public void rgb2GreyScale(String path) {

        int red;
        int green;
        int blue;

        // Luminosity constants
        float r = 0.21f;
        float g = 0.72f;
        float b = 0.07f;


        for(int x = 0; x < imageFile.getWidth(); x++) {
            for(int y = 0; y < imageFile.getHeight(); y++) {
                red = getPixel(x,y,Channel.RED);
                blue = getPixel(x,y,Channel.BLUE);
                green = getPixel(x,y,Channel.GREEN);

                float grayValue = r*red + g*green + b*blue;
                setPixel(x,y, Channel.RED,(int)grayValue);
                setPixel(x,y, Channel.BLUE,(int)grayValue);
                setPixel(x,y, Channel.GREEN,(int)grayValue);

            }
        }
        copyImage(path);

    }

    public void shift(Channel channel, int value, boolean sol) {

        int red;
        int green;
        int blue;


        for(int x = 0; x < imageFile.getWidth(); x++) {
            for(int y = 0; y < imageFile.getHeight(); y++) {
                red = getPixel(x,y,Channel.RED);
                blue = getPixel(x,y,Channel.BLUE);
                green = getPixel(x,y,Channel.GREEN);

                switch (channel) {
                    case RED:
                        if (sol) {
                            if (red + value >= 255) {
                                setPixel(x, y, Channel.RED, 255);
                            }
                            else {
                                setPixel(x, y, Channel.RED, red + value);
                            }
                        }
                        else {
                            setPixel(x, y, Channel.RED, red + value);
                        }
                        break;
                    case GREEN:
                        if (sol) {
                            if (green + value >= 255) {
                                setPixel(x, y, Channel.GREEN, 255);
                            }
                            else {
                                setPixel(x, y, Channel.GREEN, green + value);
                            }
                        }
                        else {
                            setPixel(x, y, Channel.GREEN, green + value);
                        }
                        break;
                    case BLUE:
                        if (sol) {
                            if (blue + value >= 255) {
                                setPixel(x, y, Channel.BLUE, 255);
                            }
                            else {
                                setPixel(x, y, Channel.BLUE, blue + value);
                            }
                        }
                        else {
                            setPixel(x, y, Channel.BLUE, blue + value);
                        }
                        break;
                }


            }
        }
        copyImage("res/shiftOfImage.jpg");


    }

    private BufferedImage bilinearResize(BufferedImage imageFile, int newWidth, int newHeight) {

        int oldWidth = imageFile.getWidth();
        int oldHeight = imageFile.getHeight();

        double xRatio = (oldWidth - 1) / (double) newWidth;
        double yRatio = (oldHeight - 1) / (double) newHeight;

        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        int px,py;
        double xdiff, ydiff;
        int A, B, C, D;
        for(int x = 0; x < newWidth; x++) {
            for(int y = 0; y < newHeight; y++) {

                px = (int)(x*xRatio);
                py = (int)(y*yRatio);

                xdiff = x*xRatio - px;
                ydiff = y*yRatio - py;

                int r,g,b;

                A = getPixel((int)px, (int)py, Channel.RED);
                B = getPixel((int)px + 1, (int)py, Channel.RED);
                C = getPixel((int)px, (int)py + 1, Channel.RED);
                D = getPixel((int)px + 1, (int)py + 1, Channel.RED);

                r = (int)((A * (1 - xdiff) * (1 - ydiff)) + (B * xdiff * (1 - ydiff)) + (C * ydiff * (1 - xdiff)) + (D * xdiff * ydiff));

                A = getPixel( (int)px, (int)py, Channel.GREEN);
                B = getPixel( (int)px + 1, (int)py, Channel.GREEN);
                C = getPixel( (int)px, (int)py + 1, Channel.GREEN);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.GREEN);

                g = (int)((A * (1 - xdiff) * (1 - ydiff)) + (B * xdiff * (1 - ydiff)) + (C * ydiff * (1 - xdiff)) + (D * xdiff * ydiff));

                A = getPixel( (int)px, (int)py, Channel.BLUE);
                B = getPixel( (int)px + 1, (int)py, Channel.BLUE);
                C = getPixel( (int)px, (int)py + 1, Channel.BLUE);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.BLUE);

                b = (int)(A * (1 - xdiff) * (1 - ydiff) + B * xdiff * (1 - ydiff) + C * ydiff * (1 - xdiff) + D * xdiff * ydiff);


                img.setRGB(x,y,new Color(r,b,g).getRGB());


            }
        }
        return img;
    }


    public void bilinearResize(int newWidth, int newHeight) {

        int oldWidth = imageFile.getWidth();
        int oldHeight = imageFile.getHeight();

        double xRatio = (oldWidth - 1) / (double) newWidth;
        double yRatio = (oldHeight - 1) / (double) newHeight;

        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        int px,py;
        double xdiff, ydiff;
        int A, B, C, D;
        for(int x = 0; x < newWidth; x++) {
            for(int y = 0; y < newHeight; y++) {

                px = (int)(x*xRatio);
                py = (int)(y*yRatio);

                xdiff = x*xRatio - px;
                ydiff = y*yRatio - py;

                int r,g,b;

                A = getPixel((int)px, (int)py, Channel.RED);
                B = getPixel((int)px + 1, (int)py, Channel.RED);
                C = getPixel((int)px, (int)py + 1, Channel.RED);
                D = getPixel((int)px + 1, (int)py + 1, Channel.RED);

                r = (int)((A * (1 - xdiff) * (1 - ydiff)) + (B * xdiff * (1 - ydiff)) + (C * ydiff * (1 - xdiff)) + (D * xdiff * ydiff));

                A = getPixel( (int)px, (int)py, Channel.GREEN);
                B = getPixel( (int)px + 1, (int)py, Channel.GREEN);
                C = getPixel( (int)px, (int)py + 1, Channel.GREEN);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.GREEN);

                g = (int)((A * (1 - xdiff) * (1 - ydiff)) + (B * xdiff * (1 - ydiff)) + (C * ydiff * (1 - xdiff)) + (D * xdiff * ydiff));

                A = getPixel( (int)px, (int)py, Channel.BLUE);
                B = getPixel( (int)px + 1, (int)py, Channel.BLUE);
                C = getPixel( (int)px, (int)py + 1, Channel.BLUE);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.BLUE);

                b = (int)(A * (1 - xdiff) * (1 - ydiff) + B * xdiff * (1 - ydiff) + C * ydiff * (1 - xdiff) + D * xdiff * ydiff);


                img.setRGB(x,y,new Color(r,b,g).getRGB());


            }
        }

        createImageFile(img,"res/bilinear_resize.jpg");
    }


    public void bilinearResize(int newWidth, int newHeight, String path) {

        int oldWidth = imageFile.getWidth();
        int oldHeight = imageFile.getHeight();

        double xRatio = (oldWidth - 1) / (double) newWidth;
        double yRatio = (oldHeight - 1) / (double) newHeight;

        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        int px,py;
        double xdiff, ydiff;
        int A, B, C, D;
        for(int x = 0; x < newWidth; x++) {
            for(int y = 0; y < newHeight; y++) {

                px = (int)(x*xRatio);
                py = (int)(y*yRatio);

                xdiff = x*xRatio - px;
                ydiff = y*yRatio - py;

                int r,g,b;

                A = getPixel((int)px, (int)py, Channel.RED);
                B = getPixel((int)px + 1, (int)py, Channel.RED);
                C = getPixel((int)px, (int)py + 1, Channel.RED);
                D = getPixel((int)px + 1, (int)py + 1, Channel.RED);

                r = (int)((A * (1 - xdiff) * (1 - ydiff)) + (B * xdiff * (1 - ydiff)) + (C * ydiff * (1 - xdiff)) + (D * xdiff * ydiff));

                A = getPixel( (int)px, (int)py, Channel.GREEN);
                B = getPixel( (int)px + 1, (int)py, Channel.GREEN);
                C = getPixel( (int)px, (int)py + 1, Channel.GREEN);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.GREEN);

                g = (int)((A * (1 - xdiff) * (1 - ydiff)) + (B * xdiff * (1 - ydiff)) + (C * ydiff * (1 - xdiff)) + (D * xdiff * ydiff));

                A = getPixel( (int)px, (int)py, Channel.BLUE);
                B = getPixel( (int)px + 1, (int)py, Channel.BLUE);
                C = getPixel( (int)px, (int)py + 1, Channel.BLUE);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.BLUE);

                b = (int)(A * (1 - xdiff) * (1 - ydiff) + B * xdiff * (1 - ydiff) + C * ydiff * (1 - xdiff) + D * xdiff * ydiff);


                img.setRGB(x,y,new Color(r,b,g).getRGB());


            }
        }

        createImageFile(img,path);
    }




    public void neighborResize(int newWidth, int newHeight) {

        int oldWidth = imageFile.getWidth();
        int oldHeight = imageFile.getHeight();

        double xRatio = oldWidth / (double) newWidth;
        double yRatio = oldHeight / (double) newHeight;

        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        double px,py;
        for(int x = 0; x < newWidth; x++) {
            for(int y = 0; y < newHeight; y++) {

                px = Math.floor(x*xRatio);
                py = Math.floor(y*yRatio);


                img.setRGB(x,y, imageFile.getRGB((int)px,(int)py));

            }
        }



        createImageFile(img,"res/neighbor_resize.jpg");
    }










    public void applyBoxFilter(int w) {
        double[][] filter = getBoxFilter(w);
        BufferedImage img = convolve(filter);
        createImageFile(img,"res/applied_box_filter.jpg");
    }

    public void applyHighpassFilter(int x) {
        double[][] filter = getHighpassFilter(x);
        BufferedImage img = convolve(filter);
        createImageFile(img,"res/applied_highpass_filter.jpg");
    }

    public void applySharpenFilter() {
        double[][] filter = getSharpenFilter();
        BufferedImage img = convolve(filter);
        createImageFile(img,"res/applied_sharpen_filter.jpg");
    }

    public void applyEmbossFilter() {
        double[][] filter = getEmbossFilter();
        BufferedImage img = convolve(filter);
        createImageFile(img,"res/applied_emboss_filter.jpg");
    }

    public void applyRemoveSaltAndPepper(int w) {
        double[][] filter = getBoxFilterForSaltAndPepper(w);
        BufferedImage img = convolve(filter);
        createImageFile(img,"res/saltAndPepperRemoved.jpg");
    }

    public void applyGaussianFilter(int size, double sigma, String path) {
        double[][] filter = getGaussianFilter(size,sigma);
        BufferedImage img = convolve(filter);
        createImageFile(img,path);
    }

    public void applySobelFilterX() {
        double[][] filter = getSobelFilterX();
        BufferedImage img = correlate(filter);
        createImageFile(img,"res/applied_sobel_filterX.jpg");
    }
    public void applySobelFilterY() {
        double[][] filter = getSobelFilterY();
        BufferedImage img = correlate(filter);
        createImageFile(img,"res/applied_sobel_filterY.jpg");
    }




    private double[][] getBoxFilter(int w) {


        double[][] boxFilter = new double[w][w];

        for (int i = 0; i < boxFilter.length; i++) {

            for(int j = 0; j < boxFilter.length; j++) {

                boxFilter[i][j] = 1.0/(boxFilter.length*boxFilter.length);

            }

        }
        return boxFilter;
    }

    private double[][] getBoxFilterForSaltAndPepper(int w) {


        double[][] boxFilter = new double[w][w];

        for (int i = 0; i < boxFilter.length; i++) {

            for(int j = 0; j < boxFilter.length; j++) {

                if(i != boxFilter.length/2 && i != boxFilter.length/2)
                    boxFilter[i][j] = 1.0/(boxFilter.length*boxFilter.length - 1);

            }

        }
        return boxFilter;
    }


    //TODO
    private double[][] getHighpassFilter(int x) {


        if(x == 0) {
            double[][] highpassFilter = {
                    {-1, -1, -1},
                    {-1, +8, -1},
                    {-1, -1, -1}
            };
            return highpassFilter;
        }

        else if(x == 1) {
            double[][] highpassFilter = {
                    {0, -1, 0},
                    {-1, +4, -1},
                    {0, -1, 0}
            };
            return highpassFilter;
        }

        else {
            double[][] highpassFilter = {
                    {1, -2, 1},
                    {-2, +4, -2},
                    {1, -2, 1}
            };
            return highpassFilter;
        }

    }

    //TODO
    private double[][] getSharpenFilter() {


        double[][] sharpenFilter = {
                {-1, -1, -1},
                {-1, +9, -1},
                {-1, -1, -1}
        };


        return sharpenFilter;
    }

    //TODO
    private double[][] getEmbossFilter() {


        double[][] embossFilter = {
                {-2, 0, 0},
                {0, 1, 0},
                {0, 0, 2}
        };




        return embossFilter;
    }

    //TODO
    private double[][] getIdentityFilter() {


        double[][] identityFilter = {
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
        };

        return identityFilter;
    }

    //TODO
    private double[][] getBoxForSP() {


        double[][] box = {
                {1/8, 1/8, 1/8},
                {1/8,  0,  1/8},
                {1/8, 1/8, 1/8}
        };

        return box;
    }


    //TODO
    private double[][] getSobelFilterX() {


        double[][] sobelFilterX = {
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
        };

        return sobelFilterX;
    }

    //TODO
    private double[][] getSobelFilterY() {


        double[][] sobelFilterY = {
                {1, 2,1},
                {0, 0, 0},
                {-1, -2, -1}
        };

        return sobelFilterY;
    }

    //TODO
    private double[][] getGaussianFilter(int size, double sigma) {

        double[][] gaussianFilter = new double[size][size];

        double mean = size/2.0;
        double sum = 0.0;

        for ( int x = 0; x < size; ++x ) {
            for (int y = 0; y < size; ++y) {

                gaussianFilter[x][y] = Math.exp(-0.5 * (Math.pow((x - mean) / sigma, 2.0) + Math.pow((y - mean) / sigma, 2.0)))
                        / (2 * Math.PI * sigma * sigma);
                sum += gaussianFilter[x][y];

            }
        }

        // Normalizing the kernel
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                gaussianFilter[x][y] /= sum;
            }
        }

        System.out.println("Gaussian Kernel: ");
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                System.out.print(gaussianFilter[x][y] + " ");
            }
            System.out.println();
        }
        return gaussianFilter;
    }

    private BufferedImage correlate(double[][] filter) {

        BufferedImage img = new BufferedImage(getImageFile().getWidth(), getImageFile().getHeight(), BufferedImage.TYPE_INT_RGB);

        double linearSumRed = 0;
        double linearSumBlue = 0;
        double linearSumGreen = 0;

        for (int i = 0; i < imageFile.getWidth() - filter.length; i++) {
            for(int j = 0; j < imageFile.getHeight() - filter.length; j++) {

                for(int k = 0; k < filter.length; k++) {
                    for(int l = 0; l < filter.length; l++) {

                        linearSumRed += ((double)getPixel(i + k,j + l, Channel.RED)) * filter[k][l];
                        linearSumBlue += ((double)getPixel(i + k,j + l, Channel.BLUE)) * filter[k][l];
                        linearSumGreen += ((double)getPixel(i + k,j + l, Channel.GREEN)) * filter[k][l];


                    }
                }

                //setPixelNoAlpha(img, i + filter.length/2, j + filter.length/2, Channel.RED, linearSumRed/(filter.length*filter.length));
                //setPixelNoAlpha(img, i + filter.length/2, j + filter.length/2, Channel.BLUE, linearSumBlue/(filter.length*filter.length));
                //setPixelNoAlpha(img, i + filter.length/2, j + filter.length/2, Channel.GREEN, linearSumGreen/(filter.length*filter.length));

                if(linearSumRed > 255) {
                    linearSumRed = 255;
                }
                if(linearSumGreen > 255) {
                    linearSumGreen = 255;
                }
                if(linearSumBlue > 255) {
                    linearSumBlue = 255;
                }
                if(linearSumRed < 0) {
                    linearSumRed = 0;
                }
                if(linearSumGreen < 0) {
                    linearSumGreen = 0;
                }
                if(linearSumBlue < 0) {
                    linearSumBlue = 0;
                }

                img.setRGB(i + filter.length/2, j + filter.length/2, new Color((int)linearSumRed,(int)linearSumBlue,(int)linearSumGreen).getRGB());

                linearSumRed = 0;
                linearSumBlue = 0;
                linearSumGreen = 0;
            }
        }

        //img = bilinearResize(img, getImageFile().getWidth(), getImageFile().getHeight());

        return img;
    }

    private BufferedImage convolve(double[][] filter) {
        filter = rotateFilter180(filter);
        return correlate(filter);

    }


    private double[][] rotateFilter180(double[][] filter) {

        int N = filter.length;

        for (int i = 0; i < N / 2; i++) {
            for(int j = 0; j < N; j++) {
                double temp = filter[i][j];
                filter[i][j] = filter[N - i - 1][N - j - 1];
                filter[N - i - 1][N - j - 1] = temp;
            }
        }

        return filter;
    }




}
