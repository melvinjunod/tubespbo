/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tubespbo2;
import java.sql.SQLException;

public class Tubespbo2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        menuUtama mainMenu = new menuUtama();
        mainMenu.setVisible(true);
        mainMenu.setResizable(false);
    }
    
}
