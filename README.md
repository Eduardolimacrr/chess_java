
# ♟️ Jogo de Xadrez Completo em Java

Este é um projeto de um jogo de xadrez completo, desenvolvido em Java com a biblioteca gráfica Swing. O software foi construído seguindo a arquitetura MVC (Model-View-Controller) e inclui funcionalidades avançadas como múltiplos modos de jogo (incluindo IA e rede), regras completas de xadrez, persistência de dados e ranking de jogadores.

## Screenshot

<p align="center">
  <img src="![image](https://github.com/user-attachments/assets/8349556d-0b4c-4062-83c8-22457eb13ae9)
"/>
</p>

## ✨ Funcionalidades

O jogo conta com um conjunto completo de funcionalidades, desde as regras oficiais do xadrez até modos de jogo avançados.

### Regras e Mecânicas de Jogo

  - **Movimentação Completa:** Todas as peças se movem de acordo com as regras oficiais do xadrez.
  -**Regras Essenciais:** Implementação completa de **Xeque**, **Xeque-mate** e **Empate** (Afogamento).
  - **Movimentos Especiais:**
      - **Roque** (maior e menor).
      - Captura ***En Passant***.
      - **Promoção de Peão** com opção de escolha (Dama, Torre, Bispo ou Cavalo).

### Modos de Jogo

  - **Jogador vs. Jogador:** Modo clássico para dois jogadores no mesmo computador.
  -**Jogador vs. IA (BOT):** Jogue contra um oponente controlado pelo computador.
  - A IA utiliza o algoritmo **Minimax** com a otimização de **Poda Alfa-Beta** para calcular suas jogadas.
  -**Jogo em Rede (Sockets):** Dispute partidas com outro jogador em uma rede local.
  -**Controles de Tempo:** Selecione modos de jogo com tempo, como **Bullet (1 min)**, **Blitz (5 min)** e **Rápida (10 min)**.

### Funcionalidades Adicionais

  -**Persistência de Jogo:** Salve o estado de uma partida a qualquer momento e carregue-a mais tarde para continuar jogando.
  -**Ranking de Vitórias:** Um ranking de jogadores é mantido e salvo em um arquivo, registrando o número de vitórias de cada jogador.
  -**Interface Gráfica Intuitiva:** Interface limpa construída com **Java Swing**, com destaque visual para peças selecionadas e mensagens de status claras.

## 🏛️ Arquitetura do Projeto

O software foi rigorosamente estruturado seguindo o padrão de arquitetura **MVC (Model-View-Controller)** para garantir a separação de responsabilidades, escalabilidade e manutenibilidade do código.

  * **`model`**: Contém toda a lógica de negócios e as regras do jogo. É o "cérebro" da aplicação.Ele não tem conhecimento da interface gráfica.
      * Classes principais: `Game`, `Tabuleiro`, `Peca` (e suas subclasses), `Ranking`.
  * **`view`**: Responsável por toda a apresentação visual e pela interface com o usuário.Ela apenas exibe os dados fornecidos pelo modelo e captura as interações do usuário.
      * Classe principal: `JanelaXadrez`.
  * **`controller`**: Atua como o intermediário, recebendo as ações do usuário (da `View`), processando-as, atualizando o `Model` e, em seguida, atualizando a `View` com o novo estado.
      * Classe principal: `XadrezController`.
  * **`ai`**: Pacote dedicado para a Inteligência Artificial, contendo a lógica do Minimax e a função de avaliação.
  * **`network`**: Pacote dedicado para a funcionalidade de jogo em rede, gerenciando os Sockets e a comunicação.


## 🛠️ Tecnologias Utilizadas

  * **Linguagem:** Java 8 ou superior.
  * **Interface Gráfica:** Java Swing.

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
