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
    private CalculatorFee currentCalculator;

  

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


        btnProcessPayment.addActionListener(e -> handlePayment());


        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            mainFrame.showPage("Homepage");
        });

        add(backButton);




      

    }


      private void handleCalculation(){
            String plate = plateSearchField.getText().trim();
            if (plate.isEmpty()) {
                receiptArea.setText("Please enter a license plate number.");
                return;
            }
 
            receiptArea.setText("Calculating total fee for vehicle with plate: " + plate + "...\n\n");
            receiptArea.append("Total fee calculated successfully. Please select payment method and proceed.");
            currentCalculator = new CalculatorFee();
            String result = currentCalculator.processExit(plate);
            receiptArea.append(result);
            double unpaidFines = currentCalculator.getPreviousFines(plate); 
    if (unpaidFines > 0) {
        receiptArea.append(String.format("\n!!! OUTSTANDING BALANCE FOUND !!!\n"));
        receiptArea.append(String.format("Historical Unpaid Fines: RM %.2f\n", unpaidFines));
        receiptArea.append(String.format("TOTAL TO PAY: RM %.2f\n", (currentCalculator.getTotalAmount() + unpaidFines)));
    }
            
        }


        private void handlePayment(){
            if (currentCalculator == null) {
                receiptArea.setText("Please calculate the total fee before processing payment.");
                return;
            }

            String plate = plateSearchField.getText().trim();
            String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
            double unpaidHistorical = currentCalculator.getPreviousFines(plate);
            double grandTotal = currentCalculator.getTotalAmount() + unpaidHistorical;

            boolean paymentSuccess = currentCalculator.processFinalPayment(plate, grandTotal, currentCalculator.getLastHours());

            if (paymentSuccess) {
                String receipt = currentCalculator.getFinalReceipt(plate, paymentMethod, 
                                                grandTotal, 
                                                currentCalculator.getFineAmount() , 
                                                currentCalculator.getBaseFee() , 
                                                currentCalculator.getLastHours() , 
                                                currentCalculator.getStartTime());
                receiptArea.setText("Payment successful!\n\n" + receipt);
            } else {
                receiptArea.setText("Payment failed. Please try again.");
            }

            
        }
}
