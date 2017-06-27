package tec.soda.frames;

import javax.swing.*;
import java.awt.event.*;

public class retryProcedure extends JDialog {
    private JPanel contentPane;
    private JButton buttonYes;
    private JButton buttonCancel;
    private JButton buttonNo;
    private JTextArea textAboutRetry;
    public int returnStatus=JOptionPane.DEFAULT_OPTION;

    private retryProcedure(String info) {//todo rebuild using that
        setContentPane(contentPane);
        setModal(true);
        setTitle("Retry Procedure?");
        getRootPane().setDefaultButton(buttonYes);

        textAboutRetry.setText(info);

        buttonYes.addActionListener(e -> onYes());
        buttonNo.addActionListener(e -> onNo());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onYes() {
        returnStatus=JOptionPane.YES_OPTION;
        dispose();
    }

    private void onNo() {
        returnStatus=JOptionPane.NO_OPTION;
        dispose();
    }

    private void onCancel() {
        returnStatus=JOptionPane.CANCEL_OPTION;
        dispose();
    }

    public static int showDialog(String info) {
        retryProcedure dialog = new retryProcedure(info);
        dialog.pack();
        dialog.setVisible(true);
        return dialog.returnStatus;
    }
}
