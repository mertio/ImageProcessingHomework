public class Main {

    public static void main(String[] args) {

        MyImage myImage = new MyImage("res/aboubakar.jpg");

        // enums: RED, BLUE, GREEN, ALPHA
        System.out.println("RED value of pixel(3,4): " + myImage.getPixel(3,4, MyImage.Channel.RED));

        // image to greyscale
        myImage.rgb2GreyScale();

        // sol true : maximizes channel at a high enough constant value (e.g. 255)
        // sol false : loops around at a high enough constant value (e.g. 255)
        //myImage.shift(MyImage.Channel.GREEN, 40, true);


    }
}
