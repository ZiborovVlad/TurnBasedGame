package com.example;

import java.awt.image.ImageProducer;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Класс, описывающий и фиксирующий ресурсы игроков, также реализующий выбор действий игроками.
 * Для пользователя доступны 4 действия на выбор (ввод значения с клавиатуры):
 * 1) Набрать воды;
 * 2) Полить рис - в следующий день вырастет в 2 раза больше;
 * 3) Построить дом (необходимо потратить рис, воду и крестьянина);
 * 4) Захватить территорию.
 * Искуственный интеллект через случайное число выбирает действие.
 * ИИ не может начать захват территории, если количество крестьян меньше 5.
 */
public class Player{
    private String name; // Имя игрока
    private int rice; // Количество риса
    private int water; // Количество воды
    private int peasants;// Количество крестьян
    private int houses;
    private int days;
    private int[][] territory; // Контролируемая территория
    private int x, y;
    private int[] cords = new int[2];
    private boolean watered;
    private boolean attack;
    private ArrayList<Integer> riceArr = new ArrayList<>();
    private ArrayList<Integer> waterArr = new ArrayList<>();
    private ArrayList<Integer> peasantsArr = new ArrayList<>();
    private ArrayList<Integer> housesArr = new ArrayList<>();
    private ArrayList<Integer> territoryAmountArr = new ArrayList<>();

    /**
     * Конструктор класса игроков.
     * Установка начальных параметров.
     * @param name - имя игрока.
     */
    public Player(String name) {
        this.name = name;
        this.rice = 10;
        this.water = 10;
        this.peasants = 5;
        this.houses = 1;
        this.watered = false;
        this.territory = new int[4][4]; // Это пример, можно сделать динамично
        this.attack = false;
        this.days=0;
    }

    /**
     * Метод для отслеживания изменений количества риса.
     * @param value - добавляемая величина в список значений
     */
    public void addRiceValue(int value){
        this.riceArr.add(value);
    }

    /**
     * Метод для отслеживания изменений количества воды.
     * @param value - добавляемая величина в список значений
     */
    public void addWaterValue(int value){
        this.waterArr.add(value);
    }

    /**
     * Метод для отслеживания изменений количества крестьян.
     * @param value - добавляемая величина в список значений
     */
    public void addPeasantsValue(int value){
        this.peasantsArr.add(value);
    }

    /**
     * Метод для отслеживания изменений количества домов.
     * @param value - добавляемая величина в список значений
     */
    public void addHousesValue(int value){
        this.housesArr.add(value);
    }

    /**
     * Метод для отслеживания изменений количества захваченной территории.
     * @param value - добавляемая величина в список значений
     */
    public void addTerritoryAmountValue(int value){
        this.territoryAmountArr.add(value);
    }

    /**
     * Метод для получения всех значений риса игрока за игру.
     * @return - список всех значений риса
     */
    public ArrayList<Integer> getRiceValues(){
        return riceArr;
    }

    /**
     * Метод для получения всех значений воды игрока за игру.
     * @return - список всех значений воды
     */
    public ArrayList<Integer> getWaterValues(){
        return waterArr;
    }

    /**
     * Метод для получения всех значений крестьян игрока за игру.
     * @return - список всех значений крестьян
     */
    public ArrayList<Integer> getPeasantsValues(){
        return peasantsArr;
    }

    /**
     * Метод для занесения всех значений построенных домов игрока игрока за игру.
     * @return - список всех значений домов
     */
    public ArrayList<Integer> getHousesValues(){
        return housesArr;
    }

    /**
     * Метод для занесения всех значений количества территории игрока за игру.
     * @return - список всех значений захваченной территории
     */
    public ArrayList<Integer> getTerritoryAmountValues(){
        return territoryAmountArr;
    }

    /**
     * Метод для установки позиций игроков на момент начала игры.
     */
    public void setStartPos(){
        if (name.equals("Player")){
            this.x = territory.length-1;
            this.y = territory.length-1;
            setCords();
            setTerritory(x,y);
        }
        else if(name.equals("AI")){
            this.x = 0;
            this.y = 0;
            setCords();
            setTerritory(x,y);
        }
    }

