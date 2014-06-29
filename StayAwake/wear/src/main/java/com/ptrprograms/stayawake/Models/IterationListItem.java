package com.ptrprograms.stayawake.Models;

/**
 * Created by PaulTR on 6/29/14.
 */
public class IterationListItem {
    protected long duration;
    protected String label;

    public IterationListItem( String label, long duration ) {
        this.label = label;
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration( long duration ) {
        this.duration = duration;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel( String label ) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
