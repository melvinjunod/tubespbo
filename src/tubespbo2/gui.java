/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tubespbo2;

/**
 *
 * @author ACER
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class gui extends JFrame implements ActionListener
{
    CardLayout crd;
    Container cPane;
    JPanel menuUtama;

    gui()
    {
        cPane = getContentPane();
        crd = new CardLayout(40, 40);
        cPane.setLayout(crd);
        menuUtama = new JPanel();
        menuUtama.setBounds(40, 40, 1200, 640);

        JButton datamotor = new JButton("Data Motor");
        JButton datapengguna = new JButton("Data Pengguna");
        JButton datapenjualan = new JButton("Data Penjualan");
        JButton datapembelian = new JButton("Data Pembelian");

        JButton dummy1 = new JButton("Ini tombol dummy PERTAMA");
        JButton dummy2 = new JButton("Ini tombol dummy KEDUA");

        datamotor.addActionListener((ActionEvent ae) -> {
            crd.show(cPane, "carddatamotor");
            dummy1.setText("Hahahaha");
        });
        
        datapengguna.addActionListener((ActionEvent ae) -> {
            crd.show(cPane, "c");
        });
        
        datapenjualan.addActionListener(this);
        datapembelian.addActionListener(this);
        dummy1.addActionListener(this);
        dummy2.addActionListener(this);

        menuUtama.add(datamotor);
        menuUtama.add(datapengguna);
        menuUtama.add(datapenjualan);
        menuUtama.add(datapembelian);
        menuUtama.setLayout(new GridLayout(4, 1, 40, 40));

        cPane.add("a",menuUtama);
//        cPane.add("carddatamotor", menuDataMotor);
        cPane.add("c", dummy2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        crd.next(cPane);
    }
}