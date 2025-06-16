package main;

// NENHUMA DECLARAÇÃO DE "package" AQUI

import javax.swing.SwingUtilities;

import model.Game; // Importa do pacote 'model'
import view.JanelaXadrez; // Importa do pacote 'view'
import controller.XadrezController; // Importa do pacote 'controller'

public class App {
    public static void main(String[] args){
        // Garante que a GUI seja criada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Cria o Model
                Game gameModel = new Game();
                
                // 2. Cria a View
                JanelaXadrez gameView = new JanelaXadrez();
                
                // 3. Cria o Controller e conecta Model e View
                new XadrezController(gameModel, gameView);
            }
        });
    }
}