package uniandes.dpoo.taller4.interfaz;
import uniandes.dpoo.taller4.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.ImageIcon;

public class Juego {

    private Tablero tablero;
    private TableroPanel tableroPanel;
    private JFrame ventana;
    private JComboBox<String> tamanosComboBox;
    private JRadioButton facilButton;
    private JRadioButton medioButton;
    private JRadioButton dificilButton;
    private ButtonGroup dificultadGroup;
    private JLabel jugadasLabel;
    private String nombreJugador = "";

    public Juego() {
        ventana = new JFrame("Lights Out");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLayout(new BorderLayout());

        String[] tamanos = {"3x3", "4x4", "5x5", "6x6", "7x7", "8x8", "9x9", "10x10"};
        tamanosComboBox = new JComboBox<>(tamanos);
        tamanosComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) tamanosComboBox.getSelectedItem();
                int nuevoTamano = Integer.parseInt(seleccion.substring(0, seleccion.indexOf("x")));
                tablero = new Tablero(nuevoTamano);
                tablero.desordenar(20);
                tableroPanel.repaint();
                reiniciarJugadas();
            }
        });

        JPanel opcionesPanel = new JPanel();
        opcionesPanel.add(new JLabel("Tamaño del tablero: "));
        opcionesPanel.add(tamanosComboBox);

        facilButton = new JRadioButton("Fácil");
        medioButton = new JRadioButton("Medio");
        dificilButton = new JRadioButton("Difícil");
        dificultadGroup = new ButtonGroup();
        dificultadGroup.add(facilButton);
        dificultadGroup.add(medioButton);
        dificultadGroup.add(dificilButton);

        facilButton.setSelected(true);

        ActionListener dificultadListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) tamanosComboBox.getSelectedItem();
                int nuevoTamano = Integer.parseInt(seleccion.substring(0, seleccion.indexOf("x")));
                tablero = new Tablero(nuevoTamano);
                if (facilButton.isSelected()) {
                    tablero.desordenar(3);
                } else if (medioButton.isSelected()) {
                    tablero.desordenar(5);
                } else if (dificilButton.isSelected()) {
                    tablero.desordenar(8);
                }
                tableroPanel.repaint();
                reiniciarJugadas();
            }
        };

        facilButton.addActionListener(dificultadListener);
        medioButton.addActionListener(dificultadListener);
        dificilButton.addActionListener(dificultadListener);

        JPanel dificultadPanel = new JPanel();
        dificultadPanel.add(facilButton);
        dificultadPanel.add(medioButton);
        dificultadPanel.add(dificilButton);

        JPanel opcionesDificultadPanel = new JPanel();
        opcionesDificultadPanel.setLayout(new BorderLayout());
        opcionesDificultadPanel.add(opcionesPanel, BorderLayout.NORTH);
        opcionesDificultadPanel.add(dificultadPanel, BorderLayout.CENTER);

        tableroPanel = new TableroPanel();

        jugadasLabel = new JLabel("Jugador: " + nombreJugador + " - Jugadas: 0");

        JButton nuevoButton = new JButton("Nuevo");
        nuevoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) tamanosComboBox.getSelectedItem();
                int nuevoTamano = Integer.parseInt(seleccion.substring(0, seleccion.indexOf("x")));
                tablero = new Tablero(nuevoTamano);
                if (facilButton.isSelected()) {
                    tablero.desordenar(3);
                } else if (medioButton.isSelected()) {
                    tablero.desordenar(5);
                } else if (dificilButton.isSelected()) {
                    tablero.desordenar(8);
                }
                tableroPanel.repaint();
                reiniciarJugadas();
            }
        });

        JButton reiniciarButton = new JButton("Reiniciar");
        reiniciarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tablero.reiniciar();
                tableroPanel.repaint();
                reiniciarJugadas();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1));
        buttonPanel.add(nuevoButton);
        buttonPanel.add(reiniciarButton);

        ventana.add(opcionesDificultadPanel, BorderLayout.NORTH);
        ventana.add(tableroPanel, BorderLayout.CENTER);
        ventana.add(jugadasLabel, BorderLayout.SOUTH);
        ventana.add(buttonPanel, BorderLayout.EAST);

        tablero = new Tablero(3);
        tablero.desordenar(20);

        tableroPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = e.getY() / tableroPanel.getTamanoCelda();
                int columna = e.getX() / tableroPanel.getTamanoCelda();

                if (fila >= 0 && fila < tablero.darTablero().length && columna >= 0 && columna < tablero.darTablero()[0].length) {
                    tablero.jugar(fila, columna);
                    tableroPanel.repaint();
                    actualizarJugadas();
                    if (tablero.tableroIluminado()) {
                        JOptionPane.showMessageDialog(ventana, "¡Ganaste!", "¡Felicidades!", JOptionPane.INFORMATION_MESSAGE);
                        registrarGanador(nombreJugador, tablero.darJugadas());
                    }
                }
            }
        });

        JButton top10Button = new JButton("TOP-10");
        top10Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarTop10();
            }
        });

        buttonPanel.add(top10Button);

        ventana.pack();
        ventana.setVisible(true);

        JButton cambiarJugadorButton = new JButton("CAMBIAR JUGADOR");
        cambiarJugadorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nuevoNombre = JOptionPane.showInputDialog(ventana, "Ingresa un nombre de 3 caracteres sin ';'");
                if (nuevoNombre != null && nuevoNombre.length() == 3 && !nuevoNombre.contains(";")) {
                    nombreJugador = nuevoNombre;
                    actualizarJugadas();
                } else {
                    JOptionPane.showMessageDialog(ventana, "Nombre no válido. Debe tener 3 caracteres y no debe contener ';'.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(cambiarJugadorButton);

        ventana.pack();
        ventana.setVisible(true);
    }

    private void mostrarTop10() {
        List<RegistroTop10> registros = leerTop10DesdeCSV();
        Collections.sort(registros, Comparator.comparingInt(RegistroTop10::darPuntos));

        if (registros.isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "No hay datos en el top 10.", "TOP-10 Vacío", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int cantidadAMostrar = Math.min(registros.size(), 10);
            JDialog top10Dialog = new JDialog(ventana, "TOP-10", true);
            top10Dialog.setLayout(new BorderLayout());

            JTextArea textArea = new JTextArea(15, 30);
            textArea.setEditable(false);
            textArea.setText("TOP-10:\n\n");

            for (int i = 0; i < cantidadAMostrar; i++) {
                RegistroTop10 registro = registros.get(i);
                textArea.append(registro.darNombre() + " ..... " + registro.darPuntos() + "\n");
            }

            JScrollPane scrollPane = new JScrollPane(textArea);
            top10Dialog.add(scrollPane, BorderLayout.CENTER);

            top10Dialog.pack();
            top10Dialog.setLocationRelativeTo(ventana);
            top10Dialog.setVisible(true);
        }
    }

    private List<RegistroTop10> leerTop10DesdeCSV() {
        List<RegistroTop10> top10 = new ArrayList<>();
        String csvFile = "data/top10.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] partes = line.split(";");
                if (partes.length == 2) {
                    String nombre = partes[0];
                    int puntos = Integer.parseInt(partes[1]);
                    RegistroTop10 registro = new RegistroTop10(nombre, puntos);
                    top10.add(registro);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return top10;
    }

    private void registrarGanador(String nombreJugador, int jugadas) {
        try {
            String csvFile = "data/top10.csv";
            FileWriter writer = new FileWriter(csvFile, true);
            String entrada = nombreJugador + ";" + jugadas;
            writer.write(entrada + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(ventana, "No se pudo registrar el ganador.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Juego());
    }

    private void reiniciarJugadas() {
        tablero.reiniciar();
        tableroPanel.repaint();
        actualizarJugadas();
    }

    private void actualizarJugadas() {
        int jugadas = tablero.darJugadas();
        jugadasLabel.setText("Jugador: " + nombreJugador + " - Jugadas: " + jugadas);
    }

    private class TableroPanel extends JPanel {
        private int tamanoCelda = 50;
        private int espacioEntreCeldas = 5;
        private int radioEsquinas = 10;
        private ImageIcon luzIcon;

        public int getTamanoCelda() {
            return tamanoCelda + espacioEntreCeldas;
        }

        public TableroPanel() {
            luzIcon = new ImageIcon("data/luz.png");
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            boolean[][] matriz = tablero.darTablero();

            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    if (matriz[i][j]) {
                        g.setColor(Color.YELLOW);
                        g.fillRoundRect(j * (tamanoCelda + espacioEntreCeldas),
                                i * (tamanoCelda + espacioEntreCeldas),
                                tamanoCelda, tamanoCelda, radioEsquinas, radioEsquinas);
                        luzIcon.paintIcon(this, g, j * (tamanoCelda + espacioEntreCeldas),
                                i * (tamanoCelda + espacioEntreCeldas));
                    } else {
                        g.setColor(Color.BLACK);
                        g.fillRoundRect(j * (tamanoCelda + espacioEntreCeldas),
                                i * (tamanoCelda + espacioEntreCeldas),
                                tamanoCelda, tamanoCelda, radioEsquinas, radioEsquinas);
                        luzIcon.paintIcon(this, g, j * (tamanoCelda + espacioEntreCeldas),
                                i * (tamanoCelda + espacioEntreCeldas));
                    }
                }
            }
        }
    }
}