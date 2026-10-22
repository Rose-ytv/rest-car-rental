package com.example.demo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CarController {

    private final List<Car> cars = new ArrayList<>(List.of(
        new Car("11AA22", "Ferrari", 100),
        new Car("AA11BB", "Renault", 50),
        new Car("CC33DD", "Peugeot", 40)
    ));

    private Car find(String plateNumber) {
        return cars.stream()
            .filter(car -> car.getPlateNumber().equals(plateNumber))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Voiture introuvable"));
    }

    @GetMapping("/cars")
    @ResponseStatus(HttpStatus.OK)
    public List<Car> listOfCars() {
        return cars.stream()
            .filter(car -> !car.isRented())
            .toList();
    }

    @GetMapping("/cars/{plateNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Car aCar(@PathVariable String plateNumber) {
        return find(plateNumber);
    }

    @PutMapping(value = "/cars/{plateNumber}", params = "rent=true")
    @ResponseStatus(HttpStatus.OK)
    public void rent(@PathVariable String plateNumber, @RequestBody Dates dates) {
        validateDates(dates);

        Car car = find(plateNumber);
        if (car.isRented()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Déjà louée");
        }
        car.setRented(true);
    }

    @PutMapping(value = "/cars/{plateNumber}", params = "rent=false")
    @ResponseStatus(HttpStatus.OK)
    public void giveBack(@PathVariable String plateNumber) {
        Car car = find(plateNumber);
        if (!car.isRented()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Non louée");
        }
        car.setRented(false);
    }

    private void validateDates(Dates dates) {
        if (dates == null || dates.getBegin() == null || dates.getEnd() == null
                || dates.getBegin().isBlank() || dates.getEnd().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Les dates de début et de fin sont obligatoires");
        }

        try {
            LocalDate begin = LocalDate.parse(dates.getBegin());
            LocalDate end = LocalDate.parse(dates.getEnd());
            if (begin.isAfter(end)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La date de début doit être antérieure ou égale à la date de fin");
            }
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Les dates doivent respecter le format AAAA-MM-JJ");
        }
    }
}
