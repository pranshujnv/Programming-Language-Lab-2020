
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;
import java.util.Stack;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

//Class implementing CalculatorMultipleDigit GUI, ButtonHighlighter Threads and all
// necessary functionality

public class CalculatorMultipleDigit extends javax.swing.JFrame implements KeyListener {

    private static final int NUM_BUTTONS = 10;
    private static final int FUNC_BUTTONS = 7;
    private static final int FUNC_HIGHLIGHT = 1, NUM_HIGHLIGHT = 0 ;
    private static final int CHANGE_TIMEOUT = 1000;

    // State variables for correctly displaying background color of buttons, and recording the input
    private static Color bgColor;
    private String iofieldData = "";

    // Reference arrays holding the number keys and function keys on the calculator
    private JButton[] numButtons;
    private JButton[] funcButtons;

    // SwingWorker threads for handling highlighting
    private ButtonHighlighter numHighlighter, funcHighlighter;

    // Shared data variables for handling thread states
    private volatile Integer currChange = 0, funcChange = 0, stopPressed = 0, currMode = 0;


    // Main funvtion for the class
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CalculatorMultipleDigit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CalculatorMultipleDigit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CalculatorMultipleDigit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CalculatorMultipleDigit.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                CalculatorMultipleDigit newCalculatorMultipleDigit = new CalculatorMultipleDigit();
                newCalculatorMultipleDigit.setVisible(true);
            }
        });
    }

    
    /**
     * Creates new form CalculatorMultipleDigit
     */
    public CalculatorMultipleDigit() {
        // Initialization of GUI components
        initComponents();
        // Initialization of state variables
        bgColor = numButton0.getBackground();
        numHighlighter = new ButtonHighlighter(NUM_HIGHLIGHT);
        funcHighlighter = new ButtonHighlighter(FUNC_HIGHLIGHT);
        iofield.addKeyListener(this);

        // Setting of reference array keys
        // <editor-fold defaultstate="collapsed" desc="Reference array creation">                          
        numButtons = new JButton[NUM_BUTTONS];
        funcButtons = new JButton[FUNC_BUTTONS];
        numButtons[0] = numButton0;
        numButtons[1] = numButton1;
        numButtons[2] = numButton2;
        numButtons[3] = numButton3;
        numButtons[4] = numButton4;
        numButtons[5] = numButton5;
        numButtons[6] = numButton6;
        numButtons[7] = numButton7;
        numButtons[8] = numButton8;
        numButtons[9] = numButton9;
        

        funcButtons[0] = fndivideButton;
        funcButtons[1] = fnmultiplyButton;
        funcButtons[2] = fnminusButton;
        funcButtons[3] = fnplusButton;
        funcButtons[4] = fnansButton;
        funcButtons[5] = fnstopButton;
        funcButtons[6] = fnclearButton;

        (new Thread(numHighlighter)).start();
        (new Thread(funcHighlighter)).start();

        // </editor-fold>
    }



    /**
     * Function for evaluating the expression entered Throws an exception and
     * clears the calculator if an invalid expression is encountered
     */
    private void getResult() {
        try {
            // Result evaluation by converting the string to postfix form, and performing postfix evaluation
            String result = postfixEvaluate(toPostfix(iofieldData)).toString();

            // Setting display text and internal representation
            iofieldData = result;
            iofield.setText(result);
        } catch (Exception ex) {
            // Clear calculator in case of invalid expressions
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid expression detected! Clearing calculator");
            clearscr();
        }
    }

    /**
     * Function for converting input infix expression to postfix expression
     */
    private static String toPostfix(String infix) {
        // Local state variables for conversion
        Stack<String> operators = new Stack<String>();
        Scanner tokens = new Scanner(infix);
        String postfix = "", symbol = "";

        // Parsing each element in a token-wise manner
        while (tokens.hasNext()) {
            if (tokens.hasNextInt()) {
                postfix += " " + tokens.nextInt() + " ";
             
            } 
            else {
                symbol = tokens.next().trim();
                while (!operators.isEmpty()  && prec(symbol) <= prec(operators.peek())) {
                    postfix = postfix + " " + operators.pop() + " ";
                }
                operators.push(symbol);
                
            }
        }
        // Place all symbols on stack in the postfix expression
        while (!operators.isEmpty()) {
            postfix = postfix + " " + operators.pop() + " ";
        }
        // Remove excess space
        postfix = postfix.trim();
        //System.out.println("Postfix expression: " + postfix);

        return postfix;
    }

    /**
     * Utility function for defining the precedence of operators
     */
    private static int prec(String x) {
        if ("+".equals(x) || "-".equals(x)) {
            return 1;
        }
        if ("*".equals(x) || "/".equals(x)) {
            return 2;
        }
        return 0;
    }

    /**
     * Function for evaluating postfix expression
     */
    private Integer postfixEvaluate(String exp) {
        // State variables for evaluation
        Stack<Integer> operands = new Stack<Integer>();
        Scanner tokens = new Scanner(exp);

        // Scanning input expression tokenwise
        while (tokens.hasNext()) {
            // If next item is an integer, place on stack
            if (tokens.hasNextInt()) {
                operands.push(tokens.nextInt());
            } // Otherwise, evaluate the last 2 operands with the current operator
            else {
                int operand_2 = operands.pop();
                int operand_1 = operands.pop();
                String op = tokens.next();

                if (op.equals("+")) {
                    operands.push(operand_1 + operand_2);
                } else if (op.equals("-")) {
                    operands.push(operand_1 - operand_2);
                } else if (op.equals("*")) {
                    operands.push(operand_1 * operand_2);
                } else {
                    operands.push(operand_1 / operand_2);
                }
            }
        }

        // Return final value
        return operands.pop();
    }


    /* * Function for resetting the calculator to a fresh state
     */
    private void clearscr() {
        iofield.setText("");
        stopPressed = 0;
    }

    @Override
    /**
     * Function for handling and responding to key-press events
     */
    public void keyPressed(KeyEvent e) {
        //System.out.println("Pressed " + e.getKeyChar());
        // If enter has been pressed, then respond by including the highlighted number  in the expression
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
            iofieldData +=  numButtons[currChange].getText();
            iofield.setText(iofield.getText() + numButtons[currChange].getText());
        }
        else if(e.getKeyCode() == KeyEvent.VK_SPACE){
            if (funcButtons[funcChange] == fnansButton){
                getResult();
            } else if(funcButtons[funcChange] == fnclearButton){
                clearscr();
            }
            else if (funcButtons[funcChange] == fnstopButton) {
                synchronized (stopPressed) {
                    stopPressed = 1;
                }
            }else{
                iofieldData += " " + funcButtons[funcChange].getText() + " ";
                iofield.setText(iofield.getText() + funcButtons[funcChange].getText());
            }
        }

    }

    // Empty function to satisfy KeyListener interface requirements
    @Override
    public void keyTyped(KeyEvent e) {
    }

    // Empty function to satisfy KeyListener interface requirements
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        jFrame1 = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        iofield = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        numButton8 = new javax.swing.JButton();
        numButton7 = new javax.swing.JButton();
        fndivideButton = new javax.swing.JButton();
        numButton9 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        numButton4 = new javax.swing.JButton();
        numButton5 = new javax.swing.JButton();
        numButton6 = new javax.swing.JButton();
        fnmultiplyButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        numButton1 = new javax.swing.JButton();
        numButton2 = new javax.swing.JButton();
        numButton3 = new javax.swing.JButton();
        fnminusButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        numButton0 = new javax.swing.JButton();
        fnplusButton = new javax.swing.JButton();
        fnstopButton = new javax.swing.JButton();
        fnansButton = new javax.swing.JButton();
        fnclearButton = new javax.swing.JButton();
        iofield.setEditable(false);
        setResizable(false);


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("1. Press Enter to select digit of number");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel2.setText("2. Press SpaceBar to select function");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setText("3. C to clear or  Z to stop");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3))
        );

        iofield.setFont(new java.awt.Font("Ubuntu", 0, 24)); // NOI18N
        iofield.setText("");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iofield)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(iofield, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        numButton8.setText("8");
        numButton8.setFocusable(false);
        
        numButton7.setText("7");
        numButton7.setFocusable(false);

        fndivideButton.setText("/");
        fndivideButton.setFocusable(false);
        

        numButton9.setText("9");
        numButton9.setFocusable(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(numButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(numButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(numButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(fndivideButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(fndivideButton, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
            .addComponent(numButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(numButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(numButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        numButton4.setText("4");
        numButton4.setFocusable(false);

        numButton5.setText("5");
        numButton5.setFocusable(false);

        numButton6.setText("6");
        numButton6.setFocusable(false);

        fnmultiplyButton.setText("*");
        fnmultiplyButton.setFocusable(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(numButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(numButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(numButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(fnmultiplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(numButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(fnmultiplyButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(numButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(numButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        numButton1.setText("1");
        numButton1.setFocusable(false);

        numButton2.setText("2");
        numButton2.setFocusable(false);

        numButton3.setText("3");
        numButton3.setFocusable(false);

        fnminusButton.setText("-");
        fnminusButton.setFocusable(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(numButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(numButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(numButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(fnminusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(numButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(numButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fnminusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 1, Short.MAX_VALUE))
        );

        numButton0.setText("0");
        numButton0.setFocusable(false);

        fnplusButton.setText("+");
        fnplusButton.setFocusable(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(numButton0, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124)
                .addComponent(fnplusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(numButton0, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(fnplusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        fnstopButton.setText("Z");
        fnstopButton.setFocusable(false);
        

        fnansButton.setText("=");
        fnansButton.setFocusable(false);

        fnclearButton.setText("C");
        fnclearButton.setFocusable(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fnansButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fnstopButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fnclearButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fnansButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fnstopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fnclearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        
        pack();
    }// </editor-fold>  

    /**
     * SwingWorker subclass responsible for updating the GUI buttons by
     * informing the EDT
     */
    private class ButtonHighlighter extends SwingWorker<Void, Void> {

        // Variable MODE stores the nature of the highlighter: for numbers (0), or for functions (1)
        private final int MODE;
        // Timer variable for polling the EDT regularly
        private java.util.Timer changeTimer;

        /**
         * Constructor to create the SwingWorker object
         *
         * @param MODE Specifies whether to handle numbers (0), or functions (1)
         */
        public ButtonHighlighter(int MODE) {
            this.MODE = MODE;
        }

        @Override
        /**
         * Main SwingWorker method - starts the timer variable and defines the
         * function to be called when initiated
         */
        protected Void doInBackground() throws Exception {
            changeTimer = new java.util.Timer();
            // Depending on mode of object, define response of the timer
            if (this.MODE == 0) {
                changeTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    // TimerTask element executes the defined function in the current thread every CHANGE_TIMEOUT ms
                    public void run() {
                        try {
                            // Check to see if the user has stopped number selection
                            int sP = 0;
                            synchronized (stopPressed) {
                                sP = stopPressed;
                            }
                            if (sP == 0) {
                            // If not, execute the highlight event synchronously on the EDT
                                SwingUtilities.invokeAndWait(changeColourNum);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 100, CHANGE_TIMEOUT);
            } else {
                changeTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    // TimerTask element executes the defined function in the current thread every CHANGE_TIMEOUT ms
                    public void run() {
                        try {
                            // Execute the highlight event synchronously on the EDT
                            SwingUtilities.invokeAndWait(changeColourFunc);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 100, CHANGE_TIMEOUT);
            }
            return null;
        }

        // Methods run on the EDT (must be of type Runnable to be executed by invokeAndWait())
        // changeColourNum: Changes the currently highlighted number field if the current mode is 0 (number)
        private final Runnable changeColourNum = new Runnable() {
            @Override
            public void run() {
                numButtons[currChange].setBackground(bgColor);
                currChange = (++currChange) % NUM_BUTTONS;
                numButtons[currChange].setBackground(Color.blue);
            }
        };

        // changeColourFunc: Changes the currently highlighted function field if the current mode is 1 (function)
        private final Runnable changeColourFunc = new Runnable() {
            @Override
            public void run() {
                funcButtons[funcChange].setBackground(bgColor);
                funcChange = (++funcChange) % FUNC_BUTTONS;
                funcButtons[funcChange].setBackground(Color.yellow);
            }
        };
    }                      

    /**
     * /@param args the command line arguments
     */
    
    // Variables declaration - do not modify                     
    private javax.swing.JTextField iofield;
    private javax.swing.JButton fnclearButton;
    private javax.swing.JButton fnansButton;
    private javax.swing.JButton fndivideButton;
    private javax.swing.JButton fnminusButton;
    private javax.swing.JButton fnmultiplyButton;
    private javax.swing.JButton fnplusButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JButton numButton0;
    private javax.swing.JButton numButton1;
    private javax.swing.JButton numButton2;
    private javax.swing.JButton numButton3;
    private javax.swing.JButton numButton4;
    private javax.swing.JButton numButton5;
    private javax.swing.JButton numButton6;
    private javax.swing.JButton numButton7;
    private javax.swing.JButton numButton8;
    private javax.swing.JButton numButton9;
    private javax.swing.JButton fnstopButton;
    // End of variables declaration                   
}
