package com.example.bugrap.data.dto;

public class BugDistributionData {
    private final long closed;
    private final long assignedUnresolved;
    private final long unassigned;

    public BugDistributionData(long closed, long assignedUnresolved, long unassigned) {
        this.closed = closed;
        this.assignedUnresolved = assignedUnresolved;
        this.unassigned = unassigned;
    }

    public long getClosed() {
        return this.closed;
    }

    public long getAssignedUnresolved() {
        return this.assignedUnresolved;
    }

    public long getUnassigned() {
        return this.unassigned;
    }

}
