package com.penguins.project.controller.Reservation;


import com.penguins.project.model.Arrangement.Arrangement;
import com.penguins.project.model.Person.Person;
import com.penguins.project.model.Reservation.Reservation;
import com.penguins.project.service.ArrangementService;
import com.penguins.project.service.PersonService;
import com.penguins.project.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
public class ReservationController {
    private final ReservationService reservationService;
    private final ArrangementService arrangementService;
    private final PersonService personService;

    public ReservationController(ReservationService reservationService,ArrangementService arrangementService,PersonService personService) {
        this.reservationService = reservationService;
        this.arrangementService = arrangementService;
        this.personService = personService;
    }

    @PostMapping(path = "/reservation/add", params={"arrangement_id"})
    public ResponseEntity<String> addReservation(@RequestParam Long arrangement_id, @RequestBody ReservationParam reservationParam){
        try{
            Reservation reservation = reservationParam.toReservation();
            Arrangement arrangement = arrangementService.getArrangementById(arrangement_id);
            if (arrangement == null){
                throw new IllegalStateException("Arrangement with id " + arrangement_id + " does not exists");
            }

            reservation.setArrangement(arrangement);
            Person person = reservation.getPerson();
            List<Person> persons = personService
                    .findByNameAndLastNameAndEmailAndContactAndAddress(person.getName(),person.getLastName(),person.getEmail(),person.getContact(),person.getAddress());
            if (persons.isEmpty()){
                reservation.setPerson(person);
            }else{
                reservation.setPerson(persons.get(0));
            }
            reservationService.addReservation(reservation);
        }catch(Exception e){
            return new ResponseEntity<>("Greška pri dodavanju rezervacije!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Rezervacija uspešno dodata!", HttpStatus.OK);

    }

    @GetMapping(value = "/reservation/all")
    @ResponseBody
    public List<ReservationW> getAllReservations(@RequestParam(required = false) String arrangementName){
        return reservationService.getAllReservations(arrangementName);
    }

    @PutMapping(value = "/reservation/update")
    public ResponseEntity<String> updateReservation(@RequestParam Long id, @RequestParam String accepted){
        try {
            reservationService.updateReservation(id,accepted);
        }catch(Exception e){
            return new ResponseEntity<>("Greška pri ažuriranju aranžmana!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Rezeracija uspešno ažurirana!", HttpStatus.OK);

    }
}
