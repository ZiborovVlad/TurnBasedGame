
package com.example;

import java.io.*;
import java.util.*;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс для обработки игровых событий.
 * У пользователя есть выбор - начать новую игру или загрузить последнюю из файла.
 * Инициализируется карта, 2 игрока и задаются начальные параметры.
 * В начале дня игроки своершают свои ходы по очереди, по окончанию дня растет рис и создаются жители.
 * Состояние игры записывается в файл, предварительно его очистив в конце каждого дня.
 * Реализована функция отрисовки графиков состояния ресурсов игроков по дням.
 */
public class Game {
    private int[][] map; // Игровое поле
    /** Создание объекта Игрок */
    protected Player player; // Игрок
    /** Создание объекта Искуственный интеллект */
    protected Player ai; // AI игрок
    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Конструктор класса.
     * Выбор на основе ввода с клавиатуры - начать нову игру или зайгрузить из файла.
     * @param mapSize - размер игрового поля.
     */
    public Game(int mapSize) {
        this.map = new int[mapSize][mapSize];
        this.player = new Player("Player");
        this.ai = new Player("AI");

        Scanner num = new Scanner(System.in);
        System.out.println("Выберите действие:\n1) Загрузить игру из файла \n2) Начать новую игру\n");
        int loader = num.nextInt();
        switch(loader){
            case 1:
                // Считывание карты
                ReadMap();
                // Считывание данных об игроках
                player.ReadTerritory();
                player.ReadResources();
                ai.ReadTerritory();
                ai.ReadResources();
                player.setCords();
                ai.setCords();
                break;
            case 2:
                // Инициализация карты
                generateMap();
                // Расстановка игроков
                player.setStartPos();
                ai.setStartPos();
                // Запись начальных ресурсов в данные о ресурсах
                player.addRiceValue(player.getRice());
                player.addWaterValue(player.getWater());
                player.addPeasantsValue(player.getPeasants());
                player.addHousesValue(player.getHouses());
                player.addTerritoryAmountValue(player.getTerritoryCount());
                ai.addRiceValue(ai.getRice());
                ai.addWaterValue(ai.getWater());
                ai.addPeasantsValue(ai.getPeasants());
                ai.addHousesValue(ai.getHouses());
                ai.addTerritoryAmountValue(ai.getTerritoryCount());
                break;
        }

    }

