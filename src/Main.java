import com.sun.org.apache.regexp.internal.RE;
import fourier.ComplexNumber;
import fourier.FFT;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        MyImage myImage = new MyImage("res/sample.jpeg"); // res/lena.png, res/kaplan.jpg, res/orangutan.jpg, res/david.jpg, res/victoria.jpg are other options

        //Adds salt and pepper to image and outputs saltAndPepper.jpg in res folder
        myImage.addSaltAndPepper(40);

        MyImage i = new MyImage("res/saltAndPepper.jpg");
        i.removeSaltAndPepper();

        // adds gaussian noise to image and outputs withGaussianNoise.jpg in res folder
        myImage.addGaussianNoise(2000,1);

        // removed gaussian noise with gaussian blur filter
        MyImage im = new MyImage("res/withGaussianNoise.jpg");
        im.applyGaussianFilter(5,5, "res/gaussianNoiseRemoved.jpg");



        // For the FFT, algorithm, I couldn't do it from scratch but I took some code from a Java framework called Catalano and integrated some of their
        // algorithms into my own work. I hope this is ok.
        // Link: https://github.com/DiegoCatalano/Catalano-Framework


        int resizedWidth = 512;

        String imagePath = "res/sample.jpeg"; // res/lena.png, res/kaplan.jpg, res/orangutan.jpg, res/david.jpg, res/victoria.jpg are other options

        // get image in complex numbers
        ComplexNumber[][] imageAsComplex = getImageInComplexForm(imagePath, resizedWidth);

        // convert to frequency domain
        FFT.fft2D(imageAsComplex, FFT.Direction.FORWARD);
        FFT.saveFourierImage(imageAsComplex, resizedWidth, resizedWidth, true, "imageInFrequencyDomain.jpg");

        // convert back to spatial domain
        FFT.fft2D(imageAsComplex, FFT.Direction.BACKWARD);
        FFT.saveFourierImage(imageAsComplex, resizedWidth, resizedWidth, false, "imageConvertedBackToSpatialDomain.jpg");

        // get original image again
        imageAsComplex = getImageInComplexForm(imagePath, resizedWidth);

        // apply highpass fourier filter and save result
        FFT.fft2D(imageAsComplex, FFT.Direction.FORWARD);
        FFT.fourierHighPass(imageAsComplex, 5);
        FFT.fft2D(imageAsComplex, FFT.Direction.BACKWARD);
        FFT.saveFourierImage(imageAsComplex, resizedWidth, resizedWidth, false, "highPassFilter.jpg");


        // get original
        imageAsComplex = getImageInComplexForm(imagePath, resizedWidth);


        // apply periodic noise and save result
        FFT.fft2D(imageAsComplex, FFT.Direction.FORWARD);
        FFT.addPeriodicNoise(imageAsComplex);
        FFT.fft2D(imageAsComplex, FFT.Direction.BACKWARD);
        FFT.saveFourierImage(imageAsComplex, resizedWidth, resizedWidth, false, "addedPeriodicNoise.jpg");


        // get the one with periodic noise
        imageAsComplex = getImageInComplexForm("res/fourier/addedPeriodicNoise.jpg", resizedWidth);

        // remove periodic noise and save result (not very good but still removes some of it)
        FFT.fft2D(imageAsComplex, FFT.Direction.FORWARD);
        FFT.removePeriodicNoise(imageAsComplex);
        FFT.fft2D(imageAsComplex, FFT.Direction.BACKWARD);
        FFT.saveFourierImage(imageAsComplex, resizedWidth, resizedWidth, false, "removedPeriodicNoise.jpg");











    }


    public static ComplexNumber[][] getImageInComplexForm(String filePath, int resizedWidth) {

        MyImage myImage = new MyImage(filePath);

        myImage.bilinearResize(resizedWidth, resizedWidth, "res/fourier/resizedForFFT.jpeg");
        myImage = new MyImage("res/fourier/resizedForFFT.jpeg");
        myImage.rgb2GreyScale("res/fourier/grayScaledForFFT.jpg");
        myImage = new MyImage("res/fourier/grayScaledForFFT.jpg");


        ComplexNumber[][] imageAsComplex = new ComplexNumber[resizedWidth][resizedWidth];

        for (int i = 0; i < resizedWidth; i++) {
            for (int j = 0; j < resizedWidth; j++) {

                imageAsComplex[i][j] = new ComplexNumber();
                imageAsComplex[i][j].real = myImage.getPixel(i,j, MyImage.Channel.RED);


            }
        }
        return imageAsComplex;
    }


    public static void applyFullSobel(String path) {
        MyImage myImage = new MyImage(path);
        myImage.applySobelFilterX();
        myImage = new MyImage(path);
        myImage.applySobelFilterY();
        MyImage i1 = new MyImage("res/applied_sobel_filterX.jpg");
        MyImage i2 = new MyImage("res/applied_sobel_filterY.jpg");
        addImages(i1,i2, false);

    }

    public static void generateHybridImage(String path1, String path2, int w, int h) {

        w = 800;
        h = 600;

        MyImage firstImage = new MyImage(path1);
        firstImage.rgb2GreyScale("res/hybrid/gs1.jpg");
        MyImage secondImage = new MyImage(path2);
        secondImage.rgb2GreyScale("res/hybrid/gs2.jpg");

        firstImage = new MyImage("res/hybrid/gs1.jpg");
        secondImage = new MyImage("res/hybrid/gs2.jpg");


        firstImage.bilinearResize(w,h);
        secondImage.bilinearResize(w,h);

        firstImage.applyGaussianFilter(5,1, "res/hybrid/first_with_gaussian.jpg");
        MyImage firstImageWithGaussian = new MyImage("res/hybrid/first_with_gaussian.jpg");

        secondImage.applyGaussianFilter(5,1,"res/hybrid/second_with_gaussian.jpg");
        MyImage secondImageWithGaussian = new MyImage("res/hybrid/second_with_gaussian.jpg");

        MyImage originalSecondImage = new MyImage("res/hybrid/gs2.jpg");
        originalSecondImage.bilinearResize(w,h);

        subtractImages(secondImageWithGaussian, originalSecondImage );
        MyImage subtractedImage = new MyImage("res/hybrid/subtractedImage.jpg");

        firstImageWithGaussian.bilinearResize(w,h, "res/hybrid/resized1.jpg");
        firstImageWithGaussian = new MyImage("res/hybrid/resized1.jpg");
        subtractedImage.bilinearResize(w,h, "res/hybrid/resized2.jpg");
        subtractedImage = new MyImage("res/hybrid/resized2.jpg");
        addImages(firstImageWithGaussian, subtractedImage, true);








    }

    public static MyImage addImages(MyImage firstImage, MyImage secondImage, boolean isHybrid) {

        for(int i = 0; i < firstImage.getImageFile().getWidth(); i++) {
            for(int j = 0; j < secondImage.getImageFile().getHeight(); j++) {

                int newRedValue = firstImage.getPixel(i,j, MyImage.Channel.RED) + secondImage.getPixel(i,j, MyImage.Channel.RED);
                if (newRedValue > 255)
                    firstImage.setPixel(i,j, MyImage.Channel.RED, 255);
                else
                    firstImage.setPixel(i,j, MyImage.Channel.RED, newRedValue);

                int newGreenValue = firstImage.getPixel(i,j, MyImage.Channel.GREEN) + secondImage.getPixel(i,j, MyImage.Channel.GREEN);
                if (newGreenValue > 255)
                    firstImage.setPixel(i,j, MyImage.Channel.BLUE, 255);
                else
                    firstImage.setPixel(i,j, MyImage.Channel.BLUE, newGreenValue);

                int newBlueValue = firstImage.getPixel(i,j, MyImage.Channel.BLUE) + secondImage.getPixel(i,j, MyImage.Channel.BLUE);
                if (newBlueValue > 255)
                    firstImage.setPixel(i,j, MyImage.Channel.GREEN, 255);
                else
                    firstImage.setPixel(i,j, MyImage.Channel.GREEN, newBlueValue);




            }
        }
        if(isHybrid) {
            firstImage.copyImage("res/finalHybrid.jpg");
            return new MyImage("res/finalHybrid.jpg");
        }
        else {
            firstImage.copyImage("res/finalSobel.jpg");
            return new MyImage("res/finalSobel.jpg");
        }
    }

    // subtract second from first
    public static MyImage subtractImages(MyImage firstImage, MyImage secondImage) {

        for(int i = 0; i < firstImage.getImageFile().getWidth(); i++) {
            for(int j = 0; j < secondImage.getImageFile().getHeight(); j++) {

                int newRedValue = secondImage.getPixel(i,j, MyImage.Channel.RED) - firstImage.getPixel(i,j, MyImage.Channel.RED);
                if (newRedValue < 0)
                    firstImage.setPixel(i,j, MyImage.Channel.RED, 0);
                else
                    firstImage.setPixel(i,j, MyImage.Channel.RED, newRedValue);

                int newGreenValue = secondImage.getPixel(i,j, MyImage.Channel.GREEN) - firstImage.getPixel(i,j, MyImage.Channel.GREEN);
                if (newGreenValue < 0)
                    firstImage.setPixel(i,j, MyImage.Channel.BLUE, 0);
                else
                    firstImage.setPixel(i,j, MyImage.Channel.BLUE, newGreenValue);

                int newBlueValue = secondImage.getPixel(i,j, MyImage.Channel.BLUE) - firstImage.getPixel(i,j, MyImage.Channel.BLUE);
                if (newBlueValue < 0)
                    firstImage.setPixel(i,j, MyImage.Channel.GREEN, 0);
                else
                    firstImage.setPixel(i,j, MyImage.Channel.GREEN, newBlueValue);

            }
        }
        firstImage.copyImage("res/hybrid/subtractedImage.jpg");
        return new MyImage("res/hybrid/subtractedImage.jpg");
    }



}
