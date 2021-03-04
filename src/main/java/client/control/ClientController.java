package client.control;

import client.boundary.listener.IOnEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.ImageIcon;
import shared.entity.IMessage;
import shared.entity.Message;
import shared.entity.User;
import shared.entity.UserListMessage;

/**
 * ClientController controls and show the whole graphical interface and its logic.
 * It connects to  the server using {@link MessageWorker}. The incoming message and other events are
 * received by callbacks.
 *
 * @author  Pratchaya Khansomboon
 * @author  Eric Lundin
 * @version 1.0
 */
final public class ClientController {
    private MessageWorker messageWorker;

    // Remote server configurations
    private String serverAddress;
    private int serverPort;

    private User user;
    private List<User> friendList;
    private List<User> connectedUserList;
    private List<User> recipientList;
    private HashMap<User, List<Message>> userMessageMap;

    private User selectedUser;
    private List<Message> activeMessageList;
    private List<User> activeUserList;

    private IOnEvent onUpdate;
    private IOnEvent onConnectEvent;
    private IOnEvent onDisconnectEvent;

    /**
     * Default constructor for the controller. This sets the server address to "localhost" and its
     * port to 3000.
     */
    public ClientController() {
        this("localhost", 3000);
    }

    /**
     * Create controller with specified server address and port.
     *
     * @param address The server address to use.
     * @param port    The server port.
     */
    public ClientController(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }

    /**
     * Set the port that the server is listening on.
     *
     * @param port Server port
     */
    public void setServerPort(int port) {
        this.serverPort = port;
    }

    /**
     * Set the server address to connect to.
     *
     * @param address The server raw address
     */
    public void setServerAddress(String address) {
        this.serverAddress = address;
    }

    /**
     * Connect to the server
     */
    public void connect() {
        if (messageWorker == null) {
            messageWorker = new MessageWorker(serverAddress, serverPort);

            // Listen for incoming messages
            messageWorker.setOnMessage(this::onMessage);

            // Listen for connection status
            messageWorker.setOnFailedConnect(this::onFailedConnect);
            messageWorker.setOnConnect(this::onConnect);
            messageWorker.setOnDisconnect(this::onDisconnect);
            messageWorker.setOnFailToSent(this::onFailToSent);
        }

        messageWorker.connect();
    }

    /**
     * Disconnect from the server
     */
    public void disconnect() {
        if (messageWorker != null) messageWorker.disconnect();
    }

    /**
     * Get if the user is connected to the server
     *
     * @return {@code true} for connected, {@code false} for not connected
     */
    public boolean getIsConnected() {
        synchronized (this) {
            return messageWorker.getIsConnected();
        }
    }

    public void setOnUpdate(IOnEvent onUpdate) {
        this.onUpdate = onUpdate;
    }

    public void setOnConnect(IOnEvent onConnect) {
        this.onConnectEvent = onConnect;
    }

    public void setOnDisconnect(IOnEvent onDisconnect) {
        this.onDisconnectEvent = onDisconnect;
    }

    /**
     * Set User object for logging in.
     *
     * @param user User object reference.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Get the user object.
     *
     * @return User object that's used for logging in.
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Add connected user in the list to contact list
     *
     * @param index Selected index in the user list
     */
    public void addFriend(int index) {
        if (friendList == null) friendList = new ArrayList<>();

        boolean isDuplicate = false;
        int size = friendList.size();
        for (int i = 0; i < size; i++) {
            if (friendList.get(i) == activeUserList.get(index)) {
                isDuplicate = true;
                break;
            }
        }

        if (!isDuplicate) {
            friendList.add(activeUserList.get(index));
        }
    }

    /**
     * Show connected user list
     */
    public void showOnlineList() {
        activeUserList = connectedUserList;
    }

    /**
     * Show friend list
     */
    public void showFriendList() {
        activeUserList = friendList;
    }

    /**
     * Get active user list
     *
     * @return List of users.
     */
    public List<User> getUsers() {
        return activeUserList;
    }

    /**
     * Send the text message.
     *
     * @param msg Text message to be sent
     */
    public void sendTextMessage(String msg) {
        var message = new Message();
        message.setText(msg);
        message.setSender(user);
        sendMessage(message);
    }

    /**
     * Load and send the image.
     *
     * @param filename file path to load
     */
    public void sendImageMessage(String filename) {
        ImageIcon imageIcon = new ImageIcon(filename);
        var message = new Message();
        message.setImage(imageIcon);
        message.setSender(user);
        sendMessage(message);
    }