    /**
     * Метод для реализации действий игроков.
     * Игрок через консоль выбирает действие, ИИ - на основе случайного числа.
     */
    public void startTurn() {
        // Игрок решает, что делать в свой ход: собрать рис, набрать воду, построить дом и т.д.
        System.out.println(name + "'s turn!");
        this.attack = false;
        if (name.equals("Player")){
            days++;
            boolean running = true;
            while(running) {
                Scanner in = new Scanner(System.in);
                System.out.println("Choose the action: \n1) Collect water\n2) Water the rise\n3) Build one house\n4) Conquer the territory");
                int choose = in.nextInt();
                switch (choose) {
                    case 1:
                        water += 6;
                        System.out.println("You've collected the water!");
                        running=false;
                        break;
                    case 2:
                        if (water < 3) {
                            System.out.println("You don't have enough water");
                            continue;
                        } else if (!watered) {
                            water -= 3;
                            watered = true;
                            System.out.println("You've watered the rise!");
                            running = false;
                            break;
                        } else if (watered) {
                            System.out.println("The rice is already watered");
                            break;
                        }
                    case 3:
                        if (water >= 3 && rice >= 5 && peasants >= 1) {
                            water -= 3;
                            rice -= 5;
                            peasants--;
                            houses++;
                            System.out.println("You've built the house!");
                            running = false;
                            break;
                        }
                        else {
                            System.out.println("You don't have enough resources");
                            continue;
                        }
                    case 4:
                        setAttack();
                        System.out.println("You are in attack mode!");
                        running = false;
                        break;
                    default:
                        System.out.println("Incorrect number input");
                        continue;
                }
                break;
            }
        }
        else if (name.equals("AI")) {
            days++;
            boolean running = true;
            while (running) {
                Random rand = new Random();
                System.out.println("AI, Choose the action: \n1) Collect water\n2) Water the rise\n3) Build one house\n4) Conquer the territory");
                int choose = rand.nextInt(4)+1;
                switch (choose) {
                    case 1:
                        water += 6;
                        System.out.println("AI have collected the water!");
                        running = false;
                        break;
                    case 2:
                        if (water < 3) {
                            System.out.println("AI doesn't have enough water");
                            continue;
                        } else if (!watered) {
                            water -= 3;
                            watered = true;
                            System.out.println("AI have watered the rise!");
                            running = false;
                            break;
                        } else if (watered) {
                            System.out.println("The rice is already watered");
                            break;
                        }
                    case 3:
                        if (water >= 3 && rice >= 5 && peasants >= 1) {
                            water -= 3;
                            rice -= 5;
                            peasants--;
                            houses++;
                            System.out.println("AI have built the house!");
                            running = false;
                            break;
                        } else {
                            System.out.println("AI doesn't have enough resources");
                            continue;
                        }
                    case 4:
                        if (peasants>=4) {
                            setAttack();
                            System.out.println("AI is in attack mode!");
                            running = false;
                            break;
                        }
                        else { break; }
                }
            }
        } else {
            System.out.println("error");
        }

    }

    /**
     * Метод, реализующий сбор риса.
     * Если рис полит водой, собирается в 2 раза больше.
     */
    public void collectRice() {
        // Каждый день рис растет
        int plus = watered ? 10 : 5;
        rice+=plus;
        watered = false;
    }

    /**
     * Метод, реализующий создание крестьян.
     * Число крестьян увеличивается на число построенных домов.
     */
    public void generatePeasants() {
        // Проверка на возможность создания крестьян
        peasants+=houses;
    }

    /**
     * Метод для присваивания территории.
     * @param x - координата клетки по горизонтали на поле.
     * @param y - координата клетки по вертикали на поле.
     */
    public void setTerritory(int x, int y){
        this.territory[x][y] = 1;
    }

    /**
     * Метод проверки занятости территории.
     * @param x - координата клетки по горизонтали на поле.
     * @param y - координата клетки по вертикали на поле.
     * @return true, если занято, false - если свободно
     */
    public boolean getTerritory(int x, int y){
        return this.territory[x][y] == 1;
    }

    /**
     * Метод для получения списка захваченных игроком и свободных на поле клеток.
     * @return - возвращает текущий массив клеток
     */
    public int[][] getTerritory(){
        return this.territory;
    }

