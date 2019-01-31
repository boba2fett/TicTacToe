import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Math;
import javax.swing.*;

public class TicGuiTest extends JFrame {

    public static void main(String[] args) {
        new TicGuiTest();        //main method and instantiating tic tac object and calling initialize function
    }

    private JFrame frame = new JFrame("TicTacToe");
    private JButton[] buttons = new JButton[9];
    private JButton player0 = new JButton("0 Spieler");
    private JButton player1 = new JButton("1 Spieler");
    private JButton player2 = new JButton("2 Spieler");//buttons for decisions
    private JButton diff1 = new JButton("Normal");
    private JButton diff2 = new JButton("Schwer");
    private JButton diff3 = new JButton("Unbesiegbar");//buttons for decisions
    private JButton nameP1 = new JButton("Spieler 1 Name:");
    private JButton nameP2 = new JButton("Spieler 2 Name:");
    private JButton server = new JButton("Server");
    private JButton computer = new JButton("Computer");
    private JButton selectHost = new JButton("Client");

    private JButton again = new JButton("Nochmal");
    private JButton reset = new JButton("Reset");

    private JTextField tf = new JTextField();//output for text

    private JPanel mainPanel = new JPanel(new BorderLayout());//create main panel container to put layer others on top
    private JPanel menu = new JPanel(new GridLayout(1, 3));//panel for buttons for decisions
    private JPanel game = new JPanel(new GridLayout(3, 3));//Create two more panels with layouts for buttons
    private JPanel jout = new JPanel(new GridLayout());
    private JLabel jl = new JLabel("");

    private int[][] arr;
    private int turns;
    private String[] player;
    //private String[] charOf = {"X", "O"};
    private int winner;
    private int playerCount;
    private int difficult;
    private boolean network = false;
    private boolean ready = false;
    private String host;
    private SocketController scon;
    private int playerNet;
    private int versus;

