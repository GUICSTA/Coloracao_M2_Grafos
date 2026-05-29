import java.util.*;

class Aresta {
    public String id;
    public String destino;
    public double peso;

    public Aresta(String id, String destino, double peso) {
        this.id = id;
        this.destino = destino;
        this.peso = peso;
    }
}

public class Grafo {
    private boolean dirigido;
    private Map<String, LinkedList<Aresta>> adjacencia;

    public Map<String, double[]> coordenadas = new HashMap<>();

    public void setCoordenada(String vertice, double lat, double lon) {
        coordenadas.put(vertice, new double[]{lat, lon});
    }

    //Distância de Manhattan (|x1-x2| + |y1-y2|)
    public double manhattan(String a, String b) {
        double[] c1 = coordenadas.get(a);
        double[] c2 = coordenadas.get(b);
        if (c1 == null || c2 == null) return 0; // sem coordenada
        return Math.abs(c1[0] - c2[0]) + Math.abs(c1[1] - c2[1]);
    }

    // DSATUR (Coloração de Grafos)
    public Map<String, Integer> dsatur() {
        Map<String, Integer> cores = new HashMap<>();
        Map<String, Set<Integer>> coresAdjacentes = new HashMap<>();
        List<String> naoColoridos = new ArrayList<>(getVertices());

        for (String v : getVertices()) {
            cores.put(v, -1);
            coresAdjacentes.put(v, new HashSet<>());
        }

        while (!naoColoridos.isEmpty()) {
            // Escolhe o vértice com maior grau de saturação
            String escolhido = naoColoridos.get(0);
            int maxSaturacao = -1;
            int maxGrau = -1;

            for (String v : naoColoridos) {
                int saturacao = coresAdjacentes.get(v).size();
                int grau = (adjacencia.get(v) != null) ? adjacencia.get(v).size() : 0;

                if (saturacao > maxSaturacao || (saturacao == maxSaturacao && grau > maxGrau)) {
                    maxSaturacao = saturacao;
                    maxGrau = grau;
                    escolhido = v;
                }
            }

            // menor cor
            int cor = 0;
            while (coresAdjacentes.get(escolhido).contains(cor)) {
                cor++;
            }

            cores.put(escolhido, cor);
            naoColoridos.remove(escolhido);

            // Atualiza a saturação dos vizinhos
            if (adjacencia.get(escolhido) != null) {
                for (Aresta a : adjacencia.get(escolhido)) {
                    coresAdjacentes.get(a.destino).add(cor);
                }
            }
        }
        return cores;
    }

    // A* (Caminho Mínimo
    public List<String> aStar(String inicio, String destino, StringBuilder relatorio) {
        // Tabela h(n)
        relatorio.append("--- Tabela h(n) em relação a ").append(destino).append(" ---\n");
        for (String v : getVertices()) {
            relatorio.append(v).append(": ").append(String.format(Locale.US, "%.2f", manhattan(v, destino))).append("\n");
        }
        relatorio.append("------------------------------------------\n");

        Map<String, Double> gScore = new HashMap<>();
        Map<String, Double> fScore = new HashMap<>();
        Map<String, String> cameFrom = new HashMap<>();

        for (String v : getVertices()) {
            gScore.put(v, Double.MAX_VALUE);
            fScore.put(v, Double.MAX_VALUE);
        }

        gScore.put(inicio, 0.0);
        fScore.put(inicio, manhattan(inicio, destino));

        PriorityQueue<String> openSet = new PriorityQueue<>(Comparator.comparingDouble(fScore::get));
        openSet.add(inicio);

        while (!openSet.isEmpty()) {
            String atual = openSet.poll();

            if (atual.equals(destino)) {
                List<String> caminho = new ArrayList<>();
                while (cameFrom.containsKey(atual)) {
                    caminho.add(atual);
                    atual = cameFrom.get(atual);
                }
                caminho.add(inicio);
                Collections.reverse(caminho);
                relatorio.append("Custo Total (Distância real percorrida): ").append(gScore.get(destino)).append("\n");
                return caminho;
            }

            if (adjacencia.get(atual) != null) {
                for (Aresta a : adjacencia.get(atual)) {
                    double pesoReal = a.peso;
                    double tentativaG = gScore.get(atual) + pesoReal;

                    if (tentativaG < gScore.get(a.destino)) {
                        cameFrom.put(a.destino, atual);
                        gScore.put(a.destino, tentativaG);
                        fScore.put(a.destino, tentativaG + manhattan(a.destino, destino));

                        if (!openSet.contains(a.destino)) {
                            openSet.add(a.destino);
                        }
                    }
                }
            }
        }
        relatorio.append("Caminho não encontrado!\n");
        return new ArrayList<>();
    }


    //T1

    public Grafo(boolean dirigido) {
        this.dirigido = dirigido;
        this.adjacencia = new LinkedHashMap<>();
    }

    public void inserirVertice(String v) {
        adjacencia.putIfAbsent(v, new LinkedList<>());
    }

