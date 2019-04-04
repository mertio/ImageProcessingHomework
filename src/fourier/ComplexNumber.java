package fourier;

public class ComplexNumber {

    public double real = 0;
    public double imaginary = 0;

    public ComplexNumber() {

    }

    public ComplexNumber(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public static double[] getReal(ComplexNumber[] cn){
        double[] n = new double[cn.length];
        for (int i = 0; i < n.length; i++) {
            n[i] = cn[i].real;
        }
        return n;
    }

    public static double[] getImaginary(ComplexNumber[] cn){
        double[] n = new double[cn.length];
        for (int i = 0; i < n.length; i++) {
            n[i] = cn[i].imaginary;
        }
        return n;
    }

    public double getMagnitude(){
        return Math.sqrt(real*real + imaginary*imaginary);
    }


    public double getSquaredMagnitude(){
        return real*real + imaginary*imaginary;
    }


    public double getPhase(){
        return Math.atan2(imaginary,real);
    }



}
