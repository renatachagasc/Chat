package aplicativo.chat.service;

import aplicativo.chat.message.Chat;
import aplicativo.chat.message.Chat.Acao;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.awt.windows.ThemeReader;

public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();

    public ServidorService() {
        try {
            serverSocket = new ServerSocket(5555);

            System.out.println("Servidor on!");

            while (true) {
                socket = serverSocket.accept();

                new Thread(new ListenerSocket(socket)).start();
            }

        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream (socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            Chat mensagem = null;
            try {
                while ((mensagem = (Chat) input.readObject()) != null) {
                    Acao acao = mensagem.getAction();

                    if (acao.equals(Acao.CONNECT)) {
                        boolean isConnect = conectar(mensagem, output);
                        if (isConnect) {
                            mapOnlines.put(mensagem.getName(), output);
                            sendOnlines();
                        }
                    } else if (acao.equals(Acao.DISCONNECT)) {
                        disconnect(mensagem, output);
                        sendOnlines();
                        return;
                    } else if (acao.equals(Acao.SEND_ONE)) {
                        sendOne(mensagem);
                    } else if (acao.equals(Acao.SEND_ALL)) {
                        sendAll(mensagem);
                    }
                }
            } catch (IOException ex) {
                Chat cm = new Chat();
                cm.setName(mensagem.getName());
                disconnect(cm, output);
                sendOnlines();
                System.out.println(mensagem.getName() + " deixou o chat!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean conectar(Chat mensagem, ObjectOutputStream output) {
        if (mapOnlines.size() == 0) {
            mensagem.setText("YES");
            send(mensagem, output);
            return true;
        }

        if (mapOnlines.containsKey(mensagem.getName())) {
            mensagem.setText("NO");
            send(mensagem, output);
            return false;
        } else {
            mensagem.setText("YES");
            send(mensagem, output);
            return true;
        }
    }

    private void disconnect(Chat message, ObjectOutputStream output) {
        mapOnlines.remove(message.getName());

        message.setText(" até logo!");

        message.setAction(Acao.SEND_ONE);

        sendAll(message);

        System.out.println("User " + message.getName() + " sai da sala");
    }

    private void send(Chat message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendOne(Chat message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getNomeReservada())) {
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendAll(Chat message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (!kv.getKey().equals(message.getName())) {
                message.setAction(Acao.SEND_ONE);
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendOnlines() {
        Set<String> setNames = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNames.add(kv.getKey());
        }

        Chat message = new Chat();
        message.setAction(Acao.USERS_ONLINE);
        message.setListaOnlines(setNames);

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            message.setName(kv.getKey());
            try {
                kv.getValue().writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
