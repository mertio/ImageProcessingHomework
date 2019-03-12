import com.sun.org.apache.regexp.internal.RE;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

        int sum = (red << 16) + (green << 8) + blue;
        img.setRGB(x,y,sum);


    }


    public void copyImage(String pathAndFile) {
        try {
            ImageIO.write(imageFile, "jpg", new File(pathAndFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createImageFile(BufferedImage img, String pathAndFile) {
        try {
            ImageIO.write(img, "jpg", new File(pathAndFile));
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

                setPixelNoAlpha(img, x, y, Channel.RED, r);
                setPixelNoAlpha(img, x, y, Channel.GREEN, g);
                setPixelNoAlpha(img, x, y, Channel.BLUE, b);


            }
        }

        createImageFile(img,"res/bilinear_resize.jpg");
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



    public void unintentiallyMadeCoolEffectScale(int newWidth, int newHeight) {

        int oldWidth = imageFile.getWidth();
        int oldHeight = imageFile.getHeight();

        double xRatio = (oldWidth - 1) / (double) newWidth;
        double yRatio = (oldHeight - 1) / (double) newHeight;

        BufferedImage img = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        double px,py, xdiff, ydiff;
        int A, B, C, D;
        for(int x = 0; x < newWidth; x++) {
            for(int y = 0; y < newHeight; y++) {

                px = x*xRatio;
                py = y*yRatio;

                xdiff = x*xRatio - px;
                ydiff = y*yRatio - py;

                int r,g,b;

                A = getPixel( (int)px, (int)py, Channel.RED);
                B = getPixel( (int)px + 1, (int)py, Channel.RED);
                C = getPixel( (int)px, (int)py + 1, Channel.RED);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.RED);

                r = (int)(A * (1 - xdiff) * (1 - ydiff) + B * xdiff * (1 - ydiff) + C * ydiff * (1 - xdiff) + D * xdiff * ydiff);

                A = getPixel( (int)px, (int)py, Channel.GREEN);
                B = getPixel( (int)px + 1, (int)py, Channel.GREEN);
                C = getPixel( (int)px, (int)py + 1, Channel.GREEN);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.GREEN);

                g = (int)(A * (1 - xdiff) * (1 - ydiff) + B * xdiff * (1 - ydiff) + C * ydiff * (1 - xdiff) + D * xdiff * ydiff);

                A = getPixel( (int)px, (int)py, Channel.BLUE);
                B = getPixel( (int)px + 1, (int)py, Channel.BLUE);
                C = getPixel( (int)px, (int)py + 1, Channel.BLUE);
                D = getPixel( (int)px + 1, (int)py + 1, Channel.BLUE);

                b = (int)(A * (1 - xdiff) * (1 - ydiff) + B * xdiff * (1 - ydiff) + C * ydiff * (1 - xdiff) + D * xdiff * ydiff);



                setPixel(img, x, y, Channel.RED, r);
                setPixel(img, x, y, Channel.BLUE, b);
                setPixel(img, x, y, Channel.GREEN, g);

            }
        }

        createImageFile(img,"res/cool_reddish_effect.jpg");
    }




}