    public String inserirAresta(String id, String origem, String destino, double peso) {
        if (peso < 0) {
            return "Erro: Aresta " + id + " possui peso negativo.";
        }
        for (LinkedList<Aresta> lista : adjacencia.values()) {
            for (Aresta a : lista) {
                if (a.id.equals(id)) {
                    return "Erro: Aresta com ID '" + id + "' já inserida.";
                }
            }
        }
        if (adjacencia.containsKey(origem) && adjacencia.containsKey(destino)) {
            adjacencia.get(origem).add(new Aresta(id, destino, peso));
            if (!dirigido) {
                adjacencia.get(destino).add(new Aresta(id, origem, peso));
            }
            return "Aresta '" + id + "' inserida: [" + origem + " -> " + destino + "] Peso: " + peso;
        } else {
            return "Erro: Vértice de origem ou destino não existe.";
        }
    }

    public void removerVertice(String v) {
        adjacencia.remove(v);
        for (LinkedList<Aresta> conexoes : adjacencia.values()) {
            conexoes.removeIf(a -> a.destino.equals(v));
        }
    }

    public boolean removerAresta(String id) {
        boolean removido = false;
        for (LinkedList<Aresta> conexoes : adjacencia.values()) {
            if (conexoes.removeIf(a -> a.id.equals(id))) {
                removido = true;
            }
        }
        return removido;
    }

    public boolean isDirigido() {
        return dirigido;
    }

    public Set<String> getVertices() {
        return adjacencia.keySet();
    }

    public Map<String, LinkedList<Aresta>> getAdjacencia() {
        return adjacencia;
    }

    public String prim() {
        StringBuilder sb = new StringBuilder();
        if(isDirigido() == false) {
            if (adjacencia.isEmpty()) return "Grafo vazio.\n";

            String inicio = adjacencia.keySet().iterator().next();
            PriorityQueue<ArestaPrim> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a.peso));
            Set<String> visitados = new HashSet<>();
            double custoTotal = 0;

            visitados.add(inicio);
            for (Aresta a : adjacencia.get(inicio))
                pq.add(new ArestaPrim(a.id, inicio, a.destino, a.peso));

