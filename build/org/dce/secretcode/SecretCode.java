/**
   Copyright (c) 2005, David Eccleston
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

   * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
   * Neither the name of the <ORGANIZATION> nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
**/

package org.dce.secretcode;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import java.net.URL;
import javax.swing.JOptionPane;
import java.io.File;
import javax.swing.JToolBar;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class SecretCode extends JFrame implements ActionListener, DocumentListener {
    StyledDocument styledDoc;
    Style regular;
    JTextPane textPane;

    public SecretCode() {
        super();

        this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFocusable(false);

        styledDoc = textPane.getStyledDocument();
        addStylesToDocument(styledDoc);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        panel.setFocusable(false);

        c.fill = c.NONE;
        c.weightx=0.0;
        c.weighty=0.0;
        c.gridx=0;
        c.gridy=0;
        JToolBar menubar = new JToolBar();
        menubar.setFloatable(false);

        JButton printButton = new JButton("Print");
        printButton.setFocusable(false);
        printButton.setActionCommand("Print");
        printButton.addActionListener(this);
        menubar.add(printButton);

        JButton aboutButton = new JButton("About");
        aboutButton.setFocusable(false);
        aboutButton.setActionCommand("About");
        aboutButton.addActionListener(this);
        menubar.add(aboutButton);

        panel.add(menubar, c);

        c.fill = c.BOTH;
        c.weightx=1.0;
        c.weighty=1.0;
        c.gridy++;
        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setFocusable(false);
        scroll.setPreferredSize(new Dimension(600,500));
        panel.add(scroll, c);


        c.fill = c.HORIZONTAL;
        c.weightx=0;
        c.weighty=0;
        c.gridy++;
        JTextField userField = new JTextField("");
        panel.add(userField, c);
        userField.getDocument().addDocumentListener(this);

        //         c.fill = c.NONE;
        //         c.weightx=0;
        //         c.weighty=0;
        //         c.gridx=0;
        //         c.gridy++;
        //         JButton printButton = new JButton("Print");
        //         printButton.setFocusable(false);
        //         printButton.addActionListener(this);
        //         panel.add(printButton, c);



        this.getContentPane().add(panel);

        userField.requestFocusInWindow();

        this.pack();

    }

    protected void addStylesToDocument(StyledDocument doc) {


        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        regular = doc.addStyle("regular", def);

        Vector v = new Vector();
        for (int i='a'; i<='z'; i++) {
            char a = (char)i;
            v.add(""+a);
        }
        v.add("1");

        Iterator iter = v.iterator();
        while (iter.hasNext()) {
            String letter = (String)iter.next();

            Style style = doc.addStyle(letter, regular);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);

            String imageName = "images/" + letter + ".gif";

            ImageIcon icon = getIcon(imageName);
            
            if (icon != null) {
                StyleConstants.setIcon(style, icon);
            }
        }

    }

    public ImageIcon getIcon(String imageName) {
        URL iconUrl = this.getClass().getResource(imageName);
        if (iconUrl != null) {
            return new ImageIcon(iconUrl);
        }
        return null;
    }
    protected Style getNewStyleForLetter(StyledDocument doc, String letter) {


        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        regular = doc.addStyle("regular", def);

        Style style = doc.addStyle(letter, regular);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);

        ImageIcon icon = getIcon("images/" + letter + ".gif");

        if (icon == null)
            return null;

        StyleConstants.setIcon(style, icon);
        return style;
    }

    public void changedUpdate(DocumentEvent documentEvent) {
        //         System.out.println("ChangedUpdate");
        
    }

    public void insertUpdate(DocumentEvent de) {
        //         System.out.println("insert " + de.getOffset() + " " + de.getLength());
        try {
            String message= de.getDocument().getText(de.getOffset(),
                                                     de.getLength());

            message = message.toLowerCase();

            for (int i=0; i<message.length(); i++) {
                String character = message.charAt(i)+"";
                if (character.equals(" ")) {
                    character = "1";
                }
                Style style = getNewStyleForLetter(styledDoc, character);
                if (style == null)
                    style=regular;
                AttributeSet attributes = style.copyAttributes();
                styledDoc.insertString(de.getOffset()+i,
                                       character,
                                       //                                        regular);
                                       attributes);

                //                 System.out.println("===================================");
                

                AttributeSet set = styledDoc.getStyle(character).copyAttributes();
                Enumeration enumer = set.getAttributeNames();;
                while (enumer.hasMoreElements()) {
                    Object next = enumer.nextElement();
                    //                     System.out.println(next + " " + set.getAttribute(next));
                    
                }

            }
        }
        catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }

    public void removeUpdate(DocumentEvent de) {
        //         System.out.println("remove " + de.getOffset() + " " + de.getLength());
        try {
            styledDoc.remove(de.getOffset(), de.getLength());
        }
        catch (BadLocationException ble) {
            ble.printStackTrace();
        }
    }

    /**
     * Main method.
     */
    public static void main(String s[]) throws Exception {
        SecretCode thisCode = new SecretCode();

        thisCode.show();
    }


    public void print() {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new Printable() {
                    public int print(Graphics g, PageFormat format, int pageNumber) {
                        double pageOffset = pageNumber * format.getImageableHeight();
                        View view = textPane.getUI().getRootView(textPane);
                        if(pageOffset > view.getPreferredSpan(View.Y_AXIS))
                            return Printable.NO_SUCH_PAGE;

                        ((Graphics2D)g).translate(0d, -pageOffset);
                        Shape shape = new Rectangle2D.Double(format.getImageableX(),
                                                             format.getImageableY(),
                                                             format.getImageableWidth(),
                                                             format.getImageableHeight() + pageOffset);
                        view.paint(g, shape);
                        return Printable.PAGE_EXISTS;
                    }
                });
            job.print();
        } catch(PrinterException e) {
            e.printStackTrace();
        }
    }


    // Implementation of java.awt.event.ActionListener

    /**
     * Describe <code>actionPerformed</code> method here.
     *
     * @param actionEvent an <code>ActionEvent</code> value
     */
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals("About")) {
            JOptionPane.showMessageDialog(this,
                                          "Secret Code generator by David Eccleston\n"
                                          + "deccles@mitre.org",
                                          "About",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
        if (actionEvent.getActionCommand().equals("Print")) {
            print();
        }
    }
    
}
