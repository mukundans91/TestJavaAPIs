import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ComparatorsApi8 {
    static class Endpoint {
        int e;
        boolean start;

        public Endpoint(int e, boolean start) {
            this.e = e;
            this.start = start;
        }

        @Override
        public String toString() {
            return "[" + e + ", " + start + "]";
        }
    }
    public static void main(String[] args) {
        List<Integer> arrive = Arrays.asList(1, 1, 2,3);
        ArrayList<Endpoint> endpoints = new ArrayList<>();
        endpoints.add(new Endpoint(1, false));
        for(int e : arrive) {
            endpoints.add(new Endpoint(e, true));
        }
        endpoints.sort(Comparator.comparing((Endpoint e) -> e.e)
                .thenComparing(e -> e.start ));

        endpoints.forEach(System.out::println);
    }
}
