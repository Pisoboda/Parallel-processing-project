 import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
       long min_n=100;
       long max_n=1000000000;
       int min_theard=2;
       int max_theard=10;
       int Num_tasks=8;
       int num_runs=3;
       System.out.println("PLEASE enter 1 to access to parallel processes and 2 to access to sequential processes");

       Scanner  I_process =new Scanner(System.in);
       int num_to_choice;
        num_to_choice=I_process.nextInt();
        piExperementRunner runner= new piExperementRunner(min_n,max_n,min_theard,max_theard,Num_tasks,num_runs);
        switch(num_to_choice){
               case 1:
                   runner.runParallelOnly();
                   break;
               case 2:
                   runner.runSequentialOnly();
                   break;

                   default:System.out.println("invalid input ");

        }

    }
}


