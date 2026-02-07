package client;

import java.awt.Component;	
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Cliente {
	public static void main(String[] args) {
		File[] fileToSend = new File[1];
		JFrame frame = new JFrame("Sugam's Cliente");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel title = new JLabel("Sugam's File sender");
		title.setFont(new Font("Arial", Font.BOLD, 25));
		title.setBorder(new EmptyBorder(20, 0, 10, 0));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel fileName = new JLabel("Choose a file to send");
		fileName.setFont(new Font("Arial", Font.BOLD, 20));
		fileName.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel jpbuttons = new JPanel();
		jpbuttons.setBorder(new EmptyBorder(75, 0, 10, 0));

		JButton jbSendFile = new JButton("Send FILE");
		jbSendFile.setPreferredSize(new Dimension(150, 75));
		jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));

		JButton jbChooseBtn = new JButton("Choose File");
		jbChooseBtn.setPreferredSize(new Dimension(150, 75));
		jbChooseBtn.setFont(new Font("Arial", Font.BOLD, 20));

		jpbuttons.add(jbChooseBtn);
		jpbuttons.add(jbSendFile);

		jbChooseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setDialogTitle("Choose a ile to send");
				if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					fileToSend[0] = jFileChooser.getSelectedFile();
					fileName.setText("The file we want to send is:  " + fileToSend[0].getName());

				}
			}
		});

		jbSendFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileToSend[0] == null) {
					fileName.setText("Please select a file first");
				} else {
					try {
						FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsoluteFile());
						Socket socket = new Socket("localhost", 1234);
						DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
						String fileName = fileToSend[0].getName();
						byte[] fileNameBytes = fileName.getBytes();
						byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
						fileInputStream.read(fileContentBytes);

						dataOutputStream.writeInt(fileNameBytes.length);
						dataOutputStream.write(fileNameBytes);

						dataOutputStream.writeInt(fileContentBytes.length);
						dataOutputStream.write(fileContentBytes);

					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		frame.add(title);
		frame.add(fileName);
		frame.add(jpbuttons);
		frame.setVisible(true);

	}
}
