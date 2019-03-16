import com.sun.org.apache.regexp.internal.RE;

public class Main {

    public static void main(String[] args) {

        MyImage myImage = new MyImage("res/sample.jpeg");

        // NEIGHBOR RESIZE
        // scale to HD with neighbor resize
        //myImage.neighborResize(1920,1080);

        // ***********************************************************************************************

        // BILINEAR RESIZE
        // scale to HD with bilinear resize
        //myImage.bilinearResize(1920, 1080);

        // ***********************************************************************************************

        // Applies box filter to sample, image shrinks and gets black frame because
        // edges and resize aren't handled
        //myImage.applyBoxFilter(9);

        // ***********************************************************************************************

        // Applies highpass filter
        // can use 3 types of filters as param x = 0 or 1 or 2
        //myImage.applyHighpassFilter(0);

        // ***********************************************************************************************

        // Applies sharpen filter
        //myImage.applySharpenFilter();

        // ***********************************************************************************************

        // Applies Emboss filter
        //myImage.applyEmbossFilter();

        // ***********************************************************************************************

        // Applies gaussian filter
        // myImage.applyGaussianFilter(5,1,"res/applied_gaussian_filter.jpg");

        // ***********************************************************************************************

        // Generates hybrid image, finalHybrid. Something is not right but I couldn't find what.
        // (res/hybrid has intermediate images during hybrid generation)
        // Look in finalHybrid.jpg for result

        //generateHybridImage("res/david.jpg", "res/victoria.jpg",800,600);
        //generateHybridImage("res/kaplan.jpg", "res/orangutan.jpg", 600,600);


        // ***********************************************************************************************


        // Applies Sobel filter (Look in finalHybrid.jpg for result)
        applyFullSobel("res/sample.jpeg");





    }

    public static void applyFullSobel(String path) {
        MyImage myImage = new MyImage(path);
        myImage.applySobelFilterX();
        myImage = new MyImage(path);
        myImage.applySobelFilterY();
        MyImage i1 = new MyImage("res/applied_sobel_filterX.jpg");
        MyImage i2 = new MyImage("res/applied_sobel_filterY.jpg");
        addImages(i1,i2);

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
        addImages(firstImageWithGaussian, subtractedImage);








    }

    public static MyImage addImages(MyImage firstImage, MyImage secondImage) {

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
        firstImage.copyImage("res/finalHybrid.jpg");
        return new MyImage("res/finalHybrid.jpg");
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
