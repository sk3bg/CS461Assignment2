package records;

public record RoomRecord(String name, int capacity) {
    public RoomRecord copy() {
        return new RoomRecord(name, capacity);
    }

    public String toPrettyString() {
        return name;
    }
}
