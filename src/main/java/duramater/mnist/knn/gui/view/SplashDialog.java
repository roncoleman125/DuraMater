package duramater.mnist.knn.gui.view;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author roncoleman125
 */
public class SplashDialog extends JDialog {
    /**
     * Creates new form SplashDialog
     * @param parent Parent frame
     * @param modal Modal flag
     */
    public SplashDialog(Frame parent, boolean modal) {
        super(parent, modal);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setTitle("Count Basic");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JLabel title = new JLabel("Count Basic");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title);

        ImageIcon icon = new ImageIcon("images/count-1.png");
        JLabel dacount = new JLabel();
        dacount.setIcon(icon);
        panel.add(dacount);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton ok = new JButton("Ok");
        buttonPanel.add(ok);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        for(int i=0; i < 2; i++)
            buttonPanel.add(new JLabel(" "));

        getContentPane().add(buttonPanel,BorderLayout.SOUTH);

        setSize(440, 270);
//        getContentPane().setBackground(new Color(173, 149, 245) );
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {


    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //</editor-fold>

        /* Create and display the dialog */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                final SplashDialog dialog = new SplashDialog(new JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(7000);

                            new CountBasic();

                            dialog.dispose();
                        } catch (InterruptedException ex) {

                        }
                    }
                }).start();
                dialog.setVisible(true);
            }
        });
    }
}
