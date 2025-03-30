import Phaser from "phaser";
import Scene from "./Scene.js";

class GameEngine {
    constructor(parent) {
        this.parent=parent;
        this.game=null;
    }

    initialise() {
        this.game = new Phaser.Game({
            scene: [Scene],
            width:500,
            height:500,
            parent: this.parent,
            type: Phaser.AUTO,
            physics: {
                default: 'arcade',
                fps: 60,
                arcade: {
                    debug: true
                }
            }
        });
    }
}

export default GameEngine;