package dev.oxoo2a.sim4da;

public abstract class LogicalTimestamp {
    
    public abstract LogicalTimestamp getIncremented(int nodeId);
    
    public abstract LogicalTimestamp getAdjusted(LogicalTimestamp received);
    
    public abstract long getValue(int nodeId);
    
    public abstract boolean isBeforeOrEqual(LogicalTimestamp other);
    
    public static class ExtendedLamportTimestamp extends LogicalTimestamp {
        private final int nodeId;
        private final long value;
        public ExtendedLamportTimestamp(int nodeId, long value) {
            this.nodeId = nodeId;
            this.value = value;
        }
        @Override
        public LogicalTimestamp getIncremented(int nodeId) {
            return new ExtendedLamportTimestamp(this.nodeId, value+1L);
        }
        @Override
        public LogicalTimestamp getAdjusted(LogicalTimestamp received) {
            return new ExtendedLamportTimestamp(nodeId, Math.max(value, received.getValue(nodeId))+1L);
        }
        @Override
        public long getValue(int nodeId) {
            return value;
        }
        @Override
        public boolean isBeforeOrEqual(LogicalTimestamp other) {
            long valueDiff = value-other.getValue(nodeId);
            if (valueDiff==0) // Equal values, compare using nodeId
                return nodeId<=((ExtendedLamportTimestamp) other).nodeId;
            return valueDiff<0L ? true : false;
        }
        @Override
        public String toString() {
            return "ExtendedLamportTimestamp(nodeId="+nodeId+",value="+value+")";
        }
    }
    
    public static class VectorTimestamp extends LogicalTimestamp {
        private final long[] vector;
        public VectorTimestamp(int numberOfNodes, long value) {
            vector = new long[numberOfNodes];
            for (int i = 0; i<numberOfNodes; i++) {
                vector[i] = value;
            }
        }
        private VectorTimestamp(long[] vector) {
            this.vector = vector;
        }
        @Override
        public LogicalTimestamp getIncremented(int nodeId) {
            long[] newVector = new long[vector.length];
            for (int i = 0; i<vector.length; i++) {
                newVector[i] = i==nodeId ? vector[i]+1 : vector[i];
            }
            return new VectorTimestamp(newVector);
        }
        @Override
        public LogicalTimestamp getAdjusted(LogicalTimestamp received) {
            long[] newVector = new long[vector.length];
            for (int i = 0; i<vector.length; i++) {
                newVector[i] = Math.max(getValue(i), received.getValue(i));
            }
            return new VectorTimestamp(newVector);
        }
        @Override
        public long getValue(int nodeId) {
            return vector[nodeId];
        }
        @Override
        public boolean isBeforeOrEqual(LogicalTimestamp other) {
            for (int i = 0; i<vector.length; i++) {
                if (getValue(i)>other.getValue(i)) return false;
            }
            return true;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("VectorTimestamp(vector=");
            for (int i = 0; i<vector.length; i++) {
                if (i>0) sb.append(",");
                sb.append(vector[i]);
            }
            sb.append(")");
            return sb.toString();
        }
    }
}
