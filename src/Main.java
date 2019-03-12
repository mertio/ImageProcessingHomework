public class Main {

    public static void main(String[] args) {

        MyImage myImage = new MyImage("res/sample.jpeg");


        // scale to HD with neighbor resize
        //myImage.neighborResize(1920,1080);

        // scale to HD with bilinear resize
        myImage.bilinearResize(800, 600);



    }
}