    private TicGuiTest() {
        super();
        frame.setSize(350, 450);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);        //Setting dimension of JFrame and setting parameters
        frame.setVisible(true);
        frame.setResizable(false);
        player = new String[2];  //player names
        arr = new int[3][3]; //3x3 Array for main field
        winner = -1;//-1 nobody has won
        for (int y = 0; y < 3; y++)  //fill arr with -1 for unused
        {
            for (int x = 0; x < 3; x++) {
                arr[x][y] = -1;
            }
        }
        turns = 0;
        initialize();//for buttons and so on
    }

    private void initialize()             //Initialize tic tac toe gui
    {
        frame.add(mainPanel);                                         //add main container panel to frame

        mainPanel.setPreferredSize(new Dimension(325, 425));
        menu.setPreferredSize(new Dimension(300, 50));                     //Setting dimensions of panels
        game.setPreferredSize(new Dimension(300, 300));
        jout.setPreferredSize(new Dimension(300, 50));
        mainPanel.add(menu, BorderLayout.NORTH);                   //Add three panels to the main container panel
        mainPanel.add(game, BorderLayout.SOUTH);
        mainPanel.add(jout, BorderLayout.CENTER);

        jout.add(jl);//output for text in own panel

        menu.add(player0);
        menu.add(player1);                //Add buttons to menu
        menu.add(player2);

        player0.addActionListener(new myActionListener());//playerCount
        player0.setToolTipText("Lasse den Computer gegen sich selber spielen");
        player1.addActionListener(new myActionListener());
        player1.setToolTipText("Spiele gegen den Computer oder im Netzwerk");
        player2.addActionListener(new myActionListener());
        player2.setToolTipText("Spiele mit einem Freund zusammen");

        nameP1.addActionListener(new myActionListener());//names
        nameP1.setToolTipText("Gib rechts von hier den Namen ein");
        nameP2.addActionListener(new myActionListener());
        nameP2.setToolTipText("Gib rechts von hier den Namen ein");

        server.addActionListener(new myActionListener());//type of game with 1 player
        server.setToolTipText("Agiere als Server");
        computer.addActionListener(new myActionListener());
        computer.setToolTipText("Spiele lokal gegen den Computer");
        selectHost.addActionListener(new myActionListener());
        selectHost.setToolTipText("Agiere als Client (du benötigst hostname/ip des anderen, der Server auswählt)");

        diff1.addActionListener(new myActionListener());//difficult for computer
        diff1.setToolTipText("Wähle Schwierigkeit Normal");
        diff2.addActionListener(new myActionListener());
        diff2.setToolTipText("Wähle Schwierigkeit Schwer");
        diff3.addActionListener(new myActionListener());
        diff3.setToolTipText("Wähle Schwierigkeit Unbesiegbar");

        reset.addActionListener(new myActionListener());
        reset.setToolTipText("Setze die Einstellungen komplett zurück");
        again.addActionListener(new myActionListener());//only shown at the end of a game
        again.setToolTipText("Spiele Nochmal mit den selben Einstellungen");

        for (int i = 0; i < 9; i++)                      //Create grid
        {
            buttons[i] = new JButton();                //Creating buttons
            game.add(buttons[i]);
            buttons[i].setText(" ");//is better for layout however
            buttons[i].setText("");
            buttons[i].setVisible(true);
            buttons[i].setEnabled(true);
            buttons[i].addActionListener(new myActionListener());        //Adding response event to buttons
        }
    }

    private void resetMenu() {//clear menu to add new things
        menu.setVisible(false);
        menu = new JPanel(new GridLayout(1, 3));
        menu.setPreferredSize(new Dimension(300, 50));
        mainPanel.add(menu, BorderLayout.NORTH);
    }

    private void choiceReady() {//called after all decisions about the game
        ready = true;//value to proof it
        menu.add(reset);
        for (int i = 0; i < 9; i++) {//enable all buttons and label them with the numbers 1-9
            buttons[i].setText(i + 1 + "");
            buttons[i].setEnabled(true);
        }
        if (playerCount == 2) {
            jl.setText(player[turns % 2] + "  ist an der Reihe");//only need to mention name for two players
        }
    }

    private void reset() {//total reset for all game settings
        if (network) {
            scon.close();//important because otherwise there cant be a network game anymore
        }
        jl.setText("");
        player[0] = null;
        player[1] = null;
        turns = 0;
        winner = -1;
        network = false;
        ready = false;
        host = null;
        for (int y = 0; y < 3; y++)  //fill arr with -1 for unused
        {
            for (int x = 0; x < 3; x++) {
                arr[x][y] = -1;
            }
        }
        resetMenu();
        menu.add(player0);
        menu.add(player1);
        menu.add(player2);

        game.setVisible(false);
        game = new JPanel(new GridLayout(3, 3));
        game.setPreferredSize(new Dimension(300, 300));
        mainPanel.add(game, BorderLayout.SOUTH);

        for (int i = 0; i < 9; i++)                      //Create grid
        {
            buttons[i] = new JButton();                //Creating buttons
            game.add(buttons[i]);
            buttons[i].setText(" ");//is better for layout however
            buttons[i].setText("");
            buttons[i].setVisible(true);
            buttons[i].setEnabled(true);
            buttons[i].addActionListener(new myActionListener());        //Adding response event to buttons
        }
    }


    private void again() {//next round with same settings
        if (network) {//verify the other one does want to play again too
            scon.send("ja");
            String otherIn = scon.rec();
            if (otherIn == null || !otherIn.equalsIgnoreCase("ja")) {//if not
                jl.setText("Gegner will nicht nocheinmal");
                resetMenu();
                menu.add(reset);
                return;
            }
        }
        jl.setText("");
        turns = 0;
        winner = -1;
        for (int y = 0; y < 3; y++)  //fill arr with -1 for unused
        {
            for (int x = 0; x < 3; x++) {
                arr[x][y] = -1;
            }
        }
        resetMenu();
        menu.add(reset);

        game.setVisible(false);
        game = new JPanel(new GridLayout(3, 3));
        game.setPreferredSize(new Dimension(300, 300));
        mainPanel.add(game, BorderLayout.SOUTH);

        for (int i = 0; i < 9; i++)                      //Create grid
        {
            buttons[i] = new JButton();                //Creating buttons
            game.add(buttons[i]);
            buttons[i].setText(i + 1 + "");
            buttons[i].setVisible(true);
            buttons[i].setEnabled(true);
            buttons[i].addActionListener(new myActionListener());        //Adding response event to buttons
        }

        if (playerCount == 0) {
            game(-1);
        }
        if (playerCount == 1 && network && playerNet == 1) {
            netOpponentTurn();
        }
        if (playerCount == 2) {
            jl.setText(player[turns % 2] + "  ist an der Reihe");
        }
    }


    private void game(int num) {//main method for TicTacToe logic called for every pressed numbered button
        if (playerCount == 0) {
            for (; turns < 9 && winner == -1; turns++)  // while main field is not filled and nobody has won
            {
                placeNewChar(ai());
                winner = won();
            }
            if (winner == -1) {
                jl.setText("Unentschieden");
            } else {
                jl.setText(player[winner] + " hat gewonnen");
            }
            resetMenu();
            menu.add(reset);
            menu.add(again);
            
        }

        if (playerCount == 2) {
            if (turns < 9 && winner == -1)  // while main field is not filled and nobody has won
            {
                placeNewChar((num));
                turns++;
                jl.setText(player[turns % 2] + "  ist an der Reihe");
                winner = won();
            }
            if (winner != -1) {
                jl.setText(player[winner] + " hat gewonnen");
                resetMenu();
                menu.add(reset);
                menu.add(again);
                
            } else {
                if (turns == 9) {
                    jl.setText("Unentschieden");
                    resetMenu();
                    menu.add(reset);
                    menu.add(again);
                    
                }
            }
        }

        if (playerCount == 1) {
            if (!network) {
                if (turns % 2 == 0) {
                    if (turns < 9 && winner == -1)  // while main field is not filled and nobody has won
                    {
                        placeNewChar((num));
                        turns++;
                        winner = won();
                        if (turns < 9 && winner == -1) {
                            placeNewChar(ai());
                            turns++;
                            winner = won();
                        }
                        if (winner != -1) {
                            jl.setText(player[winner] + " hat gewonnen");
                            resetMenu();
                            menu.add(reset);
                            menu.add(again);
                            
                        }
                    }
                    if (turns == 9) {
                        if (winner == -1) {
                            jl.setText("Unentschieden");
                            resetMenu();
                            menu.add(reset);
                            menu.add(again);
                            
                        } else {
                            jl.setText(player[winner] + " hat gewonnen");
                            resetMenu();
                            menu.add(reset);
                            menu.add(again);
                            
                        }
                    }
                }
            } else {
                if (turns % 2 == playerNet) {
                    if (turns < 9 && winner == -1)  // while main field is not filled and nobody has won
                    {
                        placeNewChar((num));
                        scon.send((num) + "");
                        turns++;
                        winner = won();
                        if (turns < 9 && winner == -1) {
                            netOpponentTurn();
                            winner = won();
                        }
                        if (winner != -1) {
                            jl.setText(player[winner] + " hat gewonnen");
                            resetMenu();
                            menu.add(reset);
                            menu.add(again);
                            
                        }
                    }

                    if (turns == 9) {
                        if (winner == -1) {
                            jl.setText("Unentschieden");
                            resetMenu();
                            menu.add(reset);
                            menu.add(again);
                            
                        } else {
                            jl.setText(player[winner] + " hat gewonnen");
                            resetMenu();
                            menu.add(reset);
                            menu.add(again);
                            
                        }
                    }
                }
            }
        }
    }

    private void netStart() {
        jl.setText("erstelle Netzwerkspiel...");
        scon = new SocketController(host);
        scon.send(player[playerNet]);
        player[versus] = scon.rec();
        if (player[versus] == null) {
            jl.setText("etwas ist schief gelaufen");
            resetMenu();
            menu.add(reset);
            for (int i = 0; i < 9; i++) {
                buttons[i].setText("");
                buttons[i].setEnabled(false);
            }
        } else {
            jl.setText("Du spielst gegen " + player[versus]);
            if (playerNet == 1) {
                netOpponentTurn();
            }
        }
    }

    private void netOpponentTurn() {//wait for other one to make his turn
        int num;
        String otherIn = scon.rec();
        if (otherIn == null) {
            jl.setText("etwas ist schief gelaufen");
            resetMenu();
            menu.add(reset);
            for (int i = 0; i < 9; i++) {
                buttons[i].setText("");
                buttons[i].setEnabled(false);
            }
        } else {
            try {
                num = Integer.parseInt(otherIn);//change input into number
            } catch (NumberFormatException nfe)//is no number
            {
                num = -1;//error code
            }
            if (placeNewChar(num) == -1) {
                for (int i = 1; i <= 9 && placeNewChar(i) == -1; i++)//choose next free (for text based compatibility)
                {
                    // nothing to do here
                }
            }
            turns++;
        }
    }

    private class myActionListener implements ActionListener {      //Implementing action listener for buttons
        public void actionPerformed(ActionEvent a) {
            if (ready) {
                for (int i = 0; i < 9; i++) {
                    if (a.getSource() == buttons[i]) {
                        game(i + 1);
                    }
                }
            }

            if (a.getSource() == reset) {
                reset();
            }

            if (a.getSource() == again) {
                again();
            }

            if (a.getSource() == player0) {
                playerCount = 0;
                difficult = 3;
                resetMenu();
                choiceReady();
                game(-1);
            }

            if (a.getSource() == player1) {
                playerCount = 1;
                resetMenu();
                menu.add(nameP1);
                nameP1.setText("Dein Name:");
                tf.setText(System.getProperty("user.name"));
                menu.add(tf);
            }

            if (a.getSource() == player2) {
                playerCount = 2;
                resetMenu();
                menu.add(nameP1);
                nameP1.setText("Spieler 1 Name:");
                tf.setText("");
                menu.add(tf);
            }


            if (a.getSource() == nameP1) {
                player[0] = tf.getText();
                resetMenu();
                if (playerCount == 2) {
                    menu.add(nameP2);
                    tf.setText("");
                    menu.add(tf);
                } else {
                    menu.add(computer);
                    menu.add(server);
                    menu.add(selectHost);
                }
            }

            if (a.getSource() == nameP2) {
                player[1] = tf.getText();
                resetMenu();
                choiceReady();
            }

            if (a.getSource() == computer) {
                network = false;
                player[1] = "computer";
                resetMenu();
                menu.add(diff1);
                menu.add(diff2);
                menu.add(diff3);
            }

            if (a.getSource() == diff1) {
                difficult = 1;
                resetMenu();
                choiceReady();
            }

            if (a.getSource() == diff2) {
                difficult = 2;
                resetMenu();
                choiceReady();
            }

            if (a.getSource() == diff3) {
                difficult = 3;
                resetMenu();
                choiceReady();
            }

            if (a.getSource() == server) {
                host = null;
                network = true;
                playerNet = 0;
                versus = 1;
                resetMenu();
                choiceReady();
                netStart();
            }

            if (a.getSource() == selectHost) {//used two times
                if (!network) {
                    network = true;
                    playerNet = 1;
                    versus = 0;
                    player[1] = player[0];
                    resetMenu();
                    selectHost.setText("Verbinden mit:");
                    menu.add(selectHost);
                    tf.setText("");
                    menu.add(tf);
                } else {
                    host = tf.getText();
                    selectHost.setText("Client");
                    resetMenu();
                    choiceReady();
                    netStart();
                }
            }

        }

    }

    private int coordsToNumber(int x, int y) {
        return x + 1 + y * 3;
    }

    private int placeNewChar(int num) {
        int y = (num - 1) / 3;
        int x = num - 1 - 3 * y;
        return placeNewChar(x, y);
    }

    private int placeNewChar(int x, int y) {
        try {

            if (arr[x][y] == -1) {
                arr[x][y] = turns % 2;
                int num = coordsToNumber(x, y);
                //buttons[num - 1].setText(charOf[turns % 2] + " " + (turns + 1));
                buttons[num - 1].setBackground(new Color(255*(turns%2),0,0,255));
                buttons[num - 1].setEnabled(false);
                return turns % 2;
            } else {
                //jl.setText("Feld ist belegt Neues Feld waehlen");//should not happen with gui
                return -1;
            }
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            return -1;//should also not happen with gui
        }
    }

    private int won() { //checks if somebody has won
        for (int playerNum = 0; playerNum < 2; playerNum++) {
            for (int y = 0; y < 3; y++)//test columns
            {
                if (arr[0][y] == arr[1][y] && arr[1][y] == arr[2][y] && arr[2][y] == playerNum) {
                    return playerNum;
                }
            }
            for (int x = 0; x < 3; x++)//test rows
            {
                if (arr[x][0] == arr[x][1] && arr[x][1] == arr[x][2] && arr[x][2] == playerNum) {
                    return playerNum;
                }
            }
            if (arr[0][0] == arr[1][1] && arr[1][1] == arr[2][2] && arr[2][2] == playerNum)//test Diagonal
            {
                return playerNum;
            }
            if (arr[0][2] == arr[1][1] && arr[1][1] == arr[2][0] && arr[2][0] == playerNum)//test Diagonal
            {
                return playerNum;
            }
        }
        return -1;
    }

    //------------------------------------------------------------------------------------------------------------AI

    private int ai() {//for computer based turns
        int playerNum = turns % 2;//should be 0 with 1 player   0 or 1 with 0 player
        int versus = (turns + 1) % 2;//should be 1 with 1 player   0 or 1 with 0 player

        if (arr[1][1] == -1 && playerNum == 1) {
            return coordsToNumber(1, 1);//1,1  or 5 is always a good idea for second
        }

        for (int count = 1; count >= 0; count--)//it is better to win in one turn than to defend
        {
            for (int y = 0; y < 3; y++)//test columns
            {
                if (arr[0][y] == arr[1][y] && arr[1][y] == playerNum && arr[2][y] == -1)//O|O|-
                {
                    return coordsToNumber(2, y);
                }
                if (arr[0][y] == arr[2][y] && arr[2][y] == playerNum && arr[1][y] == -1)//O|-|O
                {
                    return coordsToNumber(1, y);
                }
                if (arr[1][y] == arr[2][y] && arr[2][y] == playerNum && arr[0][y] == -1)//-|O|O
                {
                    return coordsToNumber(0, y);
                }
            }

            for (int x = 0; x < 3; x++)//test rows
            {
                if (arr[x][0] == arr[x][1] && arr[x][1] == playerNum && arr[x][2] == -1)//O|O|-
                {
                    return coordsToNumber(x, 2);
                }
                if (arr[x][0] == arr[x][2] && arr[x][2] == playerNum && arr[x][1] == -1)//O|-|O
                {
                    return coordsToNumber(x, 1);
                }
                if (arr[x][1] == arr[x][2] && arr[x][2] == playerNum && arr[x][0] == -1)//-|O|O
                {
                    return coordsToNumber(x, 0);
                }
            }

            if (arr[0][0] == arr[1][1] && arr[1][1] == playerNum && arr[2][2] == -1)//test Diagonal \
            {
                return coordsToNumber(2, 2);
            }
            if (arr[1][1] == arr[2][2] && arr[2][2] == playerNum && arr[0][0] == -1)//test Diagonal  \
            {
                return coordsToNumber(0, 0);
            }

            if (arr[0][2] == arr[1][1] && arr[1][1] == playerNum && arr[2][0] == -1)//test Diagonal  /
            {
                return coordsToNumber(2, 0);
            }
            if (arr[2][0] == arr[1][1] && arr[1][1] == playerNum && arr[0][2] == -1)//test Diagonal  /
            {
                return coordsToNumber(0, 2);
            }        //middle may not be checked because above 1,1 is set when unused

            playerNum = versus;//for next round to defend
        }

        int temp = 0;// for setting on 1,3,7,9 or on 2,4,6,8

        if (difficult > 1) {

            if (arr[1][0] == arr[0][1] && arr[0][1] == versus && arr[0][0] == -1)//corner up left
            {
                return coordsToNumber(0, 0);
            }
            if (arr[1][0] == arr[2][1] && arr[2][1] == versus && arr[2][0] == -1)//corner up right
            {
                return coordsToNumber(2, 0);
            }

            if (arr[0][1] == arr[1][2] && arr[1][2] == versus && arr[0][2] == -1)//corner down left
            {
                return coordsToNumber(0, 2);
            }
            if (arr[1][2] == arr[2][1] && arr[2][1] == versus && arr[2][2] == -1)//corner down right
            {
                return coordsToNumber(2, 2);
            }

            if (difficult == 3) {
                for (int i = 0; i <= 2; i += 2) {
                    for (int j = 0; j <= 2; j += 2) {
                        if (arr[1][j] == arr[i][2 - j] && arr[i][2 - j] == versus && arr[i][j] == -1)//against trap
                        {
                            return coordsToNumber(i, j);
                        }

                        if (arr[j][1] == arr[2 - j][i] && arr[2 - j][i] == versus && arr[j][i] == -1)//against trap
                        {
                            return coordsToNumber(j, i);
                        }
                    }
                }
            }

            temp = 0;
            if (arr[0][2] == arr[2][0] && arr[2][0] == versus)//against trap
            {
                temp = 2;
            }
            if (arr[0][0] == arr[2][2] && arr[2][2] == versus)//against trap
            {
                temp = 2;
            }

        }

        if (temp == 0) {
            for (int i = 1; i <= 9; i += 2) {
                if (checkNum(i) == -1)//setting on 1,3,7,9 is better but only if not used
                {
                    temp = 1;
                }
            }
        }


        if (temp == 2)//setting on 2,4,6,8 is because of trap better but only if not used
        {
            for (int i = 2; i <= 8; i += 2) {
                if (checkNum(i) == -1) {
                    //System.out.print(i);
                    temp = 0;
                }
            }
        }

        if (temp == 2)//2,4,6,8 are used so setting on 1,3,7,9
        {
            temp = 1;
        }
        int num = 0;
        while (checkNum(num) != -1) {
            num = ((int) (Math.random() * 5)) * 2 + temp;
        }


        return num;
    }

    //----------------------------------------------------------------------------------------------AI

    private int checkNum(int num) {
        if (num <= 9 && num > 0) {
            int y = (num - 1) / 3;
            int x = num - 1 - 3 * y;
            return arr[x][y];
        }
        return -2;//-1 can not be used here because -1 is arr code for unused
    }
}