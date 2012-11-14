
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Selection {

    //median of 5 numbers in O(1)
    public static int median5(List<Integer> list) {

        int a = list.get(0);
        int b = list.get(1);
        int c = list.get(2);
        int d = list.get(3);
        int e = list.get(4);

        boolean ab = a > b;
        boolean cd = c > d;

        int maxAB = ab ? a : b;
        int minAB = ab ? b : c;
        int maxCD = cd ? c : d;
        int minCD = cd ? d : c;

        boolean abcd = maxAB > maxCD;

        if (abcd) {
            //c, d, e, f = minAB
            int f = minAB;
            boolean ef = e > f;
            int maxEF = ef ? e : f;
            int minEF = ef ? f : e;
            boolean cdef = maxCD > maxEF;
            if (cdef) {
                //e, f, g = minCD
                int g = minCD;
                boolean efg = maxEF > g;
                if (efg) {
                    return maxEF;
                } else {
                    return g;
                }
            } else {
                //c, d, g = minEF
                int g = minEF;
                boolean cdg = maxCD > g;
                if (cdg) {
                    return maxCD;
                } else {
                    return g;
                }
            }

        } else {
            //a, b, e, f = minCD
            int f = minCD;

            boolean ef = e > f;

            int maxEF = ef ? e : f;
            int minEF = ef ? f : e;

            boolean abef = maxAB > maxEF;

            if (abef) {
                //e, f, g = minAB
                int g = minAB;
                boolean efg = maxEF > g;
                if (efg) {
                    return maxEF;
                } else {
                    return g;
                }
            } else {
                //a, b, g = minEF
                int g = minEF;
                boolean abg = maxAB > g;
                if (abg) {
                    return maxAB;
                } else {
                    return g;
                }
            }
        }
    }

    //median of list with sot in O(n*log(n))
    public static int median(List<Integer> list) {
        Collections.sort(list);
        int median = list.get(list.size() / 2);
        return median;
    }

    // selection in O(n^2) in the worst case
    public static int randomSelect(List<Integer> list, int index) {

        Random rand = new Random();
        int partIndex = rand.nextInt(list.size());

        int q = list.get(partIndex);
        int qIndex = list.indexOf(q);
        List<Integer> partitionList = new ArrayList<Integer>();
        partitionList.add(q);

        int beforeRank = 0;

        for (int i = 0; i < list.size(); i++) {
            if (i == qIndex) {
                continue;
            }
            Integer value = list.get(i);
            if (value < q) {
                partitionList.add(0, value);
                beforeRank++;
            } else {
                partitionList.add(partitionList.size(), value);
            }
        }        

        if (beforeRank > index) {
            return randomSelect(partitionList.subList(0, beforeRank), index);
        } else if (beforeRank < index) {
            return randomSelect(partitionList.subList(beforeRank + 1, list.size()), index - beforeRank - 1);
        } else {
            return q;
        }
    }

    //median of medians of list
    public static int medianOfMedians(List<Integer> list) {
        int groupCount = list.size() / 5;
        List<Integer> medians = new ArrayList<Integer>();
                
        for (int i = 0; i <= groupCount; i++) {
            List<Integer> group = new ArrayList<Integer>();
            int startIndex = i * 5;
            int endIndex = i * 5 + 5;
            boolean five = true;
            if (endIndex > list.size()) {
                endIndex = list.size();
                five = false;
            }
            group.addAll(list.subList(startIndex, endIndex));
            if (!group.isEmpty()) {
                int median;
                if (five) {
                    median = median5(group);
                } else {
                    median = median(group);
                }
                medians.add(median);
            }
        }
        
        if (medians.size() > 1) {
            return medianOfMedians(medians);
        } else {
            return medians.get(0);
        }

    }
    
    //selection in O(n) in the worst case
    public static int select(List<Integer> list, int index) {

        int medianOfMedians = medianOfMedians(list);
        int q = medianOfMedians;
        List<Integer> partitionList = new ArrayList(list);
        int beforeRank = 0;

        int i = 0;
        int j = partitionList.size() - 1;
        do {            
            while (partitionList.get(i) < q) ++i;
            while (partitionList.get(j) > q) --j;
            if (i <= j) {
                int temp = partitionList.get(i);
                partitionList.set(i, partitionList.get(j));
                partitionList.set(j, temp);
                if (partitionList.get(i).equals(partitionList.get(j))) {
                    j--;
                }
            }
        } while (i < j);
        
        beforeRank = partitionList.indexOf(q);
        
        if (beforeRank > index) {
            return select(partitionList.subList(0, beforeRank), index);
        } else if (beforeRank < index) {
            return select(partitionList.subList(beforeRank + 1, partitionList.size()), index - beforeRank - 1);
        } else {
            return q;
        }
    }
}
