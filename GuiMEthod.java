import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import java.util.Map;


public class GuiMethod extends JFrame implements ActionListener {

    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JButton addItemButton;
    private JButton editItemButton;
    private JButton saveButton;
    private JButton deleteItemButton;
    private int selectedRow = -1;
    private int popularityCount = 0;
    private JDialog editDialog;
    

    private String[] bookText = {
            "The city zoning code requires a landscaping screen along the west property line, as shown on the attached site illustration \n" +
                    "sheet. The former design does not call for a screen in this area. The screen will act as a natural barrier between the church \n" +
                    "parking lot and the private residence adjoining the church property. The code requires that the trees for this screen be a mini\n" +
                    "mum height of 8 feet with a height maturity level of at least 20 feet. The trees should be an aesthetically pleasing barrier for all \n" +
                    "parties, including the resident on the adjoining property. \n" +
                    "Church Sign \n" +
                    "After the site was incorporated into the city, the Department of Transportation decided to widen Woodstock Road and in\n" +
                    "crease the setback to 50 feet, as illustrated on our site plan. With this change, the original location of the sign falls in the road \n" +
                    "setback. Its new location must be out of the setback and closer to the new church building. \n" +
                    "Detention Pond \n" +
                    "The city’s civil engineers reviewed the original site drawing and found that the detention pond is too small. If the size of the \n" +
                    "detention pond is not increased, rainwater may build up and overflow into the building, causing a considerable amount of \n" +
                    "flood damage to property in the building and to the building itself. There is a sufficient amount of land in the rear of the site to \n" +
                    "enlarge and deepen the pond to handle all expected rainfall. \n" +
                    "Emergency Vehicle Access \n" +
                    "On the original site plan, the slope of the ground along the back of the new building is so steep that an ambulance or city fire \n" +
                    "truck will not be able to gain access to the rear of the building in the event of a fire. This area is shown on our site illustration \n" +
                    "around the north and east sides of the building. The zoning office enforces a code that is required by the fire marshal’s office. \n" +
                    "This code states that all buildings within the city limits must provide a flat and unobstructed access path around the buildings. \n" +
                    "If the access is not provided, the safety of the church building and its members would be in jeopardy. ",
    
    };

    public GuiMethod() {
    
        setTitle("Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] columnNames = {"Title", "Author", "Publication Year", "Popularity Count"};
        tableModel = new DefaultTableModel(columnNames, 0);
        itemTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addItemButtonClicked();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = itemTable.getSelectedRow();
                System.out.println("selected row = "+selectedRow);
            }
        });

        editItemButton = new JButton("Edit Item");
        editItemButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 editSelectedItem();
             }
        });
        

        JButton deleteItemButton = new JButton("Delete Book");
        deleteItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItemButtonClicked();
            }
        });

        JButton readButton = new JButton("Read");

        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increasePopularityCount();
                readSelectedItem();
                
            }
        });

        JButton popularityButton = new JButton("View Popularity");
        popularityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                
                showPopularityChart();
            }
        });

        itemTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                int row = itemTable.rowAtPoint(point);

                
                for (int i = 0; i < itemTable.getRowCount(); i++) {
                    if (i == row) {
                        itemTable.setSelectionBackground(Color.LIGHT_GRAY);
                    } else {
                        itemTable.setSelectionBackground(Color.yellow);
                    }
                }
            }
        });
        itemTable.addMouseMotionListener(new MouseAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                int row = itemTable.rowAtPoint(e.getPoint());
                if (row >= 0)
                {
                    itemTable.getSelectionModel().setSelectionInterval(row, row);
                    itemTable.setSelectionBackground(Color.ORANGE);

                }
                else
                {
                    itemTable.getSelectionModel().clearSelection();
                }
            }
        });


        editDialog = new JDialog(this, "Edit Item");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addItemButton);
        
        buttonPanel.add(editItemButton);
        buttonPanel.add(deleteItemButton);
        buttonPanel.add(readButton);
        buttonPanel.add(popularityButton);
        add(buttonPanel, BorderLayout.SOUTH);
        readDataFromFile("C:\\Academics\\Semester 5\\Semester #5\\SCD-Ass\\SCD-Ass-3\\SCD-Ass-3\\src\\date.txt");
    }

    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();


        Map<String, Integer> popularityMap = new HashMap<>();


        for (int row = 0; row < tableModel.getRowCount(); row++) {
            String title = (String) tableModel.getValueAt(row, 0);
            int popularity = Integer.parseInt((String) tableModel.getValueAt(row, 3));
            dataset.setValue(title, popularity);
            popularityMap.put(title, popularity);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Popularity Count",  
                dataset,
                true,  
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionOutlinesVisible(false);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1}"));

        return chart;
    }

    private void showPopularityChart() {
        JFreeChart pieChart = createPieChart();
        ChartPanel chartPanel = new ChartPanel(pieChart);

        JFrame chartFrame = new JFrame("Popularity Chart");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.getContentPane().add(chartPanel);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    private void readSelectedItem(){
        if(selectedRow == -1){
           
            return;
        }

//        int choice = JOptionPane.showConfirmDialog(this, "Do you want to keep reading?", "Keep Reading", JOptionPane.YES_NO_OPTION);
//        if (choice == JOptionPane.NO_OPTION) {

//            selectedRow = -1;
//        }
        String title = (String) tableModel.getValueAt(selectedRow, 0);

        
        JFrame frame = new JFrame("Read Book: " + title);
        frame.setSize(600, 400);
        frame.setFont(new Font("Arial", Font.PLAIN, 30));

        
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setCaretPosition(0); 

        
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane);


        JLabel label = new JLabel("book name "+title);


        textArea.append( "The Title : "+ title +"\n\n");
        label.setFont(new Font("Arial", Font.PLAIN, 24));
        for (String paragraph : bookText) {
            textArea.append("\n"+ paragraph );
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int choice = JOptionPane.showConfirmDialog(frame, "Do you want to keep reading?", "Keep Reading", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.NO_OPTION) {
           
                    selectedRow = -1;
                    frame.dispose(); 
                }
            }
        });

        frame.setVisible(true);

    }


    private void increasePopularityCount() {
        if (selectedRow == -1) {
           
            return;
        }

        int currentPopularity = Integer.parseInt((String) tableModel.getValueAt(selectedRow, 3));
        currentPopularity++; 
        tableModel.setValueAt(String.valueOf(currentPopularity), selectedRow, 3);

        
        updateDataInFile("C:\\Academics\\Semester 5\\Semester #5\\SCD-Ass\\SCD-Ass-3\\SCD-Ass-3\\src\\date.txt");
    }
    private void editSelectedItem() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to edit.");
            return;
        }

        
        String title = (String) tableModel.getValueAt(selectedRow, 0);
        String authorName = (String) tableModel.getValueAt(selectedRow, 1);
        String publicationYear = (String) tableModel.getValueAt(selectedRow, 2);


        
        JPanel editPanel = new JPanel(new GridLayout(4, 2));
        JTextField titleField = new JTextField(title);
        JTextField authorField = new JTextField(authorName);
        JTextField yearField = new JTextField(publicationYear);
        JButton doneButton = new JButton("Done");
        JButton cancelButton = new JButton("Cancel");

        editPanel.add(new JLabel("Title:"));
        editPanel.add(titleField);
        editPanel.add(new JLabel("Author:"));
        editPanel.add(authorField);
        editPanel.add(new JLabel("Publication Year:"));
        editPanel.add(yearField);
        editPanel.add(new JLabel(""));
        editPanel.add(doneButton);
        

