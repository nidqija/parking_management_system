package View;

import Model.CalculatorFee;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ExitPanel extends JPanel {

    private JTextField plateSearchField;
    private JButton btnSearch;
    private JTextArea receiptArea;
    private JComboBox <String> paymentMethodComboBox;


    public ExitPanel(MainFrame mainFrame) {   
        


        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

// 1. Header

        JLabel label = new JLabel("Exit Panel - Car Exit Process");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);
        add(Box.createRigidArea(new Dimension(0, 20)));

 // 2. Input Section
        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel instructionLabel = new JLabel("License Plate:");
        plateSearchField = new JTextField(15);
        btnSearch = new JButton("Calculate Total");
        
        inputPanel.add(instructionLabel);
        inputPanel.add(plateSearchField);
        inputPanel.add(btnSearch);
        add(inputPanel);


        btnSearch.addActionListener(e -> handleCalculation());

// 3. Receipt Area

        receiptArea = new JTextArea(15, 50);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        receiptArea.setBorder(BorderFactory.createTitledBorder("Payment Receipt"));
        add(receiptArea);

// 3. Payment Method Selection

        JPanel paymentPanel = new JPanel(new FlowLayout());
        JLabel paymentLabel = new JLabel("Payment Method:");
        String[] paymentMethods = {"Cash", "Card", "E-Wallet"};
        paymentMethodComboBox = new JComboBox<>(paymentMethods);
        paymentPanel.add(paymentLabel);
        paymentPanel.add(paymentMethodComboBox);
        add(paymentPanel);

// 4. Process Payment Button

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnProcessPayment = new JButton("Process Payment & Exit");
        btnPanel.add(btnProcessPayment);
        add(btnPanel);




      

    }


      private void handleCalculation(){
            String plate = plateSearchField.getText().trim();
            if (plate.isEmpty()) {
                receiptArea.setText("Please enter a license plate number.");
                return;
            }
 
            receiptArea.setText("Calculating total fee for vehicle with plate: " + plate + "...\n\n");
            receiptArea.append("Total fee calculated successfully. Please select payment method and proceed.");
            CalculatorFee calculator = new CalculatorFee();
            String result = calculator.processExit(plate);
            receiptArea.append("\n\n" + result);



            
        }
}
