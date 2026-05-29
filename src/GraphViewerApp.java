import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphViewerApp extends JFrame {

    private Grafo grafoAtivo;
    private JTextArea consoleOutput;

    // Variáveis visuais para o T2
    private Map<String, Integer> coresDsatur = new HashMap<>();
    private List<String> caminhoAStar = new ArrayList<>();

    public GraphViewerApp() {
        grafoAtivo = new Grafo(false);

        setTitle("Visualizador de Grafos");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        consoleOutput = new JTextArea();
        consoleOutput.setEditable(false);
        consoleOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        consoleOutput.setBackground(new Color(43, 43, 43));
        consoleOutput.setForeground(new Color(169, 183, 198));

        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        scrollPane.setPreferredSize(new Dimension(800, 150));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Console de Saída do Grafo"));
        add(scrollPane, BorderLayout.SOUTH);

        setJMenuBar(criarMenuBar());
        add(new GraphPanel(), BorderLayout.CENTER);

        log("Crie o grafo e insira vértices e arestas usando o menu acima.\n");
    }

    private JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuArquivo = new JMenu("Grafo");
        JMenuItem itemNovoGrafo = new JMenuItem("Novo Grafo");
        itemNovoGrafo.addActionListener(e -> {
            boolean dirigido = (JOptionPane.showConfirmDialog(this, "O grafo será Dirigido?", "Novo Grafo", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
            grafoAtivo = new Grafo(dirigido);
            coresDsatur.clear();
            caminhoAStar.clear();
            consoleOutput.setText("");
            log("Grafo " + (dirigido ? "dirigido" : "não dirigido") + " criado.\n");
            repaint();
        });

        JMenuItem itemCarregarParana = new JMenuItem("Carregar Mapa do Paraná");
        itemCarregarParana.addActionListener(e -> carregarMapaParana());

        menuArquivo.add(itemNovoGrafo);
        menuArquivo.add(itemCarregarParana);
        menuArquivo.addSeparator();
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(itemSair);

        JMenu menuEstrutura = new JMenu("Operações");
        JMenuItem itemAddVertice = new JMenuItem("Inserir Vértice");
        itemAddVertice.addActionListener(e -> {
            String v = JOptionPane.showInputDialog(this, "Nome do Vértice:");
            if (v != null && !v.trim().isEmpty()) {
                grafoAtivo.inserirVertice(v);
                log("Vértice '" + v + "' inserido.");
                repaint();
            }
        });

        JMenuItem itemAddAresta = new JMenuItem("Inserir Aresta");
        itemAddAresta.addActionListener(e -> {
            try {
                String origem = JOptionPane.showInputDialog(this, "Vértice de Origem:");
                String destino = JOptionPane.showInputDialog(this, "Vértice de Destino:");
                double peso = Double.parseDouble(JOptionPane.showInputDialog(this, "Peso da Aresta:"));
                grafoAtivo.inserirAresta("E" + System.currentTimeMillis(), origem, destino, peso);
                log("Aresta " + origem + " -> " + destino + " inserida.");
                repaint();
            } catch (Exception ex) {
                log("Erro na inserção da aresta.");
            }
        });
        menuEstrutura.add(itemAddVertice);
        menuEstrutura.add(itemAddAresta);

        JMenu menuAlgoritmos = new JMenu("Algoritmos");

        JMenuItem itemDsatur = new JMenuItem("1. Coloração (DSATUR)");
        itemDsatur.addActionListener(e -> {
            coresDsatur = grafoAtivo.dsatur();
            log("\n--- Resultado DSATUR ---");
            coresDsatur.forEach((v, cor) -> log("Vértice " + v + " -> Cor: " + cor));
            repaint();
        });

        JMenuItem itemAStar = new JMenuItem("2. Caminho Mínimo (A*)");
        itemAStar.addActionListener(e -> {
            String inicio = JOptionPane.showInputDialog(this, "Vértice de Origem:");
            String destino = JOptionPane.showInputDialog(this, "Vértice de Destino:");
            if (inicio != null && destino != null) {
                StringBuilder relatorio = new StringBuilder();
                caminhoAStar = grafoAtivo.aStar(inicio, destino, relatorio);
                log("\n" + relatorio.toString());
                log("Caminho Mínimo: " + String.join(" -> ", caminhoAStar));
                repaint();
            }
        });

        menuAlgoritmos.add(itemDsatur);
        menuAlgoritmos.add(itemAStar);

        menuBar.add(menuArquivo);
        menuBar.add(menuEstrutura);
        menuBar.add(menuAlgoritmos);

        return menuBar;
    }

    private void carregarMapaParana() {
        grafoAtivo = new Grafo(false);
        coresDsatur.clear();
        caminhoAStar.clear();

        // Cadastra Vértices e Coordenadas (Lat/Lon)
        String[] cidades = {"Cascavel", "Toledo", "Foz do Iguaçu", "Francisco Beltrão", "São Mateus do Sul", "Guarapuava", "Ponta Grossa", "Curitiba", "Paranaguá", "Londrina", "Maringá", "Umuarama"};
        double[][] coords = {
                {-24.95, -53.45}, {-24.71, -53.74}, {-25.54, -54.58}, {-26.07, -53.05}, {-25.86, -50.38}, {-25.39, -51.46}, {-25.09, -50.16}, {-25.42, -49.27}, {-25.52, -48.50}, {-23.31, -51.16}, {-23.42, -51.93}, {-23.76, -53.32}
        };

        for (int i = 0; i < cidades.length; i++) {
            grafoAtivo.inserirVertice(cidades[i]);
            grafoAtivo.setCoordenada(cidades[i], coords[i][0], coords[i][1]);
        }

        // Arestas do mapa PDF
        grafoAtivo.inserirAresta("1", "Toledo", "Cascavel", 50);
        grafoAtivo.inserirAresta("2", "Toledo", "Umuarama", 126);
        grafoAtivo.inserirAresta("3", "Cascavel", "Foz do Iguaçu", 143);
        grafoAtivo.inserirAresta("4", "Cascavel", "Guarapuava", 250);
        grafoAtivo.inserirAresta("5", "Cascavel", "Francisco Beltrão", 186);
        grafoAtivo.inserirAresta("6", "Cascavel", "Maringá", 314);
        grafoAtivo.inserirAresta("7", "Umuarama", "Maringá", 190);
        grafoAtivo.inserirAresta("8", "Maringá", "Londrina", 114);
        grafoAtivo.inserirAresta("9", "Londrina", "Ponta Grossa", 273);
        grafoAtivo.inserirAresta("10", "Guarapuava", "Ponta Grossa", 165);
        grafoAtivo.inserirAresta("11", "Ponta Grossa", "Curitiba", 114);
        grafoAtivo.inserirAresta("12", "Curitiba", "Paranaguá", 90);
        grafoAtivo.inserirAresta("13", "Curitiba", "São Mateus do Sul", 157);
        grafoAtivo.inserirAresta("14", "São Mateus do Sul", "Francisco Beltrão", 354);

        log("Mapa do Paraná carregado com sucesso!\n");
        repaint();
    }

    private void log(String message) {
        consoleOutput.append(message + "\n");
        consoleOutput.setCaretPosition(consoleOutput.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphViewerApp().setVisible(true));
    }

    // DESENHAR O GRAFO
    class GraphPanel extends JPanel {
        private final int RAIO_VERTICE = 25;

        //cores para o DSATUR
        private Color[] paleta = {Color.RED, Color.GREEN, Color.CYAN, Color.YELLOW, Color.MAGENTA, Color.ORANGE, Color.PINK};

        public GraphPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            List<String> vertices = new ArrayList<>(grafoAtivo.getVertices());
            if (vertices.isEmpty()) return;

            Map<String, Point> posicoes = new HashMap<>();
            boolean usaGeografia = grafoAtivo.coordenadas.size() == vertices.size();

            int width = getWidth();
            int height = getHeight();

            if (usaGeografia) {
                double minLat = -26.5, maxLat = -22.5, minLon = -55.0, maxLon = -48.0;
                for (String v : vertices) {
                    double[] c = grafoAtivo.coordenadas.get(v);
                    int x = (int) (50 + ((c[1] - minLon) / (maxLon - minLon)) * (width - 100));
                    int y = (int) (50 + ((maxLat - c[0]) / (maxLat - minLat)) * (height - 100));
                    posicoes.put(v, new Point(x, y));
                }
            } else {
                int cx = width / 2, cy = height / 2;
                int raio = Math.min(width, height) / 2 - 60;
                for (int i = 0; i < vertices.size(); i++) {
                    double angulo = 2 * Math.PI * i / vertices.size();
                    posicoes.put(vertices.get(i), new Point((int)(cx + raio * Math.cos(angulo)), (int)(cy + raio * Math.sin(angulo))));
                }
            }

            // DESENHAR AS ARESTAS
            Set<String> desenhadas = new HashSet<>();
            for (String origem : vertices) {
                Point p1 = posicoes.get(origem);
                if (grafoAtivo.getAdjacencia().get(origem) == null) continue;

                for (Object aObj : grafoAtivo.getAdjacencia().get(origem)) {
                    String destino = null;
                    double peso = 0;
                    try {
                        destino = (String) aObj.getClass().getField("destino").get(aObj);
                        peso = (double) aObj.getClass().getField("peso").get(aObj);
                    } catch (Exception e) {}

                    Point p2 = posicoes.get(destino);
                    if (p2 == null) continue;

                    String chaveUnica = origem.compareTo(destino) < 0 ? origem + "-" + destino : destino + "-" + origem;
                    if (!grafoAtivo.isDirigido() && desenhadas.contains(chaveUnica)) continue;
                    desenhadas.add(chaveUnica);

                    // caminho do A*
                    boolean noCaminho = false;
                    for (int i = 0; i < caminhoAStar.size() - 1; i++) {
                        if ((caminhoAStar.get(i).equals(origem) && caminhoAStar.get(i+1).equals(destino)) ||
                                (!grafoAtivo.isDirigido() && caminhoAStar.get(i).equals(destino) && caminhoAStar.get(i+1).equals(origem))) {
                            noCaminho = true; break;
                        }
                    }

                    if (noCaminho) {
                        g2d.setColor(Color.RED);
                        g2d.setStroke(new BasicStroke(4));
                    } else {
                        g2d.setColor(Color.GRAY);
                        g2d.setStroke(new BasicStroke(1));
                    }

                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);


                    g2d.setColor(Color.DARK_GRAY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.drawString(String.valueOf(peso), (p1.x + p2.x) / 2, (p1.y + p2.y) / 2 - 5);
                }
            }

            // DESENHAR OS VÉRTICES
            for (String v : vertices) {
                Point p = posicoes.get(v);

                // Pinta com a cor do DSATUR
                if (coresDsatur.containsKey(v) && coresDsatur.get(v) >= 0) {
                    g2d.setColor(paleta[coresDsatur.get(v) % paleta.length]);
                } else {
                    g2d.setColor(new Color(70, 130, 180));
                }

                g2d.fillOval(p.x - RAIO_VERTICE, p.y - RAIO_VERTICE, RAIO_VERTICE * 2, RAIO_VERTICE * 2);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(p.x - RAIO_VERTICE, p.y - RAIO_VERTICE, RAIO_VERTICE * 2, RAIO_VERTICE * 2);

                g2d.setColor(Color.BLACK);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(v, p.x - fm.stringWidth(v) / 2, p.y + RAIO_VERTICE + 15);
            }
        }
    }
}
//