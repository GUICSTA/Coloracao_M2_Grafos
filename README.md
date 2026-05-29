Implementar os algoritmos:
1) para colorir um grafo (4,0): DSATUR;
2) para caminhos mínimos (6,0): A*.
Eles podem ser incluídos na ferramenta criada no T1 ou em novo sistema.

Requisitos:
Para testar o algoritmo de A* use o mapa abaixo. Para efeito de agilizar a entrada de dados no momento
da apresentação do trabalho, as coordenadas do mapa podem estar no código, ou gravadas em arquivo
e serem recuperadas. Mas o sistema deverá permitir testar qualquer grafo.
No A* os valores da h(n) deverão ser calculadas pelo sistema via distância de Manhattan (distância em
linha reta entre cidades C1(x1, y1) e C2(x2, y2) = |x1–x2| + |y1–y2|) e sempre deve ser montada em
relação à cidade destino do caminho. A Tabela h(n) abaixo foi calculada tendo Cascavel como destino.
Assim, quando a consulta do caminho mínimo tiver outra cidade destino, esta tabela deverá ser
recalculada e reapresentada na tela do sistema. Logo, pesquisem as coordenadas geográficas (latitude,
longitude) das cidades do mapa abaixo, tais informações devem fazer parte dos vértices do grafo.
Todos os resultados deverão ser apresentados de maneira gráfica, ou seja, o sistema deverá “desenhar”
o grafo original e o grafo colorido após execução da coloração, e o grafo original deverá ter o caminho
mínimo em destaque (arestas pintadas ou de formato diferente) após a execução do A*. Caso isso não
ocorra, haverá desconto de 2,0 pontos da nota final. 
