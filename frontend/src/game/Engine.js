import Phaser from "phaser";

class GameEngine {
    constructor(parent) {
        this.parent=parent;
        this.game=null;
    }

    initialise() {
        this.game = new Phaser.Game({
            scene: {
                create: this.create,
                update: this.update,
                preload: this.preload,
            },
            width:500,
            height:500,
            parent: this.parent,
            type: Phaser.AUTO
        });
    }

    create(){
    }

    preload(){
    }

    update() {

    }
}

export default GameEngine;