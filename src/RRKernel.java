import simulator.Config;
import simulator.IODevice;
import simulator.InterruptHandler;
import simulator.Kernel;
import simulator.ProcessControlBlock;
//
import java.io.FileNotFoundException;
import java.io.IOException;
//
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Concrete Round Robin Kernel type
 * 
 * @author Mike James White
 * @version 03/05/2018
 */
public class RRKernel implements Kernel {
    
    private Deque<ProcessControlBlock> readyQueue;
    private int timeout;

    public RRKernel(int timeout) {
        // Set up the ready queue.
        readyQueue = new ArrayDeque<ProcessControlBlock>();
        this.timeout = timeout;
    }
    
    private ProcessControlBlock dispatch() {
        ProcessControlBlock toDispatch = readyQueue.poll();
		ProcessControlBlock toReturn = Config.getCPU().contextSwitch(toDispatch); // Perform context switch, swapping process currently on CPU with one at front of ready queue.
        if (toDispatch != null) {
            Config.getSystemTimer().scheduleInterrupt(timeout, this, toDispatch.getPID());
            toDispatch.setState(ProcessControlBlock.State.RUNNING);
        }

        return toReturn; // Returns process removed from CPU.
	}
                  
    public int syscall(int number, Object... varargs) {
        int result = 0;
        switch (number) {
             case MAKE_DEVICE:
                {
                    IODevice device = new IODevice((Integer)varargs[0], (String)varargs[1]);
                    Config.addDevice(device);
                }
                break;
             case EXECVE: 
                {
                    ProcessControlBlock pcb = loadProgram((String)varargs[0], (Integer) varargs[1]);
                    if (pcb!=null) {
                        // Loaded successfully.
						readyQueue.offer(pcb); // Now add to end of ready queue.
                        if (Config.getCPU().isIdle()) // If CPU is idle then dispatch().
                            dispatch();
                    }
                    else {
                        result = -1;
                    }
                }
                break;
             case IO_REQUEST: 
                {
					// IO request has come from process currently on the CPU.
                    ProcessControlBlock pcb = Config.getCPU().getCurrentProcess(); // Get PCB from CPU.
                    Config.getSystemTimer().cancelInterrupt(pcb.getPID());
					IODevice device = Config.getDevice((Integer)varargs[0]);// Find IODevice with given ID: Config.getDevice((Integer)varargs[0]);
					device.requestIO((Integer) varargs[1], pcb, this); // Make IO request on device providing burst time (varages[1]),
					// the PCB of the requesting process, and a reference to this kernel (so // that the IODevice can call interrupt() when the request is completed.
					//
					pcb.setState(ProcessControlBlock.State.WAITING); // Set the PCB state of the requesting process to WAITING.
					dispatch(); // Call dispatch().
                }
                break;
             case TERMINATE_PROCESS:
                {
					// Process on the CPU has terminated.
					ProcessControlBlock pcb = Config.getCPU().getCurrentProcess(); // Get PCB from CPU.
                    Config.getSystemTimer().cancelInterrupt(pcb.getPID());
                    pcb.setState(ProcessControlBlock.State.TERMINATED); // Set status to TERMINATED.
                    dispatch(); // Call dispatch().
                }
                break;
             default:
                result = -1;
        }
        return result;
    }

    public void interrupt(int interruptType, Object... varargs){
        switch (interruptType) {
            case TIME_OUT:
                ProcessControlBlock interupted = Config.getCPU().getCurrentProcess();
                readyQueue.offer(interupted);
                interupted.setState(ProcessControlBlock.State.READY);
                if (readyQueue.size() == 1)
                    interupted.setState(ProcessControlBlock.State.RUNNING);
                dispatch();

                break;
            case WAKE_UP:
				// IODevice has finished an IO request for a process.
				ProcessControlBlock pcb = (ProcessControlBlock) varargs[1]; // Retrieve the PCB of the process (varargs[1]), set its state
                pcb.setState(ProcessControlBlock.State.READY); // to READY, put it on the end of the ready queue.
                readyQueue.offer(pcb);
                if (Config.getCPU().isIdle()) // If CPU is idle then dispatch().
                    dispatch();
                break;
            default:
                throw new IllegalArgumentException("RRKernel:interrupt("+interruptType+"...): unknown type.");
        }
    }
    
    private static ProcessControlBlock loadProgram(String filename, int priority) {
        try {
            return ProcessControlBlockImpl.loadProgram(filename, priority);
        }
        catch (FileNotFoundException fileExp) {
            return null;
        }
        catch (IOException ioExp) {
            return null;
        }
    }
}
