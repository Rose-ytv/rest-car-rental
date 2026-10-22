package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

    @Test
    void listsAvailableCars() {
        CarController controller = new CarController();

        org.junit.jupiter.api.Assertions.assertEquals(3, controller.listOfCars().size());
    }

    @Test
    void returnsNotFoundForUnknownCar() {
        CarController controller = new CarController();

        org.junit.jupiter.api.Assertions.assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> controller.aCar("UNKNOWN"));
    }

    @Test
    void rentsCarWithValidDates() {
        CarController controller = new CarController();

        controller.rent("11AA22", dates("2026-10-22", "2026-10-25"));

        org.junit.jupiter.api.Assertions.assertTrue(controller.aCar("11AA22").isRented());
    }

    @Test
    void rejectsInvalidDates() {
        CarController controller = new CarController();

        org.junit.jupiter.api.Assertions.assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> controller.rent("11AA22", dates("2026-10-26", "2026-10-25")));
    }

    @Test
    void rejectsRentingAlreadyRentedCar() {
        CarController controller = new CarController();
        Dates dates = dates("2026-10-22", "2026-10-25");

        controller.rent("11AA22", dates);

        org.junit.jupiter.api.Assertions.assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> controller.rent("11AA22", dates));
    }

    @Test
    void returnsRentedCar() {
        CarController controller = new CarController();

        controller.rent("11AA22", dates("2026-10-22", "2026-10-25"));
        controller.giveBack("11AA22");

        org.junit.jupiter.api.Assertions.assertFalse(controller.aCar("11AA22").isRented());
    }

    @Test
    void rejectsReturningAvailableCar() {
        CarController controller = new CarController();

        org.junit.jupiter.api.Assertions.assertThrows(
            org.springframework.web.server.ResponseStatusException.class,
            () -> controller.giveBack("11AA22"));
    }

    private Dates dates(String begin, String end) {
        Dates dates = new Dates();
        dates.setBegin(begin);
        dates.setEnd(end);
        return dates;
    }
}
