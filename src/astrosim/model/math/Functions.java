package astrosim.model.math;

public class Functions {
    private Functions() {}

    public static double modulo(double n1, double n2) {
        // Java's modulo is flawed, this one deals with negative numbers.
        return (((n1 % n2) + n2) % n2);
    }
}
