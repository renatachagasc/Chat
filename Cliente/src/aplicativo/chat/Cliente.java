
package aplicativo.chat;

import aplicativo.chat.frame.ClienteFrame;
import aplicativo.chat.frame.LoginFrame;

public class Cliente {

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