//        cancelButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                selectedRow = -1; // Reset the selected row
//                editDialog.dispose();
//            }
//        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
        
                tableModel.setValueAt(titleField.getText(), selectedRow, 0);
                tableModel.setValueAt(authorField.getText(), selectedRow, 1);
                tableModel.setValueAt(yearField.getText(), selectedRow, 2);

        
                updateDataInFile("C:\\Academics\\Semester 5\\Semester #5\\SCD-Ass\\SCD-Ass-3\\SCD-Ass-3\\src\\date.txt");

                selectedRow = -1; 
                editDialog.dispose();
            }
        });
        JDialog editDialog = new JDialog(this, "Edit Item");
        editDialog.add(editPanel);
        editDialog.pack();
        
        editDialog.setLocationRelativeTo(null);
        editDialog.setVisible(true);
    }

    private void updateDataInFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String title = (String) tableModel.getValueAt(row, 0);
                String authorName = (String) tableModel.getValueAt(row, 1);
                String publicationYear = (String) tableModel.getValueAt(row, 2);
                String popularity = (String) tableModel.getValueAt(row, 3);

                writer.write(title + "," + authorName + "," + publicationYear+ "," + popularity);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }
    
    private void addItemButtonClicked() throws IOException {
        String title = JOptionPane.showInputDialog("Enter Title:");
        String authorName = JOptionPane.showInputDialog("Enter Author Name:");
        String publicationYear = JOptionPane.showInputDialog("Enter Publication Year:");
        int popularity=0;
        if (title != null && authorName != null && publicationYear != null) {
            Vector<String> row = new Vector<>();
            row.add(title);
            row.add(authorName);
            row.add(publicationYear);
            row.add(String.valueOf(popularity));
            tableModel.addRow(row);

    
            saveDataToFile("C:\\Academics\\Semester 5\\Semester #5\\SCD-Ass\\SCD-Ass-3\\SCD-Ass-3\\src\\date.txt", title, authorName, publicationYear,popularity);
        }
    }

    private void deleteItemButtonClicked() {
        String itemName = JOptionPane.showInputDialog("Enter the name of the item to delete:");
        if (itemName != null) {
            deleteItemByName(itemName);
        }
    }

    private void deleteItemByName(String itemName) {
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            String title = (String) model.getValueAt(row, 0);
            if (title != null && title.equalsIgnoreCase(itemName)) {
                model.removeRow(row);

    
                saveDataToFile("C:\\Academics\\Semester 5\\Semester #5\\SCD-Ass\\SCD-Ass-3\\SCD-Ass-3\\src\\date.txt");

                break; 
            }
        }
    }



    
    
    private void saveDataToFile(String fileName, String title, String authorName, String publicationYear,int popularity) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(title + "," + authorName + "," + publicationYear+","+popularity);
            writer.newLine();
        }
    }


    private void saveDataToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String title = (String) tableModel.getValueAt(row, 0);
                String authorName = (String) tableModel.getValueAt(row, 1);
                String publicationYear = (String) tableModel.getValueAt(row, 2);

                writer.write(title + "," + authorName + "," + publicationYear);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }

    private void readDataFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String title = parts[0].trim();
                    String authorName = parts[1].trim();
                    String publicationYear = parts[2].trim();
                    String popularity = parts[3].trim();
                    tableModel.addRow(new Object[]{title, authorName, publicationYear,popularity});
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from the file: " + e.getMessage());
        }
    }


    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addItemButton) {
    
        }
    
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GuiMethod libraryGUI = new GuiMethod();
            libraryGUI.setVisible(true);
        });
    }
}
