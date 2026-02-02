package View;

import InterfaceLibrary.Navigator;
import InterfaceLibrary.ParkingGroup;
import java.awt.CardLayout;
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

    //show different floors func
    public void showManageFloor(int floorId) {
    ManageFloorPanel panel = new ManageFloorPanel(this, floorId);
    mainPanel.add(panel, "ManageFloorPanel");
    }

    
    public void showPage(String pageName){

        if (pageName.equals("Homepage")) {
            Homepage homepage = new Homepage(this);
            mainPanel.add(homepage, "Homepage");
        }
        

        if (pageName.equals("AdminPanel")) {
            AdminPanel adminPanel = new AdminPanel(this);
            mainPanel.add(adminPanel, "AdminPanel");
        }

        if (pageName.equals("AdminSignInPage")) {
            AdminSignInPanel adminSignInPanel = new AdminSignInPanel(this);
            mainPanel.add(adminSignInPanel, "AdminSignInPage");
        }

        if (pageName.equals("ManageFloorPanel")) {
            showManageFloor(1);
        }

        if (pageName.equals("EntryPanel")) {
            EntryPanel entryPanel = new EntryPanel(this,pg);
            mainPanel.add(entryPanel, "EntryPanel");
        }

        if (pageName.equals("ExitPanel")) {
            //ExitPanel exitPanel = new ExitPanel(this);
            //mainPanel.add(exitPanel, "ExitPanel");
        }


        if(pageName.equals("ManageFines")) {
            ManageFines manageFines = new ManageFines();
            mainPanel.add(manageFines, "ManageFines");
        }

        if(pageName.equals("ReportPanel")) {
            //ReportPanel reportPanel = new ReportPanel(this);
            //mainPanel.add(reportPanel, "ReportPanel");
        }



    cardLayout.show(mainPanel, pageName);


}}
