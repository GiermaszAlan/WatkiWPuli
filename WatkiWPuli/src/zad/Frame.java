package zad;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public class Frame extends JFrame {

    JTextArea textArea = new JTextArea();



    public Frame(){
        int w = Toolkit.getDefaultToolkit().getScreenSize().width;
        int h = Toolkit.getDefaultToolkit().getScreenSize().height;


        this.setLocation((w-480)/2,(h-500)/2);
        Dimension frameSize = new Dimension(480,500);
        this.setPreferredSize(frameSize);
        this.setMaximumSize(frameSize);
        this.setMinimumSize(frameSize);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("Thread pool");

        JButton stopB = new JButton("Stop");
        JButton createB = new JButton("Create new");

        textArea.setFont(new Font("Serif", Font.ITALIC, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);






        Dimension buttonSize = new Dimension(470,50);

        textArea.setSize(470,340);
        JScrollPane js = new JScrollPane(textArea);
        js.setSize(470,340);
        stopB.setPreferredSize(buttonSize);
        stopB.setMinimumSize(buttonSize);
        stopB.setMaximumSize(buttonSize);

        createB.setPreferredSize(buttonSize);
        createB.setMinimumSize(buttonSize);
        createB.setMaximumSize(buttonSize);

        Container c = this.getContentPane();


        JPanel p1 = new JPanel();
        JPanel p2 = new JPanel();

        p1.add(stopB);
        p1.add(createB);
        p1.setLayout(new BoxLayout(p1, BoxLayout.PAGE_AXIS));

        p2.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        p1.setPreferredSize(new Dimension(470,95));
        p2.setPreferredSize(new Dimension(470,55));
        p1.setMaximumSize(new Dimension(470,95));
        p1.setMinimumSize(new Dimension(470,95));
        p2.setMinimumSize(new Dimension(480,100));
        textArea.setEditable(false);

        c.add(p1,BorderLayout.PAGE_START);
        c.add(js,BorderLayout.CENTER);


        c.add(p2,BorderLayout.PAGE_END);





        Map<JButton,A> map = new HashMap<>();

        ExecutorService executor = Executors.newCachedThreadPool();


        //działania przycisku "create new"
        createB.addActionListener(new ActionListener() {
            int i=0;




            @Override
            public void actionPerformed(ActionEvent e) {

                i++;

                JButton b = new JButton("T" + i);
                p2.add(b);
                p2.revalidate();
                p2.repaint();
                A bTh = new A(i, Frame.this);
                map.put(b, bTh);



//anulowanie wątku
b.addKeyListener(new KeyListener(){
    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyChar() == 'c' || evt.getKeyChar() == 'C') {
            map.get(b).stopProces();
            addText("Thread " + map.get(b).getI() + ": Canclled!");
            b.setText("T" + map.get(b).getI() + ": Canclled!");
            b.setEnabled(false);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
});

                //działania przycisków odpowiedzialnych za wątki
                b.addActionListener(new ActionListener() {





                    boolean click = false;
                    boolean con = true;

                    @Override
                    public void actionPerformed(ActionEvent e) {



                        if (!click) {
                            click = true;
                            b.setText("Suspend T"+map.get(b).getI());
                            FutureTask <String> futureTask = new FutureTask<>(map.get(b));
                            executor.submit(futureTask);
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    while(true){
                                        try{
                                            //poprawnie zakonczenie wątku
                                            if(futureTask.isDone() && map.get(b).getGood()){
                                                textArea.append(map.get(b).call().toString());
                                                break;
                                            }
                                            //zatrzymanie przez przycisk "stop"
                                            if((!futureTask.isDone() && map.get(b).getDone())){
                                                break;
                                            }
                                            //wątek wciąż żyje
                                            if(!futureTask.isDone() && !map.get(b).getGood()){
                                                Thread.sleep(100);
                                            }

                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                    }

                                    //usunięcie przycisku po poprawnym zakończeniu
                                    if(!map.get(b).getDone2()) {
                                        b.setText("T" + map.get(b).getI() + " done!");
                                        b.setEnabled(false);
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException interruptedException) {
                                            interruptedException.printStackTrace();
                                        }
                                        p2.remove(b);
                                        p2.revalidate();
                                        p2.repaint();
                                    }
                                }

                            });
                            t.start();



                        }else if(con){
                            //zatrzymanie działania wątku
                            b.setText("Continue T" + map.get(b).getI());
                            con=false;
                            map.get(b).setMustWait();
                        }else{
                            //wznowienie działania wątku
                            b.setText("Suspend T"+map.get(b).getI());
                            con=true;
                            synchronized (map.get(b)) {
                                map.get(b).notify();
                            }
                            map.get(b).setContinue();
                        }



                    }


                });

            }});



        //działania przycisku "stop"
        stopB.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                for (Map.Entry<JButton, A> entry  : map.entrySet()) {
                    if(!entry.getKey().getText().equals("T" + entry.getValue().getI() + ": Canclled!")) {
                        entry.getKey().setText("T" + entry.getValue().getI() + " done!");
                        entry.getKey().setEnabled(false);
                        entry.getValue().stopProces();
                    }
                }

            }
        });


    }

    public void addText(String s){
        textArea.append(s);
    }





}









