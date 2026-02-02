package View;

import java.awt.CardLayout;
import java.awt.Component;
import InterfaceLibrary.ParkingGroup;
import InterfaceLibrary.ParkingSpotInterface;
import javax.swing.*;
import InterfaceLibrary.Navigator;
import InterfaceLibrary.ParkingGroup;
import Controller.ParkingComplex;
import Controller.Floors;
import View.AdminPanel;
import View.ManageFloorPanel;
import View.ReportPanel;
import View.EntryPanel;
import View.ExitPanel;
import javax.swing.*;


public class MainFrame extends JFrame implements Navigator {
    
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private ParkingGroup pg = new ParkingGroup();

    public MainFrame() {
        
        setTitle("Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        Homepage homepage = new Homepage(this);
       
        
        mainPanel.add(homepage, "Homepage");
        add(mainPanel);


        setTitle("Parking Management System");
        setSize(800 , 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    @Override
    public void goTo(String pageName){
        showPage(pageName);
    }

    
    public void showPage(String pageName){

        if (pageName.equals("AdminPanel")) {
            AdminPanel adminPanel = new AdminPanel();
            mainPanel.add(adminPanel, "AdminPanel");
        }

        if (pageName.equals("AdminSignInPage")) {
            AdminSignInPanel adminSignInPanel = new AdminSignInPanel();
            mainPanel.add(adminSignInPanel, "AdminSignInPage");
        }

        if (pageName.equals("EntryPanel")) {
            EntryPanel entryPanel = new EntryPanel(pg);
            mainPanel.add(entryPanel, "EntryPanel");
        }

        if (pageName.equals("ExitPanel")) {
            ExitPanel exitPanel = new ExitPanel();
            mainPanel.add(exitPanel, "ExitPanel");
        }

        if(pageName.equals("ManageFloorPanel")) {
            ManageFloorPanel manageFloorPanel = new ManageFloorPanel(1);
            mainPanel.add(manageFloorPanel, "ManageFloorPanel");
        }

        if(pageName.equals("ManageFines")) {
            ManageFines manageFines = new ManageFines();
            mainPanel.add(manageFines, "ManageFines");
        }

        if(pageName.equals("ReportPanel")) {
            ReportPanel reportPanel = new ReportPanel();
            mainPanel.add(reportPanel, "ReportPanel");
        }



    cardLayout.show(mainPanel, pageName);


}}

