package client.boundary.panel;

import client.boundary.ClientUI;
import client.boundary.component.ListPanel;
import client.boundary.component.MessagePanel;
import client.boundary.listener.IOnSend;
import java.awt.BorderLayout;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * ChatPanel
 *
 * @author Pratchaya Khansomboon
 * @version 1.0
 */
public class ChatPanel {
    private final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    private JPanel panel;

    private JButton fileButton, sendButton;
    private JTextField messageTextField;

    private JPanel recipientPanel;
    private JLabel recipientNames;

    private ListPanel<MessagePanel> listPanel;

    private ClientUI clientUI;

    public ChatPanel(ClientUI clientUI) {
        this.clientUI = clientUI;

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        recipientPanel = new JPanel();
        // recipientPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        recipientPanel.setLayout(new BorderLayout());

        recipientNames = new JLabel();
        recipientPanel.add(recipientNames, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        JPanel btnPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        messageTextField = new JTextField(); // accepts upto 10 characters
        messageTextField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        messageTextField.addCaretListener(e -> onTyping());

        fileButton = new JButton("File");
        sendButton = new JButton("Send");
        inputPanel.add(messageTextField,
                       BorderLayout.CENTER); // Components Added using Flow Layout
        btnPanel.add(fileButton);
        btnPanel.add(sendButton);
        inputPanel.add(btnPanel, BorderLayout.LINE_END);

        listPanel = new ListPanel<>();
        listPanel.setEnabled(false);

        panel.add(recipientPanel, BorderLayout.PAGE_START);
        panel.add(listPanel.getPane(), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.PAGE_END);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void setRecipient(String[] names) {
        String formattedName = "";
        for (String name : names) {
            formattedName += name + ", ";
        }
        formattedName = formattedName.substring(0, formattedName.length() - 2);
        recipientNames.setText(formattedName);
    }

    public void clearMessages() {
        listPanel.clear();
    }

    public void addMessage(MessagePanel messages) {
        listPanel.add(messages);
    }

    public void addSendTextListener(IOnSend<String> listener) {
        sendButton.addActionListener(e -> onSend(listener));
        messageTextField.addActionListener(e -> onSend(listener));
    }

    public void addSendFileListener(IOnSend<String> listener) {
        fileButton.addActionListener(e -> onFileButton(listener));
    }

    /**
     * File button pressed event
     */
    private void onFileButton(IOnSend<String> listener) {
        int isApproved = clientUI.showFileDialog("Choose an image");
        if (isApproved == JFileChooser.APPROVE_OPTION) {
            File imageFile = clientUI.getSelectedFile();
            listener.send(imageFile.getAbsolutePath());
        }
    }

    /**
     * Send event from button
     */
    private void onSend(IOnSend<String> listener) {
        var text = messageTextField.getText();
        if (!text.isBlank() && listener.send(text)) messageTextField.setText("");
    }

    /**
     * Typing event from the input field
     */
    private void onTyping() {
        System.out.print(DATE_FORMAT.format(LocalDateTime.now()));
        System.out.println(": Typing...");
    }
}