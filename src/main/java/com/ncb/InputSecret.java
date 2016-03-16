package com.ncb;

import java.awt.Component;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.LayoutStyle;
import org.openide.NotifyDescriptor;
import static org.openide.NotifyDescriptor.OK_CANCEL_OPTION;
import static org.openide.NotifyDescriptor.PLAIN_MESSAGE;
import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

/**
 *
 * @author richardlowery
 */
public class InputSecret extends NotifyDescriptor {

    /**
     * The text field used to enter the input.
     */
    protected JPasswordField textField;

    /**
     * Construct dialog with the specified title and label text.
     *
     * @param text label text
     * @param title title of the dialog
     */
    public InputSecret(final String text, final String title) {
        this(text, title, OK_CANCEL_OPTION, PLAIN_MESSAGE);
    }

    /**
     * Construct dialog with the specified title, label text, option and message
     * types.
     *
     * @param text label text
     * @param title title of the dialog
     * @param optionType option type (ok, cancel, ...)
     * @param messageType message type (question, ...)
     */
    public InputSecret(final String text, final String title, final int optionType, final int messageType) {
        super(null, title, optionType, messageType, null, null);
        super.setMessage(createDesign(text));
    }

    /**
     * Get the text which the user typed into the input line.
     *
     * @return the text entered by the user
     */
    public String getInputText() {
        return new String(textField.getPassword());
    }

    /**
     * Set the text on the input line.
     *
     * @param text the new text
     */
    public void setInputText(final String text) {
        textField.setText(text);
        textField.selectAll();
    }

    /**
     * Make a component representing the input line.
     *
     * @param text a label for the input line
     * @return the component
     */
    protected Component createDesign(final String text) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JLabel textLabel = new JLabel();
        Mnemonics.setLocalizedText(textLabel, text);

        boolean longText = text.length() > 80;
        textField = new JPasswordField(25);
        textLabel.setLabelFor(textField);

        textField.requestFocus();

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        if (longText) {
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                            .addComponent(textLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGap(32, 32, 32))
                                    .addComponent(textField))
                            .addContainerGap())
            );
        } else {
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(textLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(textField, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                            .addContainerGap())
            );
        }
        if (longText) {
            layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(textLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        } else {
            layout.setVerticalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(textLabel)
                                    .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }

        javax.swing.KeyStroke enter = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0);
        javax.swing.text.Keymap map = textField.getKeymap();

        map.removeKeyStrokeBinding(enter);

        /*

                  textField.addActionListener (new java.awt.event.ActionListener () {
                      public void actionPerformed (java.awt.event.ActionEvent evt) {
            System.out.println("action: " + evt);
                        InputLine.this.setValue (OK_OPTION);
                      }
                    }
                  );
         */
        panel.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(NotifyDescriptor.class, "ACSD_InputPanel")
        );
        textField.getAccessibleContext().setAccessibleDescription(
                NbBundle.getMessage(NotifyDescriptor.class, "ACSD_InputField")
        );

        return panel;
    }

}
