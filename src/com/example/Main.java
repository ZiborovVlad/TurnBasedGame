package com.example;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Главный класс для запуска новой игры и запуска приложения, строящего графики ресурсов.
 */
public class Main extends Application {
    /**
     * Стандартный конструктор по умолчанию
     */
    public Main(){
    }
    private static final Logger Logger = LogManager.getLogger(Main.class);

    /**
     * Метод для запуска графического приложения.
     * @param args - программные аргументы
     */
    public static void main(String[] args){
        Logger.info("Запуск приложения");
        launch(args);
    }

    /**
     * Метод для запуска всей программы.
     * @param stage - холст для отображения графиков.
     */
    @Override
    public void start(Stage stage){
        System.out.println("Добро пожаловать в пошаговую игру!\nСправа снизу - Вы, а слева сверху - ИИ.\nПравила следующие:\nНабрать воды: +5 ед. воды\nПолить рис стоит 3 ед. воды, при этом за день вырастет в 2 раза больше риса.\nПостроить дом стоит 3ед. воды, 5 ед. риса, 1 крестьянин\nЗахватить территорию - необходимо иметь не меньше крестьян, чем значение клетки.\nВ конце дня:\nВырастет 5(либо 10) ед. риса\nСгенерируется количество керстьян, равное количеству построенных домов.\nВаша цель - захватить более 50% территории!\nПриятной игры!");
        try {
        Game game = new Game(4); // Игровое поле размером 10x10
        Stage stage1 = new Stage();
        Stage stage2 = new Stage();
        Stage stage3 = new Stage();
        boolean runner = true;
        while (runner) {
            game.printMap(); // Печать карты
            if (!game.startDay()) { // Начало дня
                // Печать состояния игрока
                System.out.println(game.player.getName() + "'s resources: " + game.player.getRice() + " rice, " + game.player.getWater() + " water, " + game.player.getPeasants() + " peasants, " + game.player.getHouses() + " houses.");
                System.out.println(game.ai.getName() + "'s resources: " + game.ai.getRice() + " rice, " + game.ai.getWater() + " water, " + game.ai.getPeasants() + " peasants, " + game.ai.getHouses() + " houses.");
                Logger.info("День обработан, данные записаны в файл.");
            } else {
                runner = false;
                Logger.info("Приложение завершило работу.");
                Logger.info("Построение графиков игровых ресурсов по дням.");
                game.start(stage1, stage2, stage3);
            }
        }
        } catch (Exception e){
            Logger.error("Ошибка в работе программы.");
            System.exit(0);
        }
    }
}
