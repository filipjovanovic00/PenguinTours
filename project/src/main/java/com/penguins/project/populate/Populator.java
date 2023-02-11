package com.penguins.project.populate;


import com.penguins.project.Security.JWTGenerator;
import com.penguins.project.model.Accomodation.Accomodation;
import com.penguins.project.model.Arrangement.Arrangement;
import com.penguins.project.model.Location.Location;
import com.penguins.project.model.Program.Program;
import com.penguins.project.model.Role.Role;
import com.penguins.project.model.Role.RoleRepository;
import com.penguins.project.model.User.UserEntity;
import com.penguins.project.model.User.UserRepository;
import com.penguins.project.service.ArrangementService;
import com.penguins.project.service.LocationService;
import com.penguins.project.service.ReservationService;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
public class Populator {

    private final ArrangementService arrangementService;
    private final ReservationService reservationService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;
    private final LocationService locationService;

    @Autowired
    public Populator(ArrangementService arrangementService, ReservationService reservationService, RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTGenerator jwtGenerator, LocationService locationService) {
        this.arrangementService = arrangementService;
        this.reservationService = reservationService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtGenerator = jwtGenerator;
        this.locationService = locationService;
    }
    public static Date between(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);

        return new Date(randomMillisSinceEpoch);
    }
    public void populateRole(){
        Role role = new Role();
        role.setName("ADMIN");
        roleRepository.save(role);


        role = new Role();
        role.setName("STAFF");
        roleRepository.save(role);

    }
    public void populateUser(){

        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        Role adminRole = roleRepository.findByName("admin").get();
        admin.setRoles(Collections.singletonList(adminRole));

        userRepository.save(admin);

        List<String> users = List.of("nenad","ivan","danilo","filip","luka");

        for (int i =0; i< users.size();i++){
            UserEntity user = new UserEntity();
            user.setUsername(users.get(i));
            user.setPassword(passwordEncoder.encode("staff"));
            Role staffRole = roleRepository.findByName("STAFF").get();
            user.setRoles(Collections.singletonList(staffRole));

            userRepository.save(user);
        }


    }

    public static List<String> readFile(String fileName){
        List<String> lines = new ArrayList<>();
        File file = new File(fileName);
        String s = file.getAbsolutePath();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        }

        return lines;
    }

    public static String getRandomLine(List<String> lines) {
        int randomIndex = (int) (Math.random() * lines.size());
        return lines.get(randomIndex);
    }


    public static java.sql.Date getRandomDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, (int) (Math.random() * (2024 - 2023 + 1) + 2022));
        calendar.set(Calendar.MONTH, (int) (Math.random() * (12 - 1 + 1) + 1));
        calendar.set(Calendar.DAY_OF_MONTH, (int) (Math.random() * (28 - 1 + 1) + 1));
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(date);

        try {
            Date utilDate = dateFormat.parse(dateString);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date format: " + dateString);
            return null;
        }
    }

    public static java.sql.Date getRandomDateBefore() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, (int) (Math.random() * (2022 - 2020 + 1) + 2020));
        calendar.set(Calendar.MONTH, (int) (Math.random() * (12 - 1 + 1) + 1));
        calendar.set(Calendar.DAY_OF_MONTH, (int) (Math.random() * (28 - 1 + 1) + 1));
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(date);

        try {
            Date utilDate = dateFormat.parse(dateString);
            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date format: " + dateString);
            return null;
        }
    }
    public static java.sql.Date incrementDate(java.sql.Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return new java.sql.Date(calendar.getTime().getTime());
    }

    public static String getRandomTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, new Random().nextInt(24));
        calendar.set(Calendar.MINUTE, new Random().nextInt(60));
        calendar.set(Calendar.SECOND, new Random().nextInt(60));
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }

    public static List<String> stringToList(String input) {
        String[] items = input.split(",");
        return Arrays.asList(items);
    }


    public void populateArrangements(){
        Random rand = new Random();
        Lorem lorem = LoremIpsum.getInstance();

        Integer UPPER_BOUND_PRICE = 3000;
        Integer LOWER_BOUND_PRICE = 100;

        Integer UPPER_BOUND_NUMBER_OF_PROGRAMS = 10;
        Integer LOWER_BOUND_NUMBER_OF_PROGRAMS = 1;


        List<String> transporations = List.of("Voz","Autobus","Avion","Brod","Samostalni prevoz");

        List<String> namesOfAccomodations = readFile("popular.txt");
        List<String> namesOfLocations = readFile("locations");
        List<String> namesOfStartingLocations = List.of("Kragujevac,Srbija,Evropa","Beograd,Srbija,Evropa","Nis,Srbija,Evropa");

        for(int i = 0; i < 10; i++) {
            Arrangement arrangement = new Arrangement();
            arrangement.setPrice(rand.nextInt(UPPER_BOUND_PRICE-LOWER_BOUND_PRICE)+LOWER_BOUND_PRICE);
            arrangement.setStatus("Dostupan");
            arrangement.setRemark(lorem.getParagraphs(1,3));
            arrangement.setTransportation(transporations.get(rand.nextInt(transporations.size())));

            Set<Program> programs = new HashSet<>();
            int numberOfPrograms = rand.nextInt(UPPER_BOUND_NUMBER_OF_PROGRAMS-LOWER_BOUND_NUMBER_OF_PROGRAMS)+LOWER_BOUND_NUMBER_OF_PROGRAMS;
            java.sql.Date firstDate = getRandomDate();

            String nameOfStartingLocation = namesOfStartingLocations.get(rand.nextInt(namesOfStartingLocations.size()));
            for(int j=0; j < numberOfPrograms; j++ ){
                Program program = new Program();

                Set<Location> locations = new HashSet<>();
                int numberOfLocations = rand.nextInt(3-1)+1;
                if (j==0 || j==numberOfPrograms-1){
                    Location location = new Location();
                    //String names = nameOfStartingLocation;
                    List<String> cityCountryContinent = stringToList(nameOfStartingLocation);
                    location.setCity(cityCountryContinent.get(0));
                    location.setCountry(cityCountryContinent.get(1));
                    location.setContinent(cityCountryContinent.get(2));
                    List<Location> list = locationService.findSame(location);
                    if(!list.isEmpty()){
                        locations.add(list.get(0));
                    }else{
                        locations.add(location);
                    }
                    locations.add(location);
                }
                for(int k=0; k < numberOfLocations; k++ ){
                    Location location = new Location();
                    String names = getRandomLine(namesOfLocations);
                    List<String> cityCountryContinent = stringToList(names);
                    location.setCity(cityCountryContinent.get(0));
                    location.setCountry(cityCountryContinent.get(1));
                    location.setContinent(cityCountryContinent.get(2));
                    List<Location> list = locationService.findSame(location);
                    if(!list.isEmpty()){
                        locations.add(list.get(0));
                    }else{
                        locations.add(location);
                    }

                }
                program.setLocations(locations);

                program.setDate(incrementDate(firstDate,j).toLocalDate());
                if (j==0){
                    String randomTime = getRandomTime();
                    if (arrangement.getTransportation().equals("Voz")){
                        String prefix = "Полазак са перона у " + randomTime + ". ";
                        program.setDescription(prefix + lorem.getParagraphs(1,3));
                    }
                    else if (arrangement.getTransportation().equals("Avion")) {
                        String prefix = "Полазак са аеродрома у"  + randomTime + ". ";
                        program.setDescription(prefix + lorem.getParagraphs(1,3));
                    }
                    else{
                        program.setDescription(lorem.getParagraphs(1,3));
                    }
                }
                else if (j==numberOfPrograms-1){
                    List<String> times = List.of("jутарњим","преподневним","пoподневним","вечерњим");
                    String postfix = "Долазак у  " + stringToList(getRandomLine(namesOfStartingLocations)).get(0)+ "у "  + times.get(rand.nextInt(times.size()));
                    program.setDescription(lorem.getParagraphs(1,3) + postfix);
                }
                else{
                    program.setDescription(lorem.getParagraphs(1,3));
                }

                programs.add(program);
            }

            arrangement.setPrograms(programs);
            List<Location> listOfLocationsInArrangement = new ArrayList<>();
            List<String> listOfTypesOfAccomodation = List.of("1/1","1/2","1/3","1/2+1","1/3+1","1/4");
            Set<Accomodation> accomodations = new HashSet<>();
            int numberOfAccomodations = rand.nextInt(5-1)+1;
            for (int j=0; j< numberOfAccomodations;j++){
                Accomodation accomodation = new Accomodation();

                accomodation.setName(getRandomLine(namesOfAccomodations));
                accomodation.setCategory(rand.nextInt(5-1)+1);
                accomodation.setType(listOfTypesOfAccomodation.get(rand.nextInt(listOfTypesOfAccomodation.size())));


                listOfLocationsInArrangement = arrangement.getPrograms()
                        .stream()
                        .map(program -> program.getLocations())
                        .flatMap(set -> set.stream())
                        .collect(Collectors.toList());

                Location location = listOfLocationsInArrangement.get(rand.nextInt(listOfLocationsInArrangement.size()));
                List<Location> list = locationService.findSame(location);
                if(!list.isEmpty()){
                    accomodation.setLocation(list.get(0));
                }else{
                    accomodation.setLocation(location);
                }

                accomodation.setSafe(rand.nextBoolean());
                accomodation.setAc(rand.nextBoolean());
                accomodation.setFridge(rand.nextBoolean());
                accomodation.setTv(rand.nextBoolean());

                accomodations.add(accomodation);
            }

            Map<Location, Long> frequencyMap = listOfLocationsInArrangement
                    .stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Long maxFrequency = frequencyMap.values().stream().max(Long::compareTo).orElse(0L);

            Location mostFrequentLocation = frequencyMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(maxFrequency))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            arrangement.setName(mostFrequentLocation.getCity() + " " + firstDate + "_" + rand.nextInt(100000));

            arrangement.setAccomodations(accomodations);
            arrangementService.addArrangement(arrangement);
        }

        for(int i = 0; i < 5; i++) {
            Arrangement arrangement = new Arrangement();
            arrangement.setPrice(rand.nextInt(UPPER_BOUND_PRICE-LOWER_BOUND_PRICE)+LOWER_BOUND_PRICE);
            arrangement.setStatus("Nedostupan");
            arrangement.setRemark(lorem.getParagraphs(1,3));
            arrangement.setTransportation(transporations.get(rand.nextInt(transporations.size())));

            Set<Program> programs = new HashSet<>();
            int numberOfPrograms = rand.nextInt(UPPER_BOUND_NUMBER_OF_PROGRAMS-LOWER_BOUND_NUMBER_OF_PROGRAMS)+LOWER_BOUND_NUMBER_OF_PROGRAMS;
            java.sql.Date firstDate = getRandomDateBefore();

            String nameOfStartingLocation = namesOfStartingLocations.get(rand.nextInt(namesOfStartingLocations.size()));
            for(int j=0; j < numberOfPrograms; j++ ){
                Program program = new Program();

                Set<Location> locations = new HashSet<>();
                int numberOfLocations = rand.nextInt(3-1)+1;
                if (j==0 || j==numberOfPrograms-1){
                    Location location = new Location();
                    //String names = nameOfStartingLocation;
                    List<String> cityCountryContinent = stringToList(nameOfStartingLocation);
                    location.setCity(cityCountryContinent.get(0));
                    location.setCountry(cityCountryContinent.get(1));
                    location.setContinent(cityCountryContinent.get(2));
                    List<Location> list = locationService.findSame(location);
                    if(!list.isEmpty()){
                        locations.add(list.get(0));
                    }else{
                        locations.add(location);
                    }
                    locations.add(location);
                }
                for(int k=0; k < numberOfLocations; k++ ){
                    Location location = new Location();
                    String names = getRandomLine(namesOfLocations);
                    List<String> cityCountryContinent = stringToList(names);
                    location.setCity(cityCountryContinent.get(0));
                    location.setCountry(cityCountryContinent.get(1));
                    location.setContinent(cityCountryContinent.get(2));
                    List<Location> list = locationService.findSame(location);
                    if(!list.isEmpty()){
                        locations.add(list.get(0));
                    }else{
                        locations.add(location);
                    }

                }
                program.setLocations(locations);

                program.setDate(incrementDate(firstDate,j).toLocalDate());
                if (j==0){
                    String randomTime = getRandomTime();
                    if (arrangement.getTransportation().equals("Voz")){
                        String prefix = "Полазак са перона у " + randomTime + ". ";
                        program.setDescription(prefix + lorem.getParagraphs(1,3));
                    }
                    else if (arrangement.getTransportation().equals("Avion")) {
                        String prefix = "Полазак са аеродрома у"  + randomTime + ". ";
                        program.setDescription(prefix + lorem.getParagraphs(1,3));
                    }
                    else{
                        program.setDescription(lorem.getParagraphs(1,3));
                    }
                }
                else if (j==numberOfPrograms-1){
                    List<String> times = List.of("jутарњим","преподневним","пoподневним","вечерњим");
                    String postfix = "Долазак у  " + stringToList(getRandomLine(namesOfStartingLocations)).get(0)+ " у "  + times.get(rand.nextInt(times.size())) + " часовима" ;
                    program.setDescription(lorem.getParagraphs(1,3) + postfix);
                }
                else{
                    program.setDescription(lorem.getParagraphs(1,3));
                }

                programs.add(program);
            }

            arrangement.setPrograms(programs);
            List<Location> listOfLocationsInArrangement = new ArrayList<>();
            List<String> listOfTypesOfAccomodation = List.of("1/1","1/2","1/3","1/2+1","1/3+1","1/4");
            Set<Accomodation> accomodations = new HashSet<>();
            int numberOfAccomodations = rand.nextInt(5-1)+1;
            for (int j=0; j< numberOfAccomodations;j++){
                Accomodation accomodation = new Accomodation();

                accomodation.setName(getRandomLine(namesOfAccomodations));
                accomodation.setCategory(rand.nextInt(5-1)+1);
                accomodation.setType(listOfTypesOfAccomodation.get(rand.nextInt(listOfTypesOfAccomodation.size())));


                listOfLocationsInArrangement = arrangement.getPrograms()
                        .stream()
                        .map(program -> program.getLocations())
                        .flatMap(set -> set.stream())
                        .collect(Collectors.toList());

                Location location = listOfLocationsInArrangement.get(rand.nextInt(listOfLocationsInArrangement.size()));
                List<Location> list = locationService.findSame(location);
                if(!list.isEmpty()){
                    accomodation.setLocation(list.get(0));
                }else{
                    accomodation.setLocation(location);
                }

                accomodation.setSafe(rand.nextBoolean());
                accomodation.setAc(rand.nextBoolean());
                accomodation.setFridge(rand.nextBoolean());
                accomodation.setTv(rand.nextBoolean());
                accomodation.setInternet(rand.nextBoolean());

                accomodations.add(accomodation);
            }

            Map<Location, Long> frequencyMap = listOfLocationsInArrangement
                    .stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Long maxFrequency = frequencyMap.values().stream().max(Long::compareTo).orElse(0L);

            Location mostFrequentLocation = frequencyMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(maxFrequency))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            arrangement.setName(mostFrequentLocation.getCity() + " " + firstDate + "_" + rand.nextInt(100000));

            arrangement.setAccomodations(accomodations);
            arrangementService.addArrangement(arrangement);
        }
    }


}
