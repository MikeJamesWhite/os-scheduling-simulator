import simulator.*;
import java.util.*;
import java.io.*;

/**
 * Concrete Implementation of Process Control Block used by kernel and simulator.
 * 
 * @author Michael White
 * @version 02/05/2018
 */
public class ProcessControlBlockImpl implements ProcessControlBlock {

    static int numProcesses = 0;

    private Queue<Instruction> pending;
    private Instruction current;
    private int processID;
    private int priority;
    private String name;
    private State state;

    ProcessControlBlockImpl () {
        numProcesses++;
        this.processID = numProcesses;
        this.state = State.READY;
    }

    /**
     * Load a program from a file.
     */
    static ProcessControlBlockImpl loadProgram(String filename, int priority) throws IOException, FileNotFoundException {
        ProcessControlBlockImpl pcb = new ProcessControlBlockImpl();
        pcb.priority = priority;
        pcb.pending = new ArrayDeque <Instruction>();
        pcb.name = filename;

        File f = new File(filename);
        Scanner s = new Scanner(f);
        while (s.hasNextLine()) {
            String line = s.nextLine();
            if (line.charAt(0) != '#') { // ignore comment lines
                String[] vals = line.split(" ");
                if (vals[0].equals("CPU")) {
                    CPUInstruction instr = new CPUInstruction(Integer.parseInt(vals[1]));
                    pcb.pending.offer(instr);
                }
                else {
                    IOInstruction instr = new IOInstruction(Integer.parseInt(vals[1]), Integer.parseInt(vals[2]));
                    pcb.pending.offer(instr);
                }
            }
        }

        return pcb;
    }

    /**
     * Obtain process ID.
     */
    public int getPID() {
        return processID;
    }

    /**
     * Obtain program name.
     * 
     */
    public String getProgramName() {
        return name;
    }
    
    /**
     * Obtain process priority();
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Set process priority(), returning the old value.
     */
    public int setPriority(int value) {
        int tmp = priority;
        priority = value;
        return tmp;
    }
    
    /**
     * Obtain current program 'instruction'.
     */
    public Instruction getInstruction() {
        return current;
    }
    
    /**
     * Determine if there are any more instructions.
     */
    public boolean hasNextInstruction() {
        return pending.isEmpty();
    }
    
    /**
     * Advance to next instruction.
     */
    public void nextInstruction() {
        current = pending.poll();
    }
    
    /**
     * Obtain process state.
     */
    public State getState() {
        return state;
    }
    
    /**
     * Set process state.
     * Requires <code>getState()!=State.TERMINATED</code>.
     */
    public void setState(State state) {
        if (getState() != State.TERMINATED)
            this.state = state;
    }
    
    /**
     * Obtain a String representation of the PCB of the form '{pid(&lt;pid&gt;), state(&lt;state&gt;), name(&lt;program name&gt;)}'.
     */
    public String toString() {
        return "{pid(<" + processID + ">), state(<" + this.state + ">), name(<" + name + ">)}";
    }

}
