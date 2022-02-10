import com.sun.glass.ui.Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.RootPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class MainActivity extends JFrame {
    private JPanel rootPanel;
    private JTextField textField1;
    private JTextField textField2;
    private JButton mergeButton;
    private JButton btnBuscarRuta1;
    private JCheckBox chk_nuevacarpeta;
    private JSpinner spinner1;

    public MainActivity(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        add(rootPanel);
        setTitle("ScreenTo");
        setSize(screenSize.width*35/100,screenSize.height*20/100);
        spinner1.setValue(3);
        mergeButton.addActionListener(e -> {
            String ruta_inicio = textField1.getText().toString();
            try {
                mergeImage(ruta_inicio);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        btnBuscarRuta1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               abrirArchivo();
            }
        });

    }

    private void abrirArchivo() {
        try{
            String texto="";
            if(textField1.getText().equals("")){
                File miDir = new File (".");
                texto= miDir.getCanonicalPath();
            }
            else{
                texto=textField1.getText();
            }
            System.setProperty("apple.awt.fileDialogForDirectories","true");
            JFileChooser file=new JFileChooser(texto);
            file.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            file.showDialog(this, "Select");
            if (file.getSelectedFile() != null) {
                textField1.setText(file.getSelectedFile().getParent());
            }
        }
        catch (Exception ex){
            System.err.println(ex);
        }
    }

    public void mergeImage(String ruta1) throws IOException {
        try {
            int chunkWidth, chunkHeight;
            int type;
            File directory = new File(ruta1);
            java.util.List<java.io.File> files = new ArrayList<>();

            // Get all files from a directory.
            File[] fList = directory.listFiles(File::isFile);
            Arrays.sort(fList, Comparator.comparingLong(File::lastModified));
            int cont = 0;
            if(fList != null) {
                System.out.println("entro.....");
                for (File file : fList) {
                    if (file.isFile()) {
                        System.err.println(file.getName());
                        if(file.getName().toUpperCase().endsWith(".PNG")||
                                file.getName().toUpperCase().endsWith(".JPG")||
                            file.getName().toUpperCase().endsWith(".JPEG")){
                            files.add(file);
                            cont++;
                        }
//                        else if(!file.getName().equalsIgnoreCase("desktop.ini")){
//                            files.add(file);
//                            cont++;
//                        }


                    }
                }
                fList[0].lastModified();
            }

            if (files.get(0).exists()) {
                System.out.println("existe.....");
            }

            System.out.println(cont + " elementos.....");
            int cols = (int) spinner1.getValue();
            int rows = (int) Math.ceil(((double)cont)/cols);
            //int cols = 3;

            int chunks = rows * cols;

            BufferedImage[] buffImages = new BufferedImage[chunks];
            for (int i = 0; i < chunks; i++) {
                if(i<cont){
                    buffImages[i] = ImageIO.read(files.get(i));
                }
            }

            System.out.println("probando lectura.....");

            type = buffImages[0].getType();
            if(type == 0){
                type = 5;
            }
            chunkWidth = buffImages[0].getWidth();
            chunkHeight = buffImages[0].getHeight();
            BufferedImage finalImg = new BufferedImage(chunkWidth * cols, chunkHeight * rows, type);

            int num = 0;
            int cant = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if(cant<cont){
                        finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                        num++;
                        cant++;
                    }
                }
            }
            System.out.println("Image concatenated.....");

            if(chk_nuevacarpeta.isSelected()){
                ruta1 = ruta1+"/Merged";
                File directorios = new File(ruta1);
                if (!directorios.exists()) {
                    if (directorios.mkdirs()) {
                        System.out.println("Multiples directorios fueron creados");
                    } else {
                        System.out.println("Error al crear directorios");
                    }
                }

            }

            ImageIO.write(finalImg, "PNG", new File(ruta1+"/"+textField2.getText()+".png"));
            JOptionPane.showConfirmDialog(null,"Se creó la imagen","Confirmación",JOptionPane.CLOSED_OPTION,JOptionPane.OK_OPTION);
        }catch (Exception e){
            JOptionPane.showConfirmDialog(null,
                    "No se pudo crear la imagen \n " +
                    "Nota: Debe crear una carpeta dentro de cualquier biblioteca"
                    ,"Error",JOptionPane.CLOSED_OPTION,JOptionPane.ERROR_MESSAGE);
            System.err.println("Error:"+e);
        }
        finally {
            System.out.println("Finaly merge images");
        }
    }
}
