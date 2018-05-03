import java.io.*;
import java.util.*;
import simulator.*;

public class FCFSSimulator {

    public static void main(String[] args) {
        System.out.println("*** FCFS Simulator ***");

        // get user input
        Scanner s = new Scanner(System.in);
        System.out.print("Enter configuration file name: ");
        String configFileName = s.nextLine();
        System.out.print("Enter cost of system call: ");
        int syscallCost = Integer.parseInt(s.nextLine());
        System.out.print("Enter cost of context switch: ");
        int dispatchCost = Integer.parseInt(s.nextLine());
        System.out.print("Enter trace level: ");
        int level = Integer.parseInt(s.nextLine());
        s.close();

        // run simulation
        TRACE.SET_TRACE_LEVEL(level);
        final Kernel kernel = new FCFSKernel();

        Config.init(kernel, dispatchCost, syscallCost);
        Config.buildConfiguration(configFileName);
        Config.run();

        // print results
        System.out.println("*** Results ***");        
        SystemTimer timer = Config.getSystemTimer();
        System.out.println(timer);
        System.out.println("Context switches:" + Config.getCPU().getContextSwitches());
        System.out.printf("CPU utilization: %.2f\n", ((double)timer.getUserTime())/timer.getSystemTime()*100);
    }
}