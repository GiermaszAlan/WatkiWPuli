package zad;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.Callable;

public class A implements Callable<String> {

    private int i ;
    private int limit;
    private int sum;
    private boolean done = false;
    private int temp=0;
    private Frame frame;
    private boolean mustWait = false;
    private boolean good = false;
    private boolean done2 = false;




    public A(int i, Frame frame){
        this.i = i;
        this.frame= frame;
        this.limit=i*1000;
    }


    public void setMustWait(){
        this.mustWait = true;
    }
    public void setContinue(){
        this.mustWait = false;
    }


    @Override
    public String call() {
        try {
            while (!done) {
                Random ran = new Random();
                temp = 1 + ran.nextInt(99);

                sum += temp;


                String odp = "Thread "+ i + "( limit = " + limit + ")"+ ": " + temp + ", sum = " + sum +"\n";

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        frame.addText(odp);
                        }
                });



                Thread.sleep(500 + (new Random().nextInt(500)));



                if(mustWait){
                    synchronized (this){
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (sum >= limit) {
                    done=true;
                    good=true;
                }



            }
        } catch (Exception e) {
           e.printStackTrace();
        }


        return "Thread "+ i + ": Done!\n";
    }

public void stopProces(){
        done = true;
        done2 = true;
}

    public int getI(){
        return i;
}
    public boolean getGood(){
        return good;
}
    public boolean getDone(){
        return done;
    }
    public boolean getDone2(){
        return done2;
    }


}
