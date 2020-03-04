package users.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import users.domain.Stats;
import users.domain.User;
import users.services.StatsService;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private static final Logger log = LoggerFactory.getLogger(StatsController.class);

    org.springframework.core.env.Environment environment;
    StatsService statsService;
    public StatsController(Environment environment, StatsService statsService) {
        this.environment = environment;
        this.statsService = statsService;
    }

    @GetMapping("/status")
    public String status(){
        log.info("Stats endpoint hit");
        return "Working on port " + " " + environment.getProperty("local.server.port");

    }

    @GetMapping("/")
    public ResponseEntity<Iterable<Stats>> getAllStats() {
        Iterable<Stats> foundStats = statsService.findAllStats();

        if(foundStats == null){
            log.info("no stats in database");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(foundStats);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stats> getStatsById(@PathVariable String id) {
        Stats statsById = statsService.findStatsById(id);

        if (statsById == null) {
            log.info("unable to find that stats with that id");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }


        return ResponseEntity.status(HttpStatus.OK).body(statsById);
    }


    @PostMapping("/{id}")
    public ResponseEntity<Stats> postGivenStats(@RequestBody Stats stats) {
        Stats createdStats = statsService.saveStats(stats);

        if (createdStats.getId().equals("") || createdStats.getId() == null) {
            log.info("Bad Stats creation, stats not created!");
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(createdStats);
        }
        log.info("Stats " + " " + createdStats.getId() + " " +"created and inserted successfully!");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStats(@PathVariable String id){
        Stats statsToDelete = statsService.findStatsById(id);
        if(statsToDelete == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(("Error deleting stats!!!"));
        }
        statsService.deleteStats(id);
        return ResponseEntity.status(HttpStatus.OK).body("Stats deleted successfully");
    }


}