            sb.append("\n--- Relatório: Árvore Geradora Mínima (Prim) ---\n");
            while (!pq.isEmpty() && visitados.size() < adjacencia.size()) {
                ArestaPrim ap = pq.poll();
                if (visitados.contains(ap.destino)) continue;

                visitados.add(ap.destino);
                custoTotal += ap.peso;
                sb.append("Aresta: ").append(ap.id).append(" [").append(ap.origem).append(" - ").append(ap.destino).append("] Peso: ").append(ap.peso).append("\n");

                for (Aresta a : adjacencia.get(ap.destino)) {
                    if (!visitados.contains(a.destino)) {
                        pq.add(new ArestaPrim(a.id, ap.destino, a.destino, a.peso));
                    }
                }
            }
            sb.append("Custo Total da AGM: ").append(custoTotal).append("\n");
        }else{
            sb.append("Grafo dirigido não tem como executar o algoritmo de Prim!!");
        }
        return sb.toString();
    }

    public Grafo gerarGrafoPrim() {
        if (adjacencia.isEmpty()) return null;

        Grafo agm = new Grafo(false);
        for (String v : adjacencia.keySet()) agm.inserirVertice(v);

        String inicio = adjacencia.keySet().iterator().next();
        PriorityQueue<ArestaPrim> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a.peso));
        Set<String> visitados = new HashSet<>();

        visitados.add(inicio);
        for (Aresta a : adjacencia.get(inicio))
            pq.add(new ArestaPrim(a.id, inicio, a.destino, a.peso));

        while (!pq.isEmpty() && visitados.size() < adjacencia.size()) {
            ArestaPrim ap = pq.poll();
            if (visitados.contains(ap.destino)) continue;

            visitados.add(ap.destino);
            agm.inserirAresta(ap.id, ap.origem, ap.destino, ap.peso);

            for (Aresta a : adjacencia.get(ap.destino)) {
                if (!visitados.contains(a.destino)) {
                    pq.add(new ArestaPrim(a.id, ap.destino, a.destino, a.peso));
                }
            }
        }
        return agm;
    }

    public String bfs(String start, String end) {
        if (!adjacencia.containsKey(start) || !adjacencia.containsKey(end))
            return "Vértice de início ou fim não encontrado.\n";

        Queue<String> fila = new LinkedList<>();
        Map<String, String> pred = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        fila.add(start);
        visitados.add(start);
        boolean encontrou = false;

        while (!fila.isEmpty()) {
            String u = fila.poll();
            if (u.equals(end)) { encontrou = true; break; }

            for (Aresta a : adjacencia.get(u)) {
                if (!visitados.contains(a.destino)) {
                    visitados.add(a.destino);
                    pred.put(a.destino, u);
                    fila.add(a.destino);
                }
            }
        }
        return reconstruirCaminho("BFS", start, end, encontrou, pred);
    }

    public String dfs(String start, String end) {
        if (!adjacencia.containsKey(start) || !adjacencia.containsKey(end))
            return "Vértice de início ou fim não encontrado.\n";
        Map<String, String> pred = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        boolean encontrou = dfsRecursivo(start, end, visitados, pred);
        return reconstruirCaminho("DFS (Busca em Profundidade)", start, end, encontrou, pred);
    }

    private boolean dfsRecursivo(String u, String end, Set<String> vis, Map<String, String> pred) {
        vis.add(u);
        if (u.equals(end)) return true;
        for (Aresta a : adjacencia.get(u)) {
            if (!vis.contains(a.destino)) {
                pred.put(a.destino, u);
                if (dfsRecursivo(a.destino, end, vis, pred)) return true;
            }
        }
        return false;
    }

    public Grafo gerarGrafoDfs(String start, String end) {
        if (!adjacencia.containsKey(start) || !adjacencia.containsKey(end))
            return null;

        Map<String, String> pred = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        boolean encontrou = dfsRecursivo(start, end, visitados, pred);

        if (!encontrou) return null;

        Grafo caminhoDfs = new Grafo(this.dirigido);

        for (String v : adjacencia.keySet()) {
            caminhoDfs.inserirVertice(v);
        }

        String atual = end;
        while (atual != null && pred.containsKey(atual)) {
            String pai = pred.get(atual);

            for (Aresta a : adjacencia.get(pai)) {
                if (a.destino.equals(atual)) {
                    caminhoDfs.inserirAresta(a.id, pai, atual, a.peso);
                    break;
                }
            }
            atual = pai;
        }

        return caminhoDfs;
    }

    public Grafo gerarArvoreBfsCompleta(String start) {
        if (!adjacencia.containsKey(start)) return null;

        Grafo arvoreBfs = new Grafo(this.dirigido);
        for (String v : adjacencia.keySet()) arvoreBfs.inserirVertice(v);

        Queue<String> fila = new LinkedList<>();
        Set<String> visitados = new HashSet<>();

        fila.add(start);
        visitados.add(start);

        while (!fila.isEmpty()) {
            String u = fila.poll();

            for (Aresta a : adjacencia.get(u)) {
                if (!visitados.contains(a.destino)) {
                    visitados.add(a.destino);
                    arvoreBfs.inserirAresta(a.id, u, a.destino, a.peso);
                    fila.add(a.destino);
                }
            }
        }
        return arvoreBfs;
    }

    public Grafo gerarArvoreDfsCompleta(String start) {
        if (!adjacencia.containsKey(start)) return null;

        Grafo arvoreDfs = new Grafo(this.dirigido);

        for (String v : adjacencia.keySet()) {
            arvoreDfs.inserirVertice(v);
        }

        Set<String> visitados = new HashSet<>();
        dfsArvoreRecursivo(start, visitados, arvoreDfs);

        return arvoreDfs;
    }

    private void dfsArvoreRecursivo(String u, Set<String> vis, Grafo arvore) {
        vis.add(u);

        if (adjacencia.get(u) != null) {
            for (Aresta a : adjacencia.get(u)) {
                if (!vis.contains(a.destino)) {
                    arvore.inserirAresta(a.id, u, a.destino, a.peso);
                    dfsArvoreRecursivo(a.destino, vis, arvore);
                }
            }
        }
    }

    public String roy() {
        if (adjacencia.isEmpty()) return "Grafo vazio.\n";
        List<String> vList = new ArrayList<>(adjacencia.keySet());
        int n = vList.size();
        boolean[][] alcance = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            alcance[i][i] = true;
            for (Aresta a : adjacencia.get(vList.get(i))) {
                alcance[i][vList.indexOf(a.destino)] = true;
            }
        }

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    alcance[i][j] = alcance[i][j] || (alcance[i][k] && alcance[k][j]);

        Set<Set<String>> componentes = new HashSet<>();
        for (int i = 0; i < n; i++) {
            Set<String> comp = new TreeSet<>();
            for (int j = 0; j < n; j++) {
                if (dirigido ? (alcance[i][j] && alcance[j][i]) : alcance[i][j]) {
                    comp.add(vList.get(j));
                }
            }
            componentes.add(comp);
        }
        return "\n--- Componentes (Roy) ---\nConjuntos: " + componentes + "\n";
    }

    private List<String> ultimoCaminhoBfs = new ArrayList<>();

    public List<String> getUltimoCaminhoBfs() {
        return ultimoCaminhoBfs;
    }

    private String reconstruirCaminho(String t, String s, String e, boolean achou, Map<String, String> p) {
        ultimoCaminhoBfs.clear();
        if (!achou) return "\n--- " + t + " ---\nCaminho não existe entre " + s + " e " + e + "\n";

        List<String> path = new ArrayList<>();
        for (String at = e; at != null; at = p.get(at)) path.add(at);
        Collections.reverse(path);

        if (t.equals("BFS")) {
            this.ultimoCaminhoBfs = new ArrayList<>(path);
        }

        return "\n--- " + t + " ---\nCaminho: " + String.join(" -> ", path) + "\n";
    }

    private static class ArestaPrim {
        String id, origem, destino;
        double peso;
        ArestaPrim(String id, String o, String d, double p) {
            this.id = id; this.origem = o; this.destino = d; this.peso = p;
        }
    }
}
