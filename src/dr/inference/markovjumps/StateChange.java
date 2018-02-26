package dr.inference.markovjumps;
public class StateChange {
    // time of the change in colour
    private double time;
    // the state associated with this time
    private int state;
    private int previousState;
    public StateChange(StateChange change) {
        this(change.time, change.state);
    }
    public StateChange(double time, int state, int previousState) {
        this.time = time;
        this.state = state;
        this.previousState = previousState;
    }
    public StateChange(double time, int state) {
        this.time = time;
        this.state = state;
        this.previousState = -1;
    }
    public StateChange clone() {
        return new StateChange(time, state, previousState);
    }
    public final double getTime() {
        return time;
    }
    public final int getState() {
        return state;
    }
    public final int getPreviousState() {
        return previousState;
    }
    public final void setPreviousState(int state) {
        this.previousState = state;
    }
    public final void setState(int state) {
        this.state = state;
    }
    public final void setTime(double time) {
        this.time = time;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(getTime()).append(",").append(getState()).append("}");
        return sb.toString();
    }
}