    /**
     * Генерация карты.
     * Заполнение клеток случайными величинами от 1 до 5
     * Координаты начальных позиций игроков будут равны 0.
     * Запись карты в файл.
     */
    private void generateMap() {
        Random rand = new Random();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                // Заполняем карту значениями от 1 до 5
                map[i][j] = rand.nextInt(5) + 1;
            }
        }
        map[0][0] = 0;
        map[map.length-1][map.length-1]=0;
        // Запись карты в файл
        try (PrintWriter writer = new PrintWriter(new FileWriter("map.txt"))) {
            for (int[] row : map) {
                for (int value : row) {
                    writer.print(value + " ");
                }
                writer.println(); // Переход на новую строку после каждой строки массива
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Считывание карты из файла
     */
    public void ReadMap(){
        File file = new File("map.txt");
        if(file.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader("map.txt"))) {
                String line;
                int row = 0;
                while ((line = reader.readLine()) != null && row < map.length) {
                    // Разбиваем строку на элементы
                    String[] values = line.split(" ");

                    // Заполняем строки массива
                    for (int col = 0; col < values.length; col++) {
                        map[row][col] = Integer.parseInt(values[col]);
                    }
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод, реализующий все события за день.
     * Игрок совершает ход, после совершает ход ИИ.
     * Если игрок или ИИ хочет занять новую территорию, описан механизм захвата и перемещения по карте.
     * Совершается пополнение ресурсов.
     * Производится подсчет занятой территории и проверка на победу.
     * Все параметры ресурсов обоих игроков записываются в файл.
     * @return checkVictory(); - возвращает значение true, если игра окончена, false, если победителя еще нет.
     */
    public boolean startDay() {
        // Ход игрока

        player.startTurn();

        // Захват территории игроком

        if(player.Conquer()) {
            boolean filled = true;
            while (filled) {
                Scanner in = new Scanner(System.in);
                System.out.println("Choose the side: \n1) Go right\n2) Go Left\n3) Go Up\n4) Go Down");
                int choose = in.nextInt();
                int PlayerCurrPeasants = player.getPeasants();
                Integer PlayerRightPos = (player.getCords()[0]+1<map.length)? map[player.getCords()[1]][player.getCords()[0] + 1]: null;
                Integer PlayerLeftPos = (player.getCords()[0]-1>=0)? map[player.getCords()[1]][player.getCords()[0] - 1]: null;
                Integer PlayerUpPos = (player.getCords()[1]-1>=0)? map[player.getCords()[1] - 1][player.getCords()[0]]: null;
                Integer PlayerDownPos = (player.getCords()[1]+1< map.length)? map[player.getCords()[1] + 1][player.getCords()[0]]: null;

                switch (choose) {
                    case 1:
                        if (!(player.getTerritory(player.getCords()[0] + 1, player.getCords()[1])) && (!(ai.getTerritory(player.getCords()[0] + 1, player.getCords()[1])))) {
                            if (PlayerRightPos <= PlayerCurrPeasants) {
                                filled = false;
                                player.goRight();
                                player.setPeasants(PlayerCurrPeasants - PlayerRightPos);
                                player.setTerritory(player.getCords()[0], player.getCords()[1]);
                                break;
                            } else {
                                System.out.println("You don't have enough peasants");
                                break;
                            }
                        } else if (!(ai.getTerritory(player.getCords()[0] + 1, player.getCords()[1]))) {
                            System.out.println("This territory has already been conquered!");
                            player.goRight();
                            printMap();
                            break;
                        }
                        else {
                            System.out.println("This territory has already been conquered!");
                            break;
                        }
                    case 2:
                        if (!(player.getTerritory(player.getCords()[0] - 1, player.getCords()[1])) && (!(ai.getTerritory(player.getCords()[0] - 1, player.getCords()[1])))) {
                            if (PlayerLeftPos <= PlayerCurrPeasants) {
                                filled = false;
                                player.goLeft();
                                player.setPeasants(PlayerCurrPeasants - PlayerLeftPos);
                                player.setTerritory(player.getCords()[0], player.getCords()[1]);
                                break;
                            } else {
                                System.out.println("You don't have enough peasants");
                                break;
                            }
                        } else if (!(ai.getTerritory(player.getCords()[0] - 1, player.getCords()[1]))) {
                            System.out.println("This territory has already been conquered!");
                            player.goLeft();
                            printMap();
                            break;
                        }
                        else {
                            System.out.println("This territory has already been conquered!");
                            break;
                        }
                    case 3:
                        if (!(player.getTerritory(player.getCords()[0], player.getCords()[1] - 1)) && (!(ai.getTerritory(player.getCords()[0], player.getCords()[1] - 1)))) {
                            if (PlayerUpPos <= PlayerCurrPeasants) {
                                filled = false;
                                player.goUp();
                                player.setPeasants(PlayerCurrPeasants - PlayerUpPos);
                                player.setTerritory(player.getCords()[0], player.getCords()[1]);
                                break;
                            } else {
                                System.out.println("You don't have enough peasants");
                                break;
                            }
                        } else if (!(ai.getTerritory(player.getCords()[0], player.getCords()[1] - 1))) {
                            System.out.println("This territory has already been conquered!");
                            player.goUp();
                            printMap();
                            break;
                        }
                        else {
                            System.out.println("This territory has already been conquered!");
                            break;
                        }
                    case 4:
                        if (!(player.getTerritory(player.getCords()[0], player.getCords()[1] + 1)) && (!(ai.getTerritory(player.getCords()[0], player.getCords()[1] + 1)))) {
                            if (PlayerDownPos <= PlayerCurrPeasants) {
                                filled = false;
                                player.goDown();
                                player.setPeasants(PlayerCurrPeasants - PlayerDownPos);
                                player.setTerritory(player.getCords()[0], player.getCords()[1]);
                                break;
                            } else {
                                System.out.println("You don't have enough peasants");
                                break;
                            }
                        } else if (!(ai.getTerritory(player.getCords()[0], player.getCords()[1]+1))) {
                            System.out.println("This territory has already been conquered!");
                            player.goDown();
                            printMap();
                            break;
                        }
                        else {
                            System.out.println("This territory has already been conquered!");
                            break;
                        }
                    default:
                        System.out.println("Incorrect number input");
                        continue;
                }

            }
        }
        // Ход ИИ
        ai.startTurn();

        // Захват территории ИИ
        if(ai.Conquer()) {
            boolean filled = true;
            while (filled) {
                Random rand = new Random();
                System.out.println("AI, Choose the side: \n1) Go right\n2) Go Left\n3) Go Up\n4) Go Down");
                int choose = rand.nextInt(4)+1;
                int AiCurrPeasants = ai.getPeasants();
                Integer AiRightPos = (ai.getCords()[0]+1<map.length)? map[ai.getCords()[1]][ai.getCords()[0] + 1]: null;
                Integer AiLeftPos = (ai.getCords()[0]-1>=0)? map[ai.getCords()[1]][ai.getCords()[0] - 1]: null;
                Integer AiUpPos = (ai.getCords()[1]-1>=0)? map[ai.getCords()[1] - 1][ai.getCords()[0]]: null;
                Integer AiDownPos = (ai.getCords()[1]+1<map.length)? map[ai.getCords()[1] + 1][ai.getCords()[0]]: null;

                switch (choose) {
                    case 1:
                        if(AiRightPos != null){
                            if (!(player.getTerritory(ai.getCords()[0] + 1, ai.getCords()[1])) && (!(ai.getTerritory(ai.getCords()[0] + 1, ai.getCords()[1])))) {
                                if (AiRightPos <= AiCurrPeasants) {
                                    filled = false;
                                    ai.goRight();
                                    ai.setPeasants(AiCurrPeasants - AiRightPos);
                                    ai.setTerritory(ai.getCords()[0], ai.getCords()[1]);
                                    break;
                                } else {
                                    System.out.println("Ai don't have enough peasants");
                                    break;
                                }
                            } else if (!(player.getTerritory(ai.getCords()[0] + 1, ai.getCords()[1]))) {
                                System.out.println("This territory has already been conquered!");
                                ai.goRight();
                                break;
                            }
                        } else { break; }
                    case 2:
                        if (AiLeftPos != null) {
                            if (!(player.getTerritory(ai.getCords()[0] - 1, ai.getCords()[1])) && (!(ai.getTerritory(ai.getCords()[0] - 1, ai.getCords()[1])))) {
                                if (AiLeftPos <= AiCurrPeasants) {
                                    filled = false;
                                    ai.goLeft();
                                    ai.setPeasants(AiCurrPeasants - AiLeftPos);
                                    ai.setTerritory(ai.getCords()[0], ai.getCords()[1]);
                                    break;
                                } else {
                                    System.out.println("Ai don't have enough peasants");
                                    break;
                                }
                            } else if (!(player.getTerritory(ai.getCords()[0] - 1, ai.getCords()[1]))) {
                                System.out.println("This territory has already been conquered!");
                                ai.goLeft();
                                break;
                            }
                        } else { break; }
                    case 3:
                        if (AiUpPos != null) {
                            if (!(player.getTerritory(ai.getCords()[0], ai.getCords()[1] - 1)) && (!(ai.getTerritory(ai.getCords()[0], ai.getCords()[1] - 1)))) {
                                if (AiUpPos <= AiCurrPeasants) {
                                    filled = false;
                                    ai.goUp();
                                    ai.setPeasants(AiCurrPeasants - AiUpPos);
                                    ai.setTerritory(ai.getCords()[0], ai.getCords()[1]);
                                    break;
                                } else {
                                    System.out.println("Ai don't have enough peasants");
                                    break;
                                }
                            } else if (!(player.getTerritory(ai.getCords()[0], ai.getCords()[1] - 1))) {
                                System.out.println("This territory has already been conquered!");
                                ai.goUp();
                                break;
                            }
                        } else { break; }
                    case 4:
                        if(AiDownPos != null) {
                            if (!(player.getTerritory(ai.getCords()[0], ai.getCords()[1] + 1)) && (!(ai.getTerritory(ai.getCords()[0], ai.getCords()[1] + 1)))) {
                                if (AiDownPos <= AiCurrPeasants) {
                                    filled = false;
                                    ai.goDown();
                                    ai.setPeasants(AiCurrPeasants - AiDownPos);
                                    ai.setTerritory(ai.getCords()[0], ai.getCords()[1]);
                                    break;
                                } else {
                                    System.out.println("Ai don't have enough peasants");
                                    break;
                                }
                            } else if (!(player.getTerritory(ai.getCords()[0], ai.getCords()[1] + 1))) {
                                System.out.println("This territory has already been conquered!");
                                ai.goDown();
                                break;
                            }
                        } else { break; }

                }

            }
        }

        // Рис растет и собирается
        player.collectRice();
        ai.collectRice();

        player.addRiceValue(player.getRice());
        ai.addRiceValue(ai.getRice());

        player.addWaterValue(player.getWater());
        ai.addWaterValue(ai.getWater());
        player.addHousesValue(player.getHouses());
        ai.addHousesValue(ai.getHouses());
        player.addTerritoryAmountValue(player.getTerritoryCount());
        ai.addTerritoryAmountValue(ai.getTerritoryCount());

        // Создание крестьян
        player.generatePeasants();
        ai.generatePeasants();
        player.addPeasantsValue(player.getPeasants());
        ai.addPeasantsValue(ai.getPeasants());

        // Производятся проверки победы
//        checkVictory();
        // Запись данных в файл
        player.WriteTerritory();
        player.WriteResources();
        ai.WriteTerritory();
        ai.WriteResources();
        return checkVictory();
    }

    /**
     * Метод для проверки на окончание игры.
     * @return - возвращает True, если один из игроков владеет более 50% территории, либо у обоих по 50%.
     */
    private boolean checkVictory() {
        int playerTerritory = player.getTerritoryCount();
        int aiTerritory = ai.getTerritoryCount();
        boolean TheEnd = false;
        int totalTerritory = map.length * map[0].length;

        if (playerTerritory > totalTerritory / 2) {
            System.out.println("Player wins!");
            TheEnd = true;
        } else if (aiTerritory > totalTerritory / 2) {
            System.out.println("AI wins!");
            TheEnd = true;
        } else if (aiTerritory==totalTerritory / 2 + 1 && playerTerritory == totalTerritory / 2 + 1) {
            System.out.println("Draw! Such a good game.");
            TheEnd = true;
        }
        return TheEnd;
    }

    /**
     * Вывод карты на экран.
     * В зависимости от принадлежности клетки карты игроку или ИИ, будут разные цветовые обозначения.
     * Если клетка принадлежит игроку - зеленый цвет.
     * Если принадлежит ИИ - синий.
     * Вся карта имеет белую рамку для лучшей видимости.
     * Иконки игроков на карте являются специальными символами.
     */
    public void printMap() {
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_CYAN = "\u001B[96m";
        final String ANSI_GREEN = "\u001B[92m";
        final String ANSI_WHITE_BACKGROUND = "\u001B[107m";
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (j==player.getCords()[0] && i==player.getCords()[1]){
                    System.out.print(ANSI_WHITE_BACKGROUND + ANSI_GREEN + "\u2740" + " " + ANSI_RESET);
                }
                else if (j==ai.getCords()[0] && i==ai.getCords()[1]){
                    System.out.print(ANSI_WHITE_BACKGROUND + ANSI_CYAN + "\u273E" + " " + ANSI_RESET);
                }
                else if (player.getTerritory(j, i)){
                    System.out.print(ANSI_WHITE_BACKGROUND + ANSI_GREEN + map[i][j] + " " + ANSI_RESET);
                } else if (ai.getTerritory(j, i)){
                    System.out.print(ANSI_WHITE_BACKGROUND + ANSI_CYAN + map[i][j] + " " + ANSI_RESET);
                } else {
                    System.out.print(ANSI_WHITE_BACKGROUND + ANSI_BLACK + map[i][j] + " " + ANSI_RESET);
                }
            }
            System.out.println();
        }
    }

    /**
     * Метод для создания графиков.
     * @param stage1 - сцена для графиков Воды и Риса Игрока и ИИ.
     * @param stage2 - сцена для графиков Крестьян Игрока и ИИ и Домов Игрока.
     * @param stage3 - сцена для графиков Домов ИИ и Территорий Игрока и ИИ.
     */
    public void start(Stage stage1, Stage stage2, Stage stage3){
        try {
            // Создание оси X и оси Y
            NumberAxis PlRice = new NumberAxis();
            NumberAxis AiRice = new NumberAxis();
            NumberAxis PlWater = new NumberAxis();
            NumberAxis AiWater = new NumberAxis();
            NumberAxis PlPeasants = new NumberAxis();
            NumberAxis AiPeasants = new NumberAxis();
            NumberAxis PlHouses = new NumberAxis();
            NumberAxis AiHouses = new NumberAxis();
            NumberAxis PlTerrAm = new NumberAxis();
            NumberAxis AiTerrAm = new NumberAxis();
            NumberAxis Days1 = new NumberAxis();
            NumberAxis Days2 = new NumberAxis();
            NumberAxis Days3 = new NumberAxis();
            NumberAxis Days4 = new NumberAxis();
            NumberAxis Days5 = new NumberAxis();
            NumberAxis Days6 = new NumberAxis();
            NumberAxis Days7 = new NumberAxis();
            NumberAxis Days8 = new NumberAxis();
            NumberAxis Days9 = new NumberAxis();
            NumberAxis Days10 = new NumberAxis();


            // Название осей
            PlRice.setLabel("Player's rice changing");
            AiRice.setLabel("Ai's rice changing");
            PlWater.setLabel("Player's water changing");
            AiWater.setLabel("Ai's water changing");
            PlPeasants.setLabel("Player's peasants changing");
            AiPeasants.setLabel("Ai's peasants changing");
            PlHouses.setLabel("Player's houses changing");
            AiHouses.setLabel("Ai's houses changing");
            PlTerrAm.setLabel("Player's territory amount changing");
            AiTerrAm.setLabel("Ai's territory amount changing");
            Days1.setLabel("Days");
            Days2.setLabel("Days");
            Days3.setLabel("Days");
            Days4.setLabel("Days");
            Days5.setLabel("Days");
            Days6.setLabel("Days");
            Days7.setLabel("Days");
            Days8.setLabel("Days");
            Days9.setLabel("Days");
            Days10.setLabel("Days");

            // Создание графиков с осью X и осью Y
            LineChart<Number, Number> PlayerRiceChanging = new LineChart<>(PlRice, Days1);
            LineChart<Number, Number> AiRiceChanging = new LineChart<>(AiRice, Days2);
            LineChart<Number, Number> PlayerWaterChanging = new LineChart<>(Days3, PlWater);
            LineChart<Number, Number> AiWaterChanging = new LineChart<>(Days4, AiWater);
            LineChart<Number, Number> PlayerPeasantsChanging = new LineChart<>(PlPeasants, Days5);
            LineChart<Number, Number> AiPeasantsChanging = new LineChart<>(AiPeasants, Days6);
            LineChart<Number, Number> PlayerHousesChanging = new LineChart<>(PlHouses, Days7);
            LineChart<Number, Number> AiHousesChanging = new LineChart<>(AiHouses, Days8);
            LineChart<Number, Number> PlayerTerrAmChanging = new LineChart<>(PlTerrAm, Days9);
            LineChart<Number, Number> AiTerrAmChanging = new LineChart<>(AiTerrAm, Days10);

            // Название графика
            PlayerRiceChanging.setTitle("График изменения риса Игрока");
            AiRiceChanging.setTitle("График изменения риса ИИ");
            PlayerWaterChanging.setTitle("График изменения воды Игрока");
            AiWaterChanging.setTitle("График изменения воды ИИ");
            PlayerPeasantsChanging.setTitle("График изменения крестьян Игрока");
            AiPeasantsChanging.setTitle("График изменения крестьян ИИ");
            PlayerHousesChanging.setTitle("График изменения домов Игрока");
            AiHousesChanging.setTitle("График изменения домов ИИ");
            PlayerTerrAmChanging.setTitle("График изменения количества территории Игрока");
            AiTerrAmChanging.setTitle("График изменения количества территории ИИ");


            // Создание серии данных
            XYChart.Series<Number, Number> PlRseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> AiRseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> PlWseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> AiWseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> PlPseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> AiPseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> PlHseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> AiHseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> PlTAseries = new XYChart.Series<>();
            XYChart.Series<Number, Number> AiTAseries = new XYChart.Series<>();
            PlRseries.setName("Player's Rice");
            AiRseries.setName("Ai's Rice");
            PlWseries.setName("Player's Water");
            AiWseries.setName("Ai's Water");
            PlPseries.setName("Player's Peasants");
            AiPseries.setName("Ai's Peasants");
            PlHseries.setName("Player's Houses");
            AiHseries.setName("Ai's Houses");
            PlTAseries.setName("Player's Territory");
            AiTAseries.setName("Ai's territory");


            // Добавление данных в серию (параметры: X, Y)
            for (int i = 0; i < player.getRiceValues().size(); i++) {
                PlRseries.getData().add(new XYChart.Data<>(player.getRiceValues().get(i), i + 1));
                AiRseries.getData().add(new XYChart.Data<>(ai.getRiceValues().get(i), i + 1));
                PlWseries.getData().add(new XYChart.Data<>(i + 1, player.getWaterValues().get(i)));
                AiWseries.getData().add(new XYChart.Data<>(i + 1, ai.getWaterValues().get(i)));
                PlPseries.getData().add(new XYChart.Data<>(player.getPeasantsValues().get(i), i + 1));
                AiPseries.getData().add(new XYChart.Data<>(ai.getPeasantsValues().get(i), i + 1));
                PlHseries.getData().add(new XYChart.Data<>(player.getHousesValues().get(i), i + 1));
                AiHseries.getData().add(new XYChart.Data<>(ai.getHousesValues().get(i), i + 1));
                PlTAseries.getData().add(new XYChart.Data<>(player.getTerritoryAmountValues().get(i), i + 1));
                AiTAseries.getData().add(new XYChart.Data<>(ai.getTerritoryAmountValues().get(i), i + 1));
            }

            // Добавление серии данных в графики
            PlayerRiceChanging.getData().add(PlRseries);
            AiRiceChanging.getData().add(AiRseries);
            PlayerWaterChanging.getData().add(PlWseries);
            AiWaterChanging.getData().add(AiWseries);
            PlayerPeasantsChanging.getData().add(PlPseries);
            AiPeasantsChanging.getData().add(AiPseries);
            PlayerHousesChanging.getData().add(PlHseries);
            AiHousesChanging.getData().add(AiHseries);
            PlayerTerrAmChanging.getData().add(PlTAseries);
            AiTerrAmChanging.getData().add(AiTAseries);

            HBox hBox1 = new HBox(5, PlayerRiceChanging, AiRiceChanging, PlayerWaterChanging, AiWaterChanging);
            HBox hBox2 = new HBox(5, PlayerPeasantsChanging, AiPeasantsChanging, PlayerHousesChanging);
            HBox hBox3 = new HBox(5, AiHousesChanging, PlayerTerrAmChanging, AiTerrAmChanging);
            // Настройка сцены и отображение
            Scene scene1 = new Scene(hBox1);
            Scene scene2 = new Scene(hBox2);
            Scene scene3 = new Scene(hBox3);
            stage1.setTitle("LineChartJavaFX");
            stage1.setScene(scene1);
            stage1.show();
            stage2.setScene(scene2);
            stage2.show();
            stage3.setScene(scene3);
            stage3.show();
        } catch (Exception e) {
            logger.error("Ошибка при построении графиков");
        }
    }

}


