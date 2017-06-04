public class TTableEntry {

    public long hash;
    public int depth;
    public int alpha;
    public int beta;
    public int value;
    public boolean valid;

    public TTableEntry() {
        this.valid = false;
    }

    public TTableEntry(long hash, int d, int a, int b, int v) {
        this.hash = hash;
        this.depth = d;
        this.alpha = a;
        this.beta = b;
        this.value = v;
        this.valid = true;
    }
}
