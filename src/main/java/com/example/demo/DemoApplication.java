package com.example.demo;

import au.com.bytecode.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

  // Номер колонки указанный в application.yml
  @Value("${column_number}")
  private int column_number;

  // Проверяем, является ли строка целым числом
  private static boolean checkIsInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @Override
  public void run(String... args) throws IOException {

    // Оперделение числа колонок по первой строке
    int max_column_number = 0;
    CSVReader reader = new CSVReader(new FileReader("airports.csv"), ',', '"', 0);
    String[] first_line;
		if ((first_line = reader.readNext()) != null) {
			max_column_number = first_line.length;
		}

    // Проверка наличия необязательного аргумента - номера колонки для индексации
		if (args.length > 0) {
			if (checkIsInteger(args[0])) {
				column_number = Integer.parseInt(args[0]);
				if (column_number < 1 || column_number > max_column_number) {
					System.out.println(columnWarning());
					return;
				}
			} else {
				System.out.println(columnWarning());
				return;
			}
		}

    // Чтение строки для фильтрации
    Scanner scanner = new Scanner(System.in);
    System.out.print("Введите строку: ");
    String in = scanner.nextLine();
    scanner.close();

    Set<String[]> results = new TreeSet<String[]>(new Comparator<String[]>() {
      public int compare(String[] o1, String[] o2) {
        return o1[column_number - 1].compareTo(o2[column_number - 1]);
      }
    });

    // Отмечаем начало поиска
    long start = System.currentTimeMillis();
    String[] nextLine = first_line;
		if (nextLine[column_number - 1].startsWith(in))
      results.add(nextLine);
		
    // Работаем с первой строкой
		while ((nextLine = reader.readNext()) != null)
			if (nextLine[column_number - 1].startsWith(in))
        results.add(nextLine);

    // Отмечаем завершение поиска
    long finish = System.currentTimeMillis();
    long elapsed = finish - start;

    reader.close();



    // Вывод результатов
		for (String[] s : results)
			System.out.println(Arrays.toString(s));
    System.out.println("Количество найденных строк: " + results.size());
    System.out.println("Время, затраченное на поиск: " + elapsed + " мс.");
  }

  private String help() {
    return "";
  }

  private String columnWarning() {
    return "Единственный возможный аргумент - номер колонки для индексации, он должен быть больше нуля и не больше кол-ва колонок";
  }
}
