package com.amica.games.ws;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("games")
public class GameController {
    public class OnePlayerGame {
        private int ID;

        public OnePlayerGame(int ID) {
            this.ID = ID;
        }

        public int getID() {
            return ID;
        }
    }

    @GetMapping
    public OnePlayerGame getGame() {
        return new OnePlayerGame(1);
    }
}
