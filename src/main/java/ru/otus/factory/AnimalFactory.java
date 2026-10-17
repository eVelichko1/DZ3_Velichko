package ru.otus.factory;

import ru.otus.animals.Animal;
import ru.otus.animals.Cat;
import ru.otus.animals.Dog;
import ru.otus.animals.birds.Duck;

public class AnimalFactory {

    public Animal create(AnimalType type) {
        if (type == null) {
            return null;
        }
        Animal animal;
        if (type == AnimalType.CAT) {
            animal = new Cat();
        } else if (type == AnimalType.DOG) {
            animal = new Dog();
        } else if (type == AnimalType.DUCK) {
            animal = new Duck();
        } else {
            return null;
        }
        animal.setType(type);
        return animal;
    }
}
