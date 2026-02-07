package server;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server {
    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) {
        // Frame setup
        JFrame frame = new JFrame("Sugam's Server");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Sugam's File Receiver");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.add(title);
        frame.add(jScrollPane);
        frame.setVisible(true);

        // Server setup
        try (ServerSocket server = new ServerSocket(1234)) {
            int fieldID = 0;

            while (true) {
                Socket socket = server.accept();

                // Handle each client in a separate thread
                final int currentID = fieldID;
                new Thread(() -> handleClient(socket, jPanel, frame, currentID)).start();

                fieldID++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket, JPanel jPanel, JFrame frame, int fieldID) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
            // Read filename
            int fileNameLength = dis.readInt();
            if (fileNameLength <= 0) return;

            byte[] fileNameBytes = new byte[fileNameLength];
            dis.readFully(fileNameBytes);
            String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);

            // Read file content
            int fileContentLength = dis.readInt();
            if (fileContentLength <= 0) return;

            byte[] fileContentBytes = new byte[fileContentLength];
            dis.readFully(fileContentBytes);

            // Save file object
            MyFile myFile = new MyFile(fieldID, fileName, fileContentBytes, getFileExtension(fileName));
            myFiles.add(myFile);

            // Update UI on EDT
            SwingUtilities.invokeLater(() -> addFileToUI(myFile, jPanel, frame));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFileToUI(MyFile myFile, JPanel jPanel, JFrame frame) {
        JPanel jpFileRow = new JPanel();
        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));
        jpFileRow.setName(String.valueOf(myFile.getId()));
        jpFileRow.addMouseListener(getMouseListener());

        JLabel jFileName = new JLabel(myFile.getName());
        jFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jFileName.setBorder(new EmptyBorder(10, 0, 10, 0));

        jpFileRow.add(jFileName);
        jPanel.add(jpFileRow);
        frame.validate();
    }

    private static MouseListener getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel jPanel = (JPanel) e.getSource();
                int fileId = Integer.parseInt(jPanel.getName());

                for (MyFile myFile : myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame previewFrame = createPreviewFrame(myFile);
                        previewFrame.setVisible(true);
                        break;
                    }
                }
            }
        };
    }

    private static JFrame createPreviewFrame(MyFile myFile) {
        JFrame frame = new JFrame("Sugam's File Downloader");
        frame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Sugam's File Downloader");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        JLabel prompt = new JLabel("Are you sure you want to download: " + myFile.getName());
        prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        prompt.setFont(new Font("Arial", Font.BOLD, 20));
        prompt.setBorder(new EmptyBorder(20, 0, 10, 0));

        JButton yesButton = new JButton("Yes");
        yesButton.setPreferredSize(new Dimension(150, 75));
        yesButton.setFont(new Font("Arial", Font.BOLD, 20));

        JButton noButton = new JButton("No");
        noButton.setPreferredSize(new Dimension(150, 75));
        noButton.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel contentLabel = new JLabel();
        contentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        if (myFile.getFilExtencion().equalsIgnoreCase("txt")) {
            contentLabel.setText("<html>" + new String(myFile.getData(), StandardCharsets.UTF_8) + "</html>");
        } else {
            contentLabel.setIcon(new ImageIcon(myFile.getData()));
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        yesButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(myFile.getName()));
            int choice = chooser.showSaveDialog(frame);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File outFile = chooser.getSelectedFile();
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    fos.write(myFile.getData());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        noButton.addActionListener(e -> frame.dispose());

        panel.add(title);
        panel.add(prompt);
        panel.add(contentLabel);
        panel.add(buttonPanel);

        frame.add(panel);
        return frame;
    }

    private static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf(".");
        if (i > 0 && i < fileName.length() - 1) {
            return fileName.substring(i + 1);
        }
        return "";
    }
}