    /**
     * Метод подсчета количества занятых клеток.
     * @return - возвращает количество занятых клеток
     */
    public int getTerritoryCount() {
        // Подсчёт занятой территории
        int count = 0;
        for (int i = 0; i < territory.length; i++) {
            for (int j = 0; j < territory[i].length; j++) {
                if (territory[i][j] == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Метод для получения имени игрока.
     * @return имя игрока
     */
    public String getName(){
        return name;
    }

    /**
     * Метод для получения текущего значения риса.
     * @return текущее значение риса
     */
    public int getRice() {
        return rice;
    }

    /**
     * Метод для получения текущего значения воды.
     * @return текущее значение воды
     */
    public int getWater() {
        return water;
    }

    /**
     * Метод для получения текущего значения построенных домов.
     * @return текущее значение домов
     */
    public int getHouses(){
        return houses;
    }

    /**
     * Метод для получения текущего значения крестьян.
     * @return текущее значение крестьян
     */
    public int getPeasants() {
        return peasants;
    }

    /**
     * Метод для установки нового значения крестьян.
     * @param N - новое значение крестьян
     */
    public void setPeasants(int N){
        this.peasants = N;
    }

    /**
     * Метод для получения текущих координат игрока.
     * @return - возвращает массив, в двух элементах содержащий координаты
     */
    public int[] getCords(){
        return cords;
    }

    /**
     * Метод для установки координат.
     */
    public void setCords(){
        cords[0] = x;
        cords[1] = y;
    }

    /**
     * Метод, реализующий перемещение на клетку вправо.
     */
    public void goRight(){
        x+=1;
        setCords();
    }

    /**
     * Метод, реализующий перемещение на клетку влево.
     */
    public void goLeft(){
        x-=1;
        setCords();
    }

    /**
     * Метод, реализующий перемещение на клетку вверх.
     */
    public void goUp(){
        y-=1;
        setCords();
    }

    /**
     * Метод, реализующий перемещение на клетку вниз.
     */
    public void goDown(){
        y+=1;
        setCords();
    }

    /**
     * Метод для установки флага-режима атаки в состояние true.
     */
    public void setAttack(){
        this.attack = true;
    }

    /**
     * Метод для проверки игрока на режим атаки.
     * @return - возвращает состояние атаки. True - если игрок в атаке, False - если нет.
     */
    public boolean Conquer(){
        return this.attack;
    }

    /**
     * Метод для записи территории игрока в данный момент в файл.
     */
    public void WriteTerritory(){
        try (PrintWriter writer = new PrintWriter(new FileWriter(name+"Territory.txt", false))) {
            for (int[] row : getTerritory()) {
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
     * Метод для считывания территории игрока из файла.
     */
    public void ReadTerritory(){
        File file = new File(name+"Territory.txt");
        if(file.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(name+"Territory.txt"))) {
                String line;
                int row = 0;
                while ((line = reader.readLine()) != null && row < territory.length) {
                    // Разбиваем строку на элементы
                    String[] values = line.split(" ");

                    // Заполняем строки массива
                    for (int col = 0; col < values.length; col++) {
                        territory[row][col] = Integer.parseInt(values[col]);
                    }
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод для записи имени, ресурсов и дней игрока в данный момент в файл.
     */
    public void WriteResources() {
        int[] data = {this.x, this.y, this.rice, this.water, this.peasants, this.houses, this.days};
        try (PrintWriter writer = new PrintWriter(new FileWriter(name + "Resources.txt", false))) {
            writer.println(this.name);
            for(int element:data){
                writer.println(element);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(name+"Arrays.ser"))) {
            oos.writeObject(riceArr);
            oos.writeObject(waterArr);
            oos.writeObject(peasantsArr);
            oos.writeObject(housesArr);
            oos.writeObject(territoryAmountArr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Метод для считывания имени, ресурсов и дней игрока из файла.
     */
    public void ReadResources(){
        File file = new File(name+"Resources.txt");
        if(file.exists()){
            try (BufferedReader br = Files.newBufferedReader(Paths.get(name+"Resources.txt"))) {
                // Считываем первую строку
                this.name = br.readLine();
                // Читаем оставшиеся строки с числами
                this.x = Integer.parseInt(br.readLine().trim());
                this.y = Integer.parseInt(br.readLine().trim());
                this.rice = Integer.parseInt(br.readLine().trim());
                this.water = Integer.parseInt(br.readLine().trim());
                this.peasants = Integer.parseInt(br.readLine().trim());
                this.houses = Integer.parseInt(br.readLine().trim());
                this.days = Integer.parseInt(br.readLine().trim());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(name+"Arrays.ser"))) {
                this.riceArr = (ArrayList<Integer>) ois.readObject();
                this.waterArr = (ArrayList<Integer>) ois.readObject();
                this.peasantsArr = (ArrayList<Integer>) ois.readObject();
                this.housesArr = (ArrayList<Integer>) ois.readObject();
                this.territoryAmountArr = (ArrayList<Integer>) ois.readObject();

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
