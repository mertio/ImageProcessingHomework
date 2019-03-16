public class Main {

    public static void main(String[] args) {

        MyImage myImage = new MyImage("res/sample.jpeg");

        // NEIGHBOR RESIZE
        // scale to HD with neighbor resize
        //myImage.neighborResize(1920,1080);

        // BILINEAR RESIZE
        // scale to HD with bilinear resize
        //myImage.bilinearResize(1920, 1080);


        // Applies box filter to sample, image shrinks and gets black frame because
        // edges and resize aren't handled
        //myImage.applyBoxFilter(9);


        // Applies highpass filter
        // can use 3 types of filters as param x = 0 or 1 or 2
        //myImage.applyHighpassFilter(0);


        // Applies sharpen filter
        //myImage.applySharpenFilter();

        // Applies Emboss filter
        myImage.applyEmbossFilter();






    }
}
