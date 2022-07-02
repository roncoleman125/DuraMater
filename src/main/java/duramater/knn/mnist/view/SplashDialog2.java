package duramater.knn.mnist.view;

import javax.swing.*;
import java.awt.*;

public class SplashDialog2 {
    //    public static void main(String[] args) {
//        JOptionPane.showMessageDialog(null,"Successfully Updated.","Alert",JOptionPane.WARNING_MESSAGE);
//    }
    public static void main(String[] args) throws Exception {
        ImageIcon icon = new ImageIcon("images/count-2.png");
        JLabel dacount = new JLabel(icon);
        JPanel panel = new JPanel(new GridLayout(1, 2));


        JPanel textPanel = new JPanel(new GridLayout(2, 1));

        JLabel text1 = new JLabel("Count Basic");
        text1.setFont(new Font("Arial", Font.BOLD, 20));
        textPanel.add(text1);

        JLabel text2 = new JLabel("Version 1.0");
        text2.setFont(new Font("Arial", Font.ITALIC, 14));
        textPanel.add(text2);

        panel.add(textPanel);
        panel.add(dacount);

        JOptionPane.showMessageDialog(null, panel, "Signon", JOptionPane.DEFAULT_OPTION);
        new CountBasic();
    }
}
