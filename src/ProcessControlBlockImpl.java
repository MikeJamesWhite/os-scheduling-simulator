import simulator.*;
import java.util.*;

/**
 * Concrete Implementation of Process Control Block used by kernel and simulator.
 * 
 * @author Michael White
 * @version 02/05/2018
 */
public class ProcessControlBlockImpl implements ProcessControlBlock {

    private Queue<Instruction> pending = ArrayList <Instruction>();
    private Instruction current;
    private int priority;
    private int processID;
    private String name;

    /**
     * Load a program from a file.
     */
    ProcessControlBlock loadProgram(String filename) {
        ProcessControlBlockImpl pcb;

        pcb.priority = 0;


        return 
    }

    /**
     * Obtain process ID.
     */
    int getPID() {
        return processID;
    }

    /**
     * Obtain program name.
     * 
     */
    String getProgramName() {
        return name;
    }
    
    /**
     * Obtain process priority();
     */
    int getPriority() {
        return priority;
    }
    
    /**
     * Set process priority(), returning the old value.
     */
    int setPriority(int value) {
        int tmp = priority;
        priority = value;
        return tmp;
    }
    
    /**
     * Obtain current program 'instruction'.
     */
    Instruction getInstruction() {
        return current;
    }
    
    /**
     * Determine if there are any more instructions.
     */
    boolean hasNextInstruction() {
        return pending.isEmpty();
    }
    
    /**
     * Advance to next instruction.
     */
    void nextInstruction() {
        current = pending.poll();
    }
    
    /**
     * Obtain process state.
     */
    State getState() {
        return State;
    }
    
    /**
     * Set process state.
     * Requires <code>getState()!=State.TERMINATED</code>.
     */
    void setState(State state) {
        State = state;
    }
    
    /**
     * Obtain a String representation of the PCB of the form '{pid(&lt;pid&gt;), state(&lt;state&gt;), name(&lt;program name&gt;)}'.
     */
    String toString() {
        return "{pid(<" + processID + ">), state(<" + State + ">), name(<" + name + ">)}";
    }

}
