package ru.otus.animals.birds;

import ru.otus.animals.Animal;

public class Duck extends Animal implements Flying {

    @Override
    public void fly() {
        System.out.println("Я лечу");
    }
}
