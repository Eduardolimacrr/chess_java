
# ♟️ Jogo de Xadrez Completo em Java

Este é um projeto de um jogo de xadrez completo, desenvolvido em Java com a biblioteca gráfica Swing. O software foi construído seguindo a arquitetura MVC (Model-View-Controller) e inclui funcionalidades avançadas como múltiplos modos de jogo (incluindo IA e rede), regras completas de xadrez, persistência de dados e ranking de jogadores.

> [cite\_start]**Objetivo Original do Projeto:** Desenvolver um jogo de xadrez em Java com interface gráfica usando Swing, estruturado na arquitetura MVC (Model-View-Controller), com regras simples, sem roque, xeque, xeque-mate, e com persistência do estado do jogo e ranking de jogadores[cite: 2].
>
> [cite\_start]**Resultado Final:** O projeto final superou o objetivo inicial, implementando com sucesso não apenas os requisitos básicos, mas também todas as funcionalidades complexas opcionais, incluindo as regras completas, IA e jogo em rede[cite: 20].

## Screenshot

![image](https://github.com/user-attachments/assets/71f95676-cfa4-4a24-b902-f587e17660e2)

## ✨ Funcionalidades

O jogo conta com um conjunto completo de funcionalidades, desde as regras oficiais do xadrez até modos de jogo avançados.

### Regras e Mecânicas de Jogo

  - **Movimentação Completa:** Todas as peças se movem de acordo com as regras oficiais do xadrez.
  - [cite\_start]**Regras Essenciais:** Implementação completa de **Xeque**, **Xeque-mate** e **Empate** (Afogamento)[cite: 20].
  - **Movimentos Especiais:**
      - [cite\_start]**Roque** (maior e menor)[cite: 20].
      - [cite\_start]Captura ***En Passant***[cite: 20].
      - [cite\_start]**Promoção de Peão** com opção de escolha (Dama, Torre, Bispo ou Cavalo)[cite: 20].

### Modos de Jogo

  - **Jogador vs. Jogador:** Modo clássico para dois jogadores no mesmo computador.
  - [cite\_start]**Jogador vs. IA (BOT):** Jogue contra um oponente controlado pelo computador[cite: 20].
      - [cite\_start]A IA utiliza o algoritmo **Minimax** com a otimização de **Poda Alfa-Beta** para calcular suas jogadas[cite: 20].
  - [cite\_start]**Jogo em Rede (Sockets):** Dispute partidas com outro jogador em uma rede local[cite: 20].
  - [cite\_start]**Controles de Tempo:** Selecione modos de jogo com tempo, como **Bullet (1 min)**, **Blitz (5 min)** e **Rápida (10 min)**[cite: 20].

### Funcionalidades Adicionais

  - [cite\_start]**Persistência de Jogo:** Salve o estado de uma partida a qualquer momento e carregue-a mais tarde para continuar jogando[cite: 5].
  - [cite\_start]**Ranking de Vitórias:** Um ranking de jogadores é mantido e salvo em um arquivo, registrando o número de vitórias de cada jogador[cite: 6].
  - [cite\_start]**Interface Gráfica Intuitiva:** Interface limpa construída com **Java Swing**, com destaque visual para peças selecionadas e mensagens de status claras[cite: 8, 9].

## 🏛️ Arquitetura do Projeto

[cite\_start]O software foi rigorosamente estruturado seguindo o padrão de arquitetura **MVC (Model-View-Controller)** para garantir a separação de responsabilidades, escalabilidade e manutenibilidade do código[cite: 2].

  * **`model`**: Contém toda a lógica de negócios e as regras do jogo. É o "cérebro" da aplicação. [cite\_start]Ele não tem conhecimento da interface gráfica[cite: 13].
      * Classes principais: `Game`, `Tabuleiro`, `Peca` (e suas subclasses), `Ranking`.
  * **`view`**: Responsável por toda a apresentação visual e pela interface com o usuário. [cite\_start]Ela apenas exibe os dados fornecidos pelo modelo e captura as interações do usuário[cite: 13].
      * Classe principal: `JanelaXadrez`.
  * [cite\_start]**`controller`**: Atua como o intermediário, recebendo as ações do usuário (da `View`), processando-as, atualizando o `Model` e, em seguida, atualizando a `View` com o novo estado[cite: 13].
      * Classe principal: `XadrezController`.
  * **`ai`**: Pacote dedicado para a Inteligência Artificial, contendo a lógica do Minimax e a função de avaliação.
  * **`network`**: Pacote dedicado para a funcionalidade de jogo em rede, gerenciando os Sockets e a comunicação.

[cite\_start]Esta arquitetura cumpre os requisitos de ter ao menos uma **interface** definida pelo usuário (`NetworkManager.NetworkListener`) e **herança** (`Peca` e suas subclasses)[cite: 19].

## 🛠️ Tecnologias Utilizadas

  * [cite\_start]**Linguagem:** Java 8 ou superior[cite: 12].
  * [cite\_start]**Interface Gráfica:** Java Swing[cite: 12].

## 🚀 Como Compilar e Executar

Siga as instruções abaixo para rodar o projeto.

### Pré-requisitos

  - **JDK (Java Development Kit)** 8 ou superior instalado e configurado no sistema.

### Estrutura de Arquivos

Para que os ícones das peças sejam exibidos corretamente, a estrutura de pastas do seu projeto deve conter uma pasta `resources/assets` dentro de `src`, como no exemplo abaixo:

```
seu-projeto/
└── src/
    ├── App.java
    ├── ai/
    ├── controller/
    ├── model/
    ├── network/
    ├── view/
    │
    └── resources/
        └── assets/
            ├── rei_branco.png
            ├── dama_branca.png
            └── ... (todas as 12 imagens .png)
```

### Executando a partir de um IDE (Recomendado)

1.  Abra o projeto em um IDE como **VS Code**, **Eclipse** ou **IntelliJ IDEA**.
2.  Certifique-se de que o IDE reconheceu o projeto como um projeto Java.
3.  Localize o arquivo `App.java`.
4.  Clique com o botão direito sobre ele e selecione a opção "Run" ou "Run Java".

### Executando via Linha de Comando

1.  Navegue até a pasta `src` do seu projeto pelo terminal.
2.  Compile todos os arquivos `.java` para a pasta `bin` (crie a pasta `bin` se não existir):
    ```bash
    javac -d ../bin $(find . -name "*.java")
    ```
3.  Copie a pasta `resources` para dentro da pasta `bin`:
    ```bash
    cp -r resources ../bin/
    ```
4.  Navegue para a pasta `bin` e execute a aplicação:
    ```bash
    cd ../bin
    java App
    ```

## 📖 Como Jogar

1.  Execute a aplicação. A janela principal aparecerá.
2.  Vá ao menu **"Jogo"** na barra superior.
3.  Escolha um modo de jogo:
      * **"Novo Jogo (Jogador vs Jogador)"**: Inicia uma partida local para dois jogadores.
      * **"Jogar contra IA"**: Inicia uma partida contra o computador. Você poderá escolher jogar de Brancas ou Pretas.
      * **"Jogo em Rede..."**: Permite criar um jogo (Servidor) ou se conectar a um jogo existente (Cliente) na rede local.
4.  Para os modos com tempo, uma caixa de diálogo permitirá a seleção da duração da partida.
5.  Use o menu "Jogo" para **Salvar**, **Carregar** uma partida ou ver o **Ranking**.