    public void setRecipient(int index) {
        if (recipientList == null) recipientList = new ArrayList<>();
        if (index < 0) return;
        recipientList.clear();
        recipientList.add(connectedUserList.get(index));
    }

    /**
     * Add selected user into the recipient list.
     *
     * @param index The index in the connected user list.
     */
    public void addRecipient(int index) {
        if (recipientList == null) recipientList = new ArrayList<>();
        if (index < 0) return; // Return if the list is not selected.

        boolean isDuplicate = false;
        int size = recipientList.size();

        // Linearly search for the the correct object in the recipient list.
        for (int i = 0; i < size; i++) {
            if (recipientList.get(i) == connectedUserList.get(index)) {
                isDuplicate = true;
                break;
            }
        }

        if (isDuplicate) return;
        recipientList.add(connectedUserList.get(index));

        if (onUpdate != null) onUpdate.signal();
    }

    /**
     * Remove recipient from the list.
     *
     * @param index The index in the connected user list.
     */
    public void removeRecipient(int index) {
        if (recipientList == null) return;

        int size = recipientList.size();
        int listIndex = -1;
        // Linearly search for the the correct object in the recipient list.
        for (int i = 0; i < size; i++) {
            if (recipientList.get(i) == connectedUserList.get(index)) {
                listIndex = i;
                break;
            }
        }

        if (listIndex > -1) recipientList.remove(listIndex);

        if (onUpdate != null) onUpdate.signal();
    }

    /**
     * Show message from the selected user.
     *
     * @param index The index of the selected user in the user list.
     */
    public List<Message> getMessages(int index) {
        selectedUser = activeUserList.get(index);
        return userMessageMap.get(selectedUser);
    }

    public List<Message> getMessages() {
        if (userMessageMap == null) userMessageMap = new HashMap<>();
        return userMessageMap.get(selectedUser);
    }

    public List<User> getRecipients() {
        return recipientList;
    }

    /**
     * Send the message using MessageWorker.
     *
     * @param message Message object to be sent.
     */
    private void sendMessage(Message message) {
        if (recipientList != null && !recipientList.isEmpty()) {
            final var recipients = recipientList.toArray(new User[recipientList.size()]);

            message.setReceiverList(recipients);
            messageWorker.sendMessage(message);

            message.setSentTime(new Date());
            for (var recipient : recipients) {
                var messages = userMessageMap.get(recipient);
                if (messages == null) {
                    messages = new ArrayList<>();
                    userMessageMap.put(recipient, messages);
                    messages.add(message);
                } else {
                    messages.add(message);
                }
            }

        } else {
            onFailToSent();
        }

        if (onUpdate != null) onUpdate.signal();
    }

    /**
     * Callback event for when message is failed to be sent.
     */
    private void onFailToSent() {
        // TODO: Handle failed sent
    }

    /**
     * Callback for incoming message.
     *
     * @param msg Message object from the server
     */
    private void onMessage(IMessage msg) {
        if (msg instanceof Message) {
            var message = (Message) msg;
            var sender = message.getSender();

            var senderMessages = userMessageMap.get(sender);
            if (senderMessages == null) {
                var userMessages = new ArrayList<Message>();
                userMessages.add(message);
                userMessageMap.put(sender, userMessages);
            } else {
                senderMessages.add(message);
            }
        } else if (msg instanceof UserListMessage) {
            var userList = (UserListMessage) msg;
            if (connectedUserList == null) connectedUserList = new ArrayList<>();
            var arrUser = userList.getUsers();
            connectedUserList.clear();
            for (var user : arrUser)
                if (!user.equals(this.user)) connectedUserList.add(user);

            if (activeUserList == null) activeUserList = connectedUserList;
        }

        if (onUpdate != null) onUpdate.signal();
    }

    /**
     * Callback for when connection is established.
     */
    private void onConnect() {
        System.out.println("WE ARE CONNECTED!");
        if (onConnectEvent != null) onConnectEvent.signal();

        if (user != null)
            messageWorker.sendUser(user);
        else
            disconnect();
    }

    /**
     * Callback for when connection is disconnected.
     */
    private void onDisconnect() {
        System.out.println("WE ARE DISCONNECTED!");
        if (onDisconnectEvent != null) onDisconnectEvent.signal();
    }

    /**
     * Callback for when failed to connect
     */
    private void onFailedConnect() {
        System.out.println("FAILED TO CONNECT!");

        if (onDisconnectEvent != null) onDisconnectEvent.signal();
    }
}