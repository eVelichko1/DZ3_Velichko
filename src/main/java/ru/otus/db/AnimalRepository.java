package ru.otus.db;

import ru.otus.animals.Animal;
import ru.otus.animals.Color;
import ru.otus.factory.AnimalFactory;
import ru.otus.factory.AnimalType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AnimalRepository {

    private static final String COLUMNS = "id, type, name, age, weight, color";

    private final AnimalFactory factory;

    public AnimalRepository(AnimalFactory factory) {
        this.factory = factory;
    }

    public void save(Animal animal) {
        String sql = "INSERT INTO animals (type, name, age, weight, color) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindAnimal(statement, animal);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    animal.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось сохранить животное: " + e.getMessage(), e);
        }
    }

    public List<Animal> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM animals ORDER BY id";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return readAnimals(resultSet);
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось получить список животных: " + e.getMessage(), e);
        }
    }

    public List<Animal> findByType(AnimalType type) {
        String sql = "SELECT " + COLUMNS + " FROM animals WHERE type = ? ORDER BY id";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                return readAnimals(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось получить животных по типу: " + e.getMessage(), e);
        }
    }

    public Animal findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM animals WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return mapRow(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось найти животное: " + e.getMessage(), e);
        }
    }

    public boolean update(Animal animal) {
        String sql = "UPDATE animals SET type = ?, name = ?, age = ?, weight = ?, color = ? WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindAnimal(statement, animal);
            statement.setInt(6, animal.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось обновить животное: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM animals WHERE id = ?";
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Не удалось удалить животное: " + e.getMessage(), e);
        }
    }

    private List<Animal> readAnimals(ResultSet resultSet) throws SQLException {
        List<Animal> animals = new ArrayList<>();
        while (resultSet.next()) {
            animals.add(mapRow(resultSet));
        }
        return animals;
    }

    private Animal mapRow(ResultSet resultSet) throws SQLException {
        AnimalType type = AnimalType.fromString(resultSet.getString("type"));
        Animal animal = factory.create(type);
        if (animal == null) {
            throw new SQLException("В базе сохранён неизвестный тип животного: " + resultSet.getString("type"));
        }
        animal.setId(resultSet.getInt("id"));
        animal.setName(resultSet.getString("name"));
        animal.setAge(resultSet.getInt("age"));
        animal.setWeight(resultSet.getInt("weight"));
        animal.setColor(Color.valueOf(resultSet.getString("color")));
        return animal;
    }

    private void bindAnimal(PreparedStatement statement, Animal animal) throws SQLException {
        statement.setString(1, animal.getType().name());
        statement.setString(2, animal.getName());
        statement.setInt(3, animal.getAge());
        statement.setInt(4, animal.getWeight());
        statement.setString(5, animal.getColor().name());
    }
}
