package riakdemo;


import java.util.UUID;

public class Item {
    public String userName;
    public String prodId;
    public String tombstone; // APPLE:UUID  -> DELETED:UUID


    private static final String USER_FORMAT = "- %-10s  %-16s %-16s\n";

    //UUID.randomUUID().toString()
    public Item(String userName, String prodId, String tombstone) {
        this.userName = userName;
        this.prodId = prodId;
        this.tombstone = tombstone;
    }

    @Override
    public String toString() {
        return String.format(USER_FORMAT, userName, prodId, tombstone);
    }
}
