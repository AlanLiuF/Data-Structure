import java.util.Iterator;

/**
 * Class for a simple hash map.
 * @author Brian S. Borowski, Fengzhe Liu (fl2635)
 * @version 1.0 November 9, 2022
 */
public class MyHashMap<K extends Comparable<K>, V> implements MyMap<K, V> {
    // Helpful list of primes available at:
    // https://www2.cs.arizona.edu/icon/oddsends/primes.htm
    private static final int[] primes = new int[] {
            101, 211, 431, 863, 1733, 3467, 6947, 13901, 27803, 55609, 111227,
            222461 };
    private static final double MAX_LOAD_FACTOR = 0.75;
    private Entry<K, V>[] table;
    private int primeIndex, numEntries;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        table = new Entry[primes[primeIndex]];
    }

    /**
     * Returns the number of buckets in this MyHashMap.
     * @return the number of buckets in this MyHashMap
     */
    public int getTableSize() {
        return table.length;
    }

    /**
     * Returns the number of key-value mappings in this map.
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return numEntries;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     * @return true if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return numEntries == 0;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     * @param  key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this
     *         map contains no mapping for the key
     */

    @Override
    public V get(K key) {
        Iterator<Entry<K, V>> iter = this.iterator();
        while (iter.hasNext()) {
            Entry<K, V> e = iter.next();
            if (key.equals(e.key)) {
                return e.value;
            }
        }
        return null;
    }


    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is replaced
     * by the specified value.
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    @Override
    public V put(K key, V value) {
        int x = key.hashCode();
        int index = x % primes[primeIndex];
        if (table[index] == null) {
            numEntries++;
            Entry<K, V> entry = new Entry<>(key, value);
            table[index] = entry;
            if (getLoadFactor() > MAX_LOAD_FACTOR && getTableSize() != 222461) {      // rehash
                    rehash();
            }
            return null;
        } else if (table[index].key != null && table[index].key.equals(key)) {     // replace the value
            V oldValue = table[index].value;
            table[index].value = value;
            return oldValue;
        } else if (table[index].key != null && !table[index].key.equals(key)) {
            Entry<K, V> E = table[index];
            for (int i = 1; i < findlistlen(index); i++) {
                E = E.next;
                if (E.key.equals(key)) {                // replace the value
                    V oldValue = E.value;
                    E.value = value;
                    return oldValue;
                }
            }
            // collision, use chaining to solve
            numEntries++;
            Entry<K, V> old_entry = table[index];
            table[index] = new Entry<>(key, value);
            table[index].next = old_entry;
            if (getLoadFactor() > MAX_LOAD_FACTOR && getTableSize() != 222461) {       // rehash
                    rehash();
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    private void rehash() {
            primeIndex = primeIndex + 1;
            Entry<K, V>[] new_table = new Entry[primes[primeIndex]];
            Iterator<Entry<K, V>> iter = this.iterator();
            while (iter.hasNext()) {
                Entry<K, V> e = iter.next();
                int x = e.key.hashCode();
                int index = x % primes[primeIndex];
                if (new_table[index] == null) {
                    Entry<K, V> entry = new Entry<>(e.key, e.value);
                    new_table[index] = entry;
                } else {               // collision, use chaining to solve
                    Entry<K, V> old_entry = new_table[index];
                    new_table[index] = new Entry<>(e.key, e.value);
                    new_table[index].next = old_entry;
                }
            }
            table = new_table;

    }







    /**
     * Removes the mapping for a key from this map if it is present.
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */

    @Override
    public V remove(K key) {
        Iterator<Entry<K, V>> iter = this.iterator();
        while (iter.hasNext()) {
            Entry<K, V> e = iter.next();
            if (key.equals(e.key)) {
                numEntries--;
                V oldValue = e.value;
                if (e.next == null) {
                    if (removehelper(e.key) != -1) {
                    int index = removehelper(e.key);
                        if (findlistlen(index) == 1){
                            table[index] = null;}
                        else {
                            Entry<K,V> c = table[index];
                            for(int i = 1; i < findlistlen(index) - 1; i++){
                                c=c.next;
                            }
                            c.next = null;
                        }
                    return oldValue;
                    }

                } else {      // it means that the Entry is on a chain, and not the last one
                    e.key = e.next.key;
                    e.value = e.next.value;
                    e.next = e.next.next;
                    return oldValue;
                }
            }
        }
        return null;
    }
    private int removehelper(K key) {
        for(int i = 0; i < table.length; i++){
            Entry<K,V> chain = table[i];
            for(int j = 0; j < findlistlen(i); j++){
                if (chain.key != null && chain.key.equals(key)){
                    return i;
                }else chain = chain.next;
            }
        }
        return -1;
    }

    private int findlistlen(int index){
        int count = 1;
        if (table[index] != null) {
            Entry<K, V> cur = table[index];
            while (cur.next != null) {
                count++;
                cur = cur.next;
            }
            return count;
        }
        return -1;
    }



    /**
     * Returns the load factor of this MyHashMap, defined as the number of
     * entries / table size.
     * @return the load factor of this MyHashMap
     */
    public double getLoadFactor() {
        return (double)numEntries / primes[primeIndex];
    }

    /**
     * Returns the maximum length of a chain in this MyHashMap. This value
     * provides information about how well the hash function is working. With a
     * max load factor of 0.75, we would like to see a max chain length close
     * to 1.
     * @return the maximum length of a chain in this MyHashMap
     */
    public int computeMaxChainLength() {
        int maxChainLength = 0;
        for (Entry<K, V> chain : table) {
            if (chain != null) {
                int currentChainLength = 0;
                Entry<K, V> chainPtr = chain;
                while (chainPtr != null) {
                    currentChainLength++;
                    chainPtr = chainPtr.next;
                }
                if (currentChainLength > maxChainLength) {
                    maxChainLength = currentChainLength;
                }
            }
        }
        return maxChainLength;
    }

    /**
     * Returns a string representation of this MyHashMap for tables with up
     * to and including 1000 entries.
     * @return a string representation of this MyHashMap
     */
    public String toString() {
        if (numEntries > 1000) {
            return "HashMap too large to represent as a string.";
        }
        if (numEntries == 0) {
            return "HashMap is empty.";
        }
        int maxIndex;
        for (maxIndex = table.length - 1; maxIndex >= 0; maxIndex--) {
            if (table[maxIndex] != null) {
                break;
            }
        }
        int maxIndexWidth = String.valueOf(maxIndex).length();
        StringBuilder builder = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> chain = table[i];
            if (chain != null) {
                int indexWidth = String.valueOf(i).length();
                builder.append(" ".repeat(maxIndexWidth - indexWidth));
                builder.append(i);
                builder.append(": ");
                while (chain != null) {
                    builder.append(chain);
                    if (chain.next != null) {
                        builder.append(" -> ");
                    }
                    chain = chain.next;
                }
                builder.append(newLine);
            }
        }
        return builder.toString();
    }

    /**
     * Returns an iterator over the Entries in this MyHashMap in the order
     * in which they appear in the table.
     * @return an iterator over the Entries in this MyHashMap
     */
    public Iterator<Entry<K, V>> iterator() {
        return new MapItr();
    }

    private class MapItr implements Iterator<Entry<K, V>> {
        private Entry<K, V> current;
        private int index;

        MapItr() {
            advanceToNextEntry();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Entry<K, V> next() {
            Entry<K, V> e = current;
            if (current.next == null) {
                index++;
                advanceToNextEntry();
            } else {
                current = current.next;
            }
            return e;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void advanceToNextEntry() {
            while (index < table.length && table[index] == null) {
                index++;
            }
            current = index < table.length ? table[index] : null;
        }
    }

    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        int upperLimit = 1000;
        int expectedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            map.put(String.valueOf(i), i);
            expectedSum += i;
        }


        System.out.println("Size            : " + map.size());
        System.out.println("Table size      : " + map.getTableSize());
        System.out.println("Load factor     : " + map.getLoadFactor());
        System.out.println("Max chain length: " + map.computeMaxChainLength());
        System.out.println();
        System.out.println("Expected sum: " + expectedSum);
        System.out.println(map);


        int receivedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            receivedSum += map.get(String.valueOf(i));
        }
        System.out.println("Received sum: " + receivedSum);



        // replace all the value, and inspect again
        expectedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            int newValue = upperLimit - i + 1;
            map.put(String.valueOf(i), newValue);
            expectedSum += newValue;
        }

        System.out.println("Size            : " + map.size());
        System.out.println("Table size      : " + map.getTableSize());
        System.out.println("Load factor     : " + map.getLoadFactor());
        System.out.println("Max chain length: " + map.computeMaxChainLength());
        System.out.println();
        System.out.println("Expected sum: " + expectedSum);
        System.out.println(map);


        receivedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            receivedSum += map.get(String.valueOf(i));
        }
        System.out.println("Received sum: " + receivedSum);

        receivedSum = 0;
        Iterator<Entry<String, Integer>> iter = map.iterator();
        while (iter.hasNext()) {
            receivedSum += iter.next().value;
        }
        System.out.println("Received sum: " + receivedSum);



        // remove all the key-value pair, and inspect again
        receivedSum = 0;
        for (int i = 1; i <= upperLimit; i++) {
            receivedSum += map.remove(String.valueOf(i));
        }
        System.out.println("Received sum: " + receivedSum);
        System.out.println("Size            : " + map.size());
        System.out.println("Table size      : " + map.getTableSize());
        System.out.println("Load factor     : " + map.getLoadFactor());
        System.out.println("Max chain length: " + map.computeMaxChainLength());
        System.out.println();
        System.out.println("Expected sum: " + expectedSum);


    }
}
