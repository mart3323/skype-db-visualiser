package Analyser.Chart;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

public class Chart {
    // TODO: Just test this already, see what it returns it might already be usable and only need a dedicated canvas drawer
    // TODO: Return coordinate-property pairs instead
    public final List<Pair<Double, Double>> pairs;
    public final HashMap<Object, Integer> xMap;
    public final HashMap<Object, Integer> yMap;

    public Chart(List<Pair<Double, Double>> pairs, HashMap xMap, HashMap yMap) {
        this.pairs = pairs;
        this.xMap = (HashMap<Object, Integer>)xMap;
        this.yMap = (HashMap<Object, Integer>)yMap;
    }
}
