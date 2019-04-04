package fourier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FFT {





    public enum Direction {
        FORWARD,BACKWARD
    }


    /**
     * 1-D Fast Fourier Transform.
     * @param data Data to transform.
     * @param direction Transformation direction.
     */
    public static void fft1D(ComplexNumber[] data, Direction direction){
        double[] real = ComplexNumber.getReal(data);
        double[] img = ComplexNumber.getImaginary(data);
        if(direction == Direction.FORWARD)
            FFT(real,img);
        else
            FFT(img, real);
        if(direction == Direction.FORWARD){
            for (int i = 0; i < real.length; i++) {
                data[i] = new ComplexNumber(real[i], img[i]);
            }
        }
        else{
            int n = real.length;
            for (int i = 0; i < n; i++) {
                data[i] = new ComplexNumber(real[i] / n, img[i] / n);
            }
        }
    }

    /**
     * 2-D Fast Fourier Transform.
     * @param data Data to transform.
     * @param direction Transformation direction.
     */
    public static void fft2D(ComplexNumber[][] data, Direction direction){


        if(direction.equals(Direction.FORWARD)) {
            for (int x = 0; x < data.length; x++) {
                for (int y = 0; y < data.length; y++) {
                    if (((x + y) & 0x1) != 0) {
                        data[x][y].real *= -1;
                        data[x][y].imaginary *= -1;
                    }
                }
            }
        }

        int n = data.length;
        int m = data[0].length;
        //ComplexNumber[] row = new ComplexNumber[m];//Math.max(m, n)];

        for ( int i = 0; i < n; i++ ){
            // copy row
            //for ( int j = 0; j < m; j++ )
            //row[j] = data[i][j];
            ComplexNumber[] row = data[i];
            // transform it
            FFT.fft1D( row, direction );
            // copy back
            for ( int j = 0; j < m; j++ )
                data[i][j] = row[j];
        }

        // process columns
        ComplexNumber[]	col = new ComplexNumber[n];

        for ( int j = 0; j < m; j++ ){
            // copy column
            for ( int i = 0; i < n; i++ )
                col[i] = data[i][j];
            // transform it
            FFT.fft1D( col, direction );
            // copy back
            for ( int i = 0; i < n; i++ )
                data[i][j] = col[i];
        }

        if(direction.equals(Direction.BACKWARD)) {
            for (int x = 0; x < data.length; x++) {
                for (int y = 0; y < data.length; y++) {
                    if (((x + y) & 0x1) != 0) {
                        data[x][y].real *= -1;
                        data[x][y].imaginary *= -1;
                    }
                }
            }
        }

    }

    public static void fourierHighPass(ComplexNumber[][] data, int intensity){

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {


                if(i == data.length/2 && j == data.length/2) {

                    for(int a = i - intensity; a < i + intensity; a++) {
                        for(int b = i - intensity; b < i + intensity; b++) {

                            data[a][b].real = 0;
                            data[a][b].imaginary = 0;

                        }
                    }

                }


            }
        }
    }

    public static void addPeriodicNoise(ComplexNumber[][] data){


        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {


                            if(i == data.length/2 - 16 && j == data.length/2 + 16) {
                                data[i][j].real = 3000000;
                                data[i][j].imaginary = 3000000;
                                /*
                                data[i-1][j].real = 10000000;
                                data[i-1][j].imaginary = 10000000;
                                data[i][j-1].real = 10000000;
                                data[i][j-1].imaginary = 10000000;
                                data[i-1][j-1].real = 10000000;
                                data[i-1][j-1].imaginary = 10000000;
                                */

                            }

                        }

        }
    }



    public static void removePeriodicNoise(ComplexNumber[][] data){

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {


                if(data[i][j].real > 1000000 && data[i][j].imaginary > 1000000) {

                    data[i][j].real = 20000;
                    data[i][j].imaginary = 20000;

                }


            }
        }
    }

    public static void createImageFile(BufferedImage img, String pathAndFile) {
        try {
            ImageIO.write(img, "png", new File(pathAndFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFourierImage(ComplexNumber[][] complexData, int height, int width, boolean forward, String fileName) {


        BufferedImage img = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);

        if(forward) {

            //Calculate the magnitude
            double[][] mag = new double[height][width];
            double min = Double.MAX_VALUE;
            double max = -Double.MAX_VALUE;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    //Compute log for perceptual scaling and +1 since log(0) is undefined.
                    mag[i][j] = Math.log(complexData[i][j].getMagnitude() + 1);

                    if (mag[i][j] < min) min = mag[i][j];
                    if (mag[i][j] > max) max = mag[i][j];

                }
            }


            //Scale the image
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    img.setRGB(i, j, new Color((int) scale(min, max, 0, 255, mag[i][j]), (int) scale(min, max, 0, 255, mag[i][j]), (int) scale(min, max, 0, 255, mag[i][j])).getRGB());
                }
            }

            createImageFile(img,"res/fourier/" + fileName);


        }
        else {

            //Show only the real part
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int real = (int)complexData[i][j].real;
                    if(real < 0)
                        real = 0;
                    if(real > 255)
                        real = 255;
                    img.setRGB(i,j, new Color(real,real,real).getRGB());
                }
            }

            createImageFile(img,"res/fourier/" + fileName);


        }



    }

    private static double scale(double fromMin, double fromMax, double toMin, double toMax, double x){
        if (fromMax - fromMin == 0) return 0;
        return (toMax - toMin) * (x - fromMin) / (fromMax - fromMin) + toMin;
    }





    private static void FFT(double[] real, double[] imag) {
        int n = real.length;
        if (n == 0) {
            return;
        } else if ((n & (n - 1)) == 0)  // Is power of 2
            transformRadix2(real, imag);
        else  // More complicated algorithm for arbitrary sizes
            transformBluestein(real, imag);
    }


    private static void transformRadix2(double[] real, double[] imag) {
        int n = real.length;
        int levels = 31 - Integer.numberOfLeadingZeros(n);  // Equal to floor(log2(n))
//        if (1 << levels != n)
//            throw new IllegalArgumentException("Length is not a power of 2");
        double[] cosTable = new double[n / 2];
        double[] sinTable = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            cosTable[i] = Math.cos(2 * Math.PI * i / n);
            sinTable[i] = Math.sin(2 * Math.PI * i / n);
        }

        // Bit-reversed addressing permutation
        for (int i = 0; i < n; i++) {
            int j = Integer.reverse(i) >>> (32 - levels);
            if (j > i) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }

        // Cooley-Tukey decimation-in-time radix-2 FFT
        for (int size = 2; size <= n; size *= 2) {
            int halfsize = size / 2;
            int tablestep = n / size;
            for (int i = 0; i < n; i += size) {
                for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
                    double tpre =  real[j+halfsize] * cosTable[k] + imag[j+halfsize] * sinTable[k];
                    double tpim = -real[j+halfsize] * sinTable[k] + imag[j+halfsize] * cosTable[k];
                    real[j + halfsize] = real[j] - tpre;
                    imag[j + halfsize] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }

            // Prevent overflow in 'size *= 2'
            if (size == n)
                break;
        }
    }

    private static void transformBluestein(double[] real, double[] imag) {
        int n = real.length;
        int m = Integer.highestOneBit(n * 2 + 1) << 1;

        // Trignometric tables
        double[] cosTable = new double[n];
        double[] sinTable = new double[n];
        for (int i = 0; i < n; i++) {
            int j = (int)((long)i * i % (n * 2));  // This is more accurate than j = i * i
            cosTable[i] = Math.cos(Math.PI * j / n);
            sinTable[i] = Math.sin(Math.PI * j / n);
        }

        // Temporary vectors and preprocessing
        double[] areal = new double[m];
        double[] aimag = new double[m];
        for (int i = 0; i < n; i++) {
            areal[i] =  real[i] * cosTable[i] + imag[i] * sinTable[i];
            aimag[i] = -real[i] * sinTable[i] + imag[i] * cosTable[i];
        }
        double[] breal = new double[m];
        double[] bimag = new double[m];
        breal[0] = cosTable[0];
        bimag[0] = sinTable[0];
        for (int i = 1; i < n; i++) {
            breal[i] = breal[m - i] = cosTable[i];
            bimag[i] = bimag[m - i] = sinTable[i];
        }

        // Convolution
        double[] creal = new double[m];
        double[] cimag = new double[m];
        convolve(areal, aimag, breal, bimag, creal, cimag);

        // Postprocessing
        for (int i = 0; i < n; i++) {
            real[i] =  creal[i] * cosTable[i] + cimag[i] * sinTable[i];
            imag[i] = -creal[i] * sinTable[i] + cimag[i] * cosTable[i];
        }
    }

    private static void convolve(double[] xreal, double[] ximag, double[] yreal, double[] yimag, double[] outreal, double[] outimag) {
//        if (xreal.length != ximag.length || xreal.length != yreal.length || yreal.length != yimag.length || xreal.length != outreal.length || outreal.length != outimag.length)
//            throw new IllegalArgumentException("Mismatched lengths");

        int n = xreal.length;

        FFT(xreal, ximag);
        FFT(yreal, yimag);
        for (int i = 0; i < n; i++) {
            double temp = xreal[i] * yreal[i] - ximag[i] * yimag[i];
            ximag[i] = ximag[i] * yreal[i] + xreal[i] * yimag[i];
            xreal[i] = temp;
        }
        inverseTransform(xreal, ximag);

        // Scaling (because this FFT implementation omits it)
        for (int i = 0; i < n; i++) {
            outreal[i] = xreal[i] / n;
            outimag[i] = ximag[i] / n;
        }
    }

    private static void inverseTransform(double[] real, double[] imag) {
        FFT(imag, real);
    }



}
