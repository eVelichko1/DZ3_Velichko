package ru.otus;

import ru.otus.animals.Animal;
import ru.otus.animals.Color;
import ru.otus.db.AnimalRepository;
import ru.otus.db.Database;
import ru.otus.factory.AnimalFactory;
import ru.otus.factory.AnimalType;

import java.util.List;
import java.util.Scanner;

public class App {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        AnimalFactory factory = new AnimalFactory();
        AnimalRepository repository;
        try {
            Database.init();
            repository = new AnimalRepository(factory);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            System.out.println("Проверьте, что MySQL запущен, а логин и пароль указаны в src/main/resources/db.properties");
            return;
        }

        Command currentCommand;
        do {
            currentCommand = askForCommand();
            try {
                if (currentCommand == Command.LIST) {
                    printAnimals(repository.findAll());
                } else if (currentCommand == Command.ADD) {
                    addAnimal(repository, factory);
                } else if (currentCommand == Command.EDIT) {
                    editAnimal(repository, factory);
                } else if (currentCommand == Command.FILTER) {
                    filterAnimals(repository);
                } else if (currentCommand == Command.DELETE) {
                    deleteAnimal(repository);
                }
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        } while (currentCommand != Command.EXIT);

        System.out.println("До свидания!");
    }

    private static void addAnimal(AnimalRepository repository, AnimalFactory factory) {
        Animal animal = readAnimal(factory);
        repository.save(animal);
        System.out.println("Животное сохранено в базу данных.");
        System.out.println(animal);
        animal.say();
    }

    private static void editAnimal(AnimalRepository repository, AnimalFactory factory) {
        List<Animal> animals = repository.findAll();
        if (animals.isEmpty()) {
            System.out.println("Список пуст, редактировать нечего");
            return;
        }
        printAnimals(animals);
        int id = askForId();
        Animal existing = repository.findById(id);
        if (existing == null) {
            System.out.println("Животное с id=" + id + " не найдено");
            return;
        }
        System.out.println("Редактируется: " + existing);
        System.out.println("Введите новые данные.");
        Animal animal = readAnimal(factory);
        animal.setId(existing.getId());
        if (repository.update(animal)) {
            System.out.println("Животное обновлено.");
            System.out.println(animal);
            animal.say();
        } else {
            System.out.println("Не удалось обновить животное с id=" + id);
        }
    }

    private static void filterAnimals(AnimalRepository repository) {
        AnimalType animalType = askForAnimalType();
        List<Animal> animals = repository.findByType(animalType);
        if (animals.isEmpty()) {
            System.out.println("Животные типа " + animalType.name() + " не найдены");
            return;
        }
        printAnimals(animals);
    }

    private static void deleteAnimal(AnimalRepository repository) {
        List<Animal> animals = repository.findAll();
        if (animals.isEmpty()) {
            System.out.println("Список пуст, удалять нечего");
            return;
        }
        printAnimals(animals);
        int id = askForId();
        if (repository.delete(id)) {
            System.out.println("Животное с id=" + id + " удалено");
        } else {
            System.out.println("Животное с id=" + id + " не найдено");
        }
    }

    private static Animal readAnimal(AnimalFactory factory) {
        AnimalType animalType = askForAnimalType();
        Animal animal = factory.create(animalType);
        animal.setName(askForName());
        animal.setAge(askForAge());
        animal.setWeight(askForWeight());
        animal.setColor(askForColor());
        return animal;
    }

    private static void printAnimals(List<Animal> animals) {
        if (animals.isEmpty()) {
            System.out.println("Список пуст");
            return;
        }
        for (Animal animal : animals) {
            System.out.println(animal);
        }
    }

    private static Command askForCommand() {
        String input = null;
        do {
            if (input != null) {
                System.out.println("Введена неверная команда, попробуйте ещё раз");
            }
            System.out.printf("Введите одну из команд (%s): ", String.join("/", Command.VALUES));
            input = scanner.next();
        } while (Command.doesNotContain(input));
        return Command.fromString(input);
    }

    private static AnimalType askForAnimalType() {
        String input = null;
        do {
            if (input != null) {
                System.out.println("Введен неверный тип, попробуйте ещё раз");
            }
            System.out.printf("Введите тип животного (%s): ", String.join("/", AnimalType.VALUES));
            input = scanner.next();
        } while (AnimalType.doesNotContain(input));
        return AnimalType.fromString(input);
    }

    private static int askForId() {
        int input = 0;
        do {
            System.out.print("Введите id животного: ");
            try {
                input = Integer.parseInt(scanner.next());
                if (input <= 0) {
                    System.out.println("Введен неверный id, попробуйте ещё раз");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ввод не является числом, попробуйте ещё раз");
            }
        } while (input <= 0);
        return input;
    }

    private static String askForName() {
        String name;
        while (true) {
            System.out.print("Введите имя животного: ");
            name = scanner.next().trim();

            if (name.isEmpty()) {
                System.out.println("Имя не может быть пустым. Попробуйте ещё раз.");
                continue;
            }

            if (!name.matches("[A-Za-zА-Яа-яЁё\\s]+")) {
                System.out.println("Имя должно содержать только буквы. Попробуйте ещё раз.");
                continue;
            }

            if (name.length() > 100) {
                System.out.println("Имя не должно быть длиннее 100 символов. Попробуйте ещё раз.");
                continue;
            }
            break;
        }
        return name;
    }

    private static int askForAge() {
        int input = 0;
        do {
            System.out.print("Введите возраст животного: ");
            try {
                input = Integer.parseInt(scanner.next());
                if (input <= 0) {
                    System.out.println("Введен неверный возраст, попробуйте ещё раз");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ввод не является числом, попробуйте ещё раз");
            }
        } while (input <= 0);
        return input;
    }

    private static int askForWeight() {
        int weight = 0;
        while (true) {
            System.out.print("Введите вес животного (г): ");
            String line = scanner.next().trim();
            try {
                weight = Integer.parseInt(line);
                if (weight <= 0) {
                    System.out.println("Вес должен быть положительным числом. Попробуйте ещё раз.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Ввод не является числом. Попробуйте ещё раз.");
            }
        }
        return weight;
    }

    private static Color askForColor() {
        StringBuilder prompt = new StringBuilder("Введите цвет животного (доступные варианты: ");
        for (int i = 0; i < Color.values().length; i++) {
            prompt.append(Color.values()[i].getValue());
            if (i < Color.values().length - 1) {
                prompt.append(", ");
            }
        }
        prompt.append("): ");

        while (true) {
            System.out.print(prompt);
            String userInput = scanner.next().trim();

            Color chosen = Color.fromString(userInput);
            if (chosen == null) {
                System.out.println("Неверный цвет. Попробуйте ещё раз.");
                continue;
            }
            return chosen;
        }
    }
}
