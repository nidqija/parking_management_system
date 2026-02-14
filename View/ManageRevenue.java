package View;
import Controller.ParkingComplex;
import Model.CalculatorFee;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;


public class ManageRevenue extends JPanel {
    public ManageRevenue(MainFrame mainFrame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));



        JLabel label = new JLabel("Total Revenue Generated", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(label);

        add(Box.createRigidArea(new Dimension(0, 20)));

        ParkingComplex parkingComplex = new ParkingComplex();
        double totalRevenue = parkingComplex.getFormattedRevenue();
        JLabel revenueLabel = new JLabel(String.format("Total Revenue: $%.2f", totalRevenue), SwingConstants.CENTER);
        revenueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        revenueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(revenueLabel);

        add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel headerLabel = new JLabel("Revenue Breakdown by Paid Tickets", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 15));
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(headerLabel);

        String[] columnNames = {"Plate Number", "Entry Time", "Exit Time", "Amount Paid", "Payment Method"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable revenueTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(revenueTable);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        add(scrollPane);

        CalculatorFee calculator = new CalculatorFee();
        List <Object[]> paidTicketsData = calculator.getPaidTicket();
        for (Object[] rowData : paidTicketsData) {
            tableModel.addRow(rowData);
        }


        JButton backButton = new JButton("Back to Admin Panel");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setMaximumSize(new Dimension(200, 40));
        backButton.addActionListener(e -> mainFrame.showPage("AdminPanel"));
        add(backButton);
        add(Box.createRigidArea(new Dimension(0, 20)));



        


    }
}
