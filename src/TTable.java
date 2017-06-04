public class TTable {

    private final int size = 524288;                          // size of the ttable should be power of 2
    private TTableEntry[] entries = new TTableEntry[size];    // array of table entries

    public TTable() {

        // initialize each entry
        for(int i = 0; i < size; ++i)
            entries[i] = new TTableEntry();
    }


    public TTableEntry lookup(long hash) {
        int index = (int)(hash & 524287);     // use first 20 bits for index
        if(entries[index].valid)              // return a value only if the entry is valid
            return entries[index];
        return null;
    }


    public void store(TTableEntry t) {
        int index = (int)(t.hash & 524287);   // use first 20 bits for index
        entries[index] = t;                   // place in the array
    }
}
