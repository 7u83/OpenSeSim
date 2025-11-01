/*
 * Copyright (c) 2017, 7u83 <7u83@mail.ru>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package traders.GroovyTrader;

//import org.fife.ui.rtextarea.AbstractSearchDialog;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchResult;
//import org.fife.ui.rtextarea.SearchDialog;
//import org.fife.ui.rtextarea.ReplaceDialog;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.fife.rsta.ui.search.ReplaceDialog;
import org.fife.rsta.ui.search.FindDialog;
import org.fife.rsta.ui.search.SearchEvent;
import static org.fife.rsta.ui.search.SearchEvent.Type.FIND;
import static org.fife.rsta.ui.search.SearchEvent.Type.MARK_ALL;
import static org.fife.rsta.ui.search.SearchEvent.Type.REPLACE;
import static org.fife.rsta.ui.search.SearchEvent.Type.REPLACE_ALL;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rtextarea.RTextScrollPane;

import sesim.AutoTraderGui;
//import static sun.jvm.hotspot.HelloWorld.e;

/**
 *
 * @author tube
 */
public class GroovyTraderGui extends AutoTraderGui implements SearchListener {

    RSyntaxTextArea textArea;
    GroovyTrader trader;

    ReplaceDialog replaceDialog;
    FindDialog findDialog;

    /**
     * Creates new form GrovyTraderGui
     * @param trader the trader the form belongs to
     */
    public GroovyTraderGui(GroovyTrader trader) {
        this.trader = trader;

        initComponents();

        setLayout(new BorderLayout());

        // 1. Erstellen Sie die RSyntaxTextArea
        textArea = new RSyntaxTextArea(20, 60);

        // 2. Setzen Sie den Syntax-Stil auf Groovy
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);

        // Optional: Zeilennummern anzeigen, etc.
        textArea.setCodeFoldingEnabled(true);

        this.setText(trader.getSourceCode());

        RTextScrollPane sp = new RTextScrollPane(textArea);

        // 4. Fügen Sie die ScrollPane zum JPanel hinzu
        this.add(sp, BorderLayout.CENTER);

        JDialog parent = (JDialog) SwingUtilities.getWindowAncestor(this);

        findDialog = new org.fife.rsta.ui.search.FindDialog(parent, this);
        //findDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        findDialog.setModalityType(Dialog.ModalityType.MODELESS);
        findDialog.pack();;
        findDialog.setMinimumSize(findDialog.getSize());
        replaceDialog = new org.fife.rsta.ui.search.ReplaceDialog(parent, this);
        replaceDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        SearchContext context = findDialog.getSearchContext();

        // Tastenkürzel hinzufügen: STRG+F = Suche, STRG+H = Ersetzen
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {

                    findDialog.setAlwaysOnTop(true);
                    findDialog.setLocationRelativeTo(parent);
                    findDialog.setVisible(true);

                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_H) {
                    replaceDialog.setVisible(true);
                }
            }
        });


        setVisible(true);
    }

    void setText(String text) {
        textArea.setText(text);
    }

    String getText() {
        return textArea.getText();
    }

    @Override
    public void save() {
        this.trader.setSeourceCode(getText());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(null);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void searchEvent(SearchEvent e) {
        SearchEvent.Type type = e.getType();
        SearchContext context = e.getSearchContext();
        SearchResult result;

        switch (type) {
            default: // Prevent FindBugs warning later
            case MARK_ALL:
                result = SearchEngine.markAll(textArea, context);
                break;
            case FIND:
                result = SearchEngine.find(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE:
                result = SearchEngine.replace(textArea, context);
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea);
                }
                break;
            case REPLACE_ALL:
                result = SearchEngine.replaceAll(textArea, context);
                JOptionPane.showMessageDialog(null, result.getCount()
                        + " occurrences replaced.");
                break;
        }

        String text;
        if (result.wasFound()) {
            text = "Text found; occurrences marked: " + result.getMarkedCount();
        } else if (type == SearchEvent.Type.MARK_ALL) {
            if (result.getMarkedCount() > 0) {
                text = "Occurrences marked: " + result.getMarkedCount();
            } else {
                text = "";
            }
        } else {
            text = "Text not found";
        }
        //statusBar.setLabel(text);
    }

    @Override
    public String getSelectedText() {
        return textArea.getSelectedText();

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
